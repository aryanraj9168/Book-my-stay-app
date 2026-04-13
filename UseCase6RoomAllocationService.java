import java.util.*;

/**
 * CLASS - RoomAllocationService
 * Use Case 6: Reservation Confirmation & Room Allocation
 */
class RoomAllocationService {

    // Stores all allocated room IDs (ensures uniqueness)
    private Set<String> allocatedRoomIds;

    // Stores assigned room IDs grouped by room type
    private Map<String, Set<String>> assignedRoomsByType;

    // Counter to generate unique IDs per room type
    private Map<String, Integer> roomTypeCounters;

    // Constructor
    public RoomAllocationService() {
        allocatedRoomIds = new HashSet<>();
        assignedRoomsByType = new HashMap<>();
        roomTypeCounters = new HashMap<>();
    }

    /**
     * Allocates a room for a reservation
     */
    public void allocateRoom(Reservation reservation, RoomInventory inventory) {

        String roomType = reservation.getRoomType();

        // Check availability
        if (!inventory.isAvailable(roomType)) {
            System.out.println("No rooms available for " + reservation.getGuestName());
            return;
        }

        // Generate unique room ID
        String roomId = generateRoomId(roomType);

        // Ensure no duplicate (extra safety)
        if (allocatedRoomIds.contains(roomId)) {
            System.out.println("Duplicate room detected. Allocation failed.");
            return;
        }

        // Store globally
        allocatedRoomIds.add(roomId);

        // Store by type
        assignedRoomsByType
                .computeIfAbsent(roomType, k -> new HashSet<>())
                .add(roomId);

        // Update inventory immediately
        inventory.decrement(roomType);

        // Confirm booking
        System.out.println("Booking confirmed for Guest: "
                + reservation.getGuestName()
                + ", Room ID: " + roomId);
    }

    /**
     * Generates unique room ID
     */
    private String generateRoomId(String roomType) {
        int count = roomTypeCounters.getOrDefault(roomType, 0) + 1;
        roomTypeCounters.put(roomType, count);
        return roomType + "-" + count;
    }
}

/**
 * Reservation class
 */
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

/**
 * RoomInventory class
 */
class RoomInventory {

    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRooms(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public boolean isAvailable(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrement(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }
}

/**
 * MAIN CLASS - UseCase6RoomAllocation
 */
public class UseCase6RoomAllocationService {

    public static void main(String[] args) {

        System.out.println("Room Allocation Processing\n");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRooms("Single", 2);
        inventory.addRooms("Suite", 1);

        // Booking queue (FIFO)
        Queue<Reservation> bookingQueue = new LinkedList<>();
        bookingQueue.add(new Reservation("Abhi", "Single"));
        bookingQueue.add(new Reservation("Subha", "Single"));
        bookingQueue.add(new Reservation("Vannathi", "Suite"));

        // Allocation service
        RoomAllocationService service = new RoomAllocationService();

        // Process queue
        while (!bookingQueue.isEmpty()) {
            Reservation reservation = bookingQueue.poll();
            service.allocateRoom(reservation, inventory);
        }
    }
}
