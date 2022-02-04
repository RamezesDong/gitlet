package gitlet;

import static gitlet.MoreUtils.*;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author TODO
 */
public class Main {

    /***
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     * <p>
     * init -- Initialize the gitlet.
     * <p>
     * add [file name]
     * <p>
     * commit [message]
     * <p>
     * rm [file name]
     * <p>
     * log
     * <p>
     * global-log
     * <p>
     * find [commit message]
     * <p>
     * status
     * <p>
     * checkout -- [file name]
     * [commit id] -- [file name]
     * [branch name]
     * <p>
     * branch [branch name]
     * <p>
     * rm-branch [branch name]
     * <p>
     * reset [commit id]
     * <p>
     * merge [branch name]
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                validateNumArgs(args, 1);
                Repository.init();
                break;
            case "add":
                validateNumArgs(args, 2);
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
                validateNumArgs(args, 2);
                String rmFileName = args[1];
                Repository.rm(rmFileName);
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.log();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                Repository.globalLog();
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
                checkOut(args);
                break;
            case "branch":
                validateNumArgs(args, 2);
                String branchName = args[1];
                Repository.createBranch(branchName);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                String rmBranchName = args[1];
                Repository.rmBranch(rmBranchName);
                break;
            case "reset":
                validateNumArgs(args, 2);
                String commitID = args[1];
                Repository.reset(commitID);
                break;
            case "merge":
                validateNumArgs(args, 2);
                String branchName2 = args[1];
                Repository.merge(branchName2);
                break;
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

    public static void checkOut(String[] args) {
        if (args.length == 2) {
            String branchName = args[1];
            Repository.changeBranch(branchName);
        } else if (args.length == 3) {
            if (args[1].equals("--")) {
                String fName = args[2];
                Repository.checkOutFile(fName);
            } else {
                printAndExit("Incorrect operands.");
            }
        } else if (args.length == 4) {
            if (args[2].equals("--")) {
                String bID = args[1];
                String fName = args[3];
                Repository.checkOutFileFromCommit(bID, fName);
            } else {
                printAndExit("Incorrect operands.");
            }
        } else {
            printAndExit("Incorrect operands.");
        }
    }
}
