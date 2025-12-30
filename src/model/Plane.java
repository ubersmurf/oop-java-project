package model;

import java.io.Serializable;

public class Plane implements Serializable {
    private String planeID;
    private String planeModel;
    private int capacity;
    private Seat[][] seatMatrix; // Matris: [Satır][Sütun]

    public Plane(String planeID, String planeModel, int capacity) {
        this.planeID = planeID;
        this.planeModel = planeModel;
        this.capacity = capacity;
        // Seat matrisi SeatManager tarafından veya init metodunda doldurulacak
        // Varsayılan olarak 6 sütunlu (A-F) bir düzen varsayıyoruz.
        int rows = (int) Math.ceil((double) capacity / 6);
        this.seatMatrix = new Seat[rows][6];
    }

    public Seat getSeat(int row, int col) {
        if (row >= 0 && row < seatMatrix.length && col >= 0 && col < seatMatrix[0].length) {
            return seatMatrix[row][col];
        }
        return null;
    }

    public void setSeatMatrix(Seat[][] matrix) {
        this.seatMatrix = matrix;
    }

    public Seat[][] getSeatMatrix() {
        return this.seatMatrix;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getPlaneModel() {
        return planeModel;
    }

    public void printSeatPlan() {
        System.out.println("Uçak Planı (" + planeModel + "):");
        for (int i = 0; i < seatMatrix.length; i++) {
            for (int j = 0; j < seatMatrix[i].length; j++) {
                Seat s = seatMatrix[i][j];
                if (s != null) {
                    System.out.print("[" + s.getSeatNum() + (s.isOccupied() ? " DOLU" : " BOŞ ") + "] ");
                }
            }
            System.out.println();
        }
    }
}
