// CODE BY GHOUICI AMIRA
import java.io.*;
import java.util.*;

public class HistoryManager {
    private final File file;

    public HistoryManager(String filename) {
        this.file = new File(filename);
        try { if (!file.exists()) file.createNewFile(); } catch (IOException ignored) {}
    }

    public synchronized void saveSent(String line) {
        appendLine("SENT: " + timestamp() + " " + line);
    }

    public synchronized void saveReceived(String line) {
        appendLine("RECV: " + timestamp() + " " + line);
    }

    public synchronized void saveSystem(String line) {
        appendLine("SYS: " + timestamp() + " " + line);
    }

    private void appendLine(String line) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
            out.println(line);
        } catch (IOException e) {
            System.err.println("History write error: " + e.getMessage());
        }
    }

    public synchronized List<String> loadHistory() {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String l;
            while ((l = br.readLine()) != null)
                lines.add(l);
        } catch (IOException e) {
            System.err.println("History load error: " + e.getMessage());
        }

        return lines;
    }

    private String timestamp() {
        return java.time.LocalDateTime.now().toString();
    }
}
