package Server;

import java.net.ServerSocket;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);
        ServerWorker.serverWorkerStart(ss);
    }
}