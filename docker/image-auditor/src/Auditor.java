import java.io.IOException;
import java.net.MulticastSocket;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.*;


public class Auditor {

    enum Musician{
        piano, trumpet, flute, violin, drum;
        private long lastTime;
        static final String[] sounds = {"ti-ta-ti", "pouet", "trulu", "gzi-gzi", "boum-boum"};
        Musician(){

        }
        Musician(long time){
            lastTime = time;
        }
    }

    final static String IPADDRESS = "239.255.22.5";
    final static int PORT = 9904;
    long time;
    List<Musician> musicians = new ArrayList<>();

    public static void main(String[] args) {
        while(true) {
            try (MulticastSocket socket = new MulticastSocket(PORT)) {
                InetSocketAddress group_address = new InetSocketAddress(IPADDRESS, PORT);
                NetworkInterface netif = NetworkInterface.getByName("eth0");
                socket.joinGroup(group_address, netif);

                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength(), UTF_8);

                System.out.println("Received message: " + message + " from " + packet.getAddress() + ", port " + packet.getPort());
                socket.leaveGroup(group_address, netif);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}