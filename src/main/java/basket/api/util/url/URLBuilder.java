package basket.api.util.url;

import basket.api.util.LinkBuilder;
import java.net.URL;

import static basket.api.util.url.URLConstructor.makeURL;

public class URLBuilder extends LinkBuilder<URLBuilder> {

    public URLBuilder(String uri) {
        super(uri);
    }

    public URL build() {
        return makeURL(this.getLink());
    }
}
