/*
 * InsertRow.java
 *
 * DBMS Implementation
 */

import com.sleepycat.db.*;
import com.sleepycat.bind.*;
import com.sleepycat.bind.tuple.*;

/**
 * A class that represents a row that will be inserted in a table in a
 * relational database.
 *
 * This class contains the code used to marshall the values of the
 * individual columns to a single key-data pair in the underlying
 * BDB database.
 */
public class InsertRow {
    private Table table;         // the table in which the row will be inserted
    private Object[] values;     // the individual values to be inserted
    private DatabaseEntry key;   // the key portion of the marshalled row
    private DatabaseEntry data;  // the data portion of the marshalled row
   
    /**
     * Constructs an InsertRow object for a row containing the specified
     * values that is to be inserted in the specified table.
     *
     * @param  t  the table
     * @param  values  the values in the row to be inserted
     */
    public InsertRow(Table table, Object[] values) {
        this.table = table;
        this.values = values;
        
        // These objects will be created by the marshall() method.
        this.key = null;
        this.data = null;
    }
    
    /**
     * Takes the collection of values for this InsertRow
     * and marshalls them into a key/data pair.
     */
    public void marshall() {
    	TupleOutput keyTupleBuffer = null;
    	TupleOutput dataTupleBuffer = new TupleOutput(); 
    	int byteLocation = (Integer.SIZE/8) * table.numColumns(); // First available value byte
    
    	for (int i = 0; i < table.numColumns(); i++) {
    		Column col = table.getColumn(i);
    		
    		// Write header metadata which contains byte location
    		int columnLength;
    		if (values[i] == null){
    			if (DBMS.DEBUG){
	    			System.out.println("Col " + col.getName() + " null. Setting -1");
	    		}
    			dataTupleBuffer.writeInt(-1); 
    			continue;
    		} else if (col.getType() == Column.VARCHAR || col.getType() == Column.CHAR){
    			int l = ((String)values[i]).length(); // Are we supposed to .trim() here???
    			columnLength = l;
    		} else {
    			columnLength = col.getLength(); 
    		}
    		
    		dataTupleBuffer.writeInt(byteLocation); 
    		if (DBMS.DEBUG){
    			System.out.println("Setting " + col.getName() + " header offset at pos " + 
    					byteLocation + " - Actual value: " + values[i]);
    		}
    	
    		// Set key if current column is PK // Optimize to not save pk in data ???
    		if ((table.primaryKeyColumn() != null) && col.isPrimaryKey()){	
    			keyTupleBuffer = new TupleOutput(); 
    			// keyTupleBuffer.writeInt(byteLocation); 
    			writeDataTypeValue(keyTupleBuffer, values[i], col.getType());
    		}
    		byteLocation += columnLength; // Reset next byte value
    	}

    	 // Write column values
         for (int i = 0; i < table.numColumns(); i++) {
         	Column col = table.getColumn(i);
         	Object value = values[i];
         	if (value != null){
         		writeDataTypeValue(dataTupleBuffer, value, col.getType());
         	}
         } 

         // Create appropriate DB entries
         key = (keyTupleBuffer == null) ? 
        		 new DatabaseEntry() : new DatabaseEntry(keyTupleBuffer.getBufferBytes(), 0, keyTupleBuffer.getBufferLength());
         data = new DatabaseEntry(dataTupleBuffer.getBufferBytes(), 0, dataTupleBuffer.getBufferLength());
    }
    
    /**
     * Returns the DatabaseEntry for the key in the key/data pair for this row.
     *
     * @return  the key DatabaseEntry
     */
    public DatabaseEntry getKey() {
        return this.key;
    }
    
    /**
     * Returns the DatabaseEntry for the data item in the key/data pair 
     * for this row.
     *
     * @return  the data DatabaseEntry
     */
    public DatabaseEntry getData() {
        return this.data;
    }
    
    private void writeDataTypeValue(TupleOutput tuple, Object value, int type){
    	if (type == Column.INTEGER){
     		tuple.writeInt(((Integer) value).intValue());
     	} else if (type == Column.REAL){
     		tuple.writeDouble(((Double) value).doubleValue());
     	} else {
     		tuple.writeBytes((String) value);
     	}
    }
}
