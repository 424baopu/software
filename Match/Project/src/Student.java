import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Student {
	
	String no;
	String[] tags;
	String[] deptments;
	String[] freeTime;
	int[][] dateTime;//������ʱ���ַ���תΪ����
	int numAdmit;
	static Map<String, Integer>week;
	static Map<String, Integer>hours;
	
	Student(int dept_sz,int tag_sz,int free_sz) {
		tags = new String[tag_sz];
		deptments = new String[dept_sz];
		freeTime = new String[free_sz];
		dateTime = new int[free_sz][2];
		numAdmit = 0;
	}
	
	public void init() {
		// TODO Auto-generated method stub
		week.put("Mon.", 0);
		week.put("Tues.", 1);
		week.put("Wed.", 2);
		week.put("Thur.", 3);
		week.put("Fri.", 4);
		week.put("Sat.", 5);
		week.put("Sun.", 6);
		
//		hours.put("00", value)
	}

	public int getNumAdmit() {
		return numAdmit;
	}

	public void setNumAdmit(int numAdmit) {
		this.numAdmit = numAdmit;
	}

	public int[][] getDateTime() {
		return dateTime;
	}
	
	//����ʱ������
	public void setDateTime(){
		for(int i=0;i<dateTime.length;i++){
			dateTime[i] = dealFreeTime(freeTime[i]);
		}
		sort(dateTime,0);
		merge();
		sort(dateTime,0);
//		print(dateTime);
	}
	
	private void merge() {
		int tmp = 0;
		for(int i = 1; i < dateTime.length; i++){
			if(dateTime[i][0] <= dateTime[tmp][1]){
				if(dateTime[i][1] > dateTime[tmp][1]){
					dateTime[tmp][1] = dateTime[i][1];
				}
				dateTime[i][0] = 0;
				dateTime[i][1] = 0;
			}
			else{
				tmp = i;
			}
		}
	}


	private  static void print(int[][] sample2) {
		// TODO Auto-generated method stub
		for(int i = 0; i< sample2.length; i++){
			System.out.println(sample2[i][0]+" "+sample2[i][1]);
		}
	}
	
	private void sort(int[][] dateTime2, int i) {
		// TODO Auto-generated method stub
		List<int[]> helpList = Arrays.asList(dateTime2);
		Collections.sort(helpList, new Comparator<int[]>(){
			@Override
		    public int compare(int[] arg0, int[] arg1) {
				if(arg0[0] >= arg1[0]){
				    return 1;
			    }
			    else{
				    return -1;
			    }
		    }			
		});
				
		dateTime = (int[][])helpList.toArray();
	}

	//��ȡʱ���ַ�����ת����ʱ��
		private int[] dealFreeTime(String string) {
			// TODO Auto-generated method stub
			int[] tmp = new int[2];
			int point_loc = string.indexOf('.');
			int wave_loc = string.indexOf('~');
			int fcolon_loc = string.indexOf(':');
			int scolon_loc = string.lastIndexOf(':');
			
			String begin_hour = string.substring(point_loc+1,fcolon_loc);
			String over_hour = string.substring(wave_loc+1,scolon_loc);
			String begin_minute = string.substring(fcolon_loc+1, wave_loc);
			String over_minute = string.substring(scolon_loc+1);
						
//			System.out.println(begin_hour+" "+begin_minute);
//			System.out.println(over_hour+" "+over_minute);
			
			tmp[0] = Integer.parseInt(begin_hour)*60+Integer.parseInt(begin_minute);
			tmp[1] = Integer.parseInt(over_hour)*60+Integer.parseInt(over_minute);
			String week = string.substring(0,point_loc);
			int weeks = 0;
			switch(week){
			case "Mon":
				weeks = 0;
				break;
			case "Tues":
				weeks = 1;
				break;
			case "Wed":
				weeks = 2;
				break;
			case "Thur":
				weeks = 3;
				break;
			case "Fri":
				weeks = 4;
				break;
			case "Sat":
				weeks = 5;
				break;
			case "Sun":
				weeks = 6;
				break;
			}
			
			tmp[0]+=weeks * 24 * 60;
			tmp[1]+=weeks * 24 * 60;
//			System.out.println(week+" "+tmp[0]);
//			System.out.println(begin_hour+":"+begin_minute+" "+tmp[1]);
//			System.out.println(over_hour+":"+over_minute+" "+tmp[2]);
			return tmp;
		}
	
    public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(int num,String str) {
		this.tags[num] = str;
	}

	public String[] getDeptments() {
		return deptments;
	}

	public void setDeptments(int num,String str) {
		this.deptments[num] = str;
	}

	public String[] getFreeTime() {
		return freeTime;
	}

	public void setFreeTime(int num,String str) {
		this.freeTime[num] = str;
	}

}
