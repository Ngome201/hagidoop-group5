package daemon;

import java.rmi.RemoteException;

import static daemon.JobLauncher.countDownLatch;

public class CallBackImpl implements CallBack{
    @Override
    public void completed() throws RemoteException {
        countDownLatch.countDown();
    }
}
