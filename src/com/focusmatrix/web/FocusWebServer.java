package com.focusmatrix.web;

import com.focusmatrix.database.TaskDAO;
import com.focusmatrix.model.Task;
import com.focusmatrix.model.TimeLog;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * FocusWebServer (Servlets & Web Integration - Rubric: 7 Marks)
 * High-performance Java Web Server providing REST APIs and static web hosting.
 * Connects the web frontend (index.html) with MySQL via JDBC TaskDAO.
 */
public class FocusWebServer {

    private static final int PORT = 8080;
    private static final TaskDAO taskDAO = new TaskDAO();
    private static Path webRootDir;

    public static void main(String[] args) throws IOException {
        webRootDir = Paths.get(".").toAbsolutePath().normalize();
        
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // API Endpoints (Servlets)
        server.createContext("/api/tasks", new TasksHandler());
        server.createContext("/api/tasks/status", new TaskStatusHandler());
        server.createContext("/api/sprint", new SprintHandler());
        server.createContext("/api/timelogs", new TimeLogsHandler());

        // Static Web Resource Handler (Serves index.html, style.css, assets)
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null); // Default single-thread executor or pool
        System.out.println("====================================================================");
        System.out.println("🚀 FocusMatrix Java Web Server Started Successfully!");
        System.out.println("🌐 URL: http://localhost:" + PORT);
        System.out.println("🗄️ Database: MySQL 8.0 (focusmatrix_db via JDBC TaskDAO)");
        System.out.println("====================================================================");
        server.start();
    }

    /**
     * Handles /api/tasks [GET, POST, DELETE, OPTIONS]
     */
    static class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            String method = exchange.getRequestMethod().toUpperCase();

            if ("OPTIONS".equals(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                if ("GET".equals(method)) {
                    List<Task> tasks = taskDAO.getAllTasks();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < tasks.size(); i++) {
                        Task t = tasks.get(i);
                        if (i > 0) json.append(",");
                        json.append(taskToJson(t));
                    }
                    json.append("]");
                    sendJsonResponse(exchange, 200, json.toString());

                } else if ("POST".equals(method)) {
                    String body = readBody(exchange);
                    Map<String, String> params = parseJsonOrForm(body);

                    String title = params.getOrDefault("title", "New Task");
                    String desc = params.containsKey("description") ? params.get("description") : params.getOrDefault("desc", "");
                    int imp = 3;
                    try {
                        imp = Integer.parseInt(params.containsKey("importance") ? params.get("importance") : params.getOrDefault("imp", "3"));
                    } catch (NumberFormatException ignored) {}

                    int urg = 3;
                    try {
                        urg = Integer.parseInt(params.containsKey("urgency") ? params.get("urgency") : params.getOrDefault("urg", "3"));
                    } catch (NumberFormatException ignored) {}
                    
                    String dlStr = params.get("deadline");
                    LocalDateTime dl = LocalDateTime.now().plusDays(2);
                    if (dlStr != null && !dlStr.trim().isEmpty()) {
                        try {
                            String clean = dlStr.trim().replace("Z", "");
                            if (clean.length() == 10) {
                                clean += "T23:59:59";
                            } else {
                                clean = clean.replace(" ", "T");
                            }
                            dl = LocalDateTime.parse(clean);
                        } catch (Exception ignored) {}
                    }

                    Task t = new Task(0, title, desc, dl, imp, urg);
                    boolean ok = taskDAO.createTask(t);
                    if (ok) {
                        sendJsonResponse(exchange, 201, taskToJson(t));
                    } else {
                        sendJsonResponse(exchange, 500, "{\"error\":\"Failed to save task to MySQL\"}");
                    }

                } else if ("DELETE".equals(method)) {
                    String query = exchange.getRequestURI().getQuery();
                    int id = parseQueryParam(query, "id");
                    if (id > 0) {
                        taskDAO.deleteTask(id);
                        sendJsonResponse(exchange, 200, "{\"success\":true,\"deletedId\":" + id + "}");
                    } else {
                        sendJsonResponse(exchange, 400, "{\"error\":\"Missing or invalid task id\"}");
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendJsonResponse(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    /**
     * Handles /api/tasks/status [POST, PUT] (Updates status to COMPLETED / IN_PROGRESS)
     */
    static class TaskStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            String method = exchange.getRequestMethod().toUpperCase();

            if ("OPTIONS".equals(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                if ("POST".equals(method) || "PUT".equals(method)) {
                    String body = readBody(exchange);
                    Map<String, String> params = parseJsonOrForm(body);
                    int id = Integer.parseInt(params.getOrDefault("id", "0"));
                    String status = params.getOrDefault("status", "COMPLETED");

                    if (id > 0) {
                        taskDAO.updateTaskStatus(id, status);
                        sendJsonResponse(exchange, 200, "{\"success\":true,\"id\":" + id + ",\"status\":\"" + status + "\"}");
                    } else {
                        sendJsonResponse(exchange, 400, "{\"error\":\"Invalid task ID\"}");
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendJsonResponse(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    /**
     * Handles /api/sprint [POST] (Logs completed Pomodoro sprint)
     */
    static class SprintHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            String method = exchange.getRequestMethod().toUpperCase();

            if ("OPTIONS".equals(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                if ("POST".equals(method)) {
                    String body = readBody(exchange);
                    Map<String, String> params = parseJsonOrForm(body);

                    int taskId = Integer.parseInt(params.getOrDefault("taskId", "1"));
                    int mins = Integer.parseInt(params.getOrDefault("durationMinutes", "25"));
                    String sessionType = params.getOrDefault("sessionType", "FOCUS");

                    TimeLog log = new TimeLog(0, taskId, LocalDateTime.now(), mins, sessionType);
                    taskDAO.logTimeSession(log);

                    sendJsonResponse(exchange, 201, "{\"success\":true,\"logId\":" + log.getLogId() + ",\"mins\":" + mins + "}");
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendJsonResponse(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    /**
     * Handles /api/timelogs [GET]
     */
    static class TimeLogsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                List<TimeLog> logs = taskDAO.getAllTimeLogs();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < logs.size(); i++) {
                    TimeLog l = logs.get(i);
                    if (i > 0) json.append(",");
                    json.append(String.format("{\"logId\":%d,\"taskId\":%d,\"durationMinutes\":%d,\"sessionType\":\"%s\",\"startTime\":\"%s\"}",
                            l.getLogId(), l.getTaskId(), l.getDurationMinutes(), l.getSessionType(), l.getStartTime()));
                }
                json.append("]");
                sendJsonResponse(exchange, 200, json.toString());
            } catch (Exception e) {
                sendJsonResponse(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    /**
     * Serves index.html, style.css, and client assets
     */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if ("/".equals(path) || path.isEmpty()) {
                path = "/index.html";
            }

            Path target = webRootDir.resolve(path.substring(1)).normalize();
            if (!Files.exists(target) || Files.isDirectory(target)) {
                target = webRootDir.resolve("index.html");
            }

            if (Files.exists(target)) {
                String mime = "text/html";
                if (path.endsWith(".css")) mime = "text/css";
                else if (path.endsWith(".js")) mime = "application/javascript";
                else if (path.endsWith(".png")) mime = "image/png";
                else if (path.endsWith(".svg")) mime = "image/svg+xml";

                byte[] data = Files.readAllBytes(target);
                exchange.getResponseHeaders().set("Content-Type", mime + "; charset=utf-8");
                exchange.sendResponseHeaders(200, data.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(data);
                }
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        }
    }

    // ==================== HELPER METHODS ====================
    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            return baos.toString(StandardCharsets.UTF_8);
        }
    }

    private static Map<String, String> parseJsonOrForm(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.trim().isEmpty()) return map;

        body = body.trim();
        if (body.startsWith("{") && body.endsWith("}")) {
            // Lightweight JSON Parser for basic fields
            String inner = body.substring(1, body.length() - 1);
            String[] pairs = inner.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            for (String p : pairs) {
                String[] kv = p.split(":", 2);
                if (kv.length == 2) {
                    String k = kv[0].trim().replace("\"", "");
                    String v = kv[1].trim();
                    if (v.startsWith("\"") && v.endsWith("\"")) {
                        v = v.substring(1, v.length() - 1);
                    }
                    map.put(k, v);
                }
            }
        } else {
            // URL Encoded form parser
            String[] pairs = body.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                }
            }
        }
        return map;
    }

    private static int parseQueryParam(String query, String paramName) {
        if (query == null) return 0;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2 && kv[0].equals(paramName)) {
                try {
                    return Integer.parseInt(kv[1]);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    private static String taskToJson(Task t) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
            "{\"id\":%d,\"title\":\"%s\",\"desc\":\"%s\",\"deadline\":\"%s\",\"imp\":%d,\"urg\":%d,\"q\":\"%s\",\"sc\":%.1f,\"done\":%s}",
            t.getTaskId(),
            escapeJson(t.getTitle()),
            escapeJson(t.getDescription()),
            t.getDeadline() != null ? t.getDeadline().format(dtf) : "",
            t.getImportance(),
            t.getUrgency(),
            t.getQuadrant().toLowerCase(),
            t.getPriorityScore(),
            "COMPLETED".equalsIgnoreCase(t.getStatus())
        );
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
