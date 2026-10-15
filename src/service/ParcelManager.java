package service;

import exception.InvalidParcelException;
import model.Parcel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParcelManager {
    private static ParcelManager instance;
    private final List<Parcel> parcels;

    private ParcelManager() {
        parcels = new ArrayList<>();
    }

    public static synchronized ParcelManager getInstance() {
        if (instance == null) {
            instance = new ParcelManager();
        }
        return instance;
    }

    public void addParcel(Parcel parcel) throws InvalidParcelException {
        if (isTrackingNumberExists(parcel.getTrackingNumber())) {
            throw new InvalidParcelException("Duplicate Tracking Number: " + parcel.getTrackingNumber());
        }
        parcels.add(parcel);
    }

    public boolean isTrackingNumberExists(String trackingNumber) {
        return findParcel(trackingNumber) != null;
    }

    public Parcel findParcel(String trackingNumber) {
        for (Parcel p : parcels) {
            if (p.getTrackingNumber().equalsIgnoreCase(trackingNumber)) {
                return p;
            }
        }
        return null;
    }

    public List<Parcel> getAllParcels() {
        return Collections.unmodifiableList(parcels);
    }
}
