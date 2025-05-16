module com.sk031119 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.sk031119 to javafx.fxml;
    exports com.sk031119;
}
