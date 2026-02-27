package com.reservation.server;

import com.reservation.entity.Reservation;
import com.reservation.entity.Slot;
import com.reservation.service.ReservationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final ReservationService reservationService;
    private final String clientToken;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, ReservationService reservationService) {
        this.clientSocket = socket;
        this.reservationService = reservationService;
        this.clientToken = "CLIENT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("WELCOME|Conectat cu succes! Token-ul dvs: " + clientToken);
            out.println("HELP|Comenzi disponibile: LIST, RESERVE <id>, MY, CANCEL <id>, EXIT");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("[" + clientToken + "] Comanda primita: " + inputLine);

                String response = processCommand(inputLine.trim());
                out.println(response);

                if (inputLine.trim().equalsIgnoreCase("EXIT")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Eroare la comunicarea cu clientul " + clientToken + ": " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private String processCommand(String command) {
        String[] parts = command.split("\\s+");
        String cmd = parts[0].toUpperCase();

        try {
            switch (cmd) {
                case "LIST":
                    return handleListCommand();

                case "RESERVE":
                    if (parts.length < 2) {
                        return "ERROR|Utilizare: RESERVE <slot_id>";
                    }
                    return handleReserveCommand(parts[1]);

                case "MY":
                    return handleMyCommand();

                case "CANCEL":
                    if (parts.length < 2) {
                        return "ERROR|Utilizare: CANCEL <reservation_id>";
                    }
                    return handleCancelCommand(parts[1]);

                case "EXIT":
                    return "BYE|Deconectare... La revedere!";

                default:
                    return "ERROR|Comanda necunoscuta. Utilizati: LIST, RESERVE, MY, CANCEL, EXIT";
            }
        } catch (Exception e) {
            return "ERROR|Eroare la procesarea comenzii: " + e.getMessage();
        }
    }

    private String handleListCommand() {
        List<Slot> slots = reservationService.getAvailableSlots();

        if (slots.isEmpty()) {
            return "INFO|Nu exista sloturi disponibile momentan.";
        }

        StringBuilder response = new StringBuilder("SLOTS|Sloturi disponibile:\n");
        for (Slot slot : slots) {
            response.append(slot.toString()).append("\n");
        }
        return response.toString();
    }

    private String handleReserveCommand(String slotIdStr) {
        try {
            Long slotId = Long.parseLong(slotIdStr);
            String result = reservationService.createReservation(clientToken, slotId);
            return result.startsWith("SUCCESS") ? "SUCCESS|" + result : "ERROR|" + result;
        } catch (NumberFormatException e) {
            return "ERROR|ID-ul slotului trebuie să fie un numar valid!";
        }
    }

    private String handleMyCommand() {
        List<Reservation> reservations = reservationService.getClientReservations(clientToken);

        if (reservations.isEmpty()) {
            return "INFO|Nu aveti nicio rezervare.";
        }

        StringBuilder response = new StringBuilder("RESERVATIONS|Rezervarile dvs:\n");
        for (Reservation reservation : reservations) {
            response.append(reservation.toString()).append("\n");
        }
        return response.toString();
    }

    private String handleCancelCommand(String reservationIdStr) {
        try {
            Long reservationId = Long.parseLong(reservationIdStr);
            String result = reservationService.cancelReservation(clientToken, reservationId);
            return result.startsWith("SUCCESS") ? "SUCCESS|" + result : "ERROR|" + result;
        } catch (NumberFormatException e) {
            return "ERROR|ID-ul rezervarii trebuie sa fie un numar valid!";
        }
    }

    private void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null) clientSocket.close();
            System.out.println("[" + clientToken + "] Conexiune inchisa.");
        } catch (IOException e) {
            System.err.println("Eroare la inchiderea conexiunii: " + e.getMessage());
        }
    }
}