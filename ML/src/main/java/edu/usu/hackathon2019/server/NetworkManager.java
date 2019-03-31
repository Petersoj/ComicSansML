package edu.usu.hackathon2019.server;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import edu.usu.hackathon2019.fontclassifier.FontClassifierNetwork;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import org.json.JSONObject;

import javax.imageio.ImageIO;

public class NetworkManager {
    public static final NetworkManager manager = new NetworkManager();
    private final HashMap<Character, FontClassifierNetwork> networks = new HashMap<>();

    private NetworkManager() {
        init();
    }

    private void init() {
        for (char c: NetworkManagerConfig.availableChars) {
            FontClassifierNetwork net = new FontClassifierNetwork();
            String characterPath = ((Character) c).toString();
            if (!characterPath.toUpperCase().equals(characterPath)) {
                characterPath = "_" + characterPath;
            }
            System.out.println(net.load("target/classes/models/" + characterPath + ".network"));
            networks.put(c, net);
        }
    }

    public String interpret() {
        return "interpreted";
    }

//    public JSONObject getFont(ByteArrayOutputStream stream) {
//        Image img = ImageIO.read(new ByteArrayInputStream(stream));
//    }
}
