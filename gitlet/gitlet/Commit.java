package gitlet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;
// TODO: any imports you need here
import gitlet.Utils.*;
import java.io.File;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.logging.SimpleFormatter;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private String sha1Values;
    private Date date;
    private String timeStamp;
    private String parent;
    private File[] files;
    private SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");

    public String getParent() {
        return parent;
    }

    public File[] getFiles() {
        return files;
    }

    public String getMessage() {
        return message;
    }


    public void commit(String m, String p) {
        message = m;
        date = new Date();
        timeStamp = formatter.format(date);
        parent = p;
        files = null;
        List<Object> messageToSha = new ArrayList<>();
        messageToSha.add(message);
        messageToSha.add(timeStamp);
        messageToSha.add(parent);
        messageToSha.add(files);
        sha1Values = Utils.sha1(messageToSha);
    }

    public String initialCommit() {
        message = "initial commit";
        timeStamp = "Thu Jan 1 08:00:00 1970 +0800";
        parent = null;
        files = null;
        List<Object> messageToSha = new ArrayList<>();
        messageToSha.add(message);
        messageToSha.add(timeStamp);
        messageToSha.add(parent);
        messageToSha.add(files);
        sha1Values = Utils.sha1(messageToSha);
        return sha1Values;
    }

    /* TODO: fill in the rest of this class. */
}
