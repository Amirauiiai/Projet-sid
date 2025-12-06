// CODE BY MEMBER 3 (Node + CLI)
import java.io.*;
import java.net.*;
import java.util.*;

public class Node {
    public static PeerConnection peerConn = new PeerConnection();
    public static HistoryManager history = new HistoryManager("history.txt");

    public static void main(String[] args) {
        int port = 5000; // default
        if (args.length >= 1) {
            try { port = Integer.parseInt(args[0]); } catch (NumberFormatException e) { }
        }

        System.out.println("Starting node on port " + port);

        // start listener thread
        ListenerThread listener = new ListenerThread(port, history, peerConn);
        new Thread(listener).start();

        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Commands: /connect <ip> <port> | /peers | /history | /exit");
            while (true) {
                String line = sc.nextLine();

                if (line.startsWith("/connect")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 3) {
                        String ip = parts[1];
                        int p = Integer.parseInt(parts[2]);
                        peerConn.connectToPeer(ip, p, history);
                    } else {
                        System.out.println("Usage: /connect <ip> <port>");
                    }

                } else if (line.equals("/peers")) {
                    peerConn.printPeers();

                } else if (line.equals("/history")) {
                    List<String> h = history.loadHistory();
                    System.out.println("--- History ---");
                    for (String s : h) System.out.println(s);
                    System.out.println("--- End ---");

                } else if (line.equals("/exit")) {
                    System.out.println("Shutting down...");
                    peerConn.closeAll();
                    System.exit(0);

                } else {
                    // send to peers
                    try {
                        String msg = "[" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "] " + line;
                        peerConn.sendToAll(msg);
                        history.saveSent(msg);
                    } catch (IOException e) {
                        System.err.println("Error sending: " + e.getMessage());
                    }
                }
            }
        }
    }
}
