package hdfs;

import config.Project;
import formats.Format;
import formats.KVFormat;
import formats.TxtFormat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HdfsClient {
    static ServerSocket serverSocket;

    static {
        try {
            serverSocket = new ServerSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HdfsClient() throws IOException {
    }

    public static  void HdfsRead(String fname){

    }
    public static  void HdfsWrite(int fmt,String fname) throws Exception {
        Format format;

        if (fmt == Format.FMT_KV) format = new KVFormat(fname);
        else format = new TxtFormat(fname);

        format.open("W");
        ArrayList<String> content = (ArrayList<String>) fragmentFile(fname,ServerSocket.clients.size());
        Iterator <Socket> iterator = ServerSocket.clients.iterator();
        int i=0;
        while (iterator.hasNext()){
            serverSocket.sendData(iterator.next(),content.get(i));
            i++;
        }


    }
    public static List<String> fragmentFile(String filePath, int numDataNode) {
        List<String> fragments = new ArrayList<>();
        String fragment;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder fileContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
                fileContent.append(System.lineSeparator());
            }

            String content = fileContent.toString();
            int fileSize = content.length();
            int fragmentSize = fileSize / numDataNode;

            for (int i = 0; i < numDataNode; i++) {
                int start = i * fragmentSize;
                int end = (i == numDataNode - 1) ? fileSize : start + fragmentSize;
                fragment = content.substring(start, end);
                fragments.add(fragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fragments;
    }
}
