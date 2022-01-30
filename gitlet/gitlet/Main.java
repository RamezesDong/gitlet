package gitlet;
import java.io.File;
import java.util.*;
import java.lang.reflect.Array;
import static gitlet.Utils.*;
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     *
     *  init -- Initialize the gitlet.
     *
     *  add [file name]
     *
     *  commit [message]
     *
     *  rm [file name]
     *
     *  log
     *
     *  global-log
     *
     *  find [commit message]
     *
     *  status
     *
     *  checkout -- [file name]
     *           [commit id] -- [file name]
     *           [branch name]
     *
     *  branch [branch name]
     *
     *  rm-branch [branch name]
     *
     *  reset [commit id]
     *
     *  merge [branch name]
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args == null) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs(args, 1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            case "commit":

            case "rm":

            case "log":
            case "global-log":
            case "find":
            case "status":
            case "checkout":
            case "branch":
            case "rm-branch":
            case "reset":
            case "merge":

            default:
                System.out.println("No command with that name exits.");
                System.exit(0);
        }
    }

    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
