package eu.codlab.fitit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import de.greenrobot.dao.query.QueryBuilder;
import greendao.DaoMaster;
import greendao.DaoSession;

public class DatabaseManager {

    private static final String DATABASE_NAME = "fitit";
    private static DaoSession sDaoSession;

    private static DatabaseManager ourInstance = new DatabaseManager();

    static class ModelItem {
        String packageName;
        String jsonName;

        ModelItem(String fullName, String jsonName) {
            packageName = fullName;
            this.jsonName = jsonName;
        }
    }

    private static final ModelItem[] sModelKeys =
            {
            };

    public static DatabaseManager getInstance() {
        return ourInstance;
    }

    private DatabaseManager() {

    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    * SESSION
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void startSession(final Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        sDaoSession = daoMaster.newSession();
    }

    protected DaoSession getSession() {
        return sDaoSession;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    * DATA
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void flushData() {
    }

}
