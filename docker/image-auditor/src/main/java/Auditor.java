import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Auditor {
    final static String IPADDRESS = "239.255.22.5";
    final static int UDP_PORT = 9904, TCP_PORT = 2205;
    static long time;
    static Map<String, Instrument> musicians = new HashMap<>();
    static Map<String, Long> times = new HashMap<>();

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(TCP_PORT);
            MulticastSocket udpSocket = new MulticastSocket(UDP_PORT);
            ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor()){
                while(true){
                    time = System.currentTimeMillis();
                    try {
                        Socket socket = serverSocket.accept();
                        AuditorTCP tcp = new AuditorTCP(socket);
                        AuditorUDP udp = new AuditorUDP(udpSocket);
                        exec.execute(tcp);
                        exec.execute(udp);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
    }
}