/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author lenovo
 */
public final class SQLiteDBHelper 
{
        Connection c = null;
        
        public SQLiteDBHelper()
        {
            
        }
        
        
        //TODO: MEMORY LOAD DB
        //c = DriverManager.getConnection("jdbc:sqlite:test.db:memory");
        //Starts Connection with an existing DB name, if it does not exist it creates the DB.
        public void StartConnection(String path)
        {
            try 
            {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite::memory:");
                
                System.out.println("Opened DB successfully");
            } 
            catch ( Exception e ) 
            {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
        }
        
        //Creates a new table with a given query.
        public void CreateTable(String query)
        {
            try 
            {
                Statement stmt = c.createStatement();
                stmt.executeUpdate(query);
                stmt.close();
            } catch (SQLException ex) 
            {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Created table successfully");
        }
        
        //Backups in-memory database to a file.
        public void Backup()
        {
            try {
                Statement stmt = c.createStatement();
                stmt.executeUpdate("backup to backup.db");
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        //Restores in-memory database from a file.
        public void Restore()
        {
            try {
                Statement stmt = c.createStatement();
                stmt.executeUpdate("restore from backup.db");
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Returns all records from a table.
        public ResultSet SelectAll(String tableName)
        {
            String query = "SELECT * FROM " + tableName + ";";
            ResultSet rs = null;
            try {
                Statement stmt = c.createStatement();
                rs = stmt.executeQuery(query);
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return rs;
        }
        
        //Inserts a single page with its URL and document content.
        public void InsertPage(String URL, String Document, String tableName)
        {
            try {
                String query = "INSERT INTO crawler (URL,Document) VALUES (?,?)";
                
                
                PreparedStatement pstmt = c.prepareStatement(query);
                pstmt.setString(1, URL);
                pstmt.setString(2, Document);
                pstmt.executeUpdate();
                
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        //Inserts Pages using normal insert query for each page.
        public void InsertPages(List<HtmlPage> Pages) throws SQLException
        {
            String query = "INSERT INTO crawler (URL,Document) VALUES(?,?)";
            
            //Using prepared statement to preven SQL INJECTION.
            PreparedStatement pstmt = c.prepareStatement(query);
            long startTime = System.currentTimeMillis();
            for (HtmlPage Page : Pages)
            {
                pstmt.setString(1, Page.getDomainUrlObject().getDomainUrl());
                pstmt.setString(2, Page.getHtml());
                pstmt.executeUpdate();
            }
            
            long endTime = System.currentTimeMillis();
            long elapsedTime = (endTime - startTime); //in seconds
            System.out.println("Total time required to execute 1000 SQL INSERT queries using PreparedStatement without JDBC batch update is :" + elapsedTime);
        }
        
        
        //Sets AutoCommit status to true/false
        public void SetAutoCommit(boolean state)
        {
            try {
                c.setAutoCommit(state);
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Inserts pages in a batch into crawler table in DB.
        public void InsertPagesBatch(List<HtmlPage> Pages) throws SQLException
        {
            
            //Setting auto commit to false to make all incoming inserts into one transaction.
            SetAutoCommit(false);
            String query = "INSERT INTO crawler (URL,Document) VALUES(?,?)";
            
            //Using prepared statement to preven SQL INJECTION.
            PreparedStatement pstmt = c.prepareStatement(query);
            long startTime = System.currentTimeMillis();
            for (HtmlPage Page : Pages)
            {
                pstmt.setString(1, Page.getDomainUrlObject().getDomainUrl());
                pstmt.setString(2, Page.getHtml());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            c.commit();
            pstmt.close();
            long endTime = System.currentTimeMillis();
            long elapsedTime = (endTime - startTime); //in seconds
            System.out.println("Total time required to execute 1000 queries using PreparedStatement with JDBC batch insert is :" + elapsedTime);
            
            
            /////////////////////////////////////////////////////
            //TO BE USED FOR THE 5000 PAGES PROJECT ( BATCH WITHIN BATCH )
            //Inserting each 500 pages together at once
            
            /*
            //Setting auto commit to false to make all incoming inserts into one transaction.
            SetAutoCommit(false);
            String query = "INSERT INTO crawler (URL,Document) VALUES(?,?)";
            
            //Single Batch size
            int batchSize = 500;
            
            //Using prepared statement to preven SQL INJECTION.
            PreparedStatement pstmt = c.prepareStatement(query);
            long startTime = System.currentTimeMillis();
            for (int i=0; i<Pages.size(); i++)
            {
                pstmt.setString(1, Page.getDomainUrlObject().getDomainUrl());
                pstmt.setString(2, Page.getHtml());
                pstmt.addBatch();
            
                if(i % batchSize == 0)
                    pstmt.executeBatch();
            }
            
            pstmt.executeBatch();
            long endTime = System.currentTimeMillis();
            long elapsedTime = (endTime - startTime); //in seconds
            System.out.println("Total time required to execute 1000 queries using PreparedStatement with JDBC batch insert is :" + elapsedTime);
        
            
            c.commit();
            pstmt.close();
            
            */
            
            
            
            
            
            /////////////////////////////////////////////////////
            
        }
        
        
        
        //Closes current connection.
        public void CloseConnection()
        {
            try 
            {
                c.close();
            } catch (SQLException ex) 
            {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
}
