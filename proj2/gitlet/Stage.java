package gitlet;
import java.io.Serializable;
import java.io.File;
import java.util.*;
import java.lang.reflect.Array;
import static gitlet.Utils.*;

public class Stage implements Serializable {
    private HashMap<String, String> tracked;
    private HashMap<String, String> added = new HashMap<>();
    private HashSet<String> removed = new HashSet<>();


    public Stage() {
        added = new HashMap<>();
        removed = new HashSet<>();
    }

    /***
     * git add [fileName]
     * @param fileName
     * @return whether the file to add is new in the objects files
     */
    public boolean add(String fileName) {
        File fileToAdd = new File(Repository.CWD, fileName);
        byte[] readContent = readContents(fileToAdd);
        String sha1ToAdd = sha1(readContent);
        tracked = getCurrentTracked();
        if (added.get(fileName) == null || added.get(fileName) != sha1ToAdd) {
            added.put(fileName, sha1ToAdd);
            return true;
        }
        return false;
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
        f.delete();
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


    public void save () {
        writeObject(Repository.INDEX, this);
    }

    public HashMap<String, String> getAdded() {
        return added;
    }

    public HashSet<String> getRemoved() {
        return removed;
    }
}
