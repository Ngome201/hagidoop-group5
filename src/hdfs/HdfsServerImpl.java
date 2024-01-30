package hdfs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.concurrent.atomic.AtomicInteger;
import java.net.ServerSocket;

public class HdfsServerImpl  {

    // port sur lequel le HDFS Server sera lancé
    private static final int SERVER_PORT = 1234;
    private static final String FRAGMENT_FOLDER = "/home/mahop/Bureau/sujet-hagidoop/fragments";

    public static void main(String[] args) {
        try {


          

            AtomicInteger lineCounter = new AtomicInteger(0);

            try (// Crée un serveur socket pour écouter les connexions entrantes
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
                System.out.println("Server is running and listening on port " + SERVER_PORT);

                // Attend les connexions et traite chaque fragment reçu
                while (true) {
                    Socket clientSocket = serverSocket.accept();


                    // Traite chaque fragment dans un thread séparé
                    Thread thread = new Thread(() -> {
                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);


                            String line;
                            String message = in.readLine();

                            String[] messageParts = message.split(" ", 2); // Sépare la première ligne en deux parties

                            if (messageParts.length >= 2) {
                                String command = messageParts[0]; // Première partie de la première ligne ou commande READ WRITE OU DELETE
                                String filename = messageParts[1]; // Deuxième partie de la première ligne le nom du fichier à traiter
                                Path fragmentFilePath ;
                                Path fragmentFolderPath;
                                switch (command) {
                                    case "READ":

                                        // Crée le dossier "fragments" s'il n'existe pas déjà
                                         fragmentFolderPath = Path.of(FRAGMENT_FOLDER);
                                        if (!Files.exists(fragmentFolderPath)) {
                                            Files.createDirectory(fragmentFolderPath);
                                        }

                                        fragmentFilePath = Path.of(FRAGMENT_FOLDER+"/"+filename);

                
                                            // Envoie le fragment au client
                                            sendFragmentLines(out, fragmentFilePath.toString());
                

                                        break;
                                    case "WRITE":

                                        // Crée le dossier "fragments" s'il n'existe pas déjà
                                         fragmentFolderPath = Path.of(FRAGMENT_FOLDER);
                                        if (!Files.exists(fragmentFolderPath)) {
                                            Files.createDirectory(fragmentFolderPath);
                                        }

                                         //Crée le fichier "frag.txt" s'il n'existe pas déjà
                                        fragmentFilePath = Path.of(FRAGMENT_FOLDER+'/'+filename);
                                        if (Files.notExists(fragmentFilePath)) {
                                            
                                            Files.createFile(fragmentFilePath);
                                        }else{
                                            (new File(fragmentFilePath.toString())).delete();
                                            Files.createFile(fragmentFilePath);

                                        }

                                        while ((line = in.readLine()) != null) {
                                            int lineNumber = lineCounter.incrementAndGet();
                                            System.out.println("Received line " + lineNumber + ": " + line);
        
                                            // Ajoute la ligne au fichier "frag.txt" dans le dossier "fragments"
                                            Files.writeString(fragmentFilePath, line + System.lineSeparator(), StandardOpenOption.APPEND);
                                        }
                                        break;
                                    case "DELETE":
                                        // Crée le dossier "fragments" s'il n'existe pas déjà
                                        fragmentFolderPath = Path.of(FRAGMENT_FOLDER);
                                        if (!Files.exists(fragmentFolderPath)) {
                                            Files.createDirectory(fragmentFolderPath);
                                        }

                                        fragmentFilePath = Path.of(FRAGMENT_FOLDER+"/"+filename);

                
                                        if ((new File(fragmentFilePath.toString())).exists()) {
                                            // Supprimer le fichier iniqué dans la commande du HDFS Client
                                            if ((new File(fragmentFilePath.toString())).delete()) {
                                                System.out.println("File deleted successfully.");
                                            } else {
                                                System.out.println("Failed to delete the file.");
                                            }
                                        } else {
                                            System.out.println("File does not exist.");
                                        }
                                
                
                                    default:
                                        // Commande non prise en charge, renvoie un message d'erreur au client
                                        out.println("Command not supported");
                                        break;
                                }
                            } else {
                                // Message malformaté, renvoie un message d'erreur au client
                                out.println("Malformed message");
                            }



                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    thread.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // methde pour ecrire un fichier fragmenter sur un stream d'ecriture afin de renvoyer les fragments au HDFS Client
    private static void sendFragmentLines(PrintWriter out, String fragmentFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fragmentFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line); // Envoie chaque ligne au client
            }
        }
    }
}