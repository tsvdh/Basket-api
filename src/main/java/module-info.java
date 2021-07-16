module basket.api {
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires org.jetbrains.annotations;

    requires transitive javafx.controls;
    requires transitive javafx.fxml;

    exports app;
    exports common;
    exports common.pre_built;
    exports common.pre_built.popups;
}