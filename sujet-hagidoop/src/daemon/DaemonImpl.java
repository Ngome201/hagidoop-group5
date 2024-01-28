package daemon;

import formats.Format;
import map.Map;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class DaemonImpl extends UnicastRemoteObject implements Daemon {

    public DaemonImpl() throws RemoteException {
        super();
    }

    @Override
    public void runMap(Map m, Format reader, Format writer, CallBack cb) throws RemoteException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                m.map(reader, writer);
            }
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cb.completed();
    }
}

