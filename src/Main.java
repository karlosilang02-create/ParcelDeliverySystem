import exception.InvalidParcelException;
import java.util.List;
import java.util.Scanner;
import model.*;
import service.ParcelManager;
import strategy.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ParcelManager manager = ParcelManager.getInstance();

    public static void main(String[] args) {
        while (true) {
            printMenu();
            int choice = readIntInput("Select an option (1-5): ");
            switch (choice) {
                case 1:
                    handleAddParcel();
                    break;
                case 2:
                    handleViewAllParcels();
                    break;
                case 3:
                    handleChangeDeliveryService();
                    break;
                case 4:
                    handleMarkAsDelivered();
                    break;
                case 5:
                    System.out.println("Exiting Parcel Delivery System. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid selection. Please choose an option from 1 to 5.");
                    break;
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=================================");
        System.out.println("    PARCEL DELIVERY SYSTEM       ");
        System.out.println("=================================");
        System.out.println("1. Add Parcel");
        System.out.println("2. View All Parcels");
        System.out.println("3. Change Delivery Service");
        System.out.println("4. Mark as Delivered");
        System.out.println("5. Exit");
        System.out.println("=================================");
    }

    private static void handleAddParcel() {
        System.out.println("\n--- Add Parcel ---");
        System.out.println("1. Document");
        System.out.println("2. Box");
        System.out.println("3. Fragile");
        int typeChoice = readIntInput("Select Parcel Type: ");

        if (typeChoice < 1 || typeChoice > 3) {
            System.out.println("Invalid parcel type selected.");
            return;
        }

        // 1. Tracking Number Loop
        String trackingNumber;
        while (true) {
            trackingNumber = readStringInput("Enter Tracking Number (e.g. ABC0001): ").toUpperCase();
            if (manager.isTrackingNumberExists(trackingNumber)) {
                System.out.println("Error: Tracking number already exists! Please enter a unique tracking number.");
            } else if (!trackingNumber.matches("^ABC\\d{4}$")) {
                System.out.println("Error: Tracking number must start with 'ABC' followed by 4 digits.");
            } else {
                break;
            }
        }

        // 2. Receiver Name Loop
        String receiverName;
        while (true) {
            receiverName = readStringInput("Enter Receiver Name: ");
            if (receiverName.isEmpty()) {
                System.out.println("Error: Receiver name cannot be empty.");
            } else if (!receiverName.matches("^[a-zA-Z\\s.'\\-]+$")) {
                System.out.println("Error: Receiver name can only contain letters, spaces, dots, hyphens, and apostrophes.");
            } else {
                break;
            }
        }

        // 3. Weight Loop per Type Limit
        double maxAllowedWeight = (typeChoice == 1) ? 0.5 : (typeChoice == 2) ? 50.0 : 30.0;
        double weight;
        while (true) {
            weight = readDoubleInput("Enter Weight in kg: ");
            if (weight <= 0) {
                System.out.println("Error: Weight must be strictly greater than 0 kg.");
            } else if (weight > maxAllowedWeight) {
                System.out.printf("Error: Weight for this parcel type cannot exceed %.1f kg! Please re-enter weight.%n", maxAllowedWeight);
            } else {
                break;
            }
        }

        // 4. Type-Specific Details Loop
        Parcel tempParcel = null;
        DeliveryServiceStrategy dummyStrategy = new StandardDelivery();

        if (typeChoice == 1) { // Document
            while (true) {
                int pages = readIntInput("Enter Number of Pages (1-100): ");
                try {
                    tempParcel = new DocumentParcel(trackingNumber, receiverName, weight, dummyStrategy, pages);
                    break;
                } catch (InvalidParcelException e) {
                    System.out.println("Error: " + e.getMessage() + " Please try again.");
                }
            }
        } else { // Box or Fragile
            int length, width, height;
            while (true) {
                length = readIntInput("Enter Length (cm): ");
                width = readIntInput("Enter Width (cm): ");
                height = readIntInput("Enter Height (cm): ");
                if (length <= 0 || width <= 0 || height <= 0) {
                    System.out.println("Error: Box dimensions must be greater than 0 cm! Please re-enter dimensions.");
                } else if (length > 300 || width > 300 || height > 300) {
                    System.out.println("Error: Box dimensions cannot exceed 300 cm! Please re-enter dimensions.");
                } else {
                    break;
                }
            }

            if (typeChoice == 2) { // Box
                try {
                    tempParcel = new BoxParcel(trackingNumber, receiverName, weight, dummyStrategy, length, width, height);
                } catch (InvalidParcelException e) {
                    System.out.println("Error: " + e.getMessage());
                    return;
                }
            } else { // Fragile
                while (true) {
                    String note = readStringInput("Enter Handling Note: ");
                    try {
                        tempParcel = new FragileParcel(trackingNumber, receiverName, weight, dummyStrategy, length, width, height, note);
                        break;
                    } catch (InvalidParcelException e) {
                        System.out.println("Error: " + e.getMessage() + " Please try again.");
                    }
                }
            }
        }

        // 5. Select Delivery Service Strategy Loop
        DeliveryServiceStrategy service = selectDeliveryService(tempParcel.getChargeableWeight());
        tempParcel.setDeliveryService(service);

        try {
            manager.addParcel(tempParcel);
            System.out.printf("Parcel added successfully! Delivery fee: Php %.2f%n", tempParcel.calculateDeliveryFee());
        } catch (InvalidParcelException e) {
            System.out.println("Error adding parcel: " + e.getMessage());
        }
    }

    private static DeliveryServiceStrategy selectDeliveryService(double chargeableWeight) {
        while (true) {
            System.out.println("Select Delivery Service:");
            System.out.println("1. Standard");
            System.out.println("2. Express");
            System.out.println("3. Same Day");
            int choice = readIntInput("Choice: ");

            DeliveryServiceStrategy strategy = null;
            switch (choice) {
                case 1:
                    strategy = new StandardDelivery();
                    break;
                case 2:
                    strategy = new ExpressDelivery();
                    break;
                case 3:
                    strategy = new SameDayDelivery();
                    break;
                default:
                    strategy = null;
                    break;
            }

            if (strategy == null) {
                System.out.println("Invalid choice. Please select 1, 2, or 3.");
                continue;
            }

            if (!strategy.isAllowed(chargeableWeight)) {
                System.out.println("Same Day delivery is only for parcels up to 10 kg!");
            } else {
                return strategy;
            }
        }
    }

    private static void handleViewAllParcels() {
        List<Parcel> parcels = manager.getAllParcels();
        if (parcels.isEmpty()) {
            System.out.println("\nNo parcels registered in the system.");
            return;
        }

        System.out.println("\n===============================================================================================");
        System.out.printf("%-12s | %-10s | %-20s | %-15s | %-10s | %-12s | %-10s%n",
                "Tracking No", "Type", "Receiver", "Chargeable Wt", "Service", "Fee (Php)", "Status");
        System.out.println("===============================================================================================");

        for (Parcel p : parcels) {
            System.out.printf("%-12s | %-10s | %-20s | %-15s | %-10s | %-12.2f | %-10s%n",
                    p.getTrackingNumber(),
                    p.getParcelType(),
                    truncate(p.getReceiverName(), 20),
                    String.format("%.1f kg", p.getChargeableWeight()),
                    p.getDeliveryService().getServiceName(),
                    p.calculateDeliveryFee(),
                    p.getStatus());
        }
        System.out.println("===============================================================================================");
    }

    private static void handleChangeDeliveryService() {
        System.out.println("\n--- Change Delivery Service ---");
        String tracking;
        Parcel parcel = null;

        while (true) {
            tracking = readStringInput("Enter Tracking Number: ").toUpperCase();
            if (tracking.equalsIgnoreCase("CANCEL")) {
                return;
            }
            if (!tracking.matches("^ABC\\d{4}$")) {
                System.out.println("Invalid tracking number format! Format must be ABC followed by 4 digits (e.g., ABC0001). Type 'CANCEL' to go back.");
                continue;
            }

            parcel = manager.findParcel(tracking);
            if (parcel == null) {
                System.out.println("Parcel not found! Please enter a valid tracking number or type 'CANCEL' to go back.");
            } else {
                break;
            }
        }

        if ("Delivered".equalsIgnoreCase(parcel.getStatus())) {
            System.out.println("Parcel already delivered!");
            return;
        }

        System.out.println("Current Delivery Service: " + parcel.getDeliveryService().getServiceName());
        DeliveryServiceStrategy newService = selectDeliveryService(parcel.getChargeableWeight());
        
        parcel.setDeliveryService(newService);
        System.out.printf("Delivery service changed! New fee: Php %.2f%n", parcel.calculateDeliveryFee());
    }

    private static void handleMarkAsDelivered() {
        System.out.println("\n--- Mark as Delivered ---");
        String tracking;
        Parcel parcel = null;

        while (true) {
            tracking = readStringInput("Enter Tracking Number: ").toUpperCase();
            if (tracking.equalsIgnoreCase("CANCEL")) {
                return;
            }
            if (!tracking.matches("^ABC\\d{4}$")) {
                System.out.println("Invalid tracking number format! Format must be ABC followed by 4 digits (e.g., ABC0001). Type 'CANCEL' to go back.");
                continue;
            }

            parcel = manager.findParcel(tracking);
            if (parcel == null) {
                System.out.println("Parcel not found! Please enter a valid tracking number or type 'CANCEL' to go back.");
            } else {
                break;
            }
        }

        if ("Delivered".equalsIgnoreCase(parcel.getStatus())) {
            System.out.println("Parcel was already delivered.");
        } else {
            parcel.setStatus("Delivered");
            System.out.println("Parcel delivered successfully!");
        }
    }

    private static String readStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid whole number.");
            }
        }
    }

    private static double readDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid decimal number.");
            }
        }
    }

    private static String truncate(String str, int width) {
        if (str.length() > width) {
            return str.substring(0, width - 3) + "...";
        }
        return str;
    }
}