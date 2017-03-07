# Makefile for DBMS Problem Sets

HOME = /home/csci/.e66libs
LIB = $(HOME)/lib

JAVAC = javac -J-Xms64m -J-Xmx128m -classpath .:$(LIB)/java-cup-11.jar:$(LIB)/jline.jar:$(LIB)/db.jar
JAVA = java -Xms64m -Xmx128m -classpath .:$(LIB)/java-cup-11.jar:$(LIB)/jline.jar:$(LIB)/db.jar -Djava.library.path=$(LIB)

SOURCES = AndExpression.java BeginStatement.java Catalog.java Column.java \
	ColumnOptions.java CommitStatement.java CompareTerm.java \
	Comparison.java ConditionalExpression.java CreateStatement.java \
	CrossIterator.java DBMS.java DeleteStatement.java DropStatement.java \
	InsertStatement.java InsertRow.java InvalidSyntaxException.java \
	Lexer.java Limit.java NotExpression.java \
	OrExpression.java Parser.java ProjectionIterator.java \
	RelationIterator.java RollbackStatement.java SelectStatement.java \
	SQLStatement.java sym.java Table.java TableIterator.java \
	TrueExpression.java UpdateStatement.java 

OTHER_FILES = 

default:	Lexer.java Parser.java DBMS.class

Lexer.java:	sql.flex
		$(JAVA) -jar $(LIB)/JFlex.jar sql.flex

Parser.java:    sql.cup Lexer.java
		$(JAVA) -jar $(LIB)/java-cup-11.jar -parser Parser < sql.cup

DBMS.class:	$(SOURCES)
		$(JAVAC) -g *.java

clean:
	rm -rf *~ *.class
