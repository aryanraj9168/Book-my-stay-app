import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CLASS - Service
 * This class represents an optional service that can be added to a confirmed reservation.
 */
class Service {
    private String serviceName;
    private double cost;

    /**
     * Creates a new add-on service.
     * @param serviceName name of the service
     * @param cost cost of the service
     */
    public Service(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    /**
     * @return service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @return service cost
     */
    public double getCost() {
        return cost;
    }
}

/**
 * CLASS - AddOnServiceManager
 * This class manages optional services associated with confirmed reservations.
 */
class AddOnServiceManager {
    // Maps reservation ID to a List of selected services (One-to-Many)
    private Map<String, List<Service>> servicesByReservation;

    /**
     * Initializes the service manager.
     */
    public AddOnServiceManager() {
        this.servicesByReservation = new HashMap<>();
    }

    /**
     * Attaches a service to a reservation.
     * @param reservationId confirmed reservation ID
     * @param service add-on service
     */
    public void addService(String reservationId, Service service) {
        // ComputeIfAbsent is a clean way to ensure the list exists before adding
        servicesByReservation.putIfAbsent(reservationId, new ArrayList<>());
        servicesByReservation.get(reservationId).add(service);
    }

    /**
     * Calculates total add-on cost for a reservation.
     * @param reservationId reservation ID
     * @return total service cost
     */
    public double calculateTotalServiceCost(String reservationId) {
        List<Service> services = servicesByReservation.get(reservationId);
        if (services == null || services.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Service service : services) {
            total += service.getCost();
        }
        return total;
    }
}

/**
 * MAIN CLASS - UseCase7AddOnServiceSelection
 * This class demonstrates how optional services can be attached 
 * to a confirmed booking without modifying core inventory logic.
 */
public class UseCase7AddOnServiceSelection {

    public static void main(String[] args) {
        // Initialize the Manager
        AddOnServiceManager manager = new AddOnServiceManager();

        // Target Reservation ID
        String reservationId = "Single-1";

        // Define available services
        Service breakfast = new Service("Breakfast", 500.0);
        Service spa = new Service("Spa", 1000.0);

        // Map services to the reservation
        manager.addService(reservationId, breakfast);
        manager.addService(reservationId, spa);

        // Output results to match the required console snapshot
        System.out.println("Add-On Service Selection");
        System.out.println("Reservation ID: " + reservationId);
        System.out.println("Total Add-On Cost: " + manager.calculateTotalServiceCost(reservationId));
    }
}
