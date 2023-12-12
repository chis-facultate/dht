import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        // server
        Thread st = new ServerThread();
        st.start();

        // client
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        do {
            System.out.print("> ");

            String cheie = "";
            try {
                cheie = consoleReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (cheie.equals("bye")) {
                break;
            }

            // calculeaza hash-ul valorii
            int hash = hashFunction(cheie, Config.numarPeers);
            System.out.println("Cheie: " + cheie + ", hash: " + hash);

            cautaInSuccesor(cheie, String.valueOf(Config.id));
        } while (true);

    }

    public static int hashFunction(String cheie, int N) {
        int hash = 0;
        for (int i = 0; i < cheie.length(); i++) {
            // Add the ASCII value of the character to the hash
            hash += cheie.charAt(i);
        }
        return hash % N;
    }

    public static void cautaInSuccesor(String cheie, String idQueryingNode) {
        System.out.println("Se cauta cheia " + cheie + " in alt nod");

        // daca e mai mare atunci pune id propriu in mesaj
        // pentru a sti unde trebuie sa ajunga valoarea cheii
        // gasita in alt nod
        String line = "cauta " + cheie + " " + idQueryingNode;

        // trimite mesaj spre succesor
        try {
            Socket socketSuccesor = new Socket(Config.ipSuccesor, Config.portSuccesor);
            DataOutputStream out = new DataOutputStream(socketSuccesor.getOutputStream());

            System.out.println("Conexiune stabilita cu succesorul " + socketSuccesor.getInetAddress() + ":"
                    + socketSuccesor.getPort());
            out.writeUTF(line);

            out.close();
            socketSuccesor.close();
        } catch (IOException e) {
            System.out.println("Eroare initializare socket succesor");
            throw new RuntimeException(e);
        }
    }
}
