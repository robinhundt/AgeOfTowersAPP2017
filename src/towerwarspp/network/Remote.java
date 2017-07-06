package towerwarspp.network;


import towerwarspp.preset.Player;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by robin on 04.07.17.
 */
public class Remote {

    public static final int DEFAULT_PORT = 1099;

    Registry registry;
    String host;
    int port;

    public Remote(int port) {
        this.port = port;
        host = "localhost";

        try {
            registry = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            try {
                registry = LocateRegistry.getRegistry(port);
            } catch (RemoteException re) {
                System.out.println("Couldn't create o nor get registry on port: " + port);
                System.exit(1);
            }
        }
    }

    public Remote() {
        this(DEFAULT_PORT);
    }

    public Remote(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            registry = LocateRegistry.getRegistry(host, port);
        } catch (RemoteException e) {
            System.out.println("Unable to get registry from " + host + " on port " + port);
        }
    }

    public Remote(String host) {
        this(host, DEFAULT_PORT);
    }

    public void offer(Player netPlayer, String name) {
        try {
            registry.rebind(name, netPlayer);
        } catch (RemoteException e) {
            System.out.println(e);
            System.exit(1);
        }
    }


    public Player find(String name) {
        Player player = null;
        try {
            player = (Player) registry.lookup(name);
        } catch (RemoteException | NotBoundException e) {
            System.out.println("Couldn't find player " + name + " on " + host + ":" + port);
        }
        return player;
    }

    public Player find() {
        String[] names = null;
        Player player = null;

        try {
            names = registry.list();
        } catch (RemoteException e) {
            System.out.println("Couldn't find a player on " + host + ":" + port);
        }

        System.out.println("Available players on " + host + ":" + port + "  :");
        for(String name : names) {
            System.out.println(name);
        }

        try {
            player = (Player) registry.lookup(askForName(Arrays.asList(names)));
        } catch (RemoteException | NotBoundException e) {
            System.out.println(e);
            System.exit(1);
        }
        return player;
    }

    private String askForName(List<String> names) {
        System.out.println("Please enter name of player you wish to play exactly as listed.");
        Scanner scanner = new Scanner(System.in);
        String name;
        do {
            name = scanner.next();
        } while (!names.contains(name));
        return name;
    }


}
