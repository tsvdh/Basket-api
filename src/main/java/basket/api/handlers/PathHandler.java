package basket.api.handlers;

import basket.api.app.BasketApp;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.ShlObj;
import com.sun.jna.platform.win32.WinDef;
import java.nio.file.Path;

public class PathHandler {

    // This class should not be instantiated
    private PathHandler() {}

    public static final String LAUNCHER_NAME = "Basket";

    /**
     * Returns the path of the given location
     * @param location the location to get, use a static attribute of {@code ShlObj} from {@code jna}
     * @return the desired path
     */
    public static Path getPath(int location) {
        char[] pszPath = new char[WinDef.MAX_PATH];
        Shell32.INSTANCE.SHGetFolderPath(null, location, null, ShlObj.SHGFP_TYPE_CURRENT, pszPath);
        return Path.of(Native.toString(pszPath));
    }

    // --- External ---

    public static Path getBasketHomePath() {
        return getPath(ShlObj.CSIDL_APPDATA).resolve(LAUNCHER_NAME);
    }

    public static Path getAppDataPath(String appId) {
        return getBasketHomePath().resolve("apps/data").resolve(appId);
    }

    public static Path getAppLibraryPath(String appId) {
        return getBasketHomePath().resolve("apps/library").resolve(appId);
    }

    public static Path getExternalFilePath(String fileName) {
        String appId = BasketApp.getAppId();

        Path folderPath = getAppDataPath(appId);

        return folderPath.resolve(fileName);
    }

    // --- Internal ---

    public static Path getInternalDataPath(String fileName) {
        return Path.of("/data/" + fileName);
    }

    public static Path getInternalImagesPath(String fileName) {
        return Path.of("/images/" + fileName);
    }

    public static Path getIconPath() {
        return getInternalImagesPath("icon.png");
    }

    public static Path getInternalStylePath(String fileName) {
        return Path.of("/style/" + fileName);
    }
}
