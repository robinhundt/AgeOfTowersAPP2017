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
 * Class that is used to manage creation and getting of local and remote RMI's respectively. Methods offer and
 * find can be used to either offer a Player object on a local RMI or get a reference to a remote Player from a RMI
 * at host:port .
 * Default port is 1099 per RMI specifications.
 * @author Alexander Wähling
 */
public class Remote {
    /**
     * Default port to offer and look for players on. Conforms to the standard RMI port.
     */
    public static final int DEFAULT_PORT = 1099;

    /**
     * Registry to either offer a player on or get a remote player from.
     */
    private Registry registry;
    /**
     * ip-address or resolvable name of Registry to connect with
     */
    private final String host;
    /**
     * Either player is offered on local RMI with this port, or a player is looked for on remote RMI on this port.
     */
    private final int port;

    /**
     * Setup local RMI with specified port. If there is already a RMI instance on that port running, it's used instead
     * of creating a new RMI registry.
     * @param port Port to look on
     */
    public Remote(int port) {
        this.port = port;
        host = "localhost";

        try {
            // try to create a new Registry on the specified port
            registry = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            try {
                // if creation of Registry on specified port was unsuccessful, it was probably due to another registry
                // already running on this port. In this case try to get a reference to the registry on this port.
                registry = LocateRegistry.getRegistry(port);
            } catch (RemoteException re) {
                // if create and get are both unsuccessful, a registry just can't be run on this port and the program
                // should stop
                System.out.println("Couldn't create nor get registry on port: " + port);
                System.exit(1);
            }
        }
    }

    /**
     * Setup local RMI with default port 1099. If there is already a RMI instance on that port running, it's used instead
     * of creating a new RMI registry.
     */
    public Remote() {
        this(DEFAULT_PORT);
    }

    /**
     * Get a reference to a remote RMI on the specified host and port.
     * @param host host to look for an RMI at
     * @param port port to look on
     */
    public Remote(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            // try to get a reference to a remote Registry on host:port
            registry = LocateRegistry.getRegistry(host, port);
        } catch (RemoteException e) {
            System.out.println("Unable to get registry from " + host + " on port " + port);
        }
    }

    /**
     * Get a reference to a remote RMI on the specified host and and {@link #DEFAULT_PORT} 1099.
     * @param host host to look for an RMI at
     */
    public Remote(String host) {
        this(host, DEFAULT_PORT);
    }

    /**
     * Offer a Remote Player object with the passed name on {@link #registry} obtained on object construction
     * @param netPlayer Player object to offer
     * @param name name to offer netplayer under
     */
    public void offer(Player netPlayer, String name) {
        try {
            // try to bind the Player object to the current registry. Use rebind() in case a remote Object with that
            // name is already registered with this registry
            registry.rebind(name, netPlayer);
            System.out.println("Offering player " + name + " on port " + port);
        } catch (RemoteException e) {
            System.out.println("Unable to offer " + name + " on port " + port);
            System.exit(1);
        }
    }

    /**
     * Look for a remote Player with the specified name on the {@link #registry} obtained at object construction.
     * @param name name to look for
     * @return remote reference to a Player object
     */
    public Player find(String name) {
        Player player = null;
        try {
            // get a remote reference to a Player with the passed name on the current Registry
            player = (Player) registry.lookup(name);
        } catch (RemoteException | NotBoundException e) {
            System.out.println("Couldn't find player " + name + " on " + host + ":" + port);
            System.exit(1);
        }
        return player;
    }

    /**
     * Looks for Remote objects bound to {@link #registry} obtained at object construction. If there is only one, it is automatically selected
     * and a Player reference to this object returned.
     * If more than one Remote object is bound to the Registry, all bound Objects are listed and the player is interactively
     * asked via commandline which player he wishes to play against.
     * @return a reference to a remote Player object
     */
    public Player find() {
        String[] names = null;
        Player player = null;

        try {
            // get all bound Remote objects on current registry
            names = registry.list();
        } catch (RemoteException e) {
            System.out.println("Couldn't find a player on " + host + ":" + port);
            System.exit(1);
        }

        if(names.length > 1) {
            // if there is more than one Player available, list the names of all
            System.out.println("Available players on " + host + ":" + port + "  :");
            for(String name : names) {
                System.out.println(name);
            }
            try {
                // try to get a remote reference to the Player with the name that is returned from user
                // by calling the helper method askForName() that will only return a name contained in the names array
                player = (Player) registry.lookup(askForName(Arrays.asList(names)));
            } catch (RemoteException | NotBoundException e) {
                System.out.println(e);
                System.exit(1);
            }
        } else if(names.length == 1) {
            // in case only one Remote object is bound to the registry, try to get a remote reference to it
            System.out.println("Found player " + names[0] + " on " + host + ":" + port );
            try {
                player = (Player) registry.lookup(names[0]);
            } catch (RemoteException | NotBoundException e) {
                System.out.println(e);
                System.exit(1);
            }
        } else {
            System.out.println("Couldn't find a player on " + host + ":" + port);
            System.exit(1);
        }

        return player;
    }

    /**
     * Helper method that uses a Scanner to interactively ask the user for a name contained in the passed List of names.
     * If the entered name is not present in the List, the user will be notified and prompted to enter the name again, until
     * a correct name is entered.
     * @param names list of names of possible Remote Players
     * @return name that matches the name of one of the players passed in the List names
     */
    private String askForName(List<String> names) {
        System.out.println("Please enter name of player you wish to play exactly as listed.");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.next();
        while (!names.contains(name)) {
            System.out.println("Entered Player name is not available. Please check the spellling and reenter the name:");
            name = scanner.next();
        }
        return name;
    }


}
