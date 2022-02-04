package gitlet;


import java.io.File;

import static gitlet.Utils.join;

public class MoreUtils {

    static void printAndExit(String message) {
        System.out.println(message);
        System.exit(0);
    }

    static void printOneLine(String s) {
        if (s == null) {
            System.out.println();
        } else {
            System.out.println(s);
        }
    }

    static File getFileFromID(String aBlobID) {
        File aDir = join(Repository.OBJECTS_DIR, aBlobID.substring(0, 2));
        File aFile = join(aDir, aBlobID.substring(2));
        return aFile;
    }

    static File getFileFromShortedID(String aShortID) {
        File objectDir = join(Repository.OBJECTS_DIR, aShortID.substring(0, 2));
        File[] filesList = objectDir.listFiles();
        String shortSub = aShortID.substring(2);
        int length = shortSub.length();
        for (File f : filesList) {
            String s = f.getName();
            if (s.substring(0, length).equals(shortSub)) {
                return f;
            }
        }
        return null;
    }

}
