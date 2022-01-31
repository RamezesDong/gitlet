package gitlet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

import static gitlet.Utils.join;

public class MoreUtils {

    static void printAndExit (String message) {
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
}
