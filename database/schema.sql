-- ====================================================================
-- FocusMatrix: Database Schema
-- Project: Time Management and Task Prioritization System (Topic #63)
-- Team: Byte Builder (SCSE, Galgotias University)
-- ====================================================================

-- 1. Create Database
CREATE DATABASE IF NOT EXISTS focusmatrix_db;
USE focusmatrix_db;

-- 2. Create Tasks Table (Primary Entity)
CREATE TABLE IF NOT EXISTS tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    deadline DATETIME NOT NULL,
    importance INT NOT NULL CHECK (importance BETWEEN 1 AND 5),
    urgency INT NOT NULL CHECK (urgency BETWEEN 1 AND 5),
    quadrant VARCHAR(10) NOT NULL CHECK (quadrant IN ('Q1', 'Q2', 'Q3', 'Q4')),
    priority_score DOUBLE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_priority (priority_score DESC)
);

-- 3. Create Time Logs Table (Session Tracking - 1-to-Many with tasks)
CREATE TABLE IF NOT EXISTS timelogs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    duration_minutes INT NOT NULL,
    session_type VARCHAR(20) DEFAULT 'FOCUS' CHECK (session_type IN ('FOCUS', 'SHORT_BREAK', 'LONG_BREAK')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE
);

-- 4. Sample Seed Data for Review Verification (2 Tasks per Quadrant)
INSERT INTO tasks (task_id, title, description, deadline, importance, urgency, quadrant, priority_score, status) VALUES
(1, 'Submit Review 1 Presentation & Demo', 'Upload 7-slide PDF and video presentation walkthrough on portal', NOW() + INTERVAL 1 DAY, 5, 5, 'Q1', 30.0, 'IN_PROGRESS'),
(2, 'Fix Matrix Drag-and-Drop Prioritization', 'Verify algorithmic quadrant re-triage on drop', NOW() + INTERVAL 1 DAY, 5, 4, 'Q1', 28.0, 'PENDING'),
(3, 'DSA: Trees & Dynamic Programming Revision', 'Solve 3 practice problems and analyze Big-O complexity', NOW() + INTERVAL 5 DAY, 5, 2, 'Q2', 20.7, 'PENDING'),
(4, 'System Architecture: JDBC & Servlets Review', 'Study DAO singleton pattern and HTTP exchange handling', NOW() + INTERVAL 7 DAY, 4, 2, 'Q2', 17.3, 'PENDING'),
(5, 'Team WhatsApp Group & Milestone Sync', 'Coordinate lab viva preparation schedule with teammates', NOW() + INTERVAL 1 DAY, 2, 4, 'Q3', 19.0, 'PENDING'),
(6, 'Verify College Lab Manual Signatures', 'Get mentor signatures on experiment index pages', NOW() + INTERVAL 2 DAY, 2, 5, 'Q3', 19.3, 'PENDING'),
(7, 'Clear Desktop Clutter & Downloads Folder', 'Archive old screenshots and temporary zip archives', NOW() + INTERVAL 14 DAY, 1, 1, 'Q4', 5.7, 'PENDING'),
(8, 'Unsubscribe from Non-Essential Promo Emails', 'Clean promotion newsletters for distraction-free focus', NOW() + INTERVAL 20 DAY, 1, 2, 'Q4', 7.5, 'PENDING');

INSERT INTO timelogs (task_id, start_time, duration_minutes, session_type) VALUES
(1, NOW() - INTERVAL 1 HOUR, 25, 'FOCUS'),
(1, NOW() - INTERVAL 30 MINUTE, 5, 'SHORT_BREAK');
