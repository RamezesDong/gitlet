package gitlet;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


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


    /**
     * init()
     * Initialize the directory and files in the .gitlet.
     */
    public static void init() {
        if (GITLET_DIR.exists()) {
            printAndExit("A Gitlet version-control "
                    + "system already exists in the current directory.");
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
        if (commitMessage == null || commitMessage.length() == 0) {
            printAndExit("Please enter a commit message.");
        }
        String parent = getHeaderToCommitSHA1();
        String sha = new Commit().commit(commitMessage, parent);
        File branchFile = getHeadFile();
        writeContents(branchFile, sha);
    }

    public static void rm(String rmFileName) {
        gitInitializedCheck();
        //File fileToRemove = join(CWD, rmFileName);
        //fileExistCheck(fileToRemove);
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
        Stage stage = getINDEX();
        stage.status();
        notStagedForCommitAndUnstaged();
    }

    public static void notStagedForCommitAndUnstaged() {
        printOneLine("=== Modifications Not Staged For Commit ===");
        HashMap<String, String> currentFiles = findAllCurrentFiles();
        Commit cm = getHeaderToCommit();
        HashMap<String, String> tracked = cm.getFiles();
        Stage index = getINDEX();
        HashMap<String, String> added = index.getAdded();
        HashSet<String> removed = index.getRemoved();
        HashSet<String> printed = new HashSet<>();
        for (String s : currentFiles.keySet()) {
            if ((tracked.containsKey(s) && !tracked.get(s).equals(currentFiles.get(s))
                    && !added.containsKey(s))
                    || (added.containsKey(s) && !added.get(s).equals(currentFiles.get(s)))) {
                printOneLine(s + " (modified)");
                printed.add(s);
            }
        }
        for (String s : added.keySet()) {
            if (!currentFiles.containsKey(s) && !printed.contains(s)) {
                printOneLine(s + " (deleted)");
                printed.add(s);
            }
        }
        for (String s : tracked.keySet()) {
            if (!removed.contains(s) && !currentFiles.containsKey(s) && !printed.contains(s)) {
                printOneLine(s + " (deleted)");
            }
        }
        printOneLine(null);

        printOneLine("=== Untracked Files ===");
        List<String> untracked = findFilesUntracked(currentFiles);
        for (String s : untracked) {
            printOneLine(s);
        }
        printOneLine(null);
    }


    public static void changeBranch(String branchName) {
        gitInitializedCheck();
        File branchFile = findBranchFile(branchName);
        if (branchFile == null) {
            printAndExit("No such branch exists.");
        }
        if (branchFile.getName().equals(getHeadFile().getName())) {
            printAndExit("No need to checkout the current branch.");
        }
        HashMap<String, String> nowFiles = findAllCurrentFiles();
        List<String> files = findFilesUntracked(nowFiles);
        String branchID = readContentsAsString(branchFile);
        Commit cm = readObject(getFileFromID(branchID), Commit.class);
        HashMap<String, String> targetCommitFiles = cm.getFiles();
        for (String s : files) {
            String filesID = nowFiles.get(s);
            if (!filesID.equals(targetCommitFiles.get(s))) {
                printAndExit("There is an "
                        + "untracked file in the way; delete it, or add and commit it first.");
            }
        }
        deleteCurrentFiles();
        cm.putFilesToCWD();
        writeContents(HEADFILE, branchName);
        INDEX.delete();
    }


    public static void deleteCurrentFiles() {
        File[] currentFiles = CWD.listFiles(File::isFile);
        for (File f : currentFiles) {
            restrictedDelete(f);
        }
    }

    public static HashMap<String, String> findAllCurrentFiles() {
        HashMap<String, String> currentFileMap = new HashMap<>();
        File[] currentFiles = CWD.listFiles(File::isFile);
        for (File f : currentFiles) {
            Blob nb = new Blob(f);
            currentFileMap.put(f.getName(), nb.getBlobID());
        }
        return currentFileMap;
    }

    public static List<String> findFilesUntracked(HashMap<String, String> currentFiles) {
        List<String> finds = new ArrayList<>();
        HashMap<String, String> tracked = getHeaderToCommit().getFiles();
        HashMap<String, String> added = getINDEX().getAdded();
        HashSet<String> removed = getINDEX().getRemoved();
        for (String s : currentFiles.keySet()) {
            if (tracked.containsKey(s)) {
                if (removed.contains(s)) {
                    finds.add(s);
                }
            } else if (!added.containsKey(s)) {
                finds.add(s);
            }
        }
        return finds;
    }

    public static File findBranchFile(String branchName) {
        File[] files = HEAD_DIR.listFiles();
        for (File f : files) {
            String s = f.getName();
            if (s.equals(branchName)) {
                return f;
            }
        }
        return null;
    }

    public static void checkOutFile(String fName) {
        gitInitializedCheck();
        Commit currentCommit = getHeaderToCommit();
        if (!currentCommit.checkOutFileName(fName)) {
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
        if (!currentCommit.checkOutFileName(fName)) {
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
    }

    public static void rmBranch(String branchName) {
        gitInitializedCheck();
        File[] fileList = HEAD_DIR.listFiles();
        String headName = getHEAD();
        if (branchName.equals(headName)) {
            printAndExit("Cannot remove the current branch.");
        }
        boolean flag = true;
        for (File f : fileList) {
            String s = f.getName();
            if (s.equals(branchName)) {
                flag = false;
                f.delete();
            }
        }
        if (flag) {
            printAndExit("A branch with that name does not exist.");
        }
    }

    public static void reset(String commitID) {
        gitInitializedCheck();
        File commitFile = getFileFromShortedID(commitID);
        if (commitFile == null || !commitFile.exists()) {
            printAndExit("No commit with that id exists.");
        }
        HashMap<String, String> nowFiles = findAllCurrentFiles();
        List<String> files = findFilesUntracked(nowFiles);
        Commit objectCommit = readObject(commitFile, Commit.class);
        HashMap<String, String> targetCommitFiles = objectCommit.getFiles();
        for (String s : files) {
            String filesID = nowFiles.get(s);
            if (!filesID.equals(targetCommitFiles.get(s))) {
                printAndExit("There is an "
                        + "untracked file in the way; delete it, or add and commit it first.");
            }
        }
        for (String f : nowFiles.keySet()) {
            File ff = join(CWD, f);
            restrictedDelete(ff);
        }

        objectCommit.reset();
        File branchFile = getHeadFile();
        writeContents(branchFile, commitID);
    }

    public static void merge(String branchName) {
        gitInitializedCheck();
        Stage stage = getINDEX();
        stage.checkStage();
        Commit headerToCommit = getHeaderToCommit();
        File branchFile = findBranchFile(branchName);
        if (branchFile == null) {
            printAndExit("A branch with that name does not exist.");
        }
        if (branchFile.getName().equals(getHeadFile().getName())) {
            printAndExit("Cannot merge a branch with itself.");
        }
        String id = readContentsAsString(branchFile);
        Commit givenCommit = Commit.getCommitFromID(id);
        Commit splitCommit = getSplitCommit(headerToCommit, givenCommit);
        if (splitCommit.getSha1Values().equals(givenCommit.getSha1Values())) {
            printAndExit("Given branch is an ancestor of the current branch.");
        }
        if (splitCommit.getSha1Values().equals(headerToCommit.getSha1Values())) {
            changeBranch(branchName);
            printAndExit("Current branch fast-forwarded.");
        }
        HashMap<String, String> nowFiles = findAllCurrentFiles();
        List<String> files = findFilesUntracked(nowFiles);
        if (files.size() != 0) {
            printAndExit("There is an "
                    + "untracked file in the way; delete it, or add and commit it first.");
        }
        for (String f : nowFiles.keySet()) {
            File ff = join(CWD, f);
            restrictedDelete(ff);
        }
        HashMap<String, String> resultTracked =
                getResultFiles(splitCommit, headerToCommit, givenCommit);
        StringBuilder message = new StringBuilder();
        message.append("Merged ");
        message.append(branchName);
        message.append(" into ");
        message.append(getHEAD());
        message.append(".");
        String sha = new Commit().merge(message.toString(),
                getHeaderToCommitSHA1(), id, resultTracked);
        File headerFile = getHeadFile();
        writeContents(headerFile, sha);
    }

    public static HashMap<String, String> getResultFiles(Commit split,
                                                         Commit current, Commit given) {
        boolean conflictFlag = false;
        HashMap<String, String> splitTracked = split.getFiles();
        HashMap<String, String> headerTracked = current.getFiles();
        HashMap<String, String> givenTracked = given.getFiles();
        HashMap<String, String> resultTracked = new HashMap<>();
        for (String s : splitTracked.keySet()) {
            if (headerTracked.containsKey(s)
                    && headerTracked.get(s).equals(splitTracked.get(s))) {
                if (givenTracked.containsKey(s)) {
                    resultTracked.put(s, givenTracked.get(s));
                }
            } else if (givenTracked.containsKey(s)
                    && givenTracked.get(s).equals(splitTracked.get(s))) {
                if (headerTracked.containsKey(s)) {
                    resultTracked.put(s, headerTracked.get(s));
                }
            } else if (givenTracked.containsKey(s) && headerTracked.containsKey(s)
                    && givenTracked.get(s).equals(headerTracked.get(s))) {
                resultTracked.put(s, headerTracked.get(s));
            } else if (!givenTracked.containsKey(s) && !headerTracked.containsKey(s)) {
                headerTracked.remove(s);
            } else {
                conflictFlag = true;
                String endContent = getConflict(headerTracked.get(s), givenTracked.get(s));
                String sha1Blob = sha1(endContent.getBytes(StandardCharsets.UTF_8));
                Blob nb = new Blob(s, sha1Blob, endContent.getBytes(StandardCharsets.UTF_8));
                resultTracked.put(s, nb.getBlobID());
            }
            headerTracked.remove(s);
            givenTracked.remove(s);
        }
        for (String s : headerTracked.keySet()) {
            if (!givenTracked.containsKey(s)) {
                resultTracked.put(s, headerTracked.get(s));
            } else if (headerTracked.get(s).equals(givenTracked.get(s))) {
                resultTracked.put(s, headerTracked.get(s));
            } else {
                conflictFlag = true;
                String endContent = getConflict(headerTracked.get(s), givenTracked.get(s));
                String sha1Blob = sha1(endContent.getBytes(StandardCharsets.UTF_8));
                Blob nb = new Blob(s, sha1Blob, endContent.getBytes(StandardCharsets.UTF_8));
                resultTracked.put(s, nb.getBlobID());
            }
            givenTracked.remove(s);
        }
        for (String s : givenTracked.keySet()) {
            resultTracked.put(s, givenTracked.get(s));
        }
        if (conflictFlag) {
            printOneLine("Encountered a merge conflict.");
        }
        return resultTracked;
    }

    public static String getConflict(String currentSha, String givenSha) {
        String content = "<<<<<<< HEAD\n";
        content += currentSha == null ? ""
                : readObject(getFileFromID(currentSha), Blob.class).readContentAsString();
        content += "=======\n";
        content += givenSha == null ? ""
                : readObject(getFileFromID(givenSha), Blob.class).readContentAsString();
        content += ">>>>>>>\n";
        return content;
//        Utils.message("Encountered a merge conflict.");
//        String content = "<<<<<<< HEAD\n";
//        content += thisFileKey == null ? ""
//                : Utils.readContentsAsString(getBlobFile(thisFileKey));
//        content += "=======\n";
//        content += otherFileKey == null ? ""
//                : Utils.readContentsAsString(getBlobFile(otherFileKey));
//        content += ">>>>>>>\n";
//        String blobKey =
//                Utils.sha1(content.getBytes(StandardCharsets.UTF_8));
//        createBlob(getBlobFile(blobKey),
//                content.getBytes(StandardCharsets.UTF_8));
//        theStage.add(file, blobKey, this);
//        File blobFile = getBlobFile(blobKey);
//        Utils.writeContents(userPath(file).toFile(),
//                Utils.readContents(blobFile));
    }

    public static Commit getSplitCommit(Commit current, Commit given) {
        List<String> parentListC = current.getParentList(new HashSet<>());
        List<String> parentListG = given.getParentList(new HashSet<>());
        int lenC = parentListC.size();
        for (int i = 0; i < lenC; i++) {
            if (parentListG.contains(parentListC.get(i))) {
                return Commit.getCommitFromID(parentListC.get(i));
            }
        }
        return Commit.getCommitFromID(parentListC.get(lenC - 1));
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
            printAndExit("Not in an initialized Gitlet directory.");
        }
    }
}
