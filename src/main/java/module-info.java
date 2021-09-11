module basket.api {
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires org.jetbrains.annotations;
    requires org.jfxtras.styles.jmetro;

    requires javafx.controls;
    requires javafx.fxml;

    exports basket.api.app;
    exports basket.api.common;
    exports basket.api.prebuilt;
    exports basket.api.util;
    exports basket.api.util.url;
    exports basket.api.util.uri;
}