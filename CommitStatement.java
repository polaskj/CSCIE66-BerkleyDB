/*
 * CommitStatement.java
 *
 * DBMS Implementation
 */

import com.sleepycat.db.*;

/**
 * A class that represents a COMMIT statement in SQL, which commits
 * the current transaction.
 */
public class CommitStatement extends SQLStatement {
    /** Constructs a CommitStatement object */
    public CommitStatement() {
        super();
    }
    
    public void execute() throws DatabaseException, DeadlockException {
        /* not yet implemented */
    }
}
