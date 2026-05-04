package EventManager;

import org.mapdb.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.time.format.*;
import java.time.*;

public class Student extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<String> registeredEventIDs=new ArrayList<String>();
    public ArrayList<String> notifications=new ArrayList<String>();

    public Student(String id) {
        userID=id;
    }
    public ArrayList<String> getRegisteredEventIDs(){
    	return registeredEventIDs;
    }

    public void openDashboard() {
        DB eventDB=DBMaker.newFileDB(new File("events.db")).closeOnJvmShutdown().make();
        DB studentDB=DBMaker.newFileDB(new File("students.db")).closeOnJvmShutdown().make();
        
        ConcurrentNavigableMap<String, Event> eventMap=eventDB.getTreeMap("events");
        ConcurrentNavigableMap<String, Student> studentMap=studentDB.getTreeMap("students");

        Student db=studentMap.get(userID);
        if(db!=null) {
        	this.registeredEventIDs=db.registeredEventIDs;
        	this.name=db.name;
        	this.notifications=db.notifications;
        }
        try {
	        System.out.println("\n--- Student Dashboard | ID: "+userID+" ---");
	        System.out.println("---Welcome "+name+"---");
	
	        while(true) {
	            System.out.println("\n[1] Browse Events\n"
	            		+ "[2] Register\n"
	            		+ "[3] Cancel\n"
	            		+ "[4] View My Events\n"
	            		+ "[5] View Notifications\n"
	            		+ "[6] Logout");
	            System.out.print("Choice: ");
	            String choice = EventManagementSystem.sc.next();
	            if(choice.equals("1")) {
	                for (Event e:eventMap.values()) {
	                    System.out.println(e.getEventID()+": "+e.getEventName()+",Category: "+e.getCategory()+",Seats: "+e.getAvailableSeats());
	                }
	            }else if(choice.equals("2")) {
	                System.out.print("Event ID: ");
	                String eId=EventManagementSystem.sc.next();
	                Event e=eventMap.get(eId);
	                if(e!=null && e.registerParticipant(this.userID)) {
	                    registeredEventIDs.add(eId);
	                    eventMap.put(eId,e);
	                    studentMap.put(this.userID, this);
	                    eventDB.commit();
	                    studentDB.commit();
	                    System.out.println("Registered successfully!");
	                }else {
	                    System.out.println("Registration failed.");
	                }
	            }else if(choice.equals("3")) {
	                System.out.print("Event ID: ");
	                String eId=EventManagementSystem.sc.next();
	                Event e=eventMap.get(eId);
	                if(e != null && registeredEventIDs.remove(eId)) {
	                    e.cancelParticipation(this.userID);
	                    eventMap.put(eId, e);
	                    studentMap.put(this.userID,this);
	                    eventDB.commit();
	                    studentDB.commit();
	                    System.out.println("Canceled successfully.");
	                }else {
	                	System.out.println("Cancellation Unsuccessfull.");
	                }
	            }else if(choice.equals("4")) {
	                System.out.println("My Events: ");
	                for (String eID:registeredEventIDs) {
	                	Event e=eventMap.get(eID);
	                    System.out.println(e.getEventID()+": "+e.getEventName()+",Category: "+e.getCategory()+",Venue: "+e.getVenue());
	                }
	            }else if(choice.equals("5")) {
	                System.out.println("\n---Notifications---");
	                for(String s:notifications) {
	                	System.out.println(s);
	                }
	                //maybe ill delete this(code to remind 3 days before)
	                System.out.println("---Upcoming events---");
	                for(String id:registeredEventIDs) {
	                	Event e=eventMap.get(id);
	                	String date=e.getDate();
	                	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	                	try {
	                        long daysRemaining = LocalDate.parse(date, formatter).toEpochDay() - LocalDate.now().toEpochDay();;
	                        if (daysRemaining >= 0 && daysRemaining < 3) {
	                            System.out.println("Event ID: " + e.getEventID() + "| Name: " + e.getEventName() + "| Venue: " + e.getVenue() + "| Days remaining: " + daysRemaining);
	                        }
	                        
	                    } catch (DateTimeParseException ae) {
	                        System.out.println("Error reading event date: " + date);
	                    }
	                }
	                //=====================
	            }else if(choice.equals("6")) {
	                break;
	            }
	        }
        }finally {    
        	eventDB.close();
        	studentDB.close();
        }
    }
}