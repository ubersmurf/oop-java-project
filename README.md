# ✈️ Airline Reservation and Management System

## 📘 Project Description
This project is an **Airline Reservation and Management System** developed for the  
**BLM2012 – Object Oriented Programming** course (2025–2026 Fall Semester).

The system models a real-world airline operation using **Java**, focusing on object-oriented design, concurrency, and software engineering principles.

---

## 🎯 Objectives
- Apply core **Object-Oriented Programming (OOP)** principles  
- Implement **multithreading and concurrency control**  
- Execute **asynchronous tasks** without blocking the GUI  
- Design a modular and maintainable architecture  
- Verify business logic using **JUnit 5**

---

## 🧩 System Modules
- **Flight Management** ✈️  
  Plane, flight, route, and seat structures  

- **Reservation & Ticketing** 🎫  
  Passenger, reservation, ticket, and baggage handling  

- **Service Layer** ⚙️  
  Seat allocation, flight management, and price calculation  

---

## 🔄 Concurrency Implementation
- **Simultaneous Seat Reservation**  
  Multiple passenger threads attempt to reserve seats, demonstrating synchronized and unsynchronized execution.

- **Asynchronous Report Generation**  
  Long-running report tasks are executed in a separate thread while keeping the GUI responsive.

---

## 🧪 Testing
The project includes **JUnit 5** unit tests for:
- Ticket price calculation 💰  
- Flight search and filtering 🔍  
- Seat availability and exception handling 💺  

---

## 🖥️ Graphical User Interface
The application provides a GUI (JavaFX / Swing) including:
- Login screen  
- Flight search and booking  
- Reservation management  
- Admin / staff management  

---

## 💾 Data Storage
- File-based data persistence 📂  
- No database or XML usage  

---

## ▶️ How to Run
```bash
java -jar <groupNumber>.jar