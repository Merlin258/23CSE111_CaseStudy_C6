package EventManager;

import org.mapdb.*;
import java.io.*;
import java.util.concurrent.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Admin extends User {
	private static final long serialVersionUID = 1L;
    public Admin(String id) {
    	userID=id;
    	name="Admin";
    }

    @Override
    public void openDashboard() {
        DB eventDB=DBMaker.newFileDB(new File("events.db")).closeOnJvmShutdown().make();
        DB studentDB=DBMaker.newFileDB(new File("students.db")).closeOnJvmShutdown().make();
        
        ConcurrentNavigableMap<String, Event> eventMap=eventDB.getTreeMap("events");
        ConcurrentNavigableMap<String, Student> studentMap=studentDB.getTreeMap("students");
        try {
	        System.out.println("\n--- Admin Dashboard | ID: " + userID + " ---");
	
	        while(true) {
	            System.out.println("\n[1] Create Event\n"
	            		+ "[2] View Participants\n"
	            		+ "[3] Download List\n"
	            		+ "[4] Close Registration\n"
	            		+ "[5] Update event\n"
	            		+ "[6] Logout");
	            System.out.print("Choice: ");
	            String choice = EventManagementSystem.sc.next();
	
	            if (choice.equals("1")) {
	                System.out.print("ID: ");
	                String id = EventManagementSystem.sc.next();
	            	if (eventMap.containsKey(id)) {
	            	    System.out.println("Error: An event with this ID already exists.");
	            	    continue;
	            	}
	            	EventManagementSystem.sc.nextLine();
	                System.out.print("Name: ");
	                String name = EventManagementSystem.sc.nextLine();
	                System.out.print("Category: ");
	                String cat = EventManagementSystem.sc.nextLine();
	                String date = "";
	                boolean flag = true;
	                while(flag) {
	                	System.out.print("Date (dd-mm-yyyy): ");
	                    date = EventManagementSystem.sc.next();
	                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	                    try {
	                        LocalDate parsedDate = LocalDate.parse(date, formatter);
	                        if(parsedDate.isBefore(LocalDate.now())) {
	                            System.out.println("Error: Event date cannot be in the past.");
	                        }else {
	                            flag=false;
	                        }
	                    } catch(DateTimeParseException e) {
	                        System.out.println("Error: Invalid calendar date. Please enter a real date in dd-mm-yyyy format (e.g., 05-12-2026).");
	                    }
	                }
	                EventManagementSystem.sc.nextLine();
	                System.out.print("Venue: ");
	                String ven = EventManagementSystem.sc.nextLine();
	                int s = -1;
	                while(true) {
		                try {
			                System.out.print("Seats: ");
		                    s = Integer.parseInt(EventManagementSystem.sc.nextLine());
		                    break;
		                } catch (NumberFormatException ex) {
		                    System.out.println("Please enter a valid number.");
		                    continue;
		                }
	                }
	                Event newE = new Event(id, name, cat, date, ven, s);
	                eventMap.put(id, newE);
	                eventDB.commit();
	                System.out.println("Event Created.");
	            } else if (choice.equals("2")) {
	                System.out.print("Event ID: ");
	                Event e = eventMap.get(EventManagementSystem.sc.next());
	                if(e != null) {
	                	System.out.println("Participants: " + e.getRegisteredStudentIDs());
	                }else {
	                	System.out.println("Event not found");
	                }
	            } else if (choice.equals("3")) {
	                System.out.print("Event ID: ");
	                Event e = eventMap.get(EventManagementSystem.sc.next());
	                if (e != null) {
	                    try (FileWriter fw = new FileWriter(e.getEventID()+".csv")) {
	                        fw.write("StudentIDs\n");
	                        for (String sId:e.getRegisteredStudentIDs()) {
	                        	fw.write(sId + "\n");
	                        }
	                        System.out.println("Downloaded for tracking.");
	                    } catch (Exception ex) {
	                    	ex.printStackTrace();
	                    }
	                }else {
	                	System.out.println("Event not found");
	                }
	            } else if (choice.equals("4")) {
	                System.out.print("Event ID: ");
	                Event e = eventMap.get(EventManagementSystem.sc.next());
	                if (e != null) {
	                    e.setRegistrationClosed(true);
	                    eventMap.put(e.getEventID(), e);
	                    eventDB.commit();
	                    System.out.println("Registration Closed.");
	                }else {
	                	System.out.println("Event not found");
	                }
	            } else if (choice.equals("5")) {
	            	System.out.print("Event ID: ");            	
	                Event e = eventMap.get(EventManagementSystem.sc.next());
	                if(e!=null) {
		                System.out.println("\n[1] Update venue\n"
		                		+ "[2] Close/Open registration");
		                System.out.print("Enter Choice: ");
		                String option=EventManagementSystem.sc.next();
		                EventManagementSystem.sc.nextLine();
		                if (option.equals("1")) {
		                	System.out.print("New Venue: ");
		                    String venue = EventManagementSystem.sc.nextLine();
		                    e.setVenue(venue);
		                    eventMap.put(e.getEventID(), e);
		                    eventDB.commit();
		                    for(String s:e.getRegisteredStudentIDs()){
		                    	Student stu = studentMap.get(s);
		                    	if(stu!=null) {
			                    	stu.notifications.add("Venue of Event with ID: " + e.getEventID() + " changed to " + e.getVenue());
			                    	studentMap.put(stu.getID(),stu);
			                    	studentDB.commit();
		                    	}
		                    }
		                }else if (option.equals("2")) {
		                	System.out.println("Close or open: ");
		                    String y = EventManagementSystem.sc.next();
		                    if (y.equalsIgnoreCase("close")) {
		                        e.setRegistrationClosed(true);
		                        eventMap.put(e.getEventID(), e);
		                        eventDB.commit();
		                    	System.out.print("Event Closed Succesfully ");
		                    } else if (y.equalsIgnoreCase("open")) {
		                        e.setRegistrationClosed(false);
		                        eventMap.put(e.getEventID(), e);
		                        eventDB.commit();
		                    	System.out.print("Event Opened Succesfully ");
		                    	for(String s:e.getRegisteredStudentIDs()){
		                        	Student stu = studentMap.get(s);
		                        	if(stu!=null) {
			                        	stu.notifications.add("Event with ID: " + e.getEventID() + " has been opened");
			                        	studentMap.put(stu.getID(),stu);
			                        	studentDB.commit();
		                        	}
		                        }
		                    }
		                }
	                }else {
	                	System.out.println("Event not found");
	                }
	            } else if (choice.equals("6")) {
	            	break;
	            }
	        }
        }finally {
        	eventDB.close();
        	studentDB.close();
        }
    }
}