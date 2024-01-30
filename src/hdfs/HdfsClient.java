package hdfs;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Format;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import formats.FormatReader;
import formats.KV;



public class HdfsClient {
    private static final int SERVER_PORT = 1234; // Port utilisé par les HdfsServer pour des raisons de simplicité tous utilisent le meme
    public static List<String> serverList; // liste des adresses ip des HdfsServer
    //dossier des fichiers distribués lus sur HDFS par le client et stocker en local
    private static final String RECONSTRUCTED_FOLDER = "/home/mahop/Bureau/sujet-hagidoop/reconstruct";
    //dossier des fichiers à distribuer sur HDFS
    private static final String DATA_FOLDER = "/home/mahop/Bureau/sujet-hagidoop/data";


    // Pour Ecrire un fichier stocker sur HDFS comme un ensemble de fragments
    public static void HdfsWrite(int fmt, String fname) {
            getServersAdresses();
            distributeFile(fname);
    }
    // Pour  lire un fichier en reconstituant se fragments sur les serveurs HDFS et le stocker dans le dossier data/reconstruct 
    public static void HdfsRead(String fname) throws IOException {
        // commande de lecture sera lue par le serveurHDFS afin de determiner la nature de l'operation
        String message = "READ " + fname;
        getServersAdresses();

        // Parcourt tous les serveurs du cluster
        for (String serverIP : serverList) {
            try (Socket socket = new Socket(serverIP,SERVER_PORT)) {

                // Crée le dossier "fichiers lu" sur HDFS où ils seront reconstitués s'il n'existe pas déjà
                Path reconstructedFolderPath = Path.of(RECONSTRUCTED_FOLDER);
                if (!Files.exists(reconstructedFolderPath)) {
                    Files.createDirectory(reconstructedFolderPath);
                }

                //streams de lecture et d'ecriture sur la socket partagée avec le serveur
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Envoie le message à chaque serveur du cluster
                out.println(message);

                // Écoute la réponse du serveur
                String response;
                while ((response = in.readLine()) != null) {
                    // la reponse du serveur est ecrite dans le fichier recosnt_fname du dossier data/reconstruct tant que le serveur repond
                    try (FileWriter writer = new FileWriter(reconstructedFolderPath.toString()+"/recosnt_"+fname, true)) {
                        writer.write(response + "\n");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // commande afin de supprimer un fichier stocker dans HDFS
    public static void HdfsDelete(String fname) throws IOException {

        //commande qui specifie au serveurs HDFS qu'il s'agit d'une operation de suppression
        String message = "DELETE " + fname;
        getServersAdresses();

        // Parcourt tous les serveurs du cluster
        for (String serverIP : serverList) {
            try (Socket socket = new Socket(serverIP,SERVER_PORT)) {

                //streams d'ecriture sur la socket partagée avec le serveur
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                // Envoie le message à chaque serveur du cluster la commande DELETE fname
                out.println(message);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    // cette methode permet de recuperer la liste des addresses IP dans le reseau
    public static List<String> getServersAdresses() {
        List<String> ipAddresses = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();

                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
                            && !inetAddress.isMulticastAddress()) {
                        ipAddresses.add(inetAddress.getHostAddress());
                    }
                }
            }
            // on retire l'adresse du switch
            ipAddresses.remove(0);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        HdfsClient.serverList=ipAddresses;
        return ipAddresses;
    }

    // permet de fragmenter le fichier fname et de le repartir sur le reseau
    public  static void distributeFile(String fname) {
        BufferedReader reader = null;
        List<Socket> sockets = new ArrayList<>();

        try {
            // Ouvre une connexion TCP avec chaque serveur
            for (String serverIP : serverList) {
                Socket socket = new Socket(serverIP, SERVER_PORT);
                sockets.add(socket);

            }

            reader = new BufferedReader(new FileReader(DATA_FOLDER+"/"+fname));
            String line;
            int serverIndex = 0;
            int commandIndex = 0;
            //on envoie les ligne une à une à un serveur un après l'autre jusqu'à les envoyer toutes
            while ((line = reader.readLine()) != null) {
                
                Socket socket = sockets.get(serverIndex);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // afin d'envoyer les commandes aux serveurs
                if(commandIndex<serverList.size()){
                    out.println("WRITE " +fname);
                    commandIndex +=1;
                }

                out.println(line);

                // Avance vers le prochain serveur en bouclant sur la liste des serveurs
                serverIndex = (serverIndex + 1) % serverList.size();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Ferme toutes les connexions TCP avec les serveurs
            for (Socket socket : sockets) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args) throws IOException{
       // HdfsWrite(Format.FMT_KV, "");
       HdfsRead("filesample.txt");
    }
}