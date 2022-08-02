module basket.api {
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires org.jetbrains.annotations;
    requires org.jfxtras.styles.jmetro;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    requires javafx.controls;
    requires javafx.fxml;

    opens basket.api.handlers to com.fasterxml.jackson.databind;

    exports basket.api.app;
    exports basket.api.handlers;
    exports basket.api.prebuilt;
    exports basket.api.util;
    exports basket.api.util.url;
    exports basket.api.util.uri;
}