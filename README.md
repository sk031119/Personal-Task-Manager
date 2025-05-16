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

- **Frontend**: JavaFX 22
- **UI Theme**: AtlantaFX 2.0.1
- **Backend**: Spring Boot (3.2.2)
- **Database**: H2 (embedded, local storage)
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

#### Option 3: Using the JAR file directly

```bash
# From the task_manager directory
java --module-path ~/.m2/repository/org/openjfx/javafx-controls/22/javafx-controls-22.jar:~/.m2/repository/org/openjfx/javafx-fxml/22/javafx-fxml-22.jar:~/.m2/repository/org/openjfx/javafx-graphics/22/javafx-graphics-22.jar \
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

## 🔍 Troubleshooting

### Common Issues

#### JavaFX Runtime Missing

If you see an error like:
```
Error: JavaFX runtime components are missing, and are required to run this application
```

This indicates that the JavaFX modules are not properly included in the module path. Make sure:

1. You're using the VS Code launch configuration provided with the project
2. You've built the project with `mvn clean package dependency:copy-dependencies`
3. The JavaFX version in pom.xml matches the version in your local Maven repository

#### Unsupported JavaFX Configuration Warning

If you encounter a warning like:
```
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @xxxxxxxx'
```

Make sure you're running with the proper module path as shown in the "Running the Application" section.

## 📝 License

[MIT License](LICENSE)