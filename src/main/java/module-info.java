module org.example.music_library {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens org.example.music_library to javafx.fxml;
    exports org.example.music_library;
}