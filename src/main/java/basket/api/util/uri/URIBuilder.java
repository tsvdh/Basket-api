package basket.api.util.uri;

import basket.api.util.LinkBuilder;
import java.net.URI;

import static basket.api.util.uri.URIConstructor.makeURI;

public class URIBuilder extends LinkBuilder<URIBuilder> {

    public URIBuilder(String uri) {
        super(uri);
    }

    public URI build() {
        return makeURI(this.getLink());
    }
}
