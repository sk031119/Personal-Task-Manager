# 📝 Personal Task Manager (JavaFX + Spring Boot)

A desktop-based **Task Manager** application built with JavaFX for the frontend and Spring Boot for the backend logic and data handling. The app helps users manage their personal tasks with ease — all stored locally using the embedded H2 database.

## 🎯 Features

- ✅ Add, edit, delete tasks
- 📌 Set due dates and priorities
- ✅ Mark tasks as complete or pending
- 🗂️ View tasks by status (All / Completed / Pending)
- 💾 Data is saved in a local H2 database
- 🖥️ JavaFX GUI with simple and clean layout
- 🎨 Modern UI with AtlantaFX theme

## 🛠️ Tech Stack

- **Frontend**: JavaFX
- **UI Theme**: AtlantaFX 2.0.1
- **Backend**: Spring Boot (3.2.2)
- **Database**: H2 (embedded, local storage)
- **Build Tool**: Maven
- **Language**: Java (JDK 17+ recommended, configured for JDK 21)

## 📋 Prerequisites

- Java JDK 17 or higher (JDK 21 recommended)
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

```bash
java --module-path $HOME/.m2/repository/org/openjfx/javafx-controls/20:$HOME/.m2/repository/org/openjfx/javafx-fxml/20:$HOME/.m2/repository/org/openjfx/javafx-graphics/20 \
--add-modules javafx.controls,javafx.fxml,javafx.graphics \
--add-opens java.base/java.lang=ALL-UNNAMED \
--add-opens java.base/java.util=ALL-UNNAMED \
-jar target/task_manager-1.0-SNAPSHOT.jar
```

## 🗂️ Project Structure

```
task_manager/
├── src/main/
│   ├── java/com/sk031119/
│   │   ├── App.java                  # Application entry point
│   │   ├── TaskManagerApplication.java # Main JavaFX + Spring Boot app
│   │   ├── config/                   # Configuration classes
│   │   ├── controller/               # UI controllers
│   │   ├── model/                    # Domain models
│   │   ├── repository/               # Data access layer
│   │   └── service/                  # Business logic layer
│   └── resources/
│       ├── application.properties    # Spring Boot configuration
│       └── com/sk031119/
│           ├── styles.css            # Application styles
│           └── task-dashboard.fxml   # Main UI layout
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