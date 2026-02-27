package com.reservation.server;

import com.reservation.service.ReservationService;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@QuarkusMain
public class ReservationServer implements QuarkusApplication {

    @Inject
    ReservationService reservationService;

    @ConfigProperty(name = "server.port", defaultValue = "8888")
    int serverPort;

    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private volatile boolean running = true;

    @Override
    public int run(String... args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   SERVER DE REZERVARI - PORNIT        ║");
        System.out.println("╚════════════════════════════════════════╝");

        startServer();

        Quarkus.waitForExit();
        return 0;
    }

    private void startServer() {
        threadPool = Executors.newCachedThreadPool();

        try {
            serverSocket = new ServerSocket(serverPort);
            System.out.println("✓ Serverul asculta pe portul: " + serverPort);
            System.out.println("✓ Asteptare conexiuni clienți...\n");

            Thread acceptThread = new Thread(() -> {
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        String clientAddress = clientSocket.getInetAddress().getHostAddress();
                        System.out.println("→ Client conectat din: " + clientAddress);

                        ClientHandler handler = new ClientHandler(clientSocket, reservationService);
                        threadPool.execute(handler);

                    } catch (IOException e) {
                        if (running) {
                            System.err.println("Eroare la acceptarea conexiunii: " + e.getMessage());
                        }
                    }
                }
            });

            acceptThread.start();

        } catch (IOException e) {
            System.err.println("Eroare la pornirea serverului: " + e.getMessage());
            Quarkus.asyncExit(1);
        }
    }

    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (threadPool != null) {
                threadPool.shutdown();
            }
            System.out.println("\n✓ Server oprit cu succes!");
        } catch (IOException e) {
            System.err.println("Eroare la oprirea serverului: " + e.getMessage());
        }
    }

    public static void main(String... args) {
        Quarkus.run(ReservationServer.class, args);
    }
}