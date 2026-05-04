package EventManager;
import java.io.*;

public abstract class User implements Serializable{
	private static final long serialVersionUID = 1L;
    protected String userID;
    protected String name;

    public void setID(String userID) {
    	this.userID=userID;
    }
    public void setName(String name) {
    	this.name=name;
    }
    public String getID() {
    	return userID;
    }
    public String getName() {
    	return name;
    }
    public abstract void openDashboard();
}