package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.MoreUtils.*;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    private byte[] content;
    private File blobDir;
    private File blobFile;
    private File sourceFile;
    private String blobID;

    public Blob(File f) {
        sourceFile = f;
        content = readContents(f);
        blobID = sha1(content);
        blobDir = join(Repository.OBJECTS_DIR, blobID.substring(0, 2));
        blobFile = join(blobDir, blobID.substring(2));
        writeObject(blobFile, this);
    }

    public String getBlobID() {
        return blobID;
    }

    public byte[] getContent() {
        return content;
    }

    public File getBlobFile() {
        return blobFile;
    }

    public static Blob getFromID(String aBlobID) {
        return readObject(getFileFromID(aBlobID), Blob.class);
    }

    public void writeToSourceFile() {
        writeContents(sourceFile, content);
    }
}
