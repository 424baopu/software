# coding=utf-8

import httplib2, time, json

INTERVAL = 10

class OdlUtil:
    url = ''
    def __init__(self, addr):
        self.url = addr
	self.put_flows_s1_s2_s3()
    def get_load(self,sw , port, container_name='default',username="admin", password="admin"):
		http = httplib2.Http()
		http.add_credentials(username, password)
		headers = {'Accept': 'application/json'}
		flow_name = 'flow_' + str(int(time.time()*1000))
		#h1h8body1 = r'{"flow": [{"id": "1","match": {"ethernet-match":{"ethernet-type": {"type": "2048"}},"ipv4-source":"10.0.0.1/32","ipv4-destination": "10.0.0.2/32"},"instructions": {"instruction": [{"order": "0","apply-actions": {"action": [{"output-action": {"output-node-connector": "4"},"order": "0"}]}}]},"priority": "1111","cookie": "1","table_id": "0"}]}'
		headers = {'Content-type': 'application/json'}
		response, content = http.request(uri='http://127.0.0.1:8181/restconf/operational/opendaylight-inventory:nodes/node/openflow:%d/node-connector/openflow:%d:%d'%(sw,sw,port), method='GET',headers=headers)
		data = json.loads(content)
		load = data["node-connector"][0]["opendaylight-port-statistics:flow-capable-node-connector-statistics"]["bytes"]["received"]+data["node-connector"][0]["opendaylight-port-statistics:flow-capable-node-connector-statistics"]["bytes"]["transmitted"]
		return load
    def put_flows(self,id, link_port, l_port, username="admin", password="admin"):
		http = httplib2.Http()
		http.add_credentials(username, password)
		headers = {'Accept': 'application/json'}
		flows_body = r'{"flow": [{"id": "%d","match":{"in-port": "%d"},"instructions": {"instruction": [{"order": "0","apply-actions": {"action": [{"order": "0","output-action": {"output-node-connector": "%d"}}]}}]},"priority": "1111","cookie": "1","table_id": "0"}]}'%(id,link_port,l_port)
		headers = {'Content-type': 'application/json'}
		response, content = http.request(uri='http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:4/flow-node-inventory:table/0/flow/%d'%id, body=flows_body, method='PUT',headers=headers)
    def get_load_diff(self,sw , port):
	cost = self.get_load(sw,port)
	time.sleep(INTERVAL)
	return self.get_load(sw,port)-cost



    def put_flows_s1_s2_s3(self,username="admin", password="admin"):
		http = httplib2.Http()
		http.add_credentials(username, password)
		headers = {'Accept': 'application/json'}
		headers = {'Content-type': 'application/json'}
		s2_s3_flows_body = r'{"flow": [{"id": "1","match": {"in-port": "2","ethernet-match": {"ethernet-type": {"type": "0x0800"}},"ipv4-destination": "10.0.0.1/32"},"instructions": {"instruction": [{"order": "0","apply-actions": {"action": [{"order": "0","output-action": {"output-node-connector": "1"}}]}}]},"priority": "1111","cookie": "1","table_id": "0"}]}'


		response, content = http.request(uri='http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:2/flow-node-inventory:table/0/flow/1', body=s2_s3_flows_body, method='PUT',headers=headers)
	
		print(content.decode())
		response, content = http.request(uri='http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:3/flow-node-inventory:table/0/flow/1', body=s2_s3_flows_body, method='PUT',headers=headers)
		
	
		s1_flows_body = r'{"flow": [{"id": "1","match":{"in-port": "2"},"instructions": {"instruction": [{"order": "0","apply-actions": {"action": [{"order": "0","output-action": {"output-node-connector": "1"}}]}}]},"priority": "1111","cookie": "1","table_id": "0"}]}'
		response, content = http.request(uri='http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:1/flow-node-inventory:table/0/flow/1', body=s1_flows_body, method='PUT',headers=headers)

		s1_flows_body = r'{"flow": [{"id": "2","match":{"in-port": "3"},"instructions": {"instruction": [{"order": "0","apply-actions": {"action": [{"order": "0","output-action": {"output-node-connector": "1"}}]}}]},"priority": "1111","cookie": "1","table_id": "0"}]}'
		response, content = http.request(uri='http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:1/flow-node-inventory:table/0/flow/2', body=s1_flows_body, method='PUT',headers=headers)
		

		


		

odl = OdlUtil('http://127.0.0.1:8181')

class Line:
	def __init__(self,sw, port, hop, band):
		self.sw=sw
		self.hop=hop
		self.port=port
		self.band=band
	def get_load_diff(self):
		return odl.get_load_diff(self.sw, self.port)

class Balance:
	def __init__(self,*links):
		self.links=links
	def cmp(self, c1, c2):
		if c1 <= 0 and c2 <= 0:
			return c1 < c2
		else:
			return c1 > c2
	def calc(self, fin):
		flag = False
		minCost=0
		for l in self.links:
			rest = l.band*INTERVAL-l.get_load_diff()
			if rest==0:
				continue
			cost = l.hop*(fin/rest-1)
			if flag == False:
				minCost=cost
				ret = l
				flag=True
			if self.cmp(minCost,cost):
				minCost = cost
				ret = l
				#print "debug: cmp(true) minCost(old)=%d, (new)=%d"%(minCost,cost)
			print "port=%d cost %d, fin=%d unused_band=%d"%(l.port,cost,fin,rest)
		return ret

L1 = Line(4, 2,2,10*1000*1000)
L2 = Line(4, 1,1,10*1000*1000)
L3 = Line(4, 3,2,10*1000*1000)

L4 = Line(4, 4,1,20*1000*1000)
L5 = Line(4, 5,1,20*1000*1000)
L6 = Line(4, 6,1,20*1000*1000)

balance = Balance(L1,L2,L3)

LinkIn = [L4,L5,L6]


#odl.put_flows_s2_s3(3)


#while True:
#	print odl.get_load_diff(4,6)

while True:
	for link in LinkIn:
		to = balance.calc(link.get_load_diff())
		# 将线路link的流表改为l
		odl.put_flows(link.port,link.port,to.port)
		print "link %d to %d"%(link.port,to.port)
	#time.sleep(30)
