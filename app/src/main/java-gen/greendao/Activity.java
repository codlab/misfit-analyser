package greendao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import org.joda.time.DateTime;

/**
 * Entity mapped to table ACTIVITY.
 */
public class Activity {

    private Long id;
    private int bipedal_count;
    private int points;
    private int variance;
    /**
     * Not-null value.
     */
    private java.util.Date start;
    /**
     * Not-null value.
     */
    private java.util.Date end;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Activity() {
    }

    public Activity(Long id) {
        this.id = id;
    }

    public Activity(Long id, int bipedal_count, int points, int variance, java.util.Date start, java.util.Date end) {
        this.id = id;
        this.bipedal_count = bipedal_count;
        this.points = points;
        this.variance = variance;
        this.start = start;
        this.end = end;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBipedal_count() {
        return bipedal_count;
    }

    public void setBipedal_count(int bipedal_count) {
        this.bipedal_count = bipedal_count;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getVariance() {
        return variance;
    }

    public void setVariance(int variance) {
        this.variance = variance;
    }

    /**
     * Not-null value.
     */
    public java.util.Date getStart() {
        return start;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setStart(java.util.Date start) {
        this.start = start;
    }

    /**
     * Not-null value.
     */
    public java.util.Date getEnd() {
        return end;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setEnd(java.util.Date end) {
        this.end = end;
    }

    // KEEP METHODS - put your custom methods here
    private DateTime mDate;
    private int mUid = 0;

    public int getUid() {
        if (mDate == null) {
            mDate = new DateTime(getStart());//,DateTimeZone.UTC);
            mUid = mDate.getYear() * 1000 + mDate.getDayOfYear();
        }
        return mUid;
    }
    // KEEP METHODS END

}
