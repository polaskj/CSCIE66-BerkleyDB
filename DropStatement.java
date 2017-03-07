/*
 * DropStatement.java
 *
 * DBMS Implementation
 */

import java.io.*;
import com.sleepycat.db.*;

/**
 * A class that represents a DROP TABLE statement.
 */
public class DropStatement extends SQLStatement {
    /** 
     * Constructs a DropStatement object involving the specified table.
     *
     * @param  t  the table to be dropped
     */
    public DropStatement(Table t) {
        super(t);
    }
    
    public void execute() throws DatabaseException, DeadlockException {
        Table table = this.getTable(0);
        
        try {
            // Close the table's database and remove the table from
            // the in-memory table cache if necessary.
            table.close();
            
            // Remove the table's information from the catalog.
            if (Catalog.removeMetadata(table) == OperationStatus.NOTFOUND)
                throw new Exception(table + ": no such table");
            
            // Remove the underlying database file.
            DBMS.getEnv().removeDatabase(null, table.dbName(), null);
            
            System.out.println("Dropped table " + table + ".");
        } catch (Exception e) {
            String errMsg = e.getMessage();
            if (errMsg != null)
                System.err.println(errMsg + ".");
            System.err.println("Could not drop table " + table + ".");
        }
    }
}
