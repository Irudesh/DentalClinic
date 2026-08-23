package sunrise.service.fee;

public class StandardFeeStrategy implements FeeCalculationStrategy {

    @Override
    public double calculateTotal(double baseFee, double discountPercent) {
        return round(baseFee);
    }

    public static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
