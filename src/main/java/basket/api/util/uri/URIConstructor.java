package basket.api.util.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class URIConstructor {

    public static URI makeURI(String uri) throws BadURIException {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new BadURIException(e);
        }
    }

    public static URI toURI(URL url) throws BadURIException {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new BadURIException(e);
        }
    }
}
