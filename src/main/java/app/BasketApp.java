package app;

import common.ExternalPropertiesHandler;
import common.FatalError;
import common.InternalPropertiesHandler;
import common.StyleHandler;
import java.io.IOException;
import util.Version;

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

    private static Class<?> implementingClass; // for loading from the correct module
    private static InternalPropertiesHandler pomHandler;
    private static ExternalPropertiesHandler settingsHandler;
    private static StyleHandler styleHandler;

    public static Class<?> getImplementingClass() {
        return implementingClass;
    }

    public static InternalPropertiesHandler getPomHandler() {
        return pomHandler;
    }

    public static ExternalPropertiesHandler getSettingsHandler() {
        return settingsHandler;
    }

    public static StyleHandler getStyleHandler() {
        return styleHandler;
    }

    public abstract void start();

    /**
     * Override this method if you want to change the default style sheet.
     */
    public StyleHandler makeStyleHandler() {
        return StyleHandler.with(StyleHandler.PreBuiltStyle.JMETRO);
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

        try {
            pomHandler = InternalPropertiesHandler.newHandler("pom");

            InternalPropertiesHandler fallbackSettings; // TODO: implement try with resource pattern
            try {
                fallbackSettings = InternalPropertiesHandler.newHandler("settings");
            } catch (IOException e) {
                fallbackSettings = null;
            }
            settingsHandler = ExternalPropertiesHandler.newHandler("settings", fallbackSettings);
        }
        catch (IOException e) {
            throw new FatalError(e);
        }

        styleHandler = app.makeStyleHandler();
        styleHandler.applyStyleToApplication();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // store settings in case app didn't do it
            if (settingsHandler != null) {
                try {
                    settingsHandler.save();
                } catch (IOException ignored) {} // don't display error as shutdown shouldn't be delayed
            }
        }));

        app.start();
    }

    public static String getAppName() {
        return pomHandler.getProperties().getProperty("name");
    }

    public static Version getAppVersion() {
        String version = pomHandler.getProperties().getProperty("version");
        return new Version(version);
    }
}
