import java.util.*;

/**
 * REPRESENTATION CLASS - Reservation
 */
class Reservation {
    private String guestName;
    private String roomType;
    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

/**
 * SHARED RESOURCE - BookingRequestQueue
 */
class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();
    public void addRequest(Reservation res) { queue.add(res); }
    public Reservation getNextRequest() { return queue.poll(); }
    public boolean isEmpty() { return queue.isEmpty(); }
}

/**
 * SHARED RESOURCE - RoomInventory
 */
class RoomInventory {
    private Map<String, Integer> counts = new HashMap<>();
    private Map<String, Integer> allocatedCounts = new HashMap<>();

    public RoomInventory() {
        counts.put("Single", 5);
        counts.put("Double", 3);
        counts.put("Suite", 2);
        allocatedCounts.put("Single", 0);
        allocatedCounts.put("Double", 0);
        allocatedCounts.put("Suite", 0);
    }

    public boolean isAvailable(String type) { return counts.get(type) > 0; }
    
    public void decrement(String type) {
        counts.put(type, counts.get(type) - 1);
        allocatedCounts.put(type, allocatedCounts.get(type) + 1);
    }

    public int getAllocatedCount(String type) { return allocatedCounts.get(type); }
    public int getRemaining(String type) { return counts.get(type); }
}

/**
 * SERVICE - RoomAllocationService
 */
class RoomAllocationService {
    public void allocateRoom(Reservation res, RoomInventory inventory) {
        if (res != null && inventory.isAvailable(res.getRoomType())) {
            inventory.decrement(res.getRoomType());
            System.out.println("Booking confirmed for Guest: " + res.getGuestName() + 
                               ", Room ID: " + res.getRoomType() + "-" + inventory.getAllocatedCount(res.getRoomType()));
        }
    }
}

/**
 * CLASS - ConcurrentBookingProcessor
 * Processes requests from the queue in a thread-safe manner.
 */
class ConcurrentBookingProcessor implements Runnable {
    private BookingRequestQueue bookingQueue;
    private RoomInventory inventory;
    private RoomAllocationService allocationService;

    public ConcurrentBookingProcessor(BookingRequestQueue bookingQueue, RoomInventory inventory, RoomAllocationService allocationService) {
        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
        this.allocationService = allocationService;
    }

    @Override
    public void run() {
        while (true) {
            Reservation reservation = null;

            // Critical Section 1: Synchronize on queue to retrieve request
            synchronized (bookingQueue) {
                if (bookingQueue.isEmpty()) break;
                reservation = bookingQueue.getNextRequest();
            }

            // Critical Section 2: Synchronize on inventory to mutate state atomically
            synchronized (inventory) {
                if (reservation != null) {
                    allocationService.allocateRoom(reservation, inventory);
                }
            }
            
            // Artificial delay to simulate processing time
            try { Thread.sleep(10); } catch (InterruptedException e) { break; }
        }
    }
}

/**
 * MAIN CLASS - UseCase11ConcurrentBookingSimulation
 */
public class UseCase11ConcurrentBookingSimulation {

    public static void main(String[] args) {
        System.out.println("Concurrent Booking Simulation");

        // Initialize shared resources
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();

        // Prepare some requests
        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Double"));
        bookingQueue.addRequest(new Reservation("Kural", "Suite"));
        bookingQueue.addRequest(new Reservation("Subha", "Single"));

        // Create threads (processors)
        Thread t1 = new Thread(new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService));
        Thread t2 = new Thread(new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService));

        // Start threads
        t1.start();
        t2.start();

        try {
            // Wait for both threads to finish
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }

        // Output final state
        System.out.println("\nRemaining Inventory:");
        System.out.println("Single: " + inventory.getRemaining("Single"));
        System.out.println("Double: " + inventory.getRemaining("Double"));
        System.out.println("Suite: " + inventory.getRemaining("Suite"));
    }
}
