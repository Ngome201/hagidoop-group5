package hdfs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class ServerSocket  {
    private java.net.ServerSocket serverSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Set <Socket> clients = new HashSet<>();

    void initiateSocket() throws IOException {
        serverSocket = new java.net.ServerSocket(2456);
        System.out.println("server running on host : "+serverSocket.getLocalSocketAddress());
        System.out.println("server running on port: "+serverSocket.getLocalPort());
        clients.add(serverSocket.accept());
    }
    void printConnections(){
        for (Socket client : clients) {
            System.out.println("client address : "+client.getInetAddress());
            System.out.println("client port : "+client.getPort());
        }
    }
    SocketAddress getAddress(){
        return serverSocket.getLocalSocketAddress();
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.initiateSocket();
    }
}
