package com.example.miniapphook;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 简单的 HTTP 控制服务器
 * 接收外部指令并执行
 *
 * API:
 * GET /status     - 获取模块状态
 * POST /click     - 点击按钮 {"text": "发送"}
 * GET /page       - 获取页面信息
 */
public class HttpServer implements Runnable {

    private static final String TAG = "HttpServer";
    private static final int PORT = 8888;

    private final ServerSocket serverSocket;
    private final ExecutorService executor;
    private volatile boolean running = true;

    public HttpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        executor = Executors.newCachedThreadPool();
        Log.i(TAG, "Server created on port " + port);
    }

    @Override
    public void run() {
        Log.i(TAG, "HTTP Server started on port " + PORT);
        while (running) {
            try {
                Socket client = serverSocket.accept();
                executor.submit(() -> handleClient(client));
            } catch (IOException e) {
                if (running) {
                    Log.e(TAG, "Error accepting connection", e);
                }
            }
        }
    }

    private void handleClient(Socket client) {
        try {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8)
            );
            OutputStream out = client.getOutputStream();

            // 读取请求行
            String requestLine = in.readLine();
            if (requestLine == null) {
                client.close();
                return;
            }

            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts.length > 1 ? parts[1] : "/";

            Log.i(TAG, method + " " + path);

            // 读取请求头
            Map<String, String> headers = new HashMap<>();
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                int colon = line.indexOf(':');
                if (colon > 0) {
                    headers.put(line.substring(0, colon).trim().toLowerCase(),
                               line.substring(colon + 1).trim());
                }
            }

            // 读取请求体
            StringBuilder body = new StringBuilder();
            int contentLength = headers.containsKey("content-length") 
                ? Integer.parseInt(headers.get("content-length")) : 0;
            for (int i = 0; i < contentLength; i++) {
                body.append((char) in.read());
            }

            // 处理请求
            String response = handleRequest(method, path, body.toString());

            // 发送响应
            String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Content-Length: " + response.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                response;

            out.write(httpResponse.getBytes(StandardCharsets.UTF_8));
            out.flush();
            client.close();

        } catch (Exception e) {
            Log.e(TAG, "Error handling client", e);
        }
    }

    private String handleRequest(String method, String path, String body) {
        try {
            switch (path) {
                case "/status":
                    return handleStatus();
                case "/click":
                    return handleClick(method, body);
                case "/page":
                    return handlePage();
                default:
                    return "{\"error\":\"Not found\"}";
            }
        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    private String handleStatus() {
        return "{\"module\":\"MiniAppHook\",\"version\":\"1.0\",\"status\":\"running\"}";
    }

    private String handleClick(String method, String body) {
        Log.i(TAG, "Click request: " + body);
        String text = extractJsonField(body, "text");
        if (text != null && !text.isEmpty()) {
            MainHook.clickButton(text);
            return "{\"success\":true,\"action\":\"click\",\"target\":\"" + text + "\"}";
        }
        return "{\"error\":\"Missing 'text' parameter\"}";
    }

    private String handlePage() {
        return "{\"info\":\"Use __MiniAppHook__.getPageInfo() in WebView console\"}";
    }

    private String extractJsonField(String json, String field) {
        try {
            int fieldIndex = json.indexOf("\"" + field + "\"");
            if (fieldIndex < 0) return null;
            int colonIndex = json.indexOf(":", fieldIndex);
            int startQuote = json.indexOf("\"", colonIndex);
            int endQuote = json.indexOf("\"", startQuote + 1);
            return json.substring(startQuote + 1, endQuote);
        } catch (Exception e) {
            return null;
        }
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            // ignore
        }
        executor.shutdown();
    }
}
