package strategy;

public class SameDayDelivery implements DeliveryServiceStrategy {
    @Override
    public String getServiceName() {
        return "Same Day";
    }

    @Override
    public double calculateFee(double baseFee) {
        return (baseFee * 2.0) + 100.0;
    }

    @Override
    public boolean isAllowed(double chargeableWeight) {
        return chargeableWeight <= 10.0;
    }
}
