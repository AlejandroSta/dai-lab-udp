import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AuditorTCP implements Runnable {

    private final Socket socket;
    AuditorTCP(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try(socket;
            var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8))){

            JSONArray a = new JSONArray();
            for(String s : Auditor.musicians.keySet()){
                JSONObject o = new JSONObject();
                o.put("uuid", s);
                o.put("instrument", Auditor.musicians.get(s).name());
                o.put("lastActivity", Auditor.times.get(s));
            }
            out.write(a.toString());
            out.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
