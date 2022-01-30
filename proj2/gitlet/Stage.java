package gitlet;
import java.io.Serializable;
import java.io.File;
import java.util.*;
import java.lang.reflect.Array;
import static gitlet.Utils.*;

public class Stage implements Serializable {
    private HashMap<String, String> tracked;
    private final HashMap<String, String> added = new HashMap<>();
    private final HashSet<String> removed = new HashSet<>();

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

    /***
     * Get the tracked files from the current commit.
     * @return
     */
    public HashMap<String, String> getCurrentTracked() {
        return Repository.getHeaderToCommit().getFiles();
    }

    public void save (String fileName) {
        writeObject(Repository.INDEX, this);
    }

    public HashMap<String, String> getAdded() {
        return added;
    }

    public HashSet<String> getRemoved() {
        return removed;
    }
}
