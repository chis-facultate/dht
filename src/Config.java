import java.util.HashMap;

public class Config {
    public static int id = 0;
    public static int serverPort = 5000;
    public static String ipSuccesor = "127.0.0.1";
    public static int portSuccesor = 5001;
    public static int numarPeers = 3;
    public static HashMap<String, String> dictionar;

    static {
        dictionar = new HashMap<>();
        dictionar.put("grafica", "curs");
    }
}
