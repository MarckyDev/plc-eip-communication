/**
    *********************************************************************************
     * TCP/IP PLC Communication Program                                             *               
     * Author: Marc Rovic M. Mapa                                                   *
     * Use TCP/IP to connect to a PLC, send data, and receive a response.           *
     *                                                                              *
     * Use case:                                                                    *                                              
     * Before running, ensure the PLC is powered on and configured to accept TCP/IP *
     * connections.                                                                 *
     *                                                                              *
     * Compile the program:                                                         *
     * javac -d bin src\main\java\com\project_name\App.java                         *
     *                                                                              *
     * Run the program with IP, Port, and Timeout as arguments:                     *
     * java -cp bin com.[project_name].App ip_Address port timeout                  *
     * ******************************************************************************
**/

import java.util.HashMap;
import java.util.Map;
import java.net.Socket;

public class App {

    static void useSocketConnection(Map<String, String> data, String ipAddress, int port, int timeout) {
        SocketConnection socketConnection = new SocketConnection(ipAddress, port, timeout);

        Socket conn = socketConnection.connect();
        socketConnection.sendStringsToPLC(data, conn);
        socketConnection.receiveDataFromPLC(conn);
        socketConnection.disconnect(conn);
    }

    public static void main(String[] args) {
        System.out.println("=== TCP/IP PLC Connection ===");

        Map<String, String> data = new HashMap<>(); // Dummy Data to send
        data.put("Model", "DUMMY-MODEL-12345");
        data.put("Engine", "TEST-ENG");
        data.put("Displacement", "9999");
        data.put("VIN", "DUMMY-VIN-TEST-001");
        data.put("Color", "0000");
        data.put("Trim", "XX00");
        data.put("GVM", "0000");
        data.put("Transmission", "TEST-TRANS");
        data.put("Axle", "T00T");
        data.put("Plant", "T00");
        data.put("Built", "000000");

        String ipAddress;
        int port;
        int timeout;

        if (args.length > 0) {
            System.out.println("================================================================");
            System.out.println("Running...");
            ipAddress = args[0];
            port = Integer.parseInt(args[1]);
            timeout = Integer.parseInt(args[2]);
            useSocketConnection(data, ipAddress, port, timeout);
            return;
        }

        // When no arguments provided, use default test values
        ipAddress = "127.0.0.1";
        port = 9600;
        timeout = 5000;

        useSocketConnection(data, ipAddress, port, timeout);

    }

}
