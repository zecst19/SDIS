package app;

import java.rmi.Remote;
import java.rmi.RemoteException;

public class AppInterface extends Remote{

    public String backup(String file, int rep_degree) throws RemoteException{
    	//fazer o backup, se funcionar retornar uma msg a dizer que o ficheiro file foi backed up com sucesso, se nao der msg a dizer que nao deu
    }

    public String backupenh(String file, int rep_degree) throws RemoteException{
		//o mesmo que o primeiro
    }

    public String restore(String file) throws RemoteException{
		//o mesmo que o primeiro
    }

    public String restoreenh(String file) throws RemoteException{
		//o mesmo que o primeiro
    }

    public String delete(String file) throws RemoteException{
		//o mesmo que o primeiro
    }

    public String deleteenh(String file) throws RemoteException{
		//o mesmo que o primeiro
    }

    public String reclaim(long space) throws RemoteException{
		//o mesmo que o primeiro
    }

    public String reclaimenh(long space) throws RemoteException{
		//o mesmo que o primeiro
    }
}
