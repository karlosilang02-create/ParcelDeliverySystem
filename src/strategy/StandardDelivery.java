package strategy;

public class StandardDelivery implements DeliveryServiceStrategy {
    @Override
    public String getServiceName() {
        return "Standard";
    }

    @Override
    public double calculateFee(double baseFee) {
        return baseFee;
    }

    @Override
    public boolean isAllowed(double chargeableWeight) {
        return true;
    }
}
