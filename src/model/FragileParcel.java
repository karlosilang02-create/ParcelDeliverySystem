package model;

import exception.InvalidParcelException;
import strategy.DeliveryServiceStrategy;

public class FragileParcel extends BoxParcel {
    private String handlingNote;

    public FragileParcel(String trackingNumber, String receiverName, double weight, 
                         DeliveryServiceStrategy deliveryService, int length, int width, int height, 
                         String handlingNote) throws InvalidParcelException {
        super(trackingNumber, receiverName, weight, deliveryService, length, width, height);
        setHandlingNote(handlingNote);
        validateFragileWeight();
    }

    private void validateFragileWeight() throws InvalidParcelException {
        if (getWeight() > 30.0) {
            throw new InvalidParcelException("Fragile parcels cannot exceed 30 kg.");
        }
    }

    private void setHandlingNote(String handlingNote) throws InvalidParcelException {
        if (handlingNote == null || handlingNote.trim().isEmpty()) {
            throw new InvalidParcelException("Handling note cannot be empty for fragile parcels.");
        }
        this.handlingNote = handlingNote.trim();
    }

    @Override
    public double getBaseFee() {
        return super.getBaseFee() + 100.0;
    }

    @Override
    public String getParcelType() { return "Fragile"; }

    public String getHandlingNote() { return handlingNote; }
}
