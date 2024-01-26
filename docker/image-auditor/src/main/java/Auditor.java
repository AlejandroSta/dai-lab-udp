package main.java;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.DatagramPacket;
import java.util.*;

import static java.nio.charset.StandardCharsets.*;


public class Auditor {

    enum Instrument {
        piano, trumpet, flute, violin, drum;
        final String[] sounds = {"ti-ta-ti", "pouet", "trulu", "gzi-gzi", "boum-boum"};
    }

    final static String IPADDRESS = "239.255.22.5";
    final static int PORT = 9904;
    static long time;
    static Map<String, Instrument> musicians = new HashMap<>();
    static Map<String, Long> times = new HashMap<>();

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

                //message format : "[uuid] [sound]"

                String[] components = message.split(" ");

                for(Instrument i : Instrument.values()){
                    if(i.sounds[i.ordinal()].equals(components[1])){
                        musicians.put(components[0], i);
                        times.put(components[0], System.currentTimeMillis());
                    }
                }

                time = System.currentTimeMillis();

                for(int i = 0; i < times.size(); ++i){
                    for(String s : musicians.keySet()){
                        if(times.get(s) > 5 * 1000){
                            musicians.remove(s);
                            times.remove(s);
                        }
                    }
                }
                socket.leaveGroup(group_address, netif);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}