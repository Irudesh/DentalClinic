package sunrise.model;

/**
 * Patient details as captured directly on the appointment record, per the
 * assessment brief ("Store patient and appointment details including:
 * ... patient name, address, contact number ..."). Modelled as a small
 * immutable value object rather than a separately persisted entity, since
 * the brief does not require standalone patient management.
 */
public class Patient {

    private final String name;
    private final String address;
    private final String contactNumber;

    public Patient(String name, String address, String contactNumber) {
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNumber() {
        return contactNumber;
    }
}
