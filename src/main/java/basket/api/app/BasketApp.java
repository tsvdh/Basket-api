package basket.api.app;

import basket.api.handlers.FileHandler;
import basket.api.handlers.JSONHandler;
import basket.api.handlers.PathHandler;
import basket.api.handlers.StyleHandler;
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
import org.jetbrains.annotations.Nullable;

import static basket.api.util.uri.URIConstructor.toURI;
import static java.lang.Runtime.getRuntime;

/**
 * The main class of the Basket API.
 * Use this class by implementing the {@code start} method, and then calling the {@code launch} method.
 * <p>
 * <p>
 * Some assumptions are made about the structure of your project:
 * <ul>
 *     <li>All resources files are in a directory tagged as a resources folder.</li>
 *     <li>Your resource folders are opened to the api module ({@code basket.api}) in the {@code module-info.java} file.</li>
 *     <li>Data ({@code .json}) files are in the {@code data} folder.
 *          <br> {@code _info.json} is a reserved file name, do not use it yourself.</li>
 *     <li>Optionally, you can put {@code settings.json} in the data folder to be used as standard settings.</li>
 *     <li>Images or other visual data are in the {@code images} folder.</li>
 *     <li>The main icon of your app is called {@code icon.png}</li>
 *     <li>Any style files ({@code .css}, {@code .ttf}) are in the {@code style} folder.</li>
 * </ul>
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
 *         protected @Nullable <NewT> TypeReference<NewT> makeSettingsObjectType() {
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
        return StyleHandler.with(StyleHandler.PreBuiltStyle.JMETRO);
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
            Path internalPath = PathHandler.getInternalDataPath("settings.json");

            InputStream internalStream = implementingClass.getResourceAsStream(Util.pathToJavaString(internalPath));
            if (internalStream == null) {
                throw new IOException("Could not find " + internalPath);
            }

            Path externalPath = app.makeSettingsPath();

            if (!externalPath.toFile().exists()) {
                FileHandler.makeFileDirectories(externalPath);
                Files.copy(internalStream, externalPath);
            }

            settingsHandler = new JSONHandler<>(externalPath);
        }
        catch (IOException e) {
            System.err.println("Could not create settings handler: " + e.getMessage());
        }

        if (settingsHandler != null) {
            settingsHandler.convertObjectTo(app.getSettingsObjectClass());

            getRuntime().addShutdownHook(new Thread(() -> {
                // store settings in case app didn't do it
                try {
                    settingsHandler.save();
                } catch (IOException e) {
                    // don't display error as shutdown shouldn't be delayed
                }
            }));
        }

        styleHandler = app.makeStyleHandler();
        styleHandler.applyStyleToApplication();

        app.start();
    }

    public static String getAppId() {
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

        if (currentPath.getParent().endsWith("Basket")) {
            return ".self";
        } else {
            return currentPath.getParent().getFileName().toString();
        }
    }
}
