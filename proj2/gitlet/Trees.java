package gitlet;
import java.io.Serializable;
import java.io.File;
import java.util.*;
import java.lang.reflect.Array;
import static gitlet.Utils.*;

public class Trees implements Serializable {
    private File treeDir;
    private String treeName;
    private File treeFile;
    private byte[] content;
    HashMap<String, String> tracked;
    HashMap<String, String> added;
    HashMap<String, String> removed;

    public Trees(File f) {
        content = readContents(f);

    }


}
