package hdfs;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HdfsServer extends Remote {
    void hdfsDelete(String fname) throws RemoteException;
    void hdfsWrite(int fmt, String fname) throws RemoteException;
    void hdfsRead(String fname) throws RemoteException;
}