package ftg.application.gui.support;

import javafx.application.Platform;
import javafx.concurrent.Task;

public final class QuickPlatformTask extends Task<Void> {

    private final Runnable runnable;

    public static QuickPlatformTask of(Runnable runnable) {
        return new QuickPlatformTask(runnable);
    }

    public QuickPlatformTask(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    protected Void call() throws Exception {
        Platform.runLater(runnable);
        return null;
    }
}
