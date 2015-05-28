package eu.codlab.fitit.database;

import org.json.JSONArray;

import java.util.Map;

public class DailyActivityDao extends AbstractDao {


    private DailyActivityDao() {
    }

    private static DailyActivityDao ourInstance = new DailyActivityDao();

    public static DailyActivityDao getInstance() {
        return ourInstance;
    }

    @Override
    public boolean create(Map<String, Object> params) {
        return false;
    }

    @Override
    public boolean update(Map<String, Object> params) throws NullPointerException {
        return false;
    }

    @Override
    public boolean delete(final int id) {
        return false;
    }

    @Override
    public de.greenrobot.dao.AbstractDao getDao() {
        if (mDao == null) {
            mDao = DatabaseManager.getInstance().getSession().getDailyActivityDao();
        }
        return mDao;
    }

    @Override
    protected boolean preloadData(JSONArray data) {
        return true;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    * KEEP METHODS - put your custom methods here
    * KEEP METHODS END
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

}
