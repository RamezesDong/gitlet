package gitlet;

import java.io.Serializable;
import java.io.File;
import java.util.*;
import java.lang.reflect.Array;

import static gitlet.Utils.*;
import static gitlet.MoreUtils.*;

public class Stage implements Serializable {
    private HashMap<String, String> tracked;
    private HashMap<String, String> added;
    private HashSet<String> removed;


    public Stage() {
        added = new HashMap<>();
        removed = new HashSet<>();
    }

    /***
     * git add [fileName]
     * @param fileName
     * @return whether the file to add is new in the objects files
     */
    public void add(String fileName) {
        File fileToAdd = new File(Repository.CWD, fileName);
        Blob b = new Blob(fileToAdd);
        String sha1ToAdd = b.getBlobID();
        added.put(fileName, sha1ToAdd);
        this.save();
    }

    public boolean atAdded(String fileName) {
        return added.containsKey(fileName);
    }

    public void removeFromAdd(String fileName) {
        added.remove(fileName);
        this.save();
    }

    public void addFileToRemoved(String fileName) {
        removed.add(fileName);
        File f = join(Repository.CWD, fileName);
        restrictedDelete(f);
        this.save();
    }

    public boolean atTracked(String fileName) {
        getCurrentTracked();
        return tracked.containsKey(fileName);
    }

    /***
     * Get the tracked files from the current commit.
     * @return
     */
    public HashMap<String, String> getCurrentTracked() {
        tracked = Repository.getHeaderToCommit().getFiles();
        return tracked;
    }


    public void status() {
        printOneLine("=== Staged Files ===");
        for (String s : added.keySet()) {
            printOneLine(s);
        }
        printOneLine(null);
        printOneLine("=== Removed Files ===");
        for (String s : removed) {
            printOneLine(s);
        }
        printOneLine(null);
    }

    public void save() {
        writeObject(Repository.INDEX, this);
    }

    public HashMap<String, String> getAdded() {
        return added;
    }

    public HashSet<String> getRemoved() {
        return removed;
    }
}
