// CODE BY MEMBER 1 (Listener + incoming handlers)
import java.io.*;
import java.net.*;

public class ListenerThread implements Runnable {
    private int port;
    private HistoryManager history;
    private PeerConnection peerConn;

    public ListenerThread(int port, HistoryManager history, PeerConnection peerConn) {
        this.port = port;
        this.history = history;
        this.peerConn = peerConn;
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Listener started on port " + port);

            while (true) {
                Socket client = server.accept();
                peerConn.addSocket(client);
                new Thread(new IncomingHandler(client, history)).start();
            }

        } catch (IOException e) {
            System.err.println("Listener error: " + e.getMessage());
        }
    }

    static class IncomingHandler implements Runnable {
        private Socket socket;
        private HistoryManager history;

        public IncomingHandler(Socket socket, HistoryManager history) {
            this.socket = socket;
            this.history = history;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("[RECV] " + line);
                    history.saveReceived(line);
                }
            } catch (IOException e) {
                System.err.println("Incoming handler error: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }
    }
}