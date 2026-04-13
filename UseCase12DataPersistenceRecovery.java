import java.io.*;
import java.util.*;

/**
 * CLASS - RoomInventory
 * This represents the shared inventory that needs to survive restarts.
 */
class RoomInventory {
    // LinkedHashMap preserves the order of room types for a consistent UI
    private Map<String, Integer> inventory = new LinkedHashMap<>();

    public RoomInventory() {
        // Default initial state
        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 2);
    }

    public Map<String, Integer> getInventoryData() {
        return inventory;
    }

    public void updateRoomCount(String type, int count) {
        inventory.put(type, count);
    }
}

/**
 * CLASS - FilePersistenceService
 * Handles the "Durable Storage" logic using standard Java File I/O.
 */
class FilePersistenceService {

    /**
     * Serializes room inventory state to a plain text file.
     * Format: roomType=availableCount
     */
    public void saveInventory(RoomInventory inventory, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, Integer> entry : inventory.getInventoryData().entrySet()) {
                writer.println(entry.getKey() + "=" + entry.getValue());
            }
            System.out.println("Inventory saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving inventory: " + e.getMessage());
        }
    }

    /**
     * Deserializes room inventory state from a file back into memory.
     */
    public void loadInventory(RoomInventory inventory, String filePath) {
        File file = new File(filePath);
        
        // Fail-safe: If file doesn't exist, don't crash; start fresh.
        if (!file.exists()) {
            System.out.println("No valid inventory data found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String roomType = parts[0];
                    int count = Integer.parseInt(parts[1]);
                    inventory.updateRoomCount(roomType, count);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading inventory file. Using defaults.");
        }
    }
}

/**
 * MAIN CLASS - UseCase12DataPersistenceRecovery
 * Demonstrates state survival across application execution cycles.
 */
public class UseCase12DataPersistenceRecovery {

    public static void main(String[] args) {
        System.out.println("System Recovery");

        // 1. Initialize core objects
        RoomInventory inventory = new RoomInventory();
        FilePersistenceService persistenceService = new FilePersistenceService();
        String storagePath = "inventory_state.txt";

        // 2. Load persisted data (Recovery Phase)
        persistenceService.loadInventory(inventory, storagePath);

        // 3. Display restored inventory
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.getInventoryData().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // 4. Save state for next run (Persistence Phase)
        persistenceService.saveInventory(inventory, storagePath);
    }
}
