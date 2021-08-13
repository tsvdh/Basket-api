package common;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.ShlObj;
import com.sun.jna.platform.win32.WinDef;

public class PathHandler { // TODO: allow for multiple installation possibilities

    // This class should not be instantiated
    private PathHandler() {}

    public static final String LAUNCHER_NAME = "Basket";

    static String getInternalPropertiesPath(String fileName) {
        return "/properties/" + fileName + ".properties";
    }

    static String getExternalPropertiesPath(String fileName, String appName) {
        String folderPath;
        if (appName.equals("Basket")) {
            folderPath = getBasketHomePath() + "/resources/private";
        } else {
            folderPath = getAppdataPath(appName);
        }
        return folderPath + "/" + fileName + ".properties";
    }

    private static String getPath(int location) {
        char[] pszPath = new char[WinDef.MAX_PATH];
        Shell32.INSTANCE.SHGetFolderPath(null, location, null, ShlObj.SHGFP_TYPE_CURRENT, pszPath);
        String path = Native.toString(pszPath);
        return path.replace('\\', '/');
    }

    public static String getAppdataPath(String appName) {
        return getPath(ShlObj.CSIDL_APPDATA) + "/" + LAUNCHER_NAME + "/" + appName;
    }

    public static String getProgramFilesPath() {
        return getPath(ShlObj.CSIDL_PROGRAM_FILES) + "/" + LAUNCHER_NAME;
    }

    public static String getBasketHomePath() {
        String userHome = System.getProperty("user.home").replace('\\', '/');
        return userHome + "/" + LAUNCHER_NAME;
    }

    public static String getDesktopPath() {
        return getPath(ShlObj.CSIDL_DESKTOP);
    }

    public static String getStartupPath() {
        return getPath(ShlObj.CSIDL_STARTUP);
    }

    public static String getInternalImagePath(String fileName) {
        return "/images/" + fileName;
    }

    public static String getIconPath() {
        return getInternalImagePath("icon.png");
    }

    public static String getInternalCSS(String fileName) {
        return "/style/" + fileName + ".css";
    }

    public static String getExternalCSS(String fileName) {
        return getBasketHomePath() + "/resources/public/style/" + fileName + ".css";
    }
}
