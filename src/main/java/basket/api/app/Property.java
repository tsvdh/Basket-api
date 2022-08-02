package basket.api.app;

import java.util.function.Function;

@Deprecated
@FunctionalInterface
public interface Property {

    Function<String, Object> getParser();
}
