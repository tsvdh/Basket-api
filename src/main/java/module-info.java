module basket.api {
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires org.jetbrains.annotations;
    requires org.jfxtras.styles.jmetro;

    requires transitive javafx.controls;
    requires transitive javafx.fxml;

    exports app;
    exports common;
    exports prebuilt;
    exports util;
    exports util.url;
}