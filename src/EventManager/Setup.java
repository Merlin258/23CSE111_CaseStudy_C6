package EventManager;

import org.mapdb.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Setup {

    public static void main(String[] args) {
        System.out.println("Starting System Initialization...");

        //setupPropertiesFiles();
        setupDatabases();

        System.out.println("Initialization Complete! You can now run EventManagementSystem.java.");
    }

    private static void setupPropertiesFiles() {
        Properties adminProps = new Properties();
        Properties studentProps = new Properties();

        adminProps.setProperty("admin1", "pass1");
        adminProps.setProperty("admin2", "pass2");

        for (int i = 1; i <= 20; i++) {
            studentProps.setProperty("S" + i, "pass" + i);
        }

        try (FileOutputStream adminOut = new FileOutputStream("admin.properties");
             FileOutputStream studentOut = new FileOutputStream("student.properties")) {
            
            adminProps.store(adminOut, "Admin System Database - Auto Generated");
            System.out.println("[Success] admin.properties generated.");
            
            studentProps.store(studentOut, "Student System Database - Auto Generated");
            System.out.println("[Success] student.properties generated.");
            
        } catch (IOException e) {
            System.out.println("Error writing properties files: " + e.getMessage());
        }
    }

    private static void setupDatabases() {
        DB eventDB = DBMaker.newFileDB(new File("events.db")).make();
        DB studentDB = DBMaker.newFileDB(new File("students.db")).make();

        ConcurrentNavigableMap<String, Event> eventMap = eventDB.getTreeMap("events");
        ConcurrentNavigableMap<String, Student> studentMap = studentDB.getTreeMap("students");

        // Clear existing data for a fresh start
        eventMap.clear();
        studentMap.clear();

        // 1. Create Events (Fixing the Date Format to dd-MM-yyyy)
        String[] categories = {"Hackathon", "Seminar", "Cultural", "Sports"};
        for (int i = 1; i <= 5; i++) {
            String eId = "E" + i;
            // Using 15-05-2026 to ensure LocalDate parses it safely
            Event event = new Event(eId, "Annual Event " + i, categories[i % 4], "15-05-2026", "Auditorium " + i, 50);
            eventMap.put(eId, event);
        }
        System.out.println("[Success] events.db populated with 5 events.");

        // 2. Create Students (Fixing the null name bug)
        for (int i = 1; i <= 5; i++) {
            String sId = "S" + i;
            Student student = new Student(sId);
            student.setName("Student Name " + i); // Give them a name so the dashboard doesn't say "Welcome null"
            studentMap.put(sId, student);
        }
        System.out.println("[Success] students.db populated with 5 students.");

        // 3. Simulate Data: S1 registers for E1 and E2 safely
        Student s1 = studentMap.get("S1");
        Event e1 = eventMap.get("E1");
        Event e2 = eventMap.get("E2");

        if (e1 != null && e1.registerParticipant(s1.getID())) {
            s1.getRegisteredEventIDs().add("E1");
            eventMap.put("E1", e1);
        }
        
        if (e2 != null && e2.registerParticipant(s1.getID())) {
            s1.getRegisteredEventIDs().add("E2");
            eventMap.put("E2", e2);
        }
        
        // Save S1's updated state back to the database
        studentMap.put("S1", s1);
        System.out.println("[Success] Simulated registrations for S1.");

        // Commit and safely close locks
        eventDB.commit();
        studentDB.commit();
        eventDB.close();
        studentDB.close();
        System.out.println("[Success] Databases committed and closed gracefully.");
    }
}