import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class event {
    private static final List<String> participants = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/register", new RegisterHandler());
        server.setExecutor(null);
        System.out.println("Event server started at http://localhost:" + port + " — POST /register to add participants");
        server.start();
    }

    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String msg = "Only POST is supported on this endpoint";
                exchange.sendResponseHeaders(405, msg.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(msg.getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            StringBuilder body = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                int ch;
                while ((ch = br.read()) != -1) body.append((char) ch);
            }

            Map<String, String> params = parseForm(body.toString());
            String name = params.getOrDefault("name", "").trim();
            String email = params.getOrDefault("email", "").trim();
            String date = params.getOrDefault("date", "").trim();

            String entry = name + "," + email + "," + date;
            participants.add(entry);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("participants.txt", true))) {
                bw.write(entry);
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String response = "Event Registered Successfully.";
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private static Map<String, String> parseForm(String formData) throws IOException {
        Map<String, String> map = new HashMap<>();
        if (formData == null || formData.isEmpty()) return map;
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8.name());
            String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name()) : "";
            map.put(key, value);
        }
        return map;
    }
}
