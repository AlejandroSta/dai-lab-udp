import java.io.IOException;
import java.net.*;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static java.nio.charset.StandardCharsets.*;


public class Musician {

    final static String IPADDRESS = "239.255.22.5";
    final static int PORT = 9904;

    static final UUID uuid = UUID.randomUUID();


    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {

            String message = uuid + " " + Instrument.piano.sound;
            while (true) {
                byte[] payload = message.getBytes(UTF_8);
                InetSocketAddress dest_address = new InetSocketAddress(IPADDRESS, PORT);
                var packet = new DatagramPacket(payload, payload.length, dest_address);
                socket.send(packet);
                System.out.println(message);
                try {
                    sleep(1000);
                } catch (InterruptedException ignored) { //It's the main thread so should never throw
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    enum Instrument {
        piano("ti-ta-ti"), trumpet("pouet"), flute("trulu"), violin( "gzi-gzi"), drum("boum-boum");

        final String sound;

        Instrument(String sound) {
            this.sound = sound;
        }
    }
}