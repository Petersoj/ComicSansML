package edu.usu.hackathon2019.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.usu.hackathon2019.fontclassifier.FontManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Server {

    public static void main(String[] args) throws Exception {
        FontManager.init();
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", ServerConfig.port), 0);
        server.createContext("/test", new MyHandler());
        server.createContext("/interpret", new NetWorkHandler());
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
            String response = NetworkManager.manager.interpret();
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
