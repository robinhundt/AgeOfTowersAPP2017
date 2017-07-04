package towerwarspp.network;


import towerwarspp.preset.Player;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Created by robin on 04.07.17.
 */
public class Remote {

    public static final int DEFAULT_PORT = 1099;

    public static void offer(Player netPlayer, String name, int port) {
        try {

            try {
                LocateRegistry.createRegistry(port);
            } catch (RemoteException e) {
                System.out.println("Registry running on port " + port);
            }

            try {
                Naming.rebind("rmi://:" + port +"/"+name, netPlayer);
                System.out.println("Player " + name + " ready on port: " + port);
            } catch (RemoteException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void offer(Player p, String name) {
        // use default rmiregistry port 1099
        offer(p, name, DEFAULT_PORT);
    }


    public static Player find(String host, String name, int port) {
        Player p = null;
        try {
            p = (Player) Naming.lookup("rmi://" + (host != null ? host : "") + ":" + port + "/" + name);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        if(p == null) {
            System.out.println("player not found");
        }

        return p;
    }

    public static Player find(String host, String name) {
        return find(host, name, DEFAULT_PORT);
    }

    public static Player find(String name, int port) {
        return find(null, name, port);
    }

}
