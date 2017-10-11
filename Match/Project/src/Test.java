
public class Test {

	
	public static void main(String[] args) {
		
		Input input = new Input();
	    input.dealInput();
		
		Match match = new Match();
		try {
			match.matching();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Output output = new Output();;
		try {
			output.Print();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
    

}
