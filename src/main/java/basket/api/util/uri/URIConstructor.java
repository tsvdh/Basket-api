package basket.api.util.uri;

import basket.api.util.Util;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class URIConstructor {

    public static URI makeURI(String uri) throws BadURIException {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new BadURIException(e);
        }
    }

    public static URI makeURIWithParams(String uri, Map<String, Object> params) {
        return makeURI(Util.addParamsToUri(uri, params));
    }

    public static URIBuilder newURIBuilder(String uri) {
        return new URIBuilder(uri);
    }

    public static URI toURI(URL url) throws BadURIException {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new BadURIException(e);
        }
    }
}
