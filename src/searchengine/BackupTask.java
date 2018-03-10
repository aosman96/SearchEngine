package searchengine;

import java.util.TimerTask;

/**
 *
 * @author Kamal Aly
 */
public class BackupTask extends TimerTask
{
    
    SQLiteDBHelper sqlite;
    
    public BackupTask(SQLiteDBHelper sqlite)
    {
        this.sqlite = sqlite;
    }
    
    
    @Override
    public void run()
    {
        System.out.println("Database backup to backup.db");
        sqlite.Backup();
    }
}
