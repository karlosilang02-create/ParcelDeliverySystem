package model;

import exception.InvalidParcelException;
import strategy.DeliveryServiceStrategy;

public class DocumentParcel extends Parcel {
    private int numberOfPages;

    public DocumentParcel(String trackingNumber, String receiverName, double weight, 
                          DeliveryServiceStrategy deliveryService, int numberOfPages) throws InvalidParcelException {
        super(trackingNumber, receiverName, weight, deliveryService);
        setNumberOfPages(numberOfPages);
        validateDocumentWeight();
    }

    private void validateDocumentWeight() throws InvalidParcelException {
        if (getWeight() > 0.5) {
            throw new InvalidParcelException("Document parcels cannot exceed 0.5 kg.");
        }
    }

    private void setNumberOfPages(int numberOfPages) throws InvalidParcelException {
        if (numberOfPages < 1 || numberOfPages > 100) {
            throw new InvalidParcelException("Number of pages must be between 1 and 100.");
        }
        this.numberOfPages = numberOfPages;
    }

    @Override
    public double getChargeableWeight() { return getWeight(); }

    @Override
    public double getBaseFee() { return 50.0; }

    @Override
    public String getParcelType() { return "Document"; }

    public int getNumberOfPages() { return numberOfPages; }
}
