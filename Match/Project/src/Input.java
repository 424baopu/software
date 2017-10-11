import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


public class Input {
	
	static final String PATH = "input_data.txt";
	static final String STUDENTS = "students";
	static final int SNUM = 300;
	static final String DEPARTMENTS = "departments";
	static final int DNUM = 20;
	
	static Student[] stu = new Student[SNUM];
	static Department[] depa = new Department[DNUM];
	
	public void dealInput() {
		
		//读取文件，将JSONTokener对象作为参数来构造JSONObject或JSONArray
		FileReader reader = null;
		try {
			reader = new FileReader(new File(PATH));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONTokener jsonTokener = new JSONTokener(reader);
		
		//由于输入文本最外面是object类型
		JSONObject jsonObject = new JSONObject(jsonTokener);
		
		//jsonobject包含students与departments字段，均为数组
		JSONArray students = jsonObject.getJSONArray(STUDENTS);
		dealStudents(students);
		
		JSONArray departments = jsonObject.getJSONArray(DEPARTMENTS);
		dealDepartments(departments);
		
	}

	private void dealDepartments(JSONArray departments) {
		
		final String DNO = "department_no";
		final String DMEM = "member_limit";
		final String DTAGS = "tags";
		final String DSCH = "event_schedules";
		
		for(int i = 0; i < DNUM; i++) {
			JSONObject department = departments.getJSONObject(i);
			String D_no = department.getString(DNO);
	    	int D_mem = department.getInt(DMEM);
	    	JSONArray D_tag = department.getJSONArray(DTAGS);
	    	JSONArray D_sch = department.getJSONArray(DSCH);
	    	
	    	if(depa[i] == null){
	    		int tag_sz = D_tag.length();
	    		int sch_sz = D_sch.length();
	    		depa[i] = new Department(tag_sz,sch_sz);
	    	}
	    	
	    	depa[i].setNo(D_no);
	    	depa[i].setMemberLimit(D_mem);
	    	for(int j=0;j<D_tag.length();j++){
	    		depa[i].setTags(j, D_tag.getString(j));
	    	}
	    	for(int j=0;j<D_sch.length();j++){
	    		depa[i].setEventSchedules(j, D_sch.getString(j));
	    	}
	    	
	    	depa[i].setDateTime();
		}
	}

	private void dealStudents(JSONArray students2) {

		final String SNO = "student_no";
		final String STAGS = "tags";
		final String STIME = "free_time";
		final String SDEPT = "applications_department";
		
		for(int i=0;i<SNUM;i++){
	    	//students有四个字段，编号，部门意愿，空闲时间，标签，后三个为JSONArray类型
	    	JSONObject student = students2.getJSONObject(i);
	    	String sno = student.getString(SNO);
	    	JSONArray sdept = student.getJSONArray(SDEPT);
	    	JSONArray stags = student.getJSONArray(STAGS);
	    	JSONArray stime = student.getJSONArray(STIME);
	    	
	    	if (stu[i] == null) {
	    		int dept_sz = sdept.length();
	    		int tag_sz = stags.length();
	    		int free_sz = stime.length();
	    		stu[i] = new Student(dept_sz,tag_sz,free_sz);
	    	}
	    	//Sno
	    	stu[i].setNo(sno);
	    	
	    	//Sdept
	    	for(int j=0;j<sdept.length();j++){
	    		stu[i].setDeptments(j, sdept.getString(j));
	    	}
	    	
	    	//Stag
	    	for(int j=0;j<stags.length();j++){
	    		stu[i].setTags(j, stags.getString(j));
	    	}
	    	
	    	//Sfree
	    	for(int j=0;j<stime.length();j++){
	    		stu[i].setFreeTime(j, stime.getString(j));

	    	}
	    	
	    	stu[i].setDateTime();
	    }
		
	}

}
