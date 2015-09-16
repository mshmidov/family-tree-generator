package ftg.application.gui.support;

import ftg.commons.Action;
import javafx.concurrent.Task;

public final class QuickTask extends Task<Void> {

    private final Action action;

    public static QuickTask of(Action action) {
        return new QuickTask(action);
    }

    public QuickTask(Action action) {
        this.action = action;
    }

    @Override
    protected Void call() throws Exception {
        action.perform();
        return null;
    }
}
