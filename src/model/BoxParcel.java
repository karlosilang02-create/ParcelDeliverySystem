package model;

import exception.InvalidParcelException;
import strategy.DeliveryServiceStrategy;

public class BoxParcel extends Parcel {
    private int length;
    private int width;
    private int height;

    public BoxParcel(String trackingNumber, String receiverName, double weight, 
                     DeliveryServiceStrategy deliveryService, int length, int width, int height) throws InvalidParcelException {
        super(trackingNumber, receiverName, weight, deliveryService);
        setDimensions(length, width, height);
        validateBoxWeight();
    }

    protected void validateBoxWeight() throws InvalidParcelException {
        if (getWeight() > 50.0) {
            throw new InvalidParcelException("Box parcels cannot exceed 50 kg.");
        }
    }

    private void setDimensions(int length, int width, int height) throws InvalidParcelException {
        if (length <= 0 || width <= 0 || height <= 0) {
            throw new InvalidParcelException("Box dimensions must be strictly greater than 0 cm.");
        }
        if (length > 300 || width > 300 || height > 300) {
            throw new InvalidParcelException("Individual box dimensions cannot exceed 300 cm.");
        }
        this.length = length;
        this.width = width;
        this.height = height;
    }

    public double getVolumetricWeight() {
        return (length * width * height) / 3500.0;
    }

    @Override
    public double getChargeableWeight() {
        double maxWeight = Math.max(getWeight(), getVolumetricWeight());
        return Math.ceil(maxWeight);
    }

    @Override
    public double getBaseFee() {
        return 40.0 * getChargeableWeight();
    }

    @Override
    public String getParcelType() { return "Box"; }

    public int getLength() { return length; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
