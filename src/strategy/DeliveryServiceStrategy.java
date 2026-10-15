package strategy;

public interface DeliveryServiceStrategy {
    String getServiceName();
    double calculateFee(double baseFee);
    boolean isAllowed(double chargeableWeight);
}
