package gitlet;
import java.io.Serializable;
import java.io.File;
import java.util.*;
import java.lang.reflect.Array;
import static gitlet.Utils.*;

public class Stage implements Serializable {
    HashMap<String, String> tracked;
    HashMap<String, String> added;
    HashMap<String, String> removed;


}
