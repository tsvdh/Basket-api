package api.app;

import api.common.ExternalPropertiesHandler;
import api.common.InternalPropertiesHandler;
import api.common.Version;

public abstract class App {

    public abstract void start();

    private InternalPropertiesHandler pomHandler;
    private ExternalPropertiesHandler settingsHandler;

    public InternalPropertiesHandler getPomHandler() {
        return pomHandler;
    }

    public ExternalPropertiesHandler getSettingsHandler() {
        return settingsHandler;
    }

    public static void run() {
        // determine calling class
        String callingClassName = null;
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement stackTraceElement = stack[i];

            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            // if at current method, go down one more for calling class
            if (className.equals(App.class.getName()) && methodName.equals("run")) {
                callingClassName = stack[i + 1].getClassName();
                break;
            }
        }

        if (callingClassName == null) {
            throw new RuntimeException("Couldn't find calling class");
        }

        App app;
        try {
            Class<?> callingClass = Class.forName(callingClassName);
            if (callingClass.getSuperclass() == App.class) {
                app = (App) callingClass.getConstructor().newInstance();
            }
            else {
                throw new IllegalStateException(callingClass.getName() + " must extend " + App.class.getName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        app.pomHandler = InternalPropertiesHandler.newHandler("pom");
        InternalPropertiesHandler fallbackSettings = InternalPropertiesHandler.newHandler("settings");
        app.settingsHandler = ExternalPropertiesHandler.newHandler("settings", app.getAppName(), fallbackSettings);

        app.start();

        // store settings in case app didn't do it
        app.settingsHandler.saveProperties();
    }

    public String getAppName() {
        return this.pomHandler.getProperties().getProperty("name");
    }

    public Version getAppVersion() {
        String version = this.pomHandler.getProperties().getProperty("version");
        return new Version(version);
    }
}
