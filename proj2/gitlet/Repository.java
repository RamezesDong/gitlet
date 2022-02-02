package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.lang.reflect.Array;

import static gitlet.Utils.*;
import static gitlet.MoreUtils.*;


/**
 * Represents a gitlet repository.
 * It's a good idea to give a description here of what else this Class
 * does at a high level.
 *
 * @author RamezesDong
 */
public class Repository {
    /**
     *
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File HEAD_DIR = join(GITLET_DIR, "heads");
    public static final File INDEX = join(GITLET_DIR, "INDEX");
    public static final File HEADFILE = join(GITLET_DIR, "HEAD");
    public static final File LOGS = join(GITLET_DIR, "logs");
    public static Commit head;


    /**
     * init()
     * Initialize the directory and files in the .gitlet.
     */
    public static void init() {
        if (GITLET_DIR.exists()) {
            printAndExit("A Gitlet version-control " +
                    "system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        HEAD_DIR.mkdir();
        for (int i = 0; i <= 15; i++) {
            for (int j = 0; j <= 15; j++) {
                String now = Integer.toHexString(i) + Integer.toHexString(j);
                File newDir = join(OBJECTS_DIR, now);
                newDir.mkdir();
            }
        }
        Commit master = new Commit();
        String initSHA = master.initialCommit();
        File masterFile = join(HEAD_DIR, "master");
        writeContents(masterFile, initSHA);
        writeContents(HEADFILE, "master");
    }

    public static void add(String fileName) {
        gitInitializedCheck();
        File fileToAdd = new File(CWD, fileName);
        fileExistCheck(fileToAdd);
        Stage stage = getINDEX();
        stage.add(fileName);
    }

    public static void commit(String commitMessage) {
        gitInitializedCheck();
        if (commitMessage == null) {
            printAndExit("Please enter a commit message.");
        }
        String parent = getHeaderToCommitSHA1();
        String sha = new Commit().commit(commitMessage, parent);
        File branchFile = getHeadFile();
        writeContents(branchFile, sha);
    }

    public static void rm(String rmFileName) {
        gitInitializedCheck();
        File fileToRemove = join(CWD, rmFileName);
        fileExistCheck(fileToRemove);
        Stage stage = getINDEX();
        if (stage.atAdded(rmFileName)) {
            stage.removeFromAdd(rmFileName);
        } else if (stage.atTracked(rmFileName)) {
            stage.addFileToRemoved(rmFileName);
        } else {
            printAndExit("No reason to remove the file");
        }
    }

    public static void log() {
        gitInitializedCheck();
        Commit now = getHeaderToCommit();
        now.getLog();
    }

    /***
     * displays information about all commits ever made
     */
    public static void globalLog() {
        gitInitializedCheck();
        Logs logs = new Logs();
        logs.globalLogs();
    }

    public static void find(String findMessage) {
        gitInitializedCheck();
        Logs logs = new Logs();
        logs.find(findMessage);
    }

    public static void status() {
        gitInitializedCheck();
        branchesStatus();
        Stage stage = new Stage();
        stage.status();
        printOneLine("=== Modifications Not Staged For Commit ===");
        printOneLine(null);
        printOneLine("=== Untracked Files ===");
        printOneLine(null);
    }

    public static void changeBranch(String branchName) {
        gitInitializedCheck();

    }

    public static void checkOutFile(String fName) {
        gitInitializedCheck();
        Commit currentCommit = getHeaderToCommit();
        if (!currentCommit.CheckOutFileName(fName)) {
            printAndExit("File does not exist in that commit.");
        }
    }

    public static void checkOutFileFromCommit(String id, String fName) {
        gitInitializedCheck();
        File currentCommitFile = getFileFromShortedID(id);
        if (currentCommitFile == null) {
            printAndExit("No commit with that id exist.");
        }
        Commit currentCommit = readObject(currentCommitFile, Commit.class);
        if (currentCommit == null) {
            printAndExit("No commit with that id exists.");
        }
        if (!currentCommit.CheckOutFileName(fName)) {
            printAndExit("File does not exist in that commit.");
        }
    }

    public static void createBranch(String branchName) {
        gitInitializedCheck();
        File[] filesList = HEAD_DIR.listFiles();
        for (File f : filesList) {
            if (branchName.equals(f.getName())) {
                printAndExit("A branch with that name already exists.");
            }
        }
        File f = join(HEAD_DIR, branchName);
        writeContents(f, readContents(getHeadFile()));
        writeContents(HEADFILE, branchName);
    }

    public static void rmBranch(String branchName) {
        gitInitializedCheck();
        File[] fileList = HEAD_DIR.listFiles();
        String headName = getHEAD();
        if (branchName.equals(headName)) {
            printAndExit("Cannot remove the current branch.");
        }
        for (File f : fileList) {
            String s = f.getName();
            if (s.equals(branchName)) {
                restrictedDelete(f);
                System.exit(1);
            }
        }
        printAndExit("A branch with that name does not exist.");
    }

    public static void reset(String commitID) {
        gitInitializedCheck();
        File commitFile = getFileFromID(commitID);
        if (!commitFile.exists()) {
            printAndExit("No commit with that id exists.");
        }
        Commit objectCommit = readObject(commitFile, Commit.class);
        objectCommit.reset();
    }

    public static void merge(String id) {

    }

    public static void branchesStatus() {
        File[] fileList = HEAD_DIR.listFiles();
        String headName = getHEAD();
        Arrays.sort(fileList);
        System.out.println("=== Branches ===");
        for (File f : fileList) {
            String s = f.getName();
            if (s.equals(headName)) {
                System.out.println("*" + s);
            } else {
                System.out.println(s);
            }
        }
        System.out.println();
    }

    public static Stage getINDEX() {
        if (!INDEX.exists()) {
            return new Stage();
        } else {
            return readObject(INDEX, Stage.class);
        }
    }

    public static String getHEAD() {
        return readContentsAsString(HEADFILE);
    }

    public static File getHeadFile() {
        File f = join(HEAD_DIR, getHEAD());
        return f;
    }

    public static String getHeaderToCommitSHA1() {
        return readContentsAsString(getHeadFile());
    }

    public static Commit getHeaderToCommit() {
        File f = getFileFromID(getHeaderToCommitSHA1());
        return readObject(f, Commit.class);
    }

    public static void fileExistCheck(String fileName) {
        File f = join(CWD, fileName);
        if (!f.exists()) {
            printAndExit("File does not exist.");
        }
    }

    public static void fileExistCheck(File f) {
        if (!f.exists()) {
            printAndExit("File does not exist.");
        }
    }

    private static void gitInitializedCheck() {
        if (!GITLET_DIR.exists()) {
            printAndExit("Not a gitlet repository.");
        }
    }
}
