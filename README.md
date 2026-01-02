# ✈️ YTÜ Airline Reservation System

## 📘 Project Description
This project is a comprehensive **Airline Reservation and Management System** developed for the **BLM2012 – Object Oriented Programming** course (2025–2026 Fall Semester).

The application simulates a real-world airline operation using **Java Swing** for the GUI, **Multithreading** for simulation scenarios, and **File I/O** for data persistence. It allows users to book flights, select seats visually, and enables administrators to manage flight schedules.

---

## 🚀 Quick Start (Login Credentials)
To test the system immediately, use the following credentials:

| Role | Username | Password |
| :--- | :--- | :--- |
| **Admin / Staff** | `admin` | `1234` |
| **Passenger** | *(No login required, enter Name/ID)* | - |

---

## 🎯 Key Features

### 1. 🖥️ Graphical User Interface (Swing)
* **Multi-Tab Login Screen:** Separate access for Passengers, Staff, and Simulation Mode.
* **Visual Seat Selection:** Interactive seat map (Green: Empty, Red: Occupied).
* **Admin Panel:** Add, Delete, and Update flight details (Date/Time).
* **Reservation Management:** View and cancel existing bookings.

### 2. 🔄 Concurrency & Simulation (Scenario 1 & 2)
* **Simulation Mode:** A dedicated screen demonstrating **90 concurrent threads** competing for 180 seats.
    * Includes a "Safe Mode" checkbox to toggle synchronization.
    * Visualizes thread activities in real-time.
* **Asynchronous Reporting:** Background tasks (Scenario 2) run without freezing the UI.

### 3. 💾 Data Persistence
* Uses **Java Serialization** to store objects (`Flight`, `Reservation`, `Passenger`).
* Data is saved locally in the `data/` directory (e.g., `flights.dat`).
* **Note:** The system remembers your changes even after restarting!

---

## 🛠️ Tech Stack
* **Language:** Java (JDK 17+)
* **GUI:** Java Swing (Nimbus LookAndFeel)
* **Testing:** JUnit 5
* **Data:** File-based (.dat files)

---

## ▶️ How to Run

### Option 1: Using the Executable JAR
Ensure the `data` folder is in the same directory as the `.jar` file.

```bash
java -jar <YourGroupNumber>.jar
```

### Option 2: Compiling from Source
If you want to compile the code yourself:

```bash
# Compile (Windows/Linux/Mac)
javac -cp "lib/*" -d bin -sourcepath src src/gui/LoginFrame.java

# Run (Windows uses ';', Mac/Linux uses ':')
# Windows:
java -cp "bin;lib/*" gui.LoginFrame

# Mac/Linux:
java -cp "bin:lib/*" gui.LoginFrame
```

---

## 🧪 Unit Testing
The project includes **5+ JUnit tests** verifying critical business logic:
* ✅ **Price Calculation:** Business vs. Economy pricing logic.
* ✅ **Search Engine:** Filtering by route and date validation.
* ✅ **Seat Manager:** Availability counters and exception handling.

---

## 👥 Contributors
* **Name Surname** (Student ID)
* **Taha** (Student ID)