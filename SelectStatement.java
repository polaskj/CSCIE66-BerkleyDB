/*
 * SelectStatement.java
 *
 * DBMS Implementation
 */

import java.util.*;
import com.sleepycat.db.*;

/**
 * A class that represents a SELECT statement.
 */
public class SelectStatement extends SQLStatement {
    /* Used in the selectList for SELECT * statements. */
    public static final String STAR = "*";
    
    private ArrayList<Object> selectList;
    private Limit limit;
    private boolean distinctSpecified;
    
    /** 
     * Constructs a SelectStatement object involving the specified
     * columns and other objects from the SELECT clause, the specified
     * tables from the FROM clause, the specified conditional
     * expression from the WHERE clause (if any), the specified Limit
     * object summarizing the LIMIT clause (if any), and the specified
     * value indicating whether or not we should eliminate duplicates.
     *
     * @param  selectList  the columns and other objects from the SELECT clause
     * @param  fromList  the list of tables from the FROM clause
     * @param  where  the conditional expression from the WHERE clause (if any)
     * @param  limit  summarizes the info in the LIMIT clause (if any)
     * @param  distinctSpecified  should duplicates be eliminated?
     */
    public SelectStatement(ArrayList<Object> selectList, 
                           ArrayList<Table> fromList, ConditionalExpression where,
                           Limit limit, Boolean distinctSpecified)
    {
        super(fromList, new ArrayList<Column>(), where);
        this.selectList = selectList;
        this.limit = limit;
        this.distinctSpecified = distinctSpecified.booleanValue();
        
        /* add the columns in the select list to the list of columns */
        for (int i = 0; i < selectList.size(); i++) {
            Object selectItem = selectList.get(i);
            if (selectItem instanceof Column)
                this.addColumn((Column)selectItem);
        }
    }
    
    /**
     * Returns a boolean value indicating whether duplicates should be
     * eliminated in the result of this statement -- i.e., whether the
     * user specified SELECT DISTINCT.
     */
    public boolean distinctSpecified() {
        return this.distinctSpecified;
    }
    
    public void execute() throws DatabaseException, DeadlockException {
    	if(this.numTables() > 1){
            throw new DatabaseException("Unsupported SELECT command: More than one table in the FROM clause");
        }
    	TableIterator iter = null;
        try {
            Table table = this.getTable(0);
            // Open table and assign status resp
            OperationStatus status = table.open();
            if (status == OperationStatus.KEYEXIST){
            	throw new DatabaseException(" The operation to insert data was configured to not " +
            			"allow overwrite and the key already exists in the database.");
            } else if (status == OperationStatus.KEYEMPTY){
            	throw new DatabaseException("The cursor operation was unsuccessful because the current " +
            			"record was deleted.");
            } else if (status == OperationStatus.NOTFOUND){
            	throw new DatabaseException("The requested key/data pair was not found.");
            } else if (status == OperationStatus.SUCCESS && DBMS.DEBUG){
              	System.out.println("The insert operation was successful.");
            }
            boolean hasWhere = this.numWhereColumns() > 0;
            iter = new TableIterator(this, table, hasWhere);
            iter.printAll(System.out);
        } finally {
        	iter.close();
        }
    }
}
