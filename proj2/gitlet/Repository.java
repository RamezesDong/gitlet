package gitlet;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.lang.reflect.Array;
import static gitlet.Utils.*;
import static gitlet.MoreUtils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author RamezesDong
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File HEAD_DIR = join(GITLET_DIR,"refs", "heads");
    public static final File INDEX = join(GITLET_DIR, "INDEX");
    public static final File headFile = join(GITLET_DIR, "HEAD");
    public static Commit head;
    /* TODO: fill in the rest of this class. */


    /**
     * init()
     * Initialize the directory and files in the .gitlet.
     *
     */
    public static void init() {
        if(GITLET_DIR.exists()) {
            throw new GitletException (
                "A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        HEAD_DIR.mkdir();
        for (int i = 0; i <= 15; i ++) {
            for (int j = 0; j <= 15; j++) {
                String now = Integer.toHexString(i) + Integer.toHexString(j);
                File newDir = join(OBJECTS_DIR, now);
                newDir.mkdir();
            }
        }
        Commit master = new Commit();
        String initSHA = master.initialCommit();
        File masterFile = new File(HEAD_DIR, "master");
        writeContents(masterFile, initSHA);
        writeContents(headFile, "master");
        try {
            INDEX.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void add(String fileName) {
        gitInitializedCheck();
        File fileToAdd = new File(CWD, fileName);
        if (!fileToAdd.exists()) {
            printAndExit("File does not exist.");
        }
        Stage stage = getINDEX();
        if (stage.add(fileName)) {
            stage.save(fileName);
        }
    }

    public static void commit(String commitMessage) {
        gitInitializedCheck();
        String parent =  getHeaderToCommitSHA1();
        String sha = new Commit().commit(commitMessage, parent);
        File branchFile = getHeadFile();
        writeContents(branchFile, sha);
    }

    public static Stage getINDEX() {
        if (!INDEX.exists()) {
            return new Stage();
        } else {
           return readObject(INDEX, Stage.class);
        }
    }

    public static String getHEAD() {
        return readContentsAsString(headFile);
    }

    public static File getHeadFile() {
        return join(HEAD_DIR, getHEAD());
    }

    public static String getHeaderToCommitSHA1() {
        return readContentsAsString(getHeadFile());
    }

    public static Commit getHeaderToCommit() {
        File f  = getFileFromID(getHeaderToCommitSHA1());
        return readObject(f, Commit.class);
    }

    private static void gitInitializedCheck() {
        if (!GITLET_DIR.exists()) {
            printAndExit("Not a gitlet repository.");
        }
    }
}
