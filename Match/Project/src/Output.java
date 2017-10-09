import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Output {
	
	Map<String,JSONArray> lastResult;
    JSONArray unluckyStudent;
    JSONArray admitted;
    JSONArray unluckyDepartment;
    
    Output(){
    	lastResult = new HashMap<String, JSONArray>();
    	unluckyStudent = new JSONArray();
    	admitted = new JSONArray();
    	unluckyDepartment = new JSONArray();
    }
	
	public void Print() throws Exception{
		
		//����д�ļ�·��ʱ��һ��Ҫд�Ͼ�����ļ����ƣ�xx.txt�����������־ܾ����ʡ�
		File file = new File("output_data.txt");
		if(!file.exists()){
		    //�ȵõ��ļ����ϼ�Ŀ¼���������ϼ�Ŀ¼���ڴ����ļ�
		    file.getParentFile().mkdir();
		    try {
		        //�����ļ�
		        file.createNewFile();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		
        PrintStream ps = new PrintStream(file);
        System.setOut(ps);//�Ѵ����Ĵ�ӡ���������ϵͳ����ϵͳ�´��� ps���
        
        String s1 = "unlucky_student";
        unluckyStudent = getUnluckStudent();
        lastResult.put(s1, unluckyStudent);
        
        
        String s2 = "admitted";
        admitted = getAdmitted();
        lastResult.put(s2, admitted);
        
        String s3 = "unlucky_department";
        unluckyDepartment = getUnluckyDepartment();
        lastResult.put(s3, unluckyDepartment);
		 
		
        JSONObject mapJson = new JSONObject(lastResult);
		System.out.println(mapJson);
		
	}

	private JSONArray getAdmitted() {
		
		JSONArray tmp = new JSONArray();
		int num = Match.result.size();
		JSONObject[] match = new JSONObject[num];
		
		int i = 0;
	    for (Map.Entry<String, ArrayList<String>> entry : Match.result.entrySet()) {
	    	match[i] = new JSONObject();
	    	
	    	String s = entry.getKey();
	    	ArrayList<String> arr = entry.getValue();
            
			match[i].put("department_no", s);
			match[i].put("member", arr);
			tmp.put(match[i]);
	        i++;
	    }
	    
		return tmp;
	}

	private JSONArray getUnluckyDepartment() {
		
		ArrayList<String> dno = new ArrayList<String>();
		for(int i = 0; i < Input.depa.length; i++){
			if(Input.depa[i].getMemberLimit() == Input.depa[i].getNumRemaining()){
				dno.add(Input.depa[i].getNo());
			}
		    
		}
		JSONArray tmp = new JSONArray(dno);
		return tmp;
	}

	private JSONArray getUnluckStudent() {
		
		ArrayList<String> sno = new ArrayList<String>();
		for(int i = 0; i < Input.stu.length; i++){
			if(Input.stu[i].getNumAdmit() == 0){
				sno.add(Input.stu[i].getNo());
			}
		    
		}
		JSONArray tmp = new JSONArray(sno);
		return tmp;
	}

}
