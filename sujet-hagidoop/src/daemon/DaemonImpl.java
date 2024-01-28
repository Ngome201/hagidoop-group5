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
        m.map(reader, writer);
        cb.completed();
    }
}

