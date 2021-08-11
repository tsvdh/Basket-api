package app;

import common.ExternalPropertiesHandler;
import common.InternalPropertiesHandler;
import common.Version;
import common.pre_built.StyleHandler;
import common.pre_built.popups.Message;

/**
 * The main class of the Basket API.
 * Use this class by implementing the start method, and then calling the run method.
 * <p>
 * <p>
 * Some assumptions are made about the structure of your project:
 * <ul>
 *     <li>All resources files are in a directory tagged as a resources folder.</li>
 *     <li>{@code .properties} files are in the {@code properties} folder.</li>
 *     <li>This folder must contain a file called {@code pom.properties}, which contains {@code name} and {@code version} keys.</li>
 *     <li>Images or other visual data are in the {@code images} folder.</li>
 *     <li>The main icon of your app is called {@code icon.png}</li>
 *     <li>Any style files ({@code .css}, {@code .ttf}) are in the {@code style} folder.</li>
 *     <li>Your resource folders are opened to the api module {@code (basket.api)}</li>
 * </ul>
 * <p>
 * <p>
 * Three handler objects are made available through this class:
 * <ul>
 *     <li>{@code pomHandler}, handles the {@code .properties} file of the internal pom.</li>
 *     <li>{@code settingsHandler}, handler the {@code .properties} file of the external settings file.</li>
 *     <li>{@code styleHandler}, handles applying style to JavaFX scenes.</li>
 * </ul>
 * <p>
 * Override the method {@code makeStyleHandler} if you wish to use another style.
 * <p>
 * <p>
 * Use this pattern if you are using JavaFX, in order to reduce the number of launching classes.
 * <pre>
 * public class Main extends Application {
 *
 *     public static class MyApp extends BasketApp {
 *
 *        {@code @Override}
 *         public void start() {
 *             // starting point for program
 *         }
 *
 *         public static void invokeLaunch() {
 *             MyApp.launch();
 *         }
 *     }
 *
 *    {@code @Override}
 *     public void start(Stage primaryStage) {
 *         MyApp.invokeLaunch();
 *     }
 *
 *     public static void main(String[] args) {
 *         Application.launch();
 *     }
 * }
 * </pre>
 */
public abstract class BasketApp {

    public abstract void start();

    private static Class<?> implementingClass; // for loading from the correct module
    private InternalPropertiesHandler pomHandler;
    private ExternalPropertiesHandler settingsHandler;

    public static Class<?> getImplementingClass() {
        return implementingClass;
    }

    public InternalPropertiesHandler getPomHandler() {
        return pomHandler;
    }

    public ExternalPropertiesHandler getSettingsHandler() {
        return settingsHandler;
    }

    /**
     * Override this method if you want to change the default style sheet.
     */
    public StyleHandler makeStyleHandler() {
        return new StyleHandler("clean", StyleHandler.Location.EXTERNAL);
    }

    private static Class<?> getCallingClass() {
        // determine current method
        String callingClassName = null;
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement stackTraceElement = stack[i];

            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            // if at current method, go down two more:
            //      one to the caller of this method, one more to the caller of the inquiring method
            if (className.equals(BasketApp.class.getName()) && methodName.equals("getCallingClass")) {
                callingClassName = stack[i + 2].getClassName();
                break;
            }
        }

        if (callingClassName == null) {
            throw new RuntimeException("Couldn't find calling class");
        }

        try {
            return Class.forName(callingClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void launch() {
        Class<?> callingClass = getCallingClass();

        BasketApp app;
        try {
            if (callingClass.getSuperclass() == BasketApp.class) {
                app = (BasketApp) callingClass.getConstructor().newInstance();
            }
            else {
                throw new IllegalStateException(callingClass.getName() + " must extend " + BasketApp.class.getName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        implementingClass = callingClass;

        app.pomHandler = InternalPropertiesHandler.newHandler("pom");

        InternalPropertiesHandler fallbackSettings;
        try {
            fallbackSettings = InternalPropertiesHandler.newHandler("settings");
        } catch (NotifyException e) {
            fallbackSettings = null;
        }
        app.settingsHandler = ExternalPropertiesHandler.newHandler("settings", app.getAppName(), fallbackSettings);

        app.makeStyleHandler().applyStyleToApplication();

        try {
            app.start();
        } catch (NotifyException e) {
            // display a message if the current thread is a JavaFX thread
            try {
                new Message(e.getMessage(), true);
            } catch (Exception ignored) {}
        }

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
