package basket.api.util.url;

import basket.api.util.Util;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

public class URLConstructor {

    public static URL makeURL(String url) throws BadURLException {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new BadURLException(e);
        }
    }

    public static URL makeURLWithParams(String url, Map<String, Object> params) throws BadURLException {
        return makeURL(Util.addParamsToUri(url, params));
    }

    public static URLBuilder newURLBuilder(String url) {
        return new URLBuilder(url);
    }

    public static URL toURL(URI uri) throws BadURLException {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new BadURLException(e);
        }
    }
}
