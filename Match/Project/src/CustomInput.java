import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class CustomInput {
	
	private static final int SNUM = 300;
	private static final int DNUM = 20;
	
	private Random random = new Random();
	private String[] sno = new String[SNUM];
	private String[] dno = new String[DNUM];
	private String[] time = new String[42];
	private String[] tags = new String[25];
	Map<Integer, String>week = new HashMap<Integer, String>();
	Map<Integer, String>timeSlice = new HashMap<Integer, String>();
	
	public void init(){
		sno = getSno();
		dno = getDno();
		time = getTime();
		tags = getTags();
	}
	
	private String[] getTags() {
		String[] tmp = new String[]{
			"study","English","Math","reading","stick",
			"film","music","dance","animation","cheerful",
			"chess","Gomoku","Bridge","Dominoes","Blackjack",
			"football","basketball","sport","travel","positive",
			"programming","code","bug","effort","conservative"
		};
		return tmp;
	}
	
	private String[] getTime() {
		week = getWeek();
		timeSlice = getTimeSlice();
		String[] tmp = new String[42];
		for(int i = 0; i < 7; i++) {
			for(int j = 0; j < 6; j++) {
				int index = i * 6 + j;
				tmp[index] = week.get(i) + timeSlice.get(j);
			}
		}
		return tmp;
	}

	private Map<Integer, String> getTimeSlice() {
		Map<Integer, String>tmp = new HashMap<Integer, String>();
		tmp.put(0, "8:00~10:00");
		tmp.put(1, "10:00~12:00");
		tmp.put(2, "14:00~16:00");
		tmp.put(3, "16:00~18:00");
		tmp.put(4, "18:00~20:00");
		tmp.put(5, "20:00~22:00");
		return tmp;
	}

	private Map<Integer, String> getWeek() {
		Map<Integer, String>tmp = new HashMap<Integer, String>();
		tmp.put(0, "Mon.");
		tmp.put(1, "Tues.");
		tmp.put(2, "Wed.");
		tmp.put(3, "Thur.");
		tmp.put(4, "Fri.");
		tmp.put(5, "Sat.");
		tmp.put(6, "Sun.");
		return tmp;
	}

	private String[] getDno() {
		String[] tmp = new String[DNUM];
		for(int i = 0; i < DNUM; i++) {
			String s = String.valueOf(i+1);
			if(i < 9) {
				tmp[i] = "D00"+s;
			}
			else {
				tmp[i] = "D0"+s;		
			}
		}
	    return tmp;
    }

	private String[] getSno() {
		String[] tmp = new String[SNUM];
		for(int i = 0; i < SNUM; i++) {
			int t = ((i / 50) + 1) * 100 + i % 50 + 1; 
			String s = String.valueOf(t);
			tmp[i] = "031502"+s;	
		}
		return tmp;
    }

	public void customInput() throws FileNotFoundException{
		
		File file = new File("myinput_data.txt");
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
		
		Map<String,JSONArray>map;
		JSONArray stuArr;
	    JSONArray depaArr;
		
		map = new HashMap<String, JSONArray>();
		
		stuArr = new JSONArray();
		stuArr = getStuArr();
		map.put("students", stuArr);
		
		depaArr = new JSONArray();
		depaArr = getDepaArr();
		map.put("departments", depaArr);
		
		JSONObject mapJson = new JSONObject(map);
		System.out.println(mapJson);
		
	}

	private JSONArray getDepaArr() {
		JSONArray tmp = new JSONArray();
		JSONObject[] depaObj= new JSONObject[DNUM];
		
		for(int i = 0; i < DNUM; i++) {
			depaObj[i] = new JSONObject();
			
			//得到部门纳新人数10~15
			int memberLimit = random.nextInt(6) + 10;
			//得到常规活动时间段2~3个
			int timeMemer = random.nextInt(1) + 2;
//			int timeMemer = 2;
			String[] eventTime = getEventTime(timeMemer);
			//得到标签2~3个
			int tagsMember = random.nextInt(2) + 2;
			String[] tags = getTags(tagsMember);
			
			depaObj[i].put("member_limit", memberLimit);
			depaObj[i].put("department_no", dno[i]);
			depaObj[i].put("event_schedules", eventTime);
			depaObj[i].put("tags", tags);
			
			tmp.put(depaObj[i]);
		}

		return tmp;
	}

	//部门例会时间最好要有间隔,间隔1~2天
	private String[] getEventTime(int timeMemer) {
		String[] tmp = new String[timeMemer];
		//index表示星期几的时间
		int index = random.nextInt(7);
		for(int i = 0; i < timeMemer; i++) {
			//num一天中表示第几个时段
			int num = random.nextInt(6);
			tmp[i] = time[index * 6 + num];
			
			int interval = random.nextInt(2) + 1;
			index = (index + interval) % 7;
		}
		return tmp;
	}

	private JSONArray getStuArr() {
		
		JSONArray tmp = new JSONArray();
		JSONObject[] stuObj= new JSONObject[SNUM];
		
		
		for(int i=0; i < SNUM; i++) {
			stuObj[i] = new JSONObject();
			
			//得到空闲时间10~14个
			int timeMemer = random.nextInt(5) + 10;
			String[] freeTime = getFreeTime(timeMemer);
			//得到标签2~5个
			int tagsMember = random.nextInt(4) + 2;
			String[] tags = getTags(tagsMember);
			//得到部门意愿0~5个
			int depaMember = random.nextInt(6);
			String[] depa = getDepa(depaMember);
			 
			stuObj[i].put("student_no", sno[i]);
			stuObj[i].put("free_time", freeTime);
			stuObj[i].put("tags", tags);
			stuObj[i].put("applications_department", depa);
			tmp.put(stuObj[i]);
			
		}

		return tmp;
	}

	private String[] getTags(int tagsMember) {
		String[] tmp = new String[tagsMember];
		int num = random.nextInt(5);
		int[] index = buildRandomArray(5);
		
		//在一个组内选标签,num表示第几组
		for(int i = 0; i < tagsMember; i++) {
			tmp[i] = tags[index[i] + num * 5];
		}
		
		return tmp;
	}

	private String[] getFreeTime(int timeMemer) {
		String[] tmp = new String[timeMemer];
		int[] index = buildRandomArray(time.length);
		
		for(int i = 0; i < timeMemer; i++){
			tmp[i] = time[index[i]];
		}
		return tmp ;
	}

	private String[] getDepa(int depaMember) {
		String[] tmp = new String[depaMember];
		int[] index = buildRandomArray(DNUM);
		
		for(int i = 0; i < depaMember; i++){
            tmp[i] = dno[index[i]];
		}
		return tmp ;
	}

	private int[] buildRandomArray(int length) {
		
		int[] array = new int[length];
		for(int i = 0; i < length; i++) {
			array[i] = i;
		}
		
		int randomInt = 0;
		/*
		 * 随机产生一个0到length-1的随机数，使得该下标的数值与下标为0的数值交换，
		 *  处理多次，能够获取一个有0到length-1随机排列的一维数组,
		 */
		int num = 2 * length;
		for (int i = 0; i < num; i++) {
			randomInt = random.nextInt(length);
			int temp = array[0];
			array[0] = array[randomInt];
			array[randomInt] = temp;
		}

		return array;
	}
    
	public static void main(String[] args) throws FileNotFoundException {
		
		CustomInput custom = new CustomInput();
		custom.init();
		custom.customInput();

	}
	

}
