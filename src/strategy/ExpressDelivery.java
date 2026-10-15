package strategy;

public class ExpressDelivery implements DeliveryServiceStrategy {
    @Override
    public String getServiceName() {
        return "Express";
    }

    @Override
    public double calculateFee(double baseFee) {
        return baseFee * 1.5;
    }

    @Override
    public boolean isAllowed(double chargeableWeight) {
        return true;
    }
}
