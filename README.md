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

- **Frontend**: JavaFX
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

# Build the project
mvn clean package
```

### Running the Application

#### Option 1: Using Maven

```bash
mvn javafx:run
```

#### Option 2: Using the JAR file

Make sure your JavaFX SDK path is correctly set. You can find the necessary JavaFX module paths in your local Maven repository (typically `~/.m2/repository/org/openjfx`). Adjust the paths in the command below if your JavaFX version or repository location differs.

```bash
java --module-path /path/to/your/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml,javafx.graphics \
--add-opens java.base/java.lang=ALL-UNNAMED \
--add-opens java.base/java.util=ALL-UNNAMED \
-jar target/task_manager-1.0-SNAPSHOT.jar
```

**Note on JavaFX SDK Path:**
The example command uses `/path/to/your/javafx-sdk/lib`. You need to replace this with the actual path to your JavaFX SDK's `lib` directory. If you're using Maven, the JavaFX modules are typically downloaded to your local Maven repository. For example, if you are using JavaFX version 20, the paths might look like this:
- `~/.m2/repository/org/openjfx/javafx-controls/20/javafx-controls-20-mac-aarch64.jar`
- `~/.m2/repository/org/openjfx/javafx-fxml/20/javafx-fxml-20-mac-aarch64.jar`
- `~/.m2/repository/org/openjfx/javafx-graphics/20/javafx-graphics-20-mac-aarch64.jar`

You would then construct the `--module-path` by listing these JAR files, separated by colons (or semicolons on Windows). For example:
`--module-path ~/.m2/repository/org/openjfx/javafx-controls/20/javafx-controls-20-mac-aarch64.jar:~/.m2/repository/org/openjfx/javafx-fxml/20/javafx-fxml-20-mac-aarch64.jar:~/.m2/repository/org/openjfx/javafx-graphics/20/javafx-graphics-20-mac-aarch64.jar`

Alternatively, if you have a standalone JavaFX SDK, point to its `lib` directory.

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

If you encounter a JavaFX configuration warning like:
```
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @xxxxxxxx'
```

Make sure you're running with the proper module path as shown in the "Running the Application" section.

## 📝 License

[MIT License](LICENSE)