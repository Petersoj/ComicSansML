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
    private static final NetworkManager manager = new NetworkManager();
    private final HashMap<Character, FontClassifierNetwork> networks = new HashMap<>();

    private NetworkManager() {
    }

    private void init() {
        for (char c: NetworkManagerConfig.availableChars) {
            FontClassifierNetwork net = new FontClassifierNetwork();
            net.load("Models/" + c + ".network");
            networks.put(c, net);
        }
    }

    public JSONObject getFont(ByteArrayOutputStream stream) {
        Image img = ImageIO.read(new ByteArrayInputStream(stream));
    }
}
