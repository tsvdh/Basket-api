package basket.api.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.collections.ListChangeListener;
import javafx.stage.Window;

public class Threads {

    public static void addExecutorShutdownHookOnJavaFXApplicationClose(ExecutorService executorService) {
        Window.getWindows().addListener((ListChangeListener<? super Window>) event -> {
            event.next();
            if (event.getList().size() == 0) {
                executorService.shutdown();
                boolean terminated;
                try {
                    terminated = executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    terminated = false;
                }
                if (!terminated) {
                    executorService.shutdownNow();
                }
            }
        });
    }
}
