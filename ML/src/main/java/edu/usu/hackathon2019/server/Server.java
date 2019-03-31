package edu.usu.hackathon2019.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.usu.hackathon2019.fontclassifier.FontClassifierConfig;
import edu.usu.hackathon2019.fontclassifier.FontManager;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.json.*;
import org.nd4j.linalg.indexing.INDArrayIndex;

public class Server {

    public static void main(String[] args) throws Exception {
        FontManager.init();
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", ServerConfig.port), 0);
        server.createContext("/test", new MyHandler());
        server.createContext("/interpret", new fileHandler());
        server.createContext("/webpage", new WebPageServer());
        for(String font: FontManager.getFonts()) {
            System.out.println(font);
        }
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println(server.getAddress().getHostString());
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "<html><head><title>ComicSans ML</title></head><body><center><h1>Hello World</h1></center></body></html>";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class NetWorkHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
//            String response = NetworkManager.manager.interpret();
            String response = "interpreting";
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class fileHandler implements HttpHandler {
        @Override
        public void handle(final HttpExchange t) throws IOException {
            DiskFileItemFactory d = new DiskFileItemFactory();
            try {
                ServletFileUpload up = new ServletFileUpload(d);

                List<FileItem> result = up.parseRequest(new RequestContext() {

                    public String getCharacterEncoding() {
                        return "UTF-8";
                    }

                    public int getContentLength() {
                        return 0; //tested to work with 0 as return
                    }

                    public String getContentType() {
                        return t.getRequestHeaders().getFirst("Content-type");
                    }

                    public InputStream getInputStream() throws IOException {
                        return t.getRequestBody();
                    }

                });
                t.getResponseHeaders().add("Content-type", "text/plain");
                t.sendResponseHeaders(200, 0);
                OutputStream os = t.getResponseBody();
                for(FileItem fi : result) {
//                    os.write(fi.getName().getBytes());
//                    IOUtils.copy(fi.getInputStream(), os);
//                    os.write("\r\n".getBytes());
                    BufferedImage image = toBufferedImage(ImageIO.read(fi.getInputStream()).getScaledInstance(FontClassifierConfig.imageWidth, FontClassifierConfig.imageHeight, 0));
                    INDArray guess = NetworkManager.manager.interpret(getSample(image));
                    JSONArray data = new JSONArray();
                    double[] rec = guess.data().asDouble();
                    String[] fonts = FontManager.getFonts();
                    for (int i = 0; i < fonts.length; i++) {
                        JSONObject element = new JSONObject();
                        element.put("fontName", fonts[i]);
                        element.put("probability", rec[i]);
                        data.put(element);
                    }
                    String res = data.toString();
                    os.write(res.getBytes());
                }
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
                t.sendResponseHeaders(401, 0);
                t.getResponseBody().close();
            }
        }
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static INDArray getSample(BufferedImage image) {

        int height = image.getHeight();
        int width = image.getWidth();
        double[][][][] doubleImage = new double[FontClassifierConfig.batchSize][FontClassifierConfig.channels][height][width];
        for (int b = 0; b < FontClassifierConfig.batchSize; b++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int c = image.getRGB(x, y);
                    doubleImage[b][0][y][x] = normalize(image.getColorModel().getRed(c));
                    doubleImage[b][1][y][x] = normalize(image.getColorModel().getGreen(c));
                    doubleImage[b][2][y][x] = normalize(image.getColorModel().getBlue(c));
                }
            }
        }

        return Nd4j.create(doubleImage);
    }

    private static double normalize(double num) {
        return num / 255d;
    }

    static class WebPageServer implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            URI uri = httpExchange.getRequestURI();
            String filePath = uri.getPath().substring(8);
            if (filePath.length() == 0) {
                httpExchange.getResponseHeaders().add("Location", "webpage/index.html");
                httpExchange.sendResponseHeaders(302, 0);
            } else if (filePath.contains("..")) {
                httpExchange.sendResponseHeaders(505, 0);
            } else {
                try {
                    File file = new File("target/classes/webpage" + filePath);
                    httpExchange.sendResponseHeaders(200, file.length());
                    Files.copy(file.toPath(), httpExchange.getResponseBody());
                } catch (Exception e) {
                    httpExchange.sendResponseHeaders(404, 0);
                }
            }
            httpExchange.getResponseBody().close();
        }
    }
}
