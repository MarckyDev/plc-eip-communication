/**
    *********************************************************************************
     * TCP/IP PLC Communication Program                                             *               
     * Author: Marc Rovic M. Mapa                                                   *
    *********************************************************************************
**/
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.net.ConnectException;
import java.lang.InterruptedException;
import java.util.Map;
import java.util.LinkedHashMap;

public class SocketConnection {
    private String IP_ADDRESS = "192.168.250.1";
    private int PORT = 44818;
    private int TIMEOUT = 5000; // milliseconds
    private Socket socket;

    public SocketConnection(String ipAddress, int port, int timeout) {
        this.IP_ADDRESS = ipAddress;
        this.PORT = port;
        this.TIMEOUT = timeout;
    }

    public Socket connect() {
        socket = new Socket();
        try {
            System.out.println("Connecting to PLC at " + IP_ADDRESS + ":" + PORT + "...");
            System.out.println("Timeout: " + TIMEOUT + "ms");

            long startTime = System.currentTimeMillis();
            socket.connect(new InetSocketAddress(IP_ADDRESS, PORT), TIMEOUT);
            long connectTime = System.currentTimeMillis() - startTime;

            socket.setSoTimeout(TIMEOUT);

            System.out.println("Connected successfully in " + connectTime + "ms!");
            System.out.println("  Local:  " + socket.getLocalAddress() + ":" + socket.getLocalPort());
            System.out.println("  Remote: " + socket.getInetAddress() + ":" + socket.getPort());

        } catch (UnknownHostException e) {
            System.err.println("\nERROR: Cannot find PLC at IP: " + IP_ADDRESS);
            return socket;

        } catch (ConnectException e) {
            System.err.println("\nERROR: Cannot connect to PLC");
            System.err.println("  - Check PLC is running");
            System.err.println("  - Check IP address is correct");
            System.err.println("  - Check TCP socket is open on PLC");
            return socket;

        } catch (SocketTimeoutException e) {
            System.err.println("\nERROR: Connection timeout after " + TIMEOUT + "ms");
            return socket;

        } catch (IOException e) {
            System.err.println("\nERROR: Communication failed");
            e.printStackTrace();
            return socket;
        }

        return socket;
    }

    public void disconnect(Socket socket) {
        // graceful disconnect
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
                System.out.println("Connection closed.");
            } catch (IOException e) {
                System.err.println("Error closing connection");
            }
        }
    }

    public boolean sendStringsToPLC(Map<String, String> data, Socket connection) {
        System.out.println("\n=== Preparing to Send Data to PLC ===");
        System.out.println("Data to send:");

        int lineNum = 1;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String value = entry.getValue();
            System.out.println("  [" + lineNum + "] " + entry.getKey() + ": \"" + value +
                    "\" (" + value.length() + " chars)");
            lineNum++;
        }
        System.out.println();


        // Check if connection was successful
        if (connection == null || !connection.isConnected()) {
            System.out.println("Conn = " + connection);
            System.err.println("Failed to establish connection. Aborting send operation.");
            return false;
        }

        OutputStream output = null;
        boolean success = false;

        try {
            output = connection.getOutputStream();

            System.out.println("=== Sending Data ===\n");

            lineNum = 0;
            Thread.sleep(1000); //initial delay before sending
            for (Map.Entry<String, String> entry : data.entrySet()) {
                System.out.println("Preparing to send line " + lineNum + "...");
                String value = entry.getValue();

                byte[] byteArr = value.getBytes(StandardCharsets.US_ASCII);
                System.out.println("Bytes: ");
                for (byte b : byteArr) {
                    System.out.print(b + " ");
                }
                output.write(byteArr);

                output.flush();

                System.out.println("Sleeping...");

                System.out.println("[" + lineNum + "/10] Sent: \"" + value + "\" → " + entry.getKey());
                System.out.println("       Length: " + value.length() + " chars");
                
                System.out.println();

                lineNum++;
                Thread.sleep(2000); // Short delay to ensure PLC processes each string
            }
            System.out.println("All data sent successfully!");
            success = true;

        } catch (IOException e) {
            System.err.println("ERROR: Failed to send data");
            e.printStackTrace();
            success = false;

        } catch (InterruptedException e) {
            System.err.println("ERROR: Thread interrupted");
            success = false;

        }

        return success;
    }

    public boolean receiveDataFromPLC(Socket connection) {
        // Placeholder for receiving data logic
        if(connection == null || !connection.isConnected()) {
            System.err.println("ERROR: No active connection to receive data from.");
            return false;
        }
        try{
            InputStream input = connection.getInputStream();
            var inp = input.read();
            System.out.println( "Received byte: " + inp );
        }catch(IOException e){
            System.err.println("ERROR: Failed to receive data");
            e.printStackTrace();
            return false;
        }

        return true;
    }


    // Data mapper
    public static Map<String, String> createVehicleDataMap(
            String model, String engine, String displacement, String vin,
            String color, String trim, String gvm, String transmission,
            String axle, String plant, String builtDate) {

        Map<String, String> data = new LinkedHashMap<>(); // Maintains insertion order
        data.put("EXPECTED_Model", model);
        data.put("EXPECTED_Engine", engine);
        data.put("EXPECTED_Displacement", displacement);
        data.put("EXPECTED_VIN", vin);
        data.put("EXPECTED_Color", color);
        data.put("EXPECTED_Trim", trim);
        data.put("EXPECTED_GVM", gvm);
        data.put("EXPECTED_Transmission", transmission);
        data.put("EXPECTED_Axle", axle);
        data.put("EXPECTED_Plant", plant);
        data.put("EXPECTED_Built", builtDate);

        return data;
    }

}
