package basket.api.util;

public abstract class LinkBuilder<T extends LinkBuilder<T>> {

    private final StringBuilder builder;

    public LinkBuilder(String link) {
        this.builder = new StringBuilder(link);
    }

    public T addKeyValuePair(String key, Object value) {
        char preChar;
        if (builder.indexOf("?") == -1) {
            preChar = '?';
        } else {
            preChar = '&';
        }

        builder.append("%s%s=%s".formatted(preChar, key, value));

        //noinspection unchecked
        return (T) this;
    }

    protected String getLink() {
        return this.builder.toString();
    }
}
