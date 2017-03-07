/*
 * RelationIterator.java
 *
 * DBMS Implementation
 */

import java.io.*;
import com.sleepycat.db.*;

/**
 * An abstract class for classes that serve as iterators over relations --
 * either stored relations (the class TableIterator), or relations formed
 * in the process of evaluating a query (the classes CrossIterator and
 * ProjectionIterator).
 */
public abstract class RelationIterator {
    /**
     * Closes the iterator, which closes any BDB handles that it is using.
     *
     * @throws DatabaseException if Berkeley DB encounters a problem
     *         while closing a handle
     */
    public abstract void close() throws DatabaseException;
    
    /**
     * Advances the iterator to the next tuple in the relation.  If
     * there is a WHERE clause that limits which tuples should be
     * included in the relation, this method will advance the iterator
     * to the next tuple that satisfies the WHERE clause.  If the
     * iterator is newly created, this method will position it on the first
     * tuple in the relation (that satisfies the WHERE clause).
     *
     * @return true if the iterator was advanced to a new tuple, and false
     *         if there are no more tuples to visit
     * @throws DeadlockException if deadlock occurs while accessing the
     *         underlying BDB database(s)
     * @throws DatabaseException if Berkeley DB encounters another problem
     *         while accessing the underlying database(s)
     */
    public abstract boolean next() throws DeadlockException, DatabaseException;
    
    /**
     * Gets the column at the specified index in the relation that
     * this iterator iterates over.  The leftmost column has an index of 0.
     *
     * @return  the column
     * @throws  IndexOutOfBoundsException if the specified index is invalid
     */
    public abstract Column getColumn(int colIndex);
    
    /**
     * Gets the value of the column at the specified index in the row
     * on which this iterator is currently positioned.  The leftmost
     * column has an index of 0.
     *
     * This method will unmarshall the relevant bytes from the
     * key/data pair and return the corresponding Object -- i.e.,
     * an object of type String for CHAR and VARCHAR values, an object
     * of type Integer for INTEGER values, or an object of type Double
     * for REAL values.
     *
     * @return  the value of the column
     * @throws IllegalStateException if the iterator has not yet been
     *         been positioned on a tuple using first() or next()
     * @throws  IndexOutOfBoundsException if the specified index is invalid
     */
    public abstract Object getColumnVal(int colIndex);
    
    /**
     * Gets the number of columns in the relation.
     *
     * @return  the number of columns
     */
    public abstract int numColumns();
    
    /**
     * Gets the number of tuples that the iterator has visited.
     *
     * @return  the number of tuples visited
     */
    public abstract int numTuples();
    
    /**
     * Iterates over all rows in the relation and prints them to the
     * specified PrintStream (e.g., System.out).
     *
     * @throws DeadlockException if deadlock occurs while accessing the
     *         underlying BDB database(s)
     * @throws DatabaseException if Berkeley DB encounters another problem
     *         while accessing the underlying database(s)
     */
    public void printAll(PrintStream out)
        throws DeadlockException, DatabaseException
    {
        // Display column names -- and compute the length of the separator.
        int separatorLen = 0;
        
        out.println();
        for (int i = 0; i < numColumns(); i++) {
            Column col = getColumn(i);
            out.print(" | " + col.getName());
            
            int colWidth = col.printWidth();
            for (int j = col.getName().length(); j < colWidth; j++)
                out.print(" ");
            separatorLen += (colWidth + 3);
        }
        out.println(" | ");
        separatorLen += 3;
        
        // Display the separator.
        for (int i = 0; i < separatorLen; i++)
            out.print("-");
        out.println();
        
        // Print the tuples.
        while (next()) {
            for (int i = 0; i < numColumns(); i++) {
                Object val = getColumnVal(i);
                String valString =
                    (val == null ? "null" : val.toString());
                
                out.print(" | " + valString);
                
                int valWidth = valString.length();
                int colWidth = getColumn(i).printWidth();
                for (int j = valWidth; j < colWidth; j++)
                    out.print(" ");
            }
            out.println(" | ");
        }
        out.println();
    }
}
