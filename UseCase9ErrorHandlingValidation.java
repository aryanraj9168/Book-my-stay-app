import java.util.Scanner;

/**
 * CLASS - InvalidBookingException
 * Custom exception representing domain-specific booking errors.
 */
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

/**
 * MOCK CLASS - RoomInventory 
 * (Represents the centralized inventory from previous use cases)
 */
class RoomInventory {
    public boolean isRoomTypeAvailable(String roomType) {
        // Simplified check: only Single, Double, and Suite are valid (Case Sensitive)
        return roomType.equals("Single") || roomType.equals("Double") || roomType.equals("Suite");
    }
}

/**
 * MOCK CLASS - BookingRequestQueue
 * (Represents the request handler from previous use cases)
 */
class BookingRequestQueue {
    public void addRequest(String guestName, String roomType) {
        System.out.println("Booking request added for: " + guestName);
    }
}

/**
 * CLASS - ReservationValidator
 * Centralizes all validation rules to ensure data consistency.
 */
class ReservationValidator {
    /**
     * Validates booking input provided by the user.
     * @throws InvalidBookingException if validation fails
     */
    public void validate(String guestName, String roomType, RoomInventory inventory) 
            throws InvalidBookingException {
        
        // Rule 1: Guest name cannot be empty
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        // Rule 2: Room type must exist in inventory (Case Sensitive check)
        if (!inventory.isRoomTypeAvailable(roomType)) {
            throw new InvalidBookingException("Invalid room type selected.");
        }
    }
}

/**
 * MAIN CLASS - UseCase9ErrorHandlingValidation
 * Demonstrates Fail-Fast design and graceful error handling.
 */
public class UseCase9ErrorHandlingValidation {

    public static void main(String[] args) {
        // Display application header
        System.out.println("Booking Validation");
        Scanner scanner = new Scanner(System.in);

        // Initialize required components
        RoomInventory inventory = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        try {
            // Collect User Input
            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();

            System.out.print("Enter room type (Single/Double/Suite): ");
            String roomType = scanner.nextLine();

            // Perform Validation (Fail-Fast)
            validator.validate(guestName, roomType, inventory);

            // Process Booking if validation passes
            bookingQueue.addRequest(guestName, roomType);
            System.out.println("Booking successful!");

        } catch (InvalidBookingException e) {
            // Handle domain-specific validation errors gracefully
            System.out.println("Booking failed: " + e.getMessage());
        } catch (Exception e) {
            // Handle unexpected technical errors
            System.out.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            // Always close resources
            scanner.close();
        }
    }
}
