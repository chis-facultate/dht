import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {

    @Override
    public void run() {

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(Config.serverPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Mesaj thread server: server pornit");

            do {
                try {
                    Socket socketPredecesor = serverSocket.accept();
                    DataInputStream inPredecesor = new DataInputStream(socketPredecesor.getInputStream());

                    //datele primite de la predecesor
                    String line = inPredecesor.readUTF();

                    //le afisam in consola
                    System.out.println("Mesaj primit: " + line);

                    if (line.equals("bye")) {
                        break;
                    }

                    String[] splitString = line.split(" ");
                    String comanda = splitString[0];

                    switch (comanda) {
                        case "cauta":
                            String cheie = splitString[1];
                            String idQueryingNode = splitString[2];

                            if (idQueryingNode.equals(String.valueOf(Config.id))) {
                                // daca nu s-a gasit valoarea cheii in niciun nod
                                // se face o cautare locala
                                // acesta poate fi cazul nodului care inchide inelul
                                String valoare = Config.dictionar.get(cheie);
                                if (valoare == null) {
                                    System.out.println("Nu exista valoare pentru cheie.");
                                } else {
                                    System.out.println("Valoare: " + valoare + ", nod: " + idQueryingNode);
                                }
                            } else {
                                // compara hash-ul cu id-ul propriu
                                int hash = Main.hashFunction(cheie, Config.numarPeers);
                                if (hash <= Config.id) {
                                    //daca hash <= id atunci valoarea cheii ar putea fi pe acest nod
                                    //dar nu e obligatoriu (exemplu id = 3, hash = 1, dar exista nodul cu id = 2)
                                    String valoare = Config.dictionar.get(cheie);
                                    if (valoare == null) {
                                        Main.cautaInSuccesor(cheie, idQueryingNode);
                                    } else {
                                        System.out.println("Valoarea cheii " + cheie + " a fost gasita in accest nod: "
                                                + valoare);

                                        line = "redirectioneaza " + valoare + " " + idQueryingNode + " " + Config.id;

                                        Socket socketSuccesor = new Socket(Config.ipSuccesor, Config.portSuccesor);
                                        DataOutputStream outSuccesor = new DataOutputStream(socketSuccesor.getOutputStream());

                                        outSuccesor.writeUTF(line);
                                    }
                                } else {
                                    // daca hash > id atunci valoarea nu poate fi gasita in acest nod
                                    // exceptie nodul are inchide inelul
                                    Main.cautaInSuccesor(cheie, idQueryingNode);
                                }
                            }
                            break;
                        case "redirectioneaza":
                            String idQueryingNode2 = splitString[2];

                            if (idQueryingNode2.equals(String.valueOf(Config.id))) {
                                String valoare = splitString[1];
                                String nodUndeAmGasitValoarea = splitString[3];
                                System.out.println("Valoare: " + valoare + ", nod unde am gasit valoarea: "
                                        + nodUndeAmGasitValoarea);
                            } else {
                                System.out.println("Valoarea gasita intr-un nod predecesor este redirectionata");

                                Socket socketSuccesor = new Socket(Config.ipSuccesor, Config.portSuccesor);
                                DataOutputStream outSuccesor = new DataOutputStream(socketSuccesor.getOutputStream());

                                outSuccesor.writeUTF(line);
                            }
                            break;
                        default:
                            System.out.println("! Comanda gresita");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("IOException Server Thread");
                }
            } while (true);
    }
}
