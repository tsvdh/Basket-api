package basket.api.util.url;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class URLConstructor {

    public static URL makeURL(String url) throws BadURLException {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new BadURLException(e);
        }
    }

    public static URL toURL(URI uri) throws BadURLException {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new BadURLException(e);
        }
    }
}
