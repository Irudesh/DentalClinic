package sunrise.model;

public class TreatmentType {

    private final String id;
    private String name;
    private double fee;

    public TreatmentType(String id, String name, double fee) {
        this.id = id;
        this.name = name;
        this.fee = fee;
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

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String toDataLine() {
        return String.join("|", id, name, String.valueOf(fee));
    }

    public static TreatmentType fromDataLine(String line) {
        String[] p = line.split("\\|", -1);
        return new TreatmentType(p[0], p[1], p.length > 2 ? Double.parseDouble(p[2]) : 0.0);
    }
}
