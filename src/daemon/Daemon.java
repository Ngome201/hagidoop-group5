package daemon;

import java.rmi.Remote;
import java.rmi.RemoteException;

import formats.Format;
import map.Map;

public interface Daemon extends Remote {
	public void runMap (Map m, Format reader, Format writer, CallBack cb) throws RemoteException;
}
