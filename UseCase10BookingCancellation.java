import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * MOCK CLASS - RoomInventory
 * Manages room counts and provides update methods for restoration.
 */
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        // Initializing with 5 rooms as a baseline for the demo
        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 2);
    }

    public void restoreInventory(String roomType) {
        inventory.put(roomType, inventory.get(roomType) + 1);
    }

    public int getAvailableRooms(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }
}

/**
 * CLASS - CancellationService
 * Responsible for handling booking cancellations and inventory rollback.
 */
class CancellationService {
    // Stack to store recently released room/reservation IDs (LIFO order)
    private Stack<String> releasedRoomIds;
    // Map to link reservation ID to the specific room type for rollback
    private Map<String, String> reservationRoomTypeMap;

    /**
     * Initializes cancellation tracking structures.
     */
    public CancellationService() {
        this.releasedRoomIds = new Stack<>();
        this.reservationRoomTypeMap = new HashMap<>();
    }

    /**
     * Registers a confirmed booking to allow for future cancellation.
     * @param reservationId confirmed reservation ID
     * @param roomType allocated room type
     */
    public void registerBooking(String reservationId, String roomType) {
        reservationRoomTypeMap.put(reservationId, roomType);
    }

    /**
     * Cancels a confirmed booking and restores inventory safely.
     * @param reservationId reservation to cancel
     * @param inventory centralized room inventory
     */
    public void cancelBooking(String reservationId, RoomInventory inventory) {
        if (reservationRoomTypeMap.containsKey(reservationId)) {
            String roomType = reservationRoomTypeMap.get(reservationId);
            
            // 1. Increment inventory count
            inventory.restoreInventory(roomType);
            
            // 2. Track in rollback history using Stack
            releasedRoomIds.push(reservationId);
            
            // 3. Remove from active records
            reservationRoomTypeMap.remove(reservationId);
            
            System.out.println("Booking cancelled successfully. Inventory restored for room type: " + roomType);
        } else {
            System.out.println("Cancellation failed: Reservation ID not found.");
        }
    }

    /**
     * Displays recently cancelled reservations.
     * Helps visualize the rollback order (Most Recent First).
     */
    public void showRollbackHistory() {
        System.out.println("\nRollback History (Most Recent First):");
        if (releasedRoomIds.isEmpty()) {
            System.out.println("No cancellations recorded.");
        } else {
            // Stack naturally iterates in LIFO order if we pop/peek
            // Using a loop to show contents
            for (int i = releasedRoomIds.size() - 1; i >= 0; i--) {
                System.out.println("Released Reservation ID: " + releasedRoomIds.get(i));
            }
        }
    }
}

/**
 * MAIN CLASS - UseCase10BookingCancellation
 * Demonstrates safe cancellation and state reversal.
 */
public class UseCase10BookingCancellation {

    public static void main(String[] args) {
        // Initialize Components
        RoomInventory inventory = new RoomInventory();
        CancellationService cancellationService = new CancellationService();

        // 1. Setup: Register an existing booking (Scenario: Single-1 is already booked)
        String resId = "Single-1";
        String type = "Single";
        cancellationService.registerBooking(resId, type);

        System.out.println("Booking Cancellation");
        
        // 2. Perform Cancellation
        cancellationService.cancelBooking(resId, inventory);

        // 3. Display Rollback History
        cancellationService.showRollbackHistory();

        // 4. Show Updated Inventory State
        System.out.println("\nUpdated Single Room Availability: " + inventory.getAvailableRooms("Single"));
    }
}
