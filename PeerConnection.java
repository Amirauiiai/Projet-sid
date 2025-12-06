// CODE BY MEMBER 2 (Outgoing connections + send)
import java.io.*;
import java.net.*;
import java.util.*;

public class PeerConnection {
    private final List<Socket> peers = Collections.synchronizedList(new ArrayList<>());

    public void connectToPeer(String ip, int port, HistoryManager history) {
        try {
            Socket s = new Socket(ip, port);
            peers.add(s);
            System.out.println("Connected to " + ip + ":" + port);
            history.saveSystem("Connected to " + ip + ":" + port);

            // Listen to messages coming from this peer
            new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println("[RECV] " + line);
                        history.saveReceived(line);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading from peer: " + e.getMessage());
                }
            }).start();

        } catch (IOException e) {
            System.err.println("Could not connect to peer: " + e.getMessage());
        }
    }

    public void addSocket(Socket s) {
        peers.add(s);
    }

    public void sendToAll(String msg) {
        synchronized (peers) {
            Iterator<Socket> it = peers.iterator();

            while (it.hasNext()) {
                Socket s = it.next();
                try {
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                    out.println(msg);

                } catch (IOException e) {
                    System.err.println("Error sending to peer, removing: " + e.getMessage());
                    try { s.close(); } catch (IOException ignored) {}
                    it.remove();
                }
            }
        }
    }

    public void printPeers() {
        System.out.println("Known peers:");
        synchronized (peers) {
            for (Socket s : peers) {
                System.out.println(" - " + s.getInetAddress() + ":" + s.getPort());
            }
        }
    }

    public void closeAll() {
        synchronized (peers) {
            for (Socket s : peers) {
                try { s.close(); } catch (IOException ignored) {}
            }
            peers.clear();
        }
    }
}