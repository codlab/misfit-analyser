package eu.codlab.fitit.database;

import org.json.JSONArray;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public abstract class AbstractDao {

    public static final String GUID = "guid";

    de.greenrobot.dao.AbstractDao mDao;

    AbstractDao() {
    }

    public abstract boolean create(Map<String, Object> params);

    public abstract boolean update(Map<String, Object> params);

    public abstract boolean delete(int guid);

    public abstract de.greenrobot.dao.AbstractDao getDao();

    protected abstract boolean preloadData(JSONArray data);

    public void clear() {
        getDao().deleteAll();
    }

    @SuppressWarnings("unchecked")
    public boolean create(de.greenrobot.dao.AbstractDao dao, Object... entities) {
        dao.insertInTx(entities);
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean update(de.greenrobot.dao.AbstractDao dao, Object... entities) {
        dao.insertOrReplaceInTx(entities);
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean delete(de.greenrobot.dao.AbstractDao dao, Object... entities) {
        dao.deleteInTx(entities);
        return true;
    }


    private static Map<String, AbstractDao> sMappedDao = new HashMap<>();

    private static AbstractDao getStaticDao(Class klass) {
        Method method = getStaticMethodInstance(klass);
        if (method != null) {
            try {
                return (AbstractDao) method.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Timber.e(e.getCause(), e.getMessage());
            }
        }

        return null;
    }

    private static Method getStaticMethodInstance(Class klass) {
        try {
            return klass.getDeclaredMethod("getInstance");
        } catch (NoSuchMethodException e) {
            Timber.e(e.getCause(), e.getMessage());
        }
        return null;
    }

    public static AbstractDao getFromClassName(final String klassName) throws ClassNotFoundException, NullPointerException {

        if (!sMappedDao.containsKey(klassName)) {
            Class klass = Class.forName(klassName);
            AbstractDao dao = getStaticDao(klass);
            if (dao == null)
                throw new NullPointerException("Dao Class is null for Class:[" + klassName + "]");
            sMappedDao.put(klassName, dao);
        }
        return sMappedDao.get(klassName);
    }

}
