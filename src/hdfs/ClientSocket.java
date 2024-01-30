package hdfs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocket {
    private Socket socket;
    void connectToServer() throws IOException {
        socket = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost",2456);
        socket.connect(inetSocketAddress);
        System.out.println("connected");
    }
}
