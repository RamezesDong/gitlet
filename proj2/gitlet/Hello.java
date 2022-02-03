package gitlet;

import java.io.File;

public class Hello {
    private static final File CWD = new File(System.getProperty("user.dir"));


    public static void main(String[] args) {
        File[] files = CWD.listFiles();
        for (File f : files) {
            System.out.println(f.getPath());
        }
    }

}
