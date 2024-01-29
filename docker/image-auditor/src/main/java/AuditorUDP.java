import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AuditorUDP implements Runnable {

    private final MulticastSocket socket;

    AuditorUDP(MulticastSocket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try (socket) {
            InetSocketAddress group_address = new InetSocketAddress(Auditor.IPADDRESS, Auditor.UDP_PORT);
            NetworkInterface netif = NetworkInterface.getByName("eth0");
            socket.joinGroup(group_address, netif);

            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength(), UTF_8);

            //message format : "[uuid] [sound]"

            String[] components = message.split(" ");

            for(Instrument i : Instrument.values()){
                if(i.sounds[i.ordinal()].equals(components[1])){
                    Auditor.musicians.put(components[0], i);
                    Auditor.times.put(components[0], System.currentTimeMillis());
                }
            }

            for(int i = 0; i < Auditor.times.size(); ++i){
                for(String s : Auditor.musicians.keySet()){
                    if(Auditor.times.get(s) - Auditor.time > 5 * 1000){
                        Auditor.musicians.remove(s);
                        Auditor.times.remove(s);
                    }
                }
            }
            socket.leaveGroup(group_address, netif);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
