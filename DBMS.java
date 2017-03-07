/*
 * DBMS.java
 *
 * DBMS Implementation
 */

import java.io.*;
import java.util.*;
import com.sleepycat.db.*;
import com.sleepycat.bind.tuple.*;

/**
 * The main class of a simple relational database.  It includes the
 * main() method, and methods for performing tasks or obtaining state
 * involving the DBMS application as a whole (e.g., initialization,
 * configuring the environment, aborting the application, etc.)
 *
 * To allow access to the DBMS methods from all other classes, we make
 * all methods static, so that the class name can be used to invoke them.
 *
 * To run the application, enter the command<br>
 * <br>
 * <code>java DBMS</code><br>
 * <br>
 * from the command line.
 */
public class DBMS {
    /** Set this to true to print debugging messages, and false to 
      omit them. */
    public static final boolean DEBUG = true;
    
    /** The home directory of the BDB environment. */
    public static final String DB_HOME = "db";
    
    private static Environment env;
    
    // used to keep track of the current transaction
    private static Stack<Transaction> txnStack;
    
    /**
     * The main method for the DBMS application.
     */
    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);
        
        DBMS.init();
        
        while (true) {
            try {
                /* Get the command string from the user. */
                System.out.println();
                System.out.println("Enter command (q to quit): ");
                //String commandString = console.nextLine();
                
                //TODO REMOVE LATER
                String commandString = "INSERT INTO PKCourse1 VALUES ('id001','fname001','lname001','city001',null);";
                
                if (commandString.equalsIgnoreCase("q"))
                    break;
                
                /* Parse the command string. */
                StringReader commandStream = new StringReader(commandString);
                Lexer l = new Lexer(commandStream);
                Parser p = new Parser(l);
                SQLStatement command = (SQLStatement)p.parse().value;
                if (DEBUG)
                    System.out.println(command);
                
                /* Execute the SQL command. */
                command.execute();
                
                //TODO REMOVE LATER
                break;
                
            } catch (IllegalArgumentException e) {
                System.err.println(e);
            } catch (InvalidSyntaxException e) {
                /* error message will have been printed by the parser */
            } catch (Exception e) {
                System.err.println("unexpected exception: " + e);
                e.printStackTrace();
                DBMS.abort();
            }
            
        }
        
        DBMS.shutdown();
    }
    
    /**
     * Initializes the DBMS -- initializing the underlying BDB environment,
     * the catalog, and the in-memory cache of open tables.
     */
    public static void init() {
        try {
            environmentInit();
            Catalog.open();
            Table.cacheInit();
            txnStack = new Stack<Transaction>();
        } catch(Exception e) {
            System.err.println(e);
            e.printStackTrace();
            abort();
        }
    }
    
    /**
     * Prepares the DBMS to shutdown -- closing all open tables, as well
     * as the catalog and the underlying BDB environment.
     */
    public static void shutdown() {
        try {
            while (!txnStack.empty()) {
                Transaction txn = txnStack.pop();
                txn.abort();
            }
            Table.cacheClose();
            Catalog.close();
            if (env != null)
                env.close();
        } catch(Exception e) { 
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Aborts the DBMS in the face of an unexpected or fatal exception.
     */
    public static void abort() {
        shutdown();
        System.exit(1);
    }
    
    /**
     * Returns the handle for the underlying BDB environment.
     *
     * @return  the environment handle
     */
    public static Environment getEnv() {
        return env;
    }
    
    /**
     * Returns the transaction handle at the top of the transaction
     * stack, without removing it from the stack.
     *
     * @return  the transaction handle, or null if the transaction stack
     *          is empty
     */
    public static Transaction getTxn() {
        return (txnStack.empty() ? null : txnStack.peek());
    }
    
    /**
     * Stores the specified transaction handle at the top of the
     * transaction stack.
     *
     * @param  the transaction handle to be stored
     */
    public static void pushTxn(Transaction txn) {
        txnStack.push(txn);
    }
    
    /**
     * Removes the transaction handle at the top of the transaction
     * stack and returns it.
     *
     * @return  the transaction handle, or null if the transaction stack
     *          is empty
     */
    public static Transaction popTxn() {
        return (txnStack.empty() ? null : txnStack.pop());
    }
    
    /**
     * Configures and opens the handle for the underlying DB environment
     *
     * @throws  DatabaseException if Berkeley DB encounters a problem in
     *          opening the environment handle
     * @throws  FileNotFoundException if the Berkeley DB cannot access the
     *          environment's home directory
     * @throws  RuntimeException if the environment's home directory
     *          cannot be created
     * @throws  IllegalStateException if there is an existing file (not a
     *          directory) with the name of the home directory
     */
    private static void environmentInit()
        throws DatabaseException, FileNotFoundException
    {
        /* Create the DB home directory, if necessary. */
        File home = new File(DB_HOME);
        if (!home.exists()) {
            if (!home.mkdir())
                throw new RuntimeException("could not create home directory");
        } else if (!home.isDirectory())
            throw new IllegalStateException("preexisting " + DB_HOME +
                                            " is not a directory");
        
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);
        envConfig.setAllowCreate(true);
        envConfig.setInitializeCache(true);
        envConfig.setInitializeLocking(true);
        envConfig.setInitializeLogging(true);
        envConfig.setPrivate(true);
        envConfig.setRunRecovery(true);
        env = new Environment(home, envConfig);
    }
}
