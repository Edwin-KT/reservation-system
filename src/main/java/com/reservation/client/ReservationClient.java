package com.reservation.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ReservationClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;
    private String clientToken;

    public ReservationClient() {
        scanner = new Scanner(System.in);
    }

    public void start() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║   CLIENT REZERVARI - CONECTAT         ║");
            System.out.println("╚════════════════════════════════════════╝");

            String welcomeMsg = in.readLine();
            if (welcomeMsg != null && welcomeMsg.startsWith("WELCOME|")) {
                String message = welcomeMsg.substring(8);
                System.out.println("\n✓ " + message);
                if (message.contains("CLIENT-")) {
                    clientToken = message.substring(message.indexOf("CLIENT-"), message.indexOf("CLIENT-") + 15);
                }
            }

            String helpMsg = in.readLine();
            if (helpMsg != null && helpMsg.startsWith("HELP|")) {
                System.out.println("✓ " + helpMsg.substring(5));
            }

            Thread receiverThread = new Thread(this::receiveMessages);
            receiverThread.start();

            sendCommands();

        } catch (IOException e) {
            System.err.println("Eroare la conectarea la server: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void sendCommands() {
        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("Introduceti comenzi (EXIT pentru iesire):");
        System.out.println("═══════════════════════════════════════════\n");

        while (true) {
            System.out.print(clientToken + " > ");
            String command = scanner.nextLine().trim();

            if (command.isEmpty()) {
                continue;
            }

            out.println(command);

            if (command.equalsIgnoreCase("EXIT")) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                break;
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void receiveMessages() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                processResponse(response);
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                System.err.println("\nConexiune inchisa de server.");
            }
        }
    }

    private void processResponse(String response) {
        String[] parts = response.split("\\|", 2);
        String type = parts[0];
        String message = parts.length > 1 ? parts[1] : "";

        switch (type) {
            case "SUCCESS":
                System.out.println("\n✓ SUCCES: " + message);
                break;

            case "ERROR":
                System.out.println("\n✗ EROARE: " + message);
                break;

            case "INFO":
                System.out.println("\nℹ INFO: " + message);
                break;

            case "SLOTS":
                System.out.println("\n" + "═".repeat(60));
                System.out.println(message);
                System.out.println("═".repeat(60));
                break;

            case "RESERVATIONS":
                System.out.println("\n" + "═".repeat(60));
                System.out.println(message);
                System.out.println("═".repeat(60));
                break;

            case "BYE":
                System.out.println("\n✓ " + message);
                break;

            default:
                System.out.println("\n" + response);
        }
    }

    private void closeConnection() {
        try {
            if (scanner != null) scanner.close();
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            System.out.println("\n✓ Conexiune inchisa.");
        } catch (IOException e) {
            System.err.println("Eroare la inchiderea conexiunii: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ReservationClient client = new ReservationClient();
        client.start();
    }
}