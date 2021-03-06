package greendao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import greendao.Device;
import greendao.DailyActivity;
import greendao.Activity;
import greendao.SleepSession;
import greendao.SleepSessionPoint;

import greendao.DeviceDao;
import greendao.DailyActivityDao;
import greendao.ActivityDao;
import greendao.SleepSessionDao;
import greendao.SleepSessionPointDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig deviceDaoConfig;
    private final DaoConfig dailyActivityDaoConfig;
    private final DaoConfig activityDaoConfig;
    private final DaoConfig sleepSessionDaoConfig;
    private final DaoConfig sleepSessionPointDaoConfig;

    private final DeviceDao deviceDao;
    private final DailyActivityDao dailyActivityDao;
    private final ActivityDao activityDao;
    private final SleepSessionDao sleepSessionDao;
    private final SleepSessionPointDao sleepSessionPointDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        deviceDaoConfig = daoConfigMap.get(DeviceDao.class).clone();
        deviceDaoConfig.initIdentityScope(type);

        dailyActivityDaoConfig = daoConfigMap.get(DailyActivityDao.class).clone();
        dailyActivityDaoConfig.initIdentityScope(type);

        activityDaoConfig = daoConfigMap.get(ActivityDao.class).clone();
        activityDaoConfig.initIdentityScope(type);

        sleepSessionDaoConfig = daoConfigMap.get(SleepSessionDao.class).clone();
        sleepSessionDaoConfig.initIdentityScope(type);

        sleepSessionPointDaoConfig = daoConfigMap.get(SleepSessionPointDao.class).clone();
        sleepSessionPointDaoConfig.initIdentityScope(type);

        deviceDao = new DeviceDao(deviceDaoConfig, this);
        dailyActivityDao = new DailyActivityDao(dailyActivityDaoConfig, this);
        activityDao = new ActivityDao(activityDaoConfig, this);
        sleepSessionDao = new SleepSessionDao(sleepSessionDaoConfig, this);
        sleepSessionPointDao = new SleepSessionPointDao(sleepSessionPointDaoConfig, this);

        registerDao(Device.class, deviceDao);
        registerDao(DailyActivity.class, dailyActivityDao);
        registerDao(Activity.class, activityDao);
        registerDao(SleepSession.class, sleepSessionDao);
        registerDao(SleepSessionPoint.class, sleepSessionPointDao);
    }
    
    public void clear() {
        deviceDaoConfig.getIdentityScope().clear();
        dailyActivityDaoConfig.getIdentityScope().clear();
        activityDaoConfig.getIdentityScope().clear();
        sleepSessionDaoConfig.getIdentityScope().clear();
        sleepSessionPointDaoConfig.getIdentityScope().clear();
    }

    public DeviceDao getDeviceDao() {
        return deviceDao;
    }

    public DailyActivityDao getDailyActivityDao() {
        return dailyActivityDao;
    }

    public ActivityDao getActivityDao() {
        return activityDao;
    }

    public SleepSessionDao getSleepSessionDao() {
        return sleepSessionDao;
    }

    public SleepSessionPointDao getSleepSessionPointDao() {
        return sleepSessionPointDao;
    }

}
