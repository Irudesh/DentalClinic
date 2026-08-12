package sunrise.service.fee;

/**
 * Strategy pattern: BillingService selects one of these at run time
 * depending on whether a discount applies to a given appointment (the
 * "Apply discount" &lt;&lt;extend&gt;&gt; use case), rather than branching
 * on discount logic inline. New pricing strategies (e.g. a loyalty
 * discount, a senior-citizen rate) can be added later as new classes
 * without modifying BillingService.
 */
public interface FeeCalculationStrategy {

    double calculateTotal(double baseFee, double discountPercent);
}
