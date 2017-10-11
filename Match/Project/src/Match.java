import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Match {
	
	public static Map<String, ArrayList<String>> result;
	private static ArrayList<String>[] resultList;
	private static Map<String, ArrayList<String>> map;
    private static ArrayList<String>[] list;
    private static Map<String, Integer> stuMap;
    
    
    Match(){
    	result = new HashMap<String, ArrayList<String>>();
    	resultList= new ArrayList[20];
    	map = new HashMap<String, ArrayList<String>>();
	    list= new ArrayList[20];
	    stuMap = new HashMap<String, Integer>();
    }

	public void matching() {
	    
	    //将学生学号与下标映射存入stuMap
	    for(int i = 0; i < Input.stu.length; i++) {
	    	String no = Input.stu[i].getNo();
	    	stuMap.put(no, i);
	    }
	    
	    //map存每轮报名情况，result存匹配成功的结果，每个部门初始化一个List用来存报名学生学号，
	    for(int i = 0; i < Input.depa.length; i++) {
	    	list[i] = new ArrayList<String>();
	    	resultList[i] = new ArrayList<String>();
	    	String no = Input.depa[i].getNo();
	    	
	    	map.put(no, list[i]);
	    	result.put(no, resultList[i]);
	    }
	    
	    //进行五轮匹配，匹配之后map要清除所有部门List
	    for(int i = 0; i < 5; i++) {
	    	matchProcess(i);
            clearList();
	    }
	    
	    //将没有接收到学生的部门从result里删除
	    for(int i = 0; i < Input.depa.length; i++){
	    	String dno = Input.depa[i].getNo();
	    	if(result.get(dno).size() == 0){
	    		result.remove(dno);
	    	}
	    }
	    
	}

	private void clearList() {
		// TODO Auto-generated method stub
		for(int j = 0; j < Input.depa.length; j++) {
    		list[j].clear();
    	}
	}

	private void matchProcess(int index) {
		
		//将报名情况填入Map
		for(int j = 0; j < Input.stu.length; j++) {
			//如果此轮志愿不为空
    		if(Input.stu[j].getDeptments().length > index) {
    			//取得学号与部门编号，如果二者时间不冲突，则加入map
    			String dept = Input.stu[j].getDeptments()[index];
	    	    String no = Input.stu[j].getNo();

	    	    if(isFreeTimeConflict(j,dept) == true){
	    			map.get(dept).add(no);
	    	    }
    		}
    		
    	} 
		
		//对时间不冲突的学生与部门，第二次筛选
		for(int i = 0; i < Input.depa.length; i++) {
			
			String dno = Input.depa[i].getNo();
			ArrayList<String> tmp = map.get(dno);//报名学生学号List
			int num = Input.depa[i].getNumRemaining();//部门剩余名额
			
			//如果部门还可以录取
			if(num > 0) {
				
				/*
				 * 如果部门剩余名额小于报名人数，根据优先级排序，只取出前num个学生，全部录取
				 * 参数为：部门下标，学生学号List，部门剩余名额
				 */
			    tmp = filter(i,tmp,num);
			    result.get(dno).addAll(tmp);
			    
			    //对部门剩余名额，学生录取个数，学生空闲时间进行更新
				int newNum = num - tmp.size();
				Input.depa[i].setNumRemaining(newNum);
				updateStuStatus(i, tmp);
				
			}
			
		}
		
//		System.out.println(map);
//		System.out.println(result);
	
    }
	
    //根据学生与当前部门标签匹配程度，学生已经被部门录取数目来对学生进行筛选
	private ArrayList<String> filter(int index, ArrayList<String> tmp, int num) {
		
		String[] stuNo = (String[])tmp.toArray(new String[tmp.size()]);
		String[] depaTags = Input.depa[index].getTags();
		double[] rate = new double[tmp.size()];//标签匹配程度
		
		for(int i = 0 ; i < stuNo.length; i++) {
			String sno = stuNo[i];
			int indexx = stuMap.get(sno);
			String[] stuTags = Input.stu[indexx].getTags();//得到学生标签
			
			rate[i] = getRate(depaTags,stuTags);
			double d = rate[i];
			//函数为1/(被录取次数+1)+0.3*标签匹配程度
			rate[i] = 1 / (Input.stu[indexx].getNumAdmit() + 1) + 0.3 * d;
		}
		
		stuNo = mysort(rate,stuNo);
		
		ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(stuNo));
		
		//淘汰末尾元素
		int newnum = arrayList.size() - num;
		for(int i = 0; i < newnum; i++){
			int a = arrayList.size() - 1;
			arrayList.remove(a);
		}

		return arrayList;
	}

	private String[] mysort(double[] rate, String[] stuNo) {
		
		for(int i = 0; i < rate.length-1; i++){
			int k = i;
			for(int j = i+1; j<rate.length; j++){
				if(rate[j] > rate[k]) k=j;
			}
			if(k!=i){
				double temp1;
				temp1=rate[k];
				rate[k]=rate[i];
				rate[i]=temp1;
				
				String temp2;
				temp2=stuNo[k];
				stuNo[k]=stuNo[i];
				stuNo[i]=temp2;
				
			}
			
		}
		
		return stuNo;
	}

	private double getRate(String[] depaTags, String[] stuTags) {
		// TODO Auto-generated method stub
		int num1 = depaTags.length;
		int num2 = stuTags.length;
		int num = 0;
		for(int i = 0; i < num1; i++) {
			for(int j = 0; j < num2; j++) {
				if(depaTags[i].equals(stuTags[j])){
					num++;
				}
			}
		}
		double rate = (double)num/num2;
		return rate;
	}

	private void updateStuStatus(int depaIndex, ArrayList<String> tmp) {
		
		int[][] depFreeTime = Input.depa[depaIndex].getDateTime();
		
		for(int i = 0; i < tmp.size(); i++) {
			String sno = tmp.get(i);
			int index = stuMap.get(sno);
			
			int num = Input.stu[index].getNumAdmit();
			Input.stu[index].setNumAdmit(num + 1);//更新被录取次数
			updateStuFreeTime(index, depFreeTime);//更新空闲时间
		}
		
	}

	private void updateStuFreeTime(int index, int[][] depFreeTime) {
		// TODO Auto-generated method stub
		int[][] stuFreeTime = Input.stu[index].getDateTime();
		
		for(int i = 0; i < depFreeTime.length; i++) {
			for(int j = 0; j < stuFreeTime.length; j++) {
				if(stuFreeTime[j][1] != 0){
					if(stuFreeTime[j][0] <= depFreeTime[i][0]&&stuFreeTime[j][1] >= depFreeTime[i][1]) {
					    if(stuFreeTime[j][0] == depFreeTime[i][0]){
					    	Input.stu[index].dateTime[j][0] = depFreeTime[i][1];
					    }
					    else{
					    	Input.stu[index].dateTime[j][1] = depFreeTime[i][0];
					    }
					    break;
				    }
				}
			}
		}
	}

	//判断学生部门时间是否冲突
	private boolean isFreeTimeConflict(int index, String string) {
	    
		
		int[][] stuFreeTime = Input.stu[index].getDateTime();
		int[][] depFreeTime = null;
		
		//根据部门编号得到部门例会时间
		for(int i = 0; i<Input.depa.length; i++) {
			
			if(string.equals(Input.depa[i].getNo())) {
				depFreeTime = Input.depa[i].getDateTime();
				break;
			}
		}
		
		for(int i = 0; i < depFreeTime.length; i++) {
			
			boolean isInclude = false;
			for(int j=0; j < stuFreeTime.length; j++) {
				
				if(stuFreeTime[j][1] == stuFreeTime[j][0]) {
					continue;	
				}
				if((stuFreeTime[j][0] <= depFreeTime[i][0])&&(stuFreeTime[j][1] >= depFreeTime[i][1])) {
				    isInclude = true; 
					break;
				}
				
			}
			if(isInclude == false){
				return false;
				
			}
			
		}
		return true;
	}
    
}






	