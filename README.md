# 📝 Personal Task Manager (JavaFX + Spring Boot)

A desktop-based **Task Manager** application built with JavaFX for the frontend and Spring Boot for the backend logic and data handling. The app helps users manage their tasks with ease using a multi-user system with admin capabilities — all stored locally using the embedded H2 database.

## 🎯 Features

- 👥 **Multi-user functionality** with user roles (Admin & User)
- 🔐 User authentication with login/register system
- 🛠️ Admin panel for user management
- ✅ Add, edit, delete tasks
- 📌 Set due dates and priorities
- 💫 Task status management via dropdown menu
- 🗂️ View tasks by status (All / Completed / Pending)
- 🔍 Search functionality for tasks
- 💾 Data is saved in a local H2 database
- 🖥️ JavaFX GUI with simple and clean layout
- 🎨 Modern UI with AtlantaFX theme

## 🛠️ Tech Stack

- **Frontend**: JavaFX (version 20)
  - FXML for UI structure
  - CSS for styling
- **UI Theme**: AtlantaFX 2.0.1 (Primer Light theme)
- **Backend**: 
  - Spring Boot 3.2.2
  - Spring Data JPA for data access
- **Database**: H2 (embedded, local storage)
  - Web console for database management
- **Build Tool**: Maven
- **Language**: Java (JDK 21)

## 📋 Prerequisites

- Java JDK 21
- Maven 3.6+
- macOS (for macOS-aarch64 builds) or modify pom.xml for your platform

## ⚙️ Setup & Running

### Building the Application

```bash
# Clone the repository
git clone https://github.com/yourusername/Personal-Task-Manager.git
cd Personal-Task-Manager/task_manager

# Build the project with dependencies
mvn clean package
mvn dependency:copy-dependencies
```

### Running the Application

#### Option 1: Using Maven

```bash
mvn javafx:run
```

#### Option 2: Using the Spring Boot Maven Plugin

```bash
mvn spring-boot:run
```

#### Option 3: Using the JAR file directly

```bash
# From the task_manager directory
java --module-path ~/.m2/repository/org/openjfx/javafx-controls/20/javafx-controls-20-mac-aarch64.jar:~/.m2/repository/org/openjfx/javafx-fxml/20/javafx-fxml-20-mac-aarch64.jar:~/.m2/repository/org/openjfx/javafx-graphics/20/javafx-graphics-20-mac-aarch64.jar \
--add-modules javafx.controls,javafx.fxml,javafx.graphics \
--add-opens java.base/java.lang=ALL-UNNAMED \
--add-opens java.base/java.util=ALL-UNNAMED \
-jar target/task_manager-1.0-SNAPSHOT.jar
```

**Note for Windows Users:** Replace the colons (`:`) in the module path with semicolons (`;`).

## 🗂️ Project Structure

```
task_manager/
├── src/main/
│   ├── java/com/sk031119/
│   │   ├── App.java                  # Application entry point
│   │   ├── TaskManagerApplication.java # Main JavaFX + Spring Boot app
│   │   ├── config/                   # Configuration classes
│   │   │   └── DataLoader.java       # Initial data setup
│   │   ├── controller/               # UI controllers
│   │   │   ├── AdminPanelController.java  # Admin panel controller
│   │   │   ├── LoginController.java  # Login/registration controller
│   │   │   └── TaskDashboardController.java # Main task interface controller
│   │   ├── model/                    # Domain models
│   │   │   ├── Task.java             # Task entity with priorities
│   │   │   └── User.java             # User entity with roles
│   │   ├── repository/               # Data access layer
│   │   │   ├── TaskRepository.java   # Task data operations
│   │   │   └── UserRepository.java   # User data operations
│   │   └── service/                  # Business logic layer
│   │       ├── TaskService.java      # Task business logic
│   │       └── UserService.java      # User authentication & management
│   └── resources/
│       ├── application.yml           # Spring Boot configuration
│       └── com/sk031119/
│           ├── admin-panel.fxml      # Admin UI layout
│           ├── login.fxml            # Login/registration UI
│           ├── styles.css            # Application styles
│           └── task-dashboard.fxml   # Main task UI layout
└── data/
    └── task-manager-db.mv.db        # H2 database file
```

## 📊 Database Access

The application uses an embedded H2 database for data storage. You can access the H2 console to view and manage the database directly:

1. Start the application using any of the methods mentioned above
2. Open a web browser and navigate to: `http://localhost:8080/h2-console`
3. Use the following settings to connect:
   - JDBC URL: `jdbc:h2:file:./data/task-manager-db`
   - Username: `sa`
   - Password: `password`

> Note: The H2 console is enabled only when the application is running.


## 📝 License

[MIT License](LICENSE)