package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Flight implements Serializable {
    private String flightNum;
    private String departurePlace;
    private String arrivalPlace;
    private Date date; // Tarih
    private Date hour; // Saat (Sadece zaman bilgisini tutacak)
    private int duration; // Dakika cinsinden
    private Plane plane;

    public Flight(String flightNum, String dep, String arr, Date date, Date hour, int duration, Plane plane) {
        this.flightNum = flightNum;
        this.departurePlace = dep;
        this.arrivalPlace = arr;
        this.date = date;
        this.hour = hour;
        this.duration = duration;
        this.plane = plane;
    }

    /**
     * Uçuş tarihinin geçip geçmediğini Calendar kullanarak kontrol eder.
     */
    public boolean isDatePassed() {
        // Şu anki zaman
        Calendar now = Calendar.getInstance();

        // Uçuş zamanını oluştur (Date ve Hour birleştirilerek)
        Calendar flightTime = Calendar.getInstance();
        flightTime.setTime(this.date); // Önce günü ayarla

        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(this.hour); // Saat objesinden saati al

        flightTime.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        flightTime.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));

        return flightTime.before(now);
    }

    public int getAvailableSeatCount() {
        if (plane == null || plane.getSeatMatrix() == null) return 0;

        int emptyCount = 0;
        Seat[][] seats = plane.getSeatMatrix();
        for (Seat[] row : seats) {
            for (Seat seat : row) {
                if (seat != null && !seat.isOccupied()) {
                    emptyCount++;
                }
            }
        }
        return emptyCount;
    }

    // Getterlar (Manager işlemleri için gerekli)
    public String getFlightNum() { return flightNum; }
    public String getDeparturePlace() { return departurePlace; }
    public String getArrivalPlace() { return arrivalPlace; }
    public Date getDate() { return date; }
    public Plane getPlane() { return plane; }

    //update kullanımı için
    public void setDate(Date date) {
        this.date = date;
    }
    public void setHour(Date hour) {
        this.hour = hour;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public void setPlane(Plane plane) {
        this.plane = plane;
    }
    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }

    //

    @Override
    public String toString() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        return "Uçuş: " + flightNum + " | " + departurePlace + " -> " + arrivalPlace +
                " | Tarih: " + sdfDate.format(date) + " " + sdfTime.format(hour);
    }
}