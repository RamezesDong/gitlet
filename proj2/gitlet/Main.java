package gitlet;
import java.io.File;
import java.util.*;
import java.lang.reflect.Array;
import static gitlet.Utils.*;
import static gitlet.MoreUtils.*;
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
                validateNumArgs(args,2);
                String fileName = args[1];
                Repository.add(fileName);
                break;
            case "commit":
                if (args.length == 1) {
                    printAndExit("Please enter a commit message.");
                }
                validateNumArgs(args, 2);
                String message = args[1];
                Repository.commit(message);
                break;
            case "rm":
                validateNumArgs(args,2);
                String rmFileName = args[1];
                Repository.rm(rmFileName);
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.log();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                Repository.global_log();
                break;
            case "find":
                validateNumArgs(args, 2);
                String findMessage = args[1];
                Repository.find(findMessage);
                break;
            case "status":
                validateNumArgs(args, 1);
                Repository.status();
                break;
            case "checkout":
                if (args.length == 2) {
                    String branchName = args[1];
                    Repository.changeBranch(branchName);
                    break;
                } else if (args.length == 3) {
                    if (args[1] == "--") {
                        String fName = args[2];
                        Repository.checkOutFile(fName);
                    } else {
                        printAndExit("Incorrect operands.");
                    }
                } else if (args.length == 4) {
                    if (args[2] == "--") {
                        String bID = args[1];
                        String fName = args[3];
                        Repository.checkOutFileFromCommit(bID, fName);
                    } else {
                        printAndExit("Incorrect operands.");
                    }
                } else {
                    printAndExit("Incorrect operands.");
                }
                break;
            case "branch":
            case "rm-branch":
            case "reset":
            case "merge":
            default:
                printAndExit("No command with that name exits.");
        }
    }

    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
