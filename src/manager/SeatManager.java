package manager;

import model.Flight;
import model.Plane;
import model.Seat;
import model.SeatType;

public class SeatManager {

    /**
     * UML [cite: 65] gereği koltuk planını oluşturur.
     * Kapasiteye göre satır sayısını belirler, standart 6 sütun (A-F) kullanır.
     */
    public Seat[][] createSeatPlan(Plane plane) {
        int capacity = plane.getCapacity();
        int cols = 6;
        int rows = (int) Math.ceil((double) capacity / cols);

        Seat[][] seats = new Seat[rows][cols];
        char[] colLabels = {'A', 'B', 'C', 'D', 'E', 'F'};

        int seatsCreated = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (seatsCreated >= capacity) break;

                String seatNum = (i + 1) + "" + colLabels[j];

                // İlk 5 sıra Business, diğerleri Economy olsun
                SeatType type = (i < 5) ? SeatType.BUSINESS : SeatType.ECONOMY;
                double price = (type == SeatType.BUSINESS) ? 2000.0 : 500.0;

                seats[i][j] = new Seat(seatNum, type, price);
                seatsCreated++;
            }
        }

        // Oluşturulan matrisi uçağa ata
        plane.setSeatMatrix(seats);
        return seats;
    }

    public int getEmptySeatCount(Flight flight) {
        return flight.getAvailableSeatCount();
    }

    public boolean bookSeat(Flight flight, int row, int col) {
        Plane plane = flight.getPlane();
        Seat seat = plane.getSeat(row, col);
        // olmayan koltuk için Exception fırlatılıyor 2.b seatmanagertest için
        if (seat == null) {
            throw new IllegalArgumentException("Hata: Böyle bir koltuk numarası yok! (" + row + "," + col + ")");
        }
        if (!seat.isOccupied()) {
            seat.reserve();
            return true;
        }
        return false;
    }

    public Seat findSeatByNumber(Flight flight, String seatNum) {
        Seat[][] matrix = flight.getPlane().getSeatMatrix();
        if (matrix == null) return null;

        for (Seat[] row : matrix) {
            for (Seat seat : row) {
                if (seat != null && seat.getSeatNum().equalsIgnoreCase(seatNum)) {
                    return seat;
                }
            }
        }
        return null;
    }
}