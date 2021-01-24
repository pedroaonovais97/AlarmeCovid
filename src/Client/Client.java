package Client;
import Demultiplexer.Demultiplexer;
import Demultiplexer.TaggedConnection;

import java.net.Socket;
public class Client {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 12345);
        Demultiplexer c = new Demultiplexer(new TaggedConnection(s));
        ClientLN.menuPrincipal(s, c);
    }
}
