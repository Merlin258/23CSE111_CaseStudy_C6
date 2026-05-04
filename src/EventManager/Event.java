package EventManager;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String eventID;
    private String eventName;
    private String category;
    private String date;
    private String venue;
    private int totalSeats;
    private int availableSeats;
    private boolean isRegistrationClosed;
    private ArrayList<String> registeredStudentIDs;

    public Event(String eventID,String eventName,String category,String date,String venue,int totalSeats) {
        this.eventID=eventID;
        this.eventName=eventName;
        this.category=category;
        this.date=date;
        this.venue=venue;
        this.totalSeats=totalSeats;
        availableSeats=totalSeats;
        isRegistrationClosed=false;
        registeredStudentIDs=new ArrayList<>();
    }

    public boolean registerParticipant(String studentID) {
        if(isRegistrationClosed || availableSeats<=0 || registeredStudentIDs.contains(studentID)) {
        	return false;
        }
        registeredStudentIDs.add(studentID);
        availableSeats--;
        return true;
    }

    public boolean cancelParticipation(String studentID) {
        if (registeredStudentIDs.remove(studentID)) {
            availableSeats++;
            return true;
        }
        return false;
    }

    
    public String getEventID() { return eventID; }
    public String getEventName() { return eventName; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getVenue() { return venue; }
    public int getTotalSeats() { return totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public boolean isRegistrationClosed() { return isRegistrationClosed; }
    public ArrayList<String> getRegisteredStudentIDs() { return registeredStudentIDs; }

    public void setVenue(String venue) { this.venue = venue; }
    public void setRegistrationClosed(boolean closed) { isRegistrationClosed = closed; }
}