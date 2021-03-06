package greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import greendao.Activity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ACTIVITY.
*/
public class ActivityDao extends AbstractDao<Activity, Long> {

    public static final String TABLENAME = "ACTIVITY";

    /**
     * Properties of entity Activity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Bipedal_count = new Property(1, int.class, "bipedal_count", false, "BIPEDAL_COUNT");
        public final static Property Points = new Property(2, int.class, "points", false, "POINTS");
        public final static Property Variance = new Property(3, int.class, "variance", false, "VARIANCE");
        public final static Property Start = new Property(4, java.util.Date.class, "start", false, "START");
        public final static Property End = new Property(5, java.util.Date.class, "end", false, "END");
    };


    public ActivityDao(DaoConfig config) {
        super(config);
    }
    
    public ActivityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ACTIVITY' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'BIPEDAL_COUNT' INTEGER NOT NULL ," + // 1: bipedal_count
                "'POINTS' INTEGER NOT NULL ," + // 2: points
                "'VARIANCE' INTEGER NOT NULL ," + // 3: variance
                "'START' INTEGER NOT NULL ," + // 4: start
                "'END' INTEGER NOT NULL );"); // 5: end
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_ACTIVITY_BIPEDAL_COUNT ON ACTIVITY" +
                " (BIPEDAL_COUNT);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_ACTIVITY_POINTS ON ACTIVITY" +
                " (POINTS);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_ACTIVITY_START ON ACTIVITY" +
                " (START);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_ACTIVITY_END ON ACTIVITY" +
                " (END);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ACTIVITY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Activity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getBipedal_count());
        stmt.bindLong(3, entity.getPoints());
        stmt.bindLong(4, entity.getVariance());
        stmt.bindLong(5, entity.getStart().getTime());
        stmt.bindLong(6, entity.getEnd().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Activity readEntity(Cursor cursor, int offset) {
        Activity entity = new Activity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // bipedal_count
            cursor.getInt(offset + 2), // points
            cursor.getInt(offset + 3), // variance
            new java.util.Date(cursor.getLong(offset + 4)), // start
            new java.util.Date(cursor.getLong(offset + 5)) // end
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Activity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setBipedal_count(cursor.getInt(offset + 1));
        entity.setPoints(cursor.getInt(offset + 2));
        entity.setVariance(cursor.getInt(offset + 3));
        entity.setStart(new java.util.Date(cursor.getLong(offset + 4)));
        entity.setEnd(new java.util.Date(cursor.getLong(offset + 5)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Activity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Activity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
