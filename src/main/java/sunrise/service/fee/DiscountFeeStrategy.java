package sunrise.service.fee;

public class DiscountFeeStrategy implements FeeCalculationStrategy {

    @Override
    public double calculateTotal(double baseFee, double discountPercent) {
        double discountAmount = baseFee * (discountPercent / 100.0);
        return StandardFeeStrategy.round(baseFee - discountAmount);
    }
}
