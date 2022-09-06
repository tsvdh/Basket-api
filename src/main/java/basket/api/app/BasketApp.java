package basket.api.app;

import basket.api.handlers.FileHandler;
import basket.api.handlers.JSONHandler;
import basket.api.handlers.PathHandler;
import basket.api.handlers.StyleHandler;
import basket.api.handlers.StyleHandler.ApiStyle;
import basket.api.handlers.StyleHandlerBuilder;
import basket.api.util.FatalError;
import basket.api.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import jfxtras.styles.jmetro.Style;
import org.jetbrains.annotations.Nullable;

import static basket.api.util.uri.URIConstructor.toURI;

/**
 * For more info about the general API, see the <a href="https://github.com/tsvdh/Basket-api">README</a>.
 * <p>
 * The main class of the Basket API.
 * Use this class by implementing the {@code start} method, and then calling the {@code launch} method.
 * <p>
 * <p>
 * Two handler objects are made available through this class:
 * <ul>
 *     <li>{@code settingsHandler}, handler of the external settings ({@code .json}) file.</li>
 *     <li>{@code styleHandler}, handles applying style to JavaFX scenes.</li>
 * </ul>
 * <p>
 * Override {@code make-} methods to modify default behaviour.
 * <p>
 * <p>
 * Use the API with this pattern if you are using JavaFX.
 * <pre>{@code
 * public class Main extends Application {
 *
 *     public static class MyApp extends BasketApp {
 *
 *         @Override
 *         protected @Nullable Class<?> getSettingsObjectClass() {
 *             return null;
 *         }
 *
 *         @Override
 *         protected void start() {
 *             // entry point for program
 *         }
 *     }
 *
 *     @Override
 *     public void start(Stage primaryStage) {
 *         MyApp.launch(MyApp.class);
 *     }
 *
 *     public static void main(String[] args) {
 *         Application.launch();
 *     }
 * }
 * }</pre>
 */
public abstract class BasketApp {

    private static Class<? extends BasketApp> implementingClass; // for loading from the correct module

    private static JSONHandler<Object> settingsHandler;

    private static StyleHandler styleHandler;

    private static String appId;

    public static Class<?> getImplementingClass() {
        return implementingClass;
    }

    public static JSONHandler<Object> getSettingsHandler() {
        return settingsHandler;
    }

    public static StyleHandler getStyleHandler() {
        return styleHandler;
    }

    /**
     * Returns a class to convert the settings handler to.
     * @return A {@code Class} or null
     */
    protected abstract @Nullable Class<?> getSettingsObjectClass();

    /**
     * Contains the code to start your app.
     */
    protected abstract void start();

    /**
     * Override this method if you want to change the default style sheet.
     * @return the style handler to use for the app
     */
    protected StyleHandler makeStyleHandler() {
        return new StyleHandlerBuilder()
                .setApiStyle(ApiStyle.JMETRO_TWEAKED)
                .setJMetroStyle(Style.LIGHT)
                .build();
    }

    /**
     * Override this method if you want to use a different settings file.
     * @return the new {@code Path}
     */
    protected Path makeSettingsPath() {
        return PathHandler.getExternalFilePath("settings.json");
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

    /**
     * Call this method to launch the app.
     * This method must be called from a class implementing {@code BasketApp}.
     * You can call this method from another class with {@link #launch(Class)}.
     */
    public static void launch() {
        Class<?> callingClass = getCallingClass();
        Class<? extends BasketApp> implementingClass;

        try {
            implementingClass = callingClass.asSubclass(BasketApp.class);
        } catch (ClassCastException e) {
            throw new FatalError("%s must extend %s".formatted(callingClass.getName(), BasketApp.class.getName()), e);
        }

        launch(implementingClass);
    }

    /**
     * Call this method to launch the app from another class.
     * @param implementingClass the class that implements {@code BasketApp}
     */
    public static void launch(Class<? extends BasketApp> implementingClass) {
        BasketApp.implementingClass = implementingClass;

        BasketApp app;
        try {
            app = implementingClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new FatalError("Could not instantiate "+ implementingClass.getName(), e);
        }

        try {
            Path externalPath = app.makeSettingsPath();

            if (!externalPath.toFile().exists()) {
                Path internalPath = PathHandler.getInternalDataPath("settings.json");

                InputStream internalStream = implementingClass.getResourceAsStream(Util.pathToJavaString(internalPath));
                if (internalStream == null) {
                    throw new IOException("Could not find " + internalPath);
                }

                FileHandler.makeFileDirectories(externalPath);
                Files.copy(internalStream, externalPath);
            }

            settingsHandler = JSONHandler.read(externalPath);
        }
        catch (IOException e) {
            System.err.println("Could not create settings handler: " + e.getMessage());
        }

        if (settingsHandler != null) {
            settingsHandler.convertObjectTo(app.getSettingsObjectClass());
        }

        styleHandler = app.makeStyleHandler();
        styleHandler.applyStyleToApplication();

        app.start();
    }

    public static String getAppId() {
        if (appId != null) {
            return appId;
        }

        URL url = BasketApp.class.getResource(BasketApp.class.getSimpleName() + ".class");

        if (url == null) {
            throw new FatalError("Cannot find current class");
        }

        var env = new HashMap<String, String>();
        env.put("create", "true");

        FileSystem jarFs;
        try {
            jarFs = FileSystems.newFileSystem(toURI(url), env);
        } catch (IOException e) {
            throw new FatalError(e);
        }

        Path currentPath = Path.of(jarFs.toString());

        try {
            jarFs.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        if (!currentPath.startsWith(PathHandler.getBasketHomePath().toAbsolutePath())) {
            // not in installed directory, so running locally
            return ".self";
        }

        while (!currentPath.endsWith("image")) {
            currentPath = currentPath.getParent();
        }

        String foundId;

        if (currentPath.getParent().endsWith("Basket")) {
            foundId = ".self";
        } else {
            foundId = currentPath.getParent().getFileName().toString();
        }

        appId = foundId;
        return appId;
    }
}
