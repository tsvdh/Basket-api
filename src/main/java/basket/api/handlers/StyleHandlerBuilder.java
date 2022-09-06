package basket.api.handlers;

import basket.api.handlers.StyleHandler.ApiStyle;
import java.util.LinkedList;
import java.util.List;
import jfxtras.styles.jmetro.Style;

public class StyleHandlerBuilder {

    private Style jMetroStyle;
    private boolean useFXSkins;
    private ApiStyle apiStyle;
    private List<String> filePaths;

    public StyleHandlerBuilder setJMetroStyle(Style jMetroStyle) {
        this.jMetroStyle = jMetroStyle;
        return this;
    }

    public StyleHandlerBuilder setUseFXSkins(boolean useFXSkins) {
        this.useFXSkins = useFXSkins;
        return this;
    }

    public StyleHandlerBuilder setApiStyle(ApiStyle apiStyle) {
        this.apiStyle = apiStyle;
        return this;
    }

    public StyleHandlerBuilder addFilePath(String filePath) {
        if (filePaths == null) {
            filePaths = new LinkedList<>();
        }
        filePaths.add(filePath);
        return this;
    }

    public StyleHandler build() {
        return new StyleHandler(jMetroStyle, useFXSkins, apiStyle, filePaths);
    }
}
