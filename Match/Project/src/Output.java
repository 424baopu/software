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
		
		//在填写文件路径时，一定要写上具体的文件名称（xx.txt），否则会出现拒绝访问。
		File file = new File("output_data.txt");
		if(!file.exists()){
		    //先得到文件的上级目录，并创建上级目录，在创建文件
		    file.getParentFile().mkdir();
		    try {
		        //创建文件
		        file.createNewFile();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		
        PrintStream ps = new PrintStream(file);
        System.setOut(ps);//把创建的打印输出流赋给系统。即系统下次向 ps输出
        
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
