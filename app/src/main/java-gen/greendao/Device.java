package greendao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table DEVICE.
 */
public class Device {

    private Long id;
    /** Not-null value. */
    private java.util.Date created_at;
    /** Not-null value. */
    private String serial;
    /** Not-null value. */
    private String name;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Device() {
    }

    public Device(Long id) {
        this.id = id;
    }

    public Device(Long id, java.util.Date created_at, String serial, String name) {
        this.id = id;
        this.created_at = created_at;
        this.serial = serial;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public java.util.Date getCreated_at() {
        return created_at;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCreated_at(java.util.Date created_at) {
        this.created_at = created_at;
    }

    /** Not-null value. */
    public String getSerial() {
        return serial;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSerial(String serial) {
        this.serial = serial;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}