package daemon;

import application.MyMapReduce;
import formats.Format;
import formats.KVFormat;
import formats.TxtFormat;
import hdfs.HdfsClient;

import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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
        } catch (RemoteException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        HdfsClient.HdfsRead(fname);
        myMapReduce.reduce(reader, writer);
    }

    private static void runMapTaskParallel(MyMapReduce myMapReduce, Format reader, Format writer, CallBack cb) throws RemoteException {
        CallBack callBackStub = (CallBack) UnicastRemoteObject.exportObject(cb,0);
        Registry registry = LocateRegistry.getRegistry("<daemon-host>", 1099);
        for (Socket i : clients) {
            try {
                Daemon daemon = (Daemon) registry.lookup("Datanode");
                daemon.runMap(myMapReduce,reader,writer,callBackStub);
            } catch (NotBoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
