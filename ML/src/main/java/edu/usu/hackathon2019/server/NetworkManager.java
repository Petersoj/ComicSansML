package edu.usu.hackathon2019.server;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import edu.usu.hackathon2019.fontclassifier.FontClassifierNetwork;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.json.JSONObject;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.primitives.Pair;

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

    public INDArray interpret(INDArray data) {
        return networks.get('8').output(data);
    }

//    public JSONObject getFont(ByteArrayOutputStream stream) {
//        Image img = ImageIO.read(new ByteArrayInputStream(stream));
//    }
}
