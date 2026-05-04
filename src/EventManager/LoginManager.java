package EventManager;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class LoginManager {
	public static User authenticateUser() throws InvalidLoginException{
		Properties stu=new Properties();
		Properties adm=new Properties();
		
		try (FileInputStream input = new FileInputStream("student.properties")) {
		    stu.load(input);
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		try (FileInputStream input = new FileInputStream("admin.properties")) {
		    adm.load(input);
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		
		boolean flag=false;
		
		do{System.out.print("Enter Username:");
		String id=EventManagementSystem.sc.next();
		if(id.equals("close")) {
			throw new InvalidLoginException("close");
		}
		if(adm.containsKey(id)) {
			String yn;
			do{System.out.println("Are you trying to log in as an administrator? Y/N");
			yn=EventManagementSystem.sc.next().toLowerCase();}while(!yn.equals("y") && !yn.equals("n"));
			if(yn.equals("y")) {
				flag=false;
				/*code for admin*/
				System.out.print("Enter password:");
				String pw=EventManagementSystem.sc.next();
				if(pw.equals(adm.getProperty(id))) {
					User user=new Admin(id);
					return user;
				}else {
					throw new InvalidLoginException("Error: Wrong Password.");
				}
			}
			else {
				System.out.println("Please try again");
				flag=true;
			}
		}else if(stu.containsKey(id)) {
			flag=false;
			/*code for student*/
			System.out.print("Enter password:");
			String pw=EventManagementSystem.sc.next();
			if(pw.equals(stu.getProperty(id))) {
				User user=new Student(id);
				return user;
			}else {
				throw new InvalidLoginException("Error: Wrong Password.");
			}
		}else {
			System.out.println("Username doesn't exist");
			System.out.println("Type close to exit login");
			flag=true;
		}}while(flag);
		return null;
	    }
	public static void signup() {
		Properties stu=new Properties();
		Properties adm=new Properties();
		DB studentDB=DBMaker.newFileDB(new File("students.db")).closeOnJvmShutdown().make();
		ConcurrentNavigableMap<String, Student> studentMap=studentDB.getTreeMap("students");
		try (FileInputStream input = new FileInputStream("student.properties")) {
		    stu.load(input);
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		try (FileInputStream input = new FileInputStream("admin.properties")) {
		    adm.load(input);
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		boolean flag=true;
		String id="";
		while(flag) {
			System.out.print("Create ID:");
			id=EventManagementSystem.sc.next();
			if(stu.containsKey(id) || adm.containsKey(id)) {
				System.out.println("ID already exists. please choose another");
			}else {
				flag=false;
			}
		}
		EventManagementSystem.sc.nextLine();
		System.out.println("Enter Name");
		String name=EventManagementSystem.sc.nextLine();
		String pass="";
		flag=true;
		while(flag) {
			String repass;
			Console console = System.console();
	        if (console != null) {
	            pass = new String(console.readPassword("Create a password: "));
	            repass=new String(console.readPassword("Re enter password: "));
	        } else {
	        	System.out.print("Create a password(no spaces): ");
	        	pass=EventManagementSystem.sc.next();
				System.out.print("Re enter password: ");
				repass=EventManagementSystem.sc.next();
	        }
			if(pass.equals(repass) && pass.length() > 0) {
				flag=false;
			}else {
				System.out.println("Passwords do not match.Please try again.");
			}
		}
		stu.setProperty(id,pass);
		try (FileOutputStream output = new FileOutputStream("student.properties")) {
		    stu.store(output, null);
		    Student student=new Student(id);
		    student.setName(name);
		    studentMap.put(id,student);
		    studentDB.commit();
		    System.out.println("You have successfully signed up.");
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
	}
}
