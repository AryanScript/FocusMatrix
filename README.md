# FocusMatrix: Algorithmic Time Management & Task Prioritization System

> **B.Tech Project Review 1 — Java Web-Based Projects (Topic #63)**  
> **Team Name:** Byte Builder  
> **School:** School of Computing Science and Engineering (SCSE), Galgotias University  

---

## 📌 1. Project Overview & Problem Statement

Modern students and knowledge workers struggle with **decision fatigue**, **urgency blindness**, and **unstructured multitasking**. Existing to-do apps act as passive dumping grounds without guiding the user on *what to execute first* or *how to protect deep focus*.

**FocusMatrix** solves this by engineering a dual-engine productivity system:
1. **Dynamic 2x2 Eisenhower Prioritization Engine:** Algorithmic ranking driven by importance, urgency, and exponential deadline decay.
2. **Pomodoro Deep-Work Execution Unit:** 25-minute distraction-free focus cycles integrated with ambient audio oscillators and real-time focus analytics.

---

## 🏆 2. Academic Rubric Mapping (33 / 33 Marks)

| Evaluation Component | Marks | Technical Implementation in FocusMatrix | Key Source Files |
| :--- | :---: | :--- | :--- |
| **Problem Understanding & Solution Design** | **8 M** | • Algorithmic triage formula incorporating deadline decay<br>• Real-time auto-escalation (Q2 ➔ Q1 within 24h deadline)<br>• Normalized 1:N relational architecture | [`PriorityEngine.java`](src/com/focusmatrix/engine/PriorityEngine.java)<br>[`schema.sql`](database/schema.sql) |
| **Core Java Concepts (OOP & Collections)** | **10 M** | • **Abstraction & Interfaces:** `Prioritizable` interface<br>• **Inheritance:** `AbstractTask` base entity<br>• **Encapsulation:** Private fields, validation getters/setters in `Task`<br>• **Collections & Generics:** `PriorityQueue<Task>` with Custom Comparator<br>• **Exception Handling:** Custom `InvalidTaskException`, Try-With-Resources | [`Prioritizable.java`](src/com/focusmatrix/model/Prioritizable.java)<br>[`AbstractTask.java`](src/com/focusmatrix/model/AbstractTask.java)<br>[`Task.java`](src/com/focusmatrix/model/Task.java)<br>[`InvalidTaskException.java`](src/com/focusmatrix/exception/InvalidTaskException.java) |
| **Database Integration / JDBC** | **8 M** | • MySQL 8.0 relational database (`focusmatrix_db`)<br>• `tasks` (Primary) + `timelogs` (Foreign Key `ON DELETE CASCADE`)<br>• Singleton Connection Manager (`DBConnection`)<br>• DAO Pattern (`TaskDAO`) utilizing secure `PreparedStatement` | [`DBConnection.java`](src/com/focusmatrix/database/DBConnection.java)<br>[`TaskDAO.java`](src/com/focusmatrix/database/TaskDAO.java)<br>[`schema.sql`](database/schema.sql) |
| **Servlets & Web Integration** | **7 M** | • Built-in Java HTTP Web Server (`FocusWebServer` on port 8080)<br>• RESTful API Endpoints (`/api/tasks`, `/api/tasks/status`, `/api/sprint`, `/api/timelogs`)<br>• Dynamic JSON Serialization & AJAX Fetch Sync | [`FocusWebServer.java`](src/com/focusmatrix/web/FocusWebServer.java)<br>[`index.html`](index.html) |

---

## 📐 3. Mathematical Priority Scoring Formula

The core algorithmic scoring engine ranks tasks inside the `PriorityQueue<Task>`:

$$\text{Priority Score} = (\text{Importance} \times 3) + \left(\frac{10}{\text{Days Remaining} + 1}\right) + (\text{Urgency} \times 2)$$

### Quadrant Triage Matrix:
* **Q1 · DO FIRST** ($\text{Imp} \ge 4$, $\text{Urg} \ge 4$ or $\text{Days} \le 1$): Immediate critical deadlines.
* **Q2 · SCHEDULE** ($\text{Imp} \ge 4$, $\text{Urg} < 4$): High-value deep work, strategic goals.
* **Q3 · DELEGATE / BATCH** ($\text{Imp} < 4$, $\text{Urg} \ge 4$): Admin tasks, quick communication.
* **Q4 · ELIMINATE** ($\text{Imp} < 4$, $\text{Urg} < 4$): Low-value distractions.

---

## 🏗️ 4. System Architecture

```text
+-----------------------------------------------------------------+
|                   CLIENT TIER (Frontend UI)                     |
|  - Eisenhower 2x2 Matrix Grid        - Apple Health Ring HUD    |
|  - 25:00 Web Audio Focus Engine      - Micro-Steps Checklist    |
|  - Auto-Triage Quick NLP Bar         - Anti-Exploit XP Engine   |
+-----------------------------------------------------------------+
                                |
                   HTTP / REST (JSON API)
                                v
+-----------------------------------------------------------------+
|                 APPLICATION TIER (Java Backend)                 |
|  - FocusWebServer (Port 8080 - HTTP Exchange Routing)           |
|  - PriorityEngine (PriorityQueue<Task> & Collections Comparator)|
|  - Task & TimeLog Domain Models (OOP & Custom Exceptions)       |
+-----------------------------------------------------------------+
                                |
                     JDBC (PreparedStatement)
                                v
+-----------------------------------------------------------------+
|                    DATA TIER (MySQL 8.0)                        |
|  - Database: focusmatrix_db                                     |
|  - Table: tasks (task_id, title, deadline, imp, urg, score)    |
|  - Table: timelogs (log_id, task_id [FK ON DELETE CASCADE])    |
+-----------------------------------------------------------------+
```

---

## 💾 5. Database Schema & Tables

### 1. `tasks` (Primary Entity)
```sql
CREATE TABLE tasks (
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
```

### 2. `timelogs` (Session Tracking Entity)
```sql
CREATE TABLE timelogs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    duration_minutes INT NOT NULL,
    session_type VARCHAR(20) DEFAULT 'FOCUS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE
);
```

---

## 🚀 6. Quick Start & Execution

### Prerequisites:
* **Java Development Kit (JDK 17+)**
* **MySQL Server 8.0** running on `localhost:3306`

### 1-Click Launch (Windows):
1. **Double-click `run_server.bat`**  
   *(Compiles Java source files and starts the server on `http://localhost:8080`)*
2. **Open browser:** Go to `http://localhost:8080`

### Manual CLI Compilation & Launch:
```bash
# 1. Compile Java files with MySQL Connector
javac -cp "lib/mysql-connector-j-8.4.0.jar" -d bin src/com/focusmatrix/**/*.java src/com/focusmatrix/*.java

# 2. Run Main application and web server
java -cp "lib/mysql-connector-j-8.4.0.jar;bin" com.focusmatrix.Main
```

---

## 📁 7. Project Structure

```text
Time_Management_Task_Prioritization_System/
│
├── database/
│   ├── schema.sql                 # Complete MySQL DDL & Seed Script
│   └── seed_all_quadrants.sql     # 8-Task 4-Quadrant active seed data
│
├── lib/
│   └── mysql-connector-j-8.4.0.jar# Official MySQL JDBC Driver
│
├── src/com/focusmatrix/
│   ├── Main.java                  # CLI runner and server starter
│   ├── database/
│   │   ├── DBConnection.java      # Singleton JDBC Connection Manager
│   │   └── TaskDAO.java           # CRUD operations using PreparedStatement
│   ├── engine/
│   │   └── PriorityEngine.java    # PriorityQueue & Stream sorting algorithms
│   ├── exception/
│   │   └── InvalidTaskException.java # Custom business validation exception
│   ├── model/
│   │   ├── Prioritizable.java     # Priority interface
│   │   ├── AbstractTask.java      # Abstract class implementing Comparable
│   │   ├── Task.java              # Concrete Task entity with encapsulation
│   │   └── TimeLog.java           # Focus sprint log entity
│   └── web/
│       └── FocusWebServer.java    # REST API endpoints & static server
│
├── index.html                     # Responsive UI (Eisenhower, Pomodoro, Health HUD)
├── style.css                      # Cyberpunk & Light mode design system
├── run_server.bat                 # 1-Click Windows execution script
├── compile.bat                    # 1-Click compilation batch file
├── ByteBUILDERS.pdf               # Official Review 1 Pitch Deck & Presentation
└── README.md                      # Academic documentation & viva guide
```

---

## 👥 8. Team & Submission Credits
* **Project Name:** FocusMatrix
* **Team:** Byte Builder
* **Course:** Java Web-Based Projects
* **Institution:** Galgotias University, Greater Noida
