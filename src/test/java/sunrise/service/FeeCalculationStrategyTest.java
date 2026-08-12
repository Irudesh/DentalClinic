package sunrise.service;

import org.junit.jupiter.api.Test;
import sunrise.service.fee.DiscountFeeStrategy;
import sunrise.service.fee.FeeCalculationStrategy;
import sunrise.service.fee.StandardFeeStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeeCalculationStrategyTest {

    @Test
    void standardStrategyIgnoresDiscountAndReturnsBaseFee() {
        FeeCalculationStrategy strategy = new StandardFeeStrategy();
        assertEquals(1500.0, strategy.calculateTotal(1500.0, 0));
        assertEquals(1500.0, strategy.calculateTotal(1500.0, 20)); // ignored on purpose
    }

    @Test
    void discountStrategyAppliesPercentageOffBaseFee() {
        FeeCalculationStrategy strategy = new DiscountFeeStrategy();
        // 3000 with 10% off => 2700
        assertEquals(2700.0, strategy.calculateTotal(3000.0, 10));
    }

    @Test
    void discountStrategyHandlesFullDiscount() {
        FeeCalculationStrategy strategy = new DiscountFeeStrategy();
        assertEquals(0.0, strategy.calculateTotal(1500.0, 100));
    }

    @Test
    void discountStrategyHandlesZeroDiscountAsNoChange() {
        FeeCalculationStrategy strategy = new DiscountFeeStrategy();
        assertEquals(4500.0, strategy.calculateTotal(4500.0, 0));
    }
}
