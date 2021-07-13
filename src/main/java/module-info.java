module Basket.api {
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires org.jetbrains.annotations;
    requires javafx.controls;
    requires javafx.fxml;

    exports app;
    exports common;
    exports common.pre_built;
    exports common.pre_built.popups;
}