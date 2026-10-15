package model;

import exception.InvalidParcelException;
import strategy.DeliveryServiceStrategy;

public abstract class Parcel {
    private String trackingNumber;
    private String receiverName;
    private double weight;
    private DeliveryServiceStrategy deliveryService;
    private String status;

    public Parcel(String trackingNumber, String receiverName, double weight, DeliveryServiceStrategy deliveryService) 
            throws InvalidParcelException {
        setTrackingNumber(trackingNumber);
        setReceiverName(receiverName);
        setWeight(weight);
        this.deliveryService = deliveryService;
        this.status = "Pending";
    }

    public abstract double getChargeableWeight();
    public abstract double getBaseFee();
    public abstract String getParcelType();

    public double calculateDeliveryFee() {
        return deliveryService.calculateFee(getBaseFee());
    }

    private void setTrackingNumber(String trackingNumber) throws InvalidParcelException {
        if (trackingNumber == null || !trackingNumber.matches("^ABC\\d{4}$")) {
            throw new InvalidParcelException("Tracking number must start with 'ABC' followed by exactly 4 digits (e.g., ABC0001).");
        }
        this.trackingNumber = trackingNumber;
    }

    private void setReceiverName(String receiverName) throws InvalidParcelException {
        if (receiverName == null || receiverName.trim().isEmpty()) {
            throw new InvalidParcelException("Receiver name cannot be empty.");
        }
        if (!receiverName.matches("^[a-zA-Z\\s.'\\-]+$")) {
            throw new InvalidParcelException("Receiver name contains invalid characters.");
        }
        this.receiverName = receiverName.trim();
    }

    protected void setWeight(double weight) throws InvalidParcelException {
        if (weight <= 0) {
            throw new InvalidParcelException("Weight must be strictly greater than 0 kg.");
        }
        this.weight = weight;
    }

    public String getTrackingNumber() { return trackingNumber; }
    public String getReceiverName() { return receiverName; }
    public double getWeight() { return weight; }
    public DeliveryServiceStrategy getDeliveryService() { return deliveryService; }
    public void setDeliveryService(DeliveryServiceStrategy deliveryService) { this.deliveryService = deliveryService; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
