package basket.api.common;

import basket.api.app.BasketApp;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.ShlObj;
import com.sun.jna.platform.win32.WinDef;
import java.nio.file.Path;

public class PathHandler { // TODO: allow for multiple installation possibilities

    // This class should not be instantiated
    private PathHandler() {}

    public static final String LAUNCHER_NAME = "Basket";

    public static Path getInternalPropertiesPath(String fileName) {
        return Path.of("/properties/" + fileName + ".properties");
    }

    public static Path getExternalPropertiesPath(String fileName) {
        String appName = BasketApp.getAppName();
        Path folderPath;
        if (appName.equals("Basket")) {
            folderPath = getBasketHomePath().resolve("resources/private");
        } else {
            folderPath = getAppFolderPath(appName);
        }
        return folderPath.resolve(fileName + ".properties");
    }

    private static Path getPath(int location) {
        char[] pszPath = new char[WinDef.MAX_PATH];
        Shell32.INSTANCE.SHGetFolderPath(null, location, null, ShlObj.SHGFP_TYPE_CURRENT, pszPath);
        return Path.of(Native.toString(pszPath));
    }

    public static Path getAppFolderPath(String appName) {
        return getPath(ShlObj.CSIDL_APPDATA).resolve(LAUNCHER_NAME + "/" + appName);
    }

    public static Path getProgramFilesPath() {
        return getPath(ShlObj.CSIDL_PROGRAM_FILES).resolve(LAUNCHER_NAME);
    }

    private static Path getBasketHomePath() {
        String userHome = System.getProperty("user.home");
        return Path.of(userHome + "/" + LAUNCHER_NAME);
    }

    public static Path getAppHomePath(String appName) {
        Path basketHome = getBasketHomePath();
        return basketHome.resolve("library/" + appName);
    }

    public static Path getDesktopPath() {
        return getPath(ShlObj.CSIDL_DESKTOP);
    }

    public static Path getStartupPath() {
        return getPath(ShlObj.CSIDL_STARTUP);
    }

    public static Path getInternalImagePath(String fileName) {
        return Path.of("/images/" + fileName);
    }

    public static Path getIconPath() {
        return getInternalImagePath("icon.png");
    }

    public static Path getInternalCSS(String fileName) {
        return Path.of("/style/" + fileName + ".css");
    }

    public static Path getExternalCSS(String fileName) {
        return getBasketHomePath().resolve("resources/public/style/" + fileName + ".css");
    }
}
