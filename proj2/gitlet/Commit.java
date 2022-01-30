package gitlet;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;
// TODO: any imports you need here
import gitlet.Utils.*;
import java.io.File;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.logging.SimpleFormatter;

import static gitlet.MoreUtils.getFileFromID;
import static gitlet.MoreUtils.printAndExit;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
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
    private String timeStamp;
    private ArrayList<String> parent;
    private HashMap<String, String> files = new HashMap<>();
    private SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");

    public ArrayList<String> getParent() {
        return parent;
    }


    public String getMessage() {
        return message;
    }

    public HashMap<String, String> getFiles() {
        return files;
    }

    public String commit(String m, String p) {
        message = m;
        Date date = new Date();
        timeStamp = formatter.format(date);
        parent = new ArrayList<>();
        parent.add(p);
        HashMap<String, String> added = Repository.getINDEX().getAdded();
        Commit parentCommit = readObject(getFileFromID(parent.get(0)), Commit.class);
        HashMap<String, String> parentFiles = getFiles();
        HashSet<String> removed = Repository.getINDEX().getRemoved();
        if (added.isEmpty() && removed.isEmpty()) {
            printAndExit("No changes added to the commit.");
        }
        for (String e : parentFiles.keySet()) {
            if (added.get(e) == null || !removed.contains(e)) {
                this.files.put(e, parentFiles.get(e));
            }
        }
        for (String e : added.keySet()) {
            this.files.put(e, added.get(e));
        }
        sha1Values = sha1( timeStamp, message, parent.toString(), files.toString());
        File fileToSave = getFileFromID(sha1Values);
        writeObject(fileToSave, this);
        return sha1Values;
    }

    public String initialCommit() {
        message = "initial commit";
        timeStamp = "Thu Jan 1 08:00:00 1970 +0800";
        parent = new ArrayList<>();
        sha1Values = sha1( timeStamp, message, parent.toString(), files.toString());
        File fileToSave = getFileFromID(sha1Values);
        writeObject(fileToSave, this);
        return sha1Values;
    }

    /* TODO: fill in the rest of this class. */
}
