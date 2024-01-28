package daemon;

import application.MyMapReduce;
import formats.Format;
import formats.KVFormat;
import formats.TxtFormat;
import hdfs.ConfigurationFileReader;
import hdfs.HdfsClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.StringConcatFactory;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static hdfs.ServerSocket.clients;

public class JobLauncher {
    public static CountDownLatch countDownLatch = new CountDownLatch(clients.size());

    public static void startJob(MyMapReduce myMapReduce, int fmtTxt, String fname) {
        Format reader = fmtTxt == 1 ? new KVFormat(fname) : new TxtFormat(fname);
        Format writer = fmtTxt == 1 ? new KVFormat(fname) : new TxtFormat(fname);
        CallBackImpl cb = new CallBackImpl();
        try {
            runMapTaskParallel(myMapReduce, reader, writer, cb);
            countDownLatch.await();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        HdfsClient.HdfsRead(fname);
        myMapReduce.reduce(reader, writer);
    }

    private static void runMapTaskParallel(MyMapReduce myMapReduce, Format reader, Format writer, CallBack cb) throws IOException {
        CallBack callBackStub = (CallBack) UnicastRemoteObject.exportObject(cb,0);
        for (Socket machine : clients) {
            try {
                Registry registry = LocateRegistry.getRegistry(String.valueOf(machine.getInetAddress()), 2456);
                Daemon daemon = (Daemon) registry.lookup("DemonImpl");
                Daemon daemonStub = (Daemon) UnicastRemoteObject.exportObject(daemon, 0);
                daemonStub.runMap(myMapReduce, reader, writer, callBackStub);
            } catch (NotBoundException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
