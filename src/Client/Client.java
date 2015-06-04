import java.io.Console;
import java.util.Scanner;

class Client{
	private static boolean login_checked = false;
	private static String username;
	private static String password;

	public static void main(String[] args) {
		// Local variable
		int swValue;
		
		do{
		// Display menu graphics
		System.out.println("================================");
		System.out.println("|        Client Menu           |");
		System.out.println("================================");
		System.out.println("| Options:                     |");
		System.out.println("|        1. Login              |");
		System.out.println("|        2. Change Permissions |");
		System.out.println("|        3. Log                |");
		System.out.println("|        4. Exit               |");
		System.out.println("================================");
		swValue = Keyin.inInt(" Select option: ");

		Init db = new Init();
		
		// db.log <- devolve o log
		//db.username <- username
		//db.password  <- password
 		
		// Switch construct
		
			switch (swValue) {
			case 1:
				System.out.println("LOGIN");
				//METHOD TO ASK FOR CREDENTIALS AND TRY TO LOGIN FROM DB
				Scanner s = new Scanner(System.in);
				System.out.println("Username: " );

				//TODO CHECK IF USERNAME EXISTS
				username = s.next();

				if(true){
					System.out.println("Password: ");
					//TODO CHECK IF password matches EXISTS
					//s.next()
					password = s.next();

				}

				break;
			case 2:
				if(login_checked == true){

					
				}else{
					System.out.println("Please login first.");
				}
				break;
			case 3:
				if(login_checked == true){

					
				}else{
					System.out.println("Please login first.");
				}
				break;
			case 4:

				
				break;
			default:
				System.out.println("Invalid selection");
				break; // This break is not really necessary
			}
		}while(swValue != 4);
	}
}
