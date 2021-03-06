package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static gitlet.Utils.*;
import static gitlet.MoreUtils.*;

public class Logs implements Serializable {
    private List<String> ids;

    public Logs() {
        if (!Repository.LOGS.exists()) {
            ids = new ArrayList<>();
        } else {
            Logs l = readObject(Repository.LOGS, Logs.class);
            this.ids = l.ids;
        }
    }

    public void addLogs(String s) {
        ids.add(s);
    }

    public void writeToLogs() {
        writeObject(Repository.LOGS, this);
    }

    public void globalLogs() {
        HashSet<String> differentID = new HashSet<>();
        for (String e : ids) {
            if (!differentID.contains(e)) {
                differentID.add(e);
            }
        }
        for (String e : differentID) {
            Commit cm = Commit.getCommitFromID(e);
            cm.getSelfLog();
        }
    }

    public void find(String s) {
        HashSet<String> differentID = new HashSet<>();
        for (String e : ids) {
            if (!differentID.contains(e)) {
                differentID.add(e);
            }
        }
        boolean flag = true;
        for (String e : differentID) {
            Commit cm = Commit.getCommitFromID(e);
            if (s.equals(cm.getMessage())) {
                flag = false;
                System.out.println(cm.getSha1Values());
            }
        }
        if (flag) {
            printAndExit("Found no commit with that message.");
        }
    }

    public List<String> getIds() {
        return ids;
    }
}
