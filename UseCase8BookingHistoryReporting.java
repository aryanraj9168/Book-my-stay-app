import java.util.ArrayList;
import java.util.List;

/**
 * REPRESENTATION CLASS - Reservation
 * (Simplified for Use Case 8 context)
 */
class Reservation {
    private String reservationId;
    private String guestName;

    public Reservation(String reservationId, String guestName) {
        this.reservationId = reservationId;
        this.guestName = guestName;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }

    @Override
    public String toString() {
        return "Reservation [ID: " + reservationId + ", Guest: " + guestName + "]";
    }
}

/**
 * CLASS - BookingHistory
 * This class maintains an ordered record of confirmed reservations.
 */
class BookingHistory {
    /**
     * List that stores confirmed reservations.
     */
    private List<Reservation> confirmedReservations;

    /**
     * Initializes an empty booking history.
     */
    public BookingHistory() {
        this.confirmedReservations = new ArrayList<>();
    }

    /**
     * Adds a confirmed reservation to booking history.
     * @param reservation confirmed booking
     */
    public void addReservation(Reservation reservation) {
        confirmedReservations.add(reservation);
    }

    /**
     * Returns all confirmed reservations.
     * @return list of reservations
     */
    public List<Reservation> getConfirmedReservations() {
        return confirmedReservations;
    }
}

/**
 * CLASS - BookingReportService
 * This class generates reports from booking history data.
 * Reporting logic is separated from data storage.
 */
class BookingReportService {
    /**
     * Displays a summary report of all confirmed bookings.
     * @param history booking history
     */
    public void generateReport(BookingHistory history) {
        List<Reservation> reservations = history.getConfirmedReservations();
        
        System.out.println("--- Booking History Report ---");
        if (reservations.isEmpty()) {
            System.out.println("No confirmed bookings found.");
        } else {
            for (Reservation res : reservations) {
                System.out.println(res.toString());
            }
        }
        System.out.println("Total Bookings: " + reservations.size());
        System.out.println("------------------------------");
    }
}

/**
 * MAIN CLASS - UseCase8BookingHistoryReporting
 * Demonstrates historical tracking and reporting of bookings.
 */
public class UseCase8BookingHistoryReporting {

    public static void main(String[] args) {
        // 1. Initialize History and Reporting Service
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();

        // 2. Simulate bookings being confirmed
        Reservation res1 = new Reservation("Single-1", "Alice");
        Reservation res2 = new Reservation("Double-2", "Bob");
        Reservation res3 = new Reservation("Suite-3", "Charlie");

        // 3. Add to History (Maintains insertion order)
        history.addReservation(res1);
        history.addReservation(res2);
        history.addReservation(res3);

        // 4. Generate Report
        System.out.println("Use Case 8: Booking History & Reporting");
        reportService.generateReport(history);
    }
}
