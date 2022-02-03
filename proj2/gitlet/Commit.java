package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;

import gitlet.Utils.*;

import java.io.File;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import static gitlet.MoreUtils.getFileFromID;
import static gitlet.MoreUtils.printAndExit;
import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 * does at a high level.
 *
 * @author RamezesDong
 */
public class Commit implements Serializable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private String message;
    private String sha1Values;
    private String timeStamp;
    private ArrayList<String> parent;
    private HashMap<String, String> files = new HashMap<>();
    private SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");

    public ArrayList<String> getParent() {
        return parent;
    }

    public String getSha1Values() {
        return sha1Values;
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
        HashMap<String, String> parentFiles = parentCommit.getFiles();
        HashSet<String> removed = Repository.getINDEX().getRemoved();
        if (added.isEmpty() && removed.isEmpty()) {
            printAndExit("No changes added to the commit.");
        }
        this.files = new HashMap<>(parentFiles);
        for (String e : added.keySet()) {
            this.files.put(e, added.get(e));
        }
        for (String e : removed) {
            if (this.files.containsKey(e)) {
                this.files.remove(e);
            }
        }
        restrictedDelete(Repository.INDEX);
        sha1Values = sha1(timeStamp, message, parent.toString(), files.toString());
        File fileToSave = getFileFromID(sha1Values);
        writeObject(fileToSave, this);
        writeToGlobalLog();
        return sha1Values;
    }

    public String initialCommit() {
        message = "initial commit";
        timeStamp = "Thu Jan 1 08:00:00 1970 +0800";
        parent = new ArrayList<>();
        sha1Values = sha1(timeStamp, message, parent.toString(), files.toString());
        File fileToSave = getFileFromID(sha1Values);
        writeObject(fileToSave, this);
        writeToGlobalLog();
        return sha1Values;
    }

    public void getSelfLog() {
        System.out.println("===");
        System.out.println("commit " + this.sha1Values);
        System.out.println("Date: " + this.timeStamp);
        System.out.println(this.message);
        System.out.println();
    }

    public void getLog() {
        this.getSelfLog();
        if (this.parent.size() != 0) {
            Commit parent = getCommitFromID(this.parent.get(0));
            parent.getLog();
        }
    }

    public void writeToGlobalLog() {
        Logs logs = new Logs();
        logs.addLogs(this.sha1Values);
        logs.writeToLogs();
    }

    public static Commit getCommitFromID(String id) {
        File f = getFileFromID(id);
        if (!f.exists()) {
            return null;
        }
        return readObject(f, Commit.class);
    }

    public boolean CheckOutFileName(String fName) {
        String sha1Str = files.get(fName);
        if (sha1Str == null) {
            return false;
        }
        Blob.getFromID(sha1Str).writeToSourceFile();
        return true;
    }

    public void putFilesToCWD() {
        for (String s : files.keySet()) {
            String shaForS = files.get(s);
            Blob b = Blob.getFromID(shaForS);
            b.writeToSourceFile();
        }
    }

    public void reset() {
        for (String e : files.keySet()) {
            String sha1 = files.get(e);
            Blob trackedBlob = Blob.getFromID(sha1);
            trackedBlob.writeToSourceFile();
        }
        restrictedDelete(Repository.INDEX);
    }

}
