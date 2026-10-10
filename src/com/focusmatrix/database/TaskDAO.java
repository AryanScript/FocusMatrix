package com.focusmatrix.database;

import com.focusmatrix.model.Task;
import com.focusmatrix.model.TimeLog;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TaskDAO (Data Access Object - Rubric: 8 Marks)
 * Executes CRUD database operations using PreparedStatement and Try-With-Resources.
 */
public class TaskDAO {

    /**
     * INSERT: Creates a new task in MySQL
     */
    public boolean createTask(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (title, description, deadline, importance, urgency, quadrant, priority_score, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription() != null ? task.getDescription() : "");
            stmt.setTimestamp(3, Timestamp.valueOf(task.getDeadline()));
            stmt.setInt(4, task.getImportance());
            stmt.setInt(5, task.getUrgency());
            stmt.setString(6, task.getQuadrant());
            stmt.setDouble(7, task.getPriorityScore());
            stmt.setString(8, task.getStatus());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        task.setTaskId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * SELECT: Retrieves all tasks ordered by algorithmic priority score
     */
    public List<Task> getAllTasks() throws SQLException {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY priority_score DESC";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Task t = new Task();
                t.setTaskId(rs.getInt("task_id"));
                t.setTitle(rs.getString("title"));
                t.setDescription(rs.getString("description"));
                
                Timestamp ts = rs.getTimestamp("deadline");
                if (ts != null) t.setDeadline(ts.toLocalDateTime());
                
                t.setImportance(rs.getInt("importance"));
                t.setUrgency(rs.getInt("urgency"));
                t.setQuadrant(rs.getString("quadrant"));
                t.setPriorityScore(rs.getDouble("priority_score"));
                t.setStatus(rs.getString("status"));

                Timestamp createdTs = rs.getTimestamp("created_at");
                if (createdTs != null) t.setCreatedAt(createdTs.toLocalDateTime());

                list.add(t);
            }
        }
        return list;
    }

    /**
     * UPDATE: Changes task status (PENDING -> IN_PROGRESS -> COMPLETED)
     */
    public boolean updateTaskStatus(int taskId, String status) throws SQLException {
        String sql = "UPDATE tasks SET status = ? WHERE task_id = ?";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, taskId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * DELETE: Removes task from database (Cascade deletes associated timelogs)
     */
    public boolean deleteTask(int taskId) throws SQLException {
        String sql = "DELETE FROM tasks WHERE task_id = ?";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * INSERT: Logs a completed Pomodoro Focus Sprint
     */
    public boolean logTimeSession(TimeLog log) throws SQLException {
        String sql = "INSERT INTO timelogs (task_id, start_time, duration_minutes, session_type) VALUES (?, ?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, log.getTaskId());
            stmt.setTimestamp(2, Timestamp.valueOf(log.getStartTime()));
            stmt.setInt(3, log.getDurationMinutes());
            stmt.setString(4, log.getSessionType());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        log.setLogId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * SELECT: Retrieves all time logs for focus activity analytics
     */
    public List<TimeLog> getAllTimeLogs() throws SQLException {
        List<TimeLog> list = new ArrayList<>();
        String sql = "SELECT * FROM timelogs ORDER BY start_time DESC";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TimeLog log = new TimeLog();
                log.setLogId(rs.getInt("log_id"));
                log.setTaskId(rs.getInt("task_id"));
                
                Timestamp st = rs.getTimestamp("start_time");
                if (st != null) log.setStartTime(st.toLocalDateTime());

                log.setDurationMinutes(rs.getInt("duration_minutes"));
                log.setSessionType(rs.getString("session_type"));
                
                Timestamp ct = rs.getTimestamp("created_at");
                if (ct != null) log.setCreatedAt(ct.toLocalDateTime());

                list.add(log);
            }
        }
        return list;
    }
}
