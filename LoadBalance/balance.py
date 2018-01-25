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
		#print (content)
		data = json.loads(content)
		load = data["node-connector"][0]["opendaylight-port-statistics:flow-capable-node-connector-statistics"]["bytes"]["received"]+data["node-connector"][0]["opendaylight-port-statistics:flow-capable-node-connector-statistics"]["bytes"]["transmitted"]
		return load

    def put_flows(self,id, link_port, l_port, username="admin", password="admin"):
		http = httplib2.Http()
		http.add_credentials(username, password)
		headers = {'Accept': 'application/json'}
		flows_body = r'{"flow": [{"id": "%d","match":{"in-port": "%d","ethernet-match": {"ethernet-type": {"type": "0x0800"}},"ipv4-destination": "10.0.0.1/32"},"instructions": {"instruction": [{"order": "0","apply-actions": {"action": [{"order": "0","output-action": {"output-node-connector": "%d"}}]}}]},"priority": "1111","cookie": "1","table_id": "0"}]}'%(id,link_port,l_port)
		headers = {'Content-type': 'application/json'}
		response, content = http.request(uri='http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:4/flow-node-inventory:table/0/flow/%d'%id, body=flows_body, method='PUT',headers=headers)






    def put_flows_s1_s2_s3(self,username="admin", password="admin"):
		http = httplib2.Http()
		http.add_credentials(username, password)
		headers = {'Accept': 'application/json'}
		headers = {'Content-type': 'application/json'}
		s2_s3_flows_body = r'{"flow": [{"id": "1","match": {"in-port": "2","ethernet-match": {"ethernet-type": {"type": "0x0800"}},"ipv4-destination": "10.0.0.1/32"},"instructions": {"instruction": [{"order": "0","apply-actions": {"action": [{"order": "0","output-action": {"output-node-connector": "1"}}]}}]},"priority": "1111","cookie": "1","table_id": "0"}]}'


		response, content = http.request(uri='http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:2/flow-node-inventory:table/0/flow/1', body=s2_s3_flows_body, method='PUT',headers=headers)
	
		#print(content.decode())
		response, content = http.request(uri='http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:3/flow-node-inventory:table/0/flow/1', body=s2_s3_flows_body, method='PUT',headers=headers)
		
	
		s1_flows_body = r'{"flow": [{"id": "1","match":{"in-port": "2"},"instructions": {"instruction": [{"order": "0","apply-actions": {"action": [{"order": "0","output-action": {"output-node-connector": "1"}}]}}]},"priority": "1111","cookie": "1","table_id": "0"}]}'
		response, content = http.request(uri='http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:1/flow-node-inventory:table/0/flow/1', body=s1_flows_body, method='PUT',headers=headers)

		s1_flows_body = r'{"flow": [{"id": "2","match":{"in-port": "3"},"instructions": {"instruction": [{"order": "0","apply-actions": {"action": [{"order": "0","output-action": {"output-node-connector": "1"}}]}}]},"priority": "1111","cookie": "1","table_id": "0"}]}'
		response, content = http.request(uri='http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:1/flow-node-inventory:table/0/flow/2', body=s1_flows_body, method='PUT',headers=headers)

		

# 用于保存监控的链接信息
class Spy_port:

	def __init__(self):
		self.last_cost = 0
		self.cost = 0

		self.has_links = []

# 用于保存链接服务器端口信息
class Server_port:

	def __init__(self):
		# 引流
		self.dir = ""
		self.last_cost = 0
		self.cost = 0



class Util:

	# init
	def __init__(self):
		# start
		print("start\n")

		# init odl
		self.odl = OdlUtil('http://127.0.0.1:8181')

		# init info
		self.all_ports = {}
		self.all_ports["port-1"] = Spy_port()
		self.all_ports["port-1"].has_links = [5]
		self.all_ports["port-2"] = Spy_port()
		self.all_ports["port-2"].has_links = [4]
		self.all_ports["port-3"] = Spy_port()
		self.all_ports["port-3"].has_links = [6]
		self.all_ports["port-4"] = Server_port()
		self.all_ports["port-4"].dir = 1
		self.all_ports["port-5"] = Server_port()
		self.all_ports["port-5"].dir = 1
		self.all_ports["port-6"] = Server_port()
		self.all_ports["port-6"].dir = 1

		# define max (byte / 1000)
		self.max_cost = 1500


	def balance(self):
		sw = 4
		while True:
			time.sleep(1)
		

			# 获得数据
			for i in range(1, 7):
				index = "port-" + str(i)

				if self.all_ports[index].last_cost == 0:
					self.all_ports[index].last_cost = self.odl.get_load(sw, i)
					print "init port " + str(i)
					continue

				last = self.all_ports[index].last_cost
				now = self.odl.get_load(sw, i)
				cost = (now - last) / 1000
				self.all_ports[index].cost = cost

				if now - last == 0:
					#print "=="
					continue

				print "port " + str(i) + ":" + str(cost)
				if i == 6:
					
					self.do_balance()
				self.all_ports[index].last_cost = now		


	def do_balance(self):
		# 进行balance
		for i in range(1, 4):
			index = "port-" + str(i)

			if self.all_ports[index].cost > self.max_cost * 0.8:
				print index + " overloaded"
				if len(self.all_ports[index].has_links) > 1:
					temp_cost = 0
					max_index = 0
					for j in range(0, len(self.all_ports[index].has_links)):
						if temp_cost < self.all_ports["port-" + str(j + 1 + 3)].cost:
							temp_cost =  self.all_ports["port-" + str(j + 1 + 3)].cost
							max_index = j + 1 + 3

					temp_cost = 9999999999
					min_index = 0
					for j in range(1, 4):
						if temp_cost > self.all_ports["port-" + str(j)].cost:
							temp_cost = self.all_ports["port-" + str(j)].cost
							min_index = j

					if min_index != i:
						print "turn port-" + str(max_index) + " to port-" + str(min_index)
						self.odl.put_flows(max_index -3, max_index, min_index)
						self.all_ports[index].has_links.remove(max_index)
						self.all_ports["port-" + str(min_index)].has_links.append(max_index)
						break
						
				else:
					print "do nothing"
			elif self.all_ports[index].cost < self.max_cost * 0.2:
				if i == 1:
					continue
				if len(self.all_ports[index].has_links) >= 1:
					print index + " need recyle"
					for j in self.all_ports[index].has_links:
						print "turn port-" + str(j) + " to port-1"
						self.odl.put_flows(j -3, j, 1)
						self.all_ports[index].has_links.remove(j)
						self.all_ports["port-1"].has_links.append(j)

		print "\n\n"


					

				





util = Util()
util.balance()


		


