package sunrise.model;

public class Dentist {

    private final String id;
    private String name;
    private String specialization;

    public Dentist(String id, String name, String specialization) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String toDataLine() {
        return String.join("|", id, name, specialization);
    }

    public static Dentist fromDataLine(String line) {
        String[] p = line.split("\\|", -1);
        return new Dentist(p[0], p[1], p.length > 2 ? p[2] : "");
    }
}
