package ftg.application.gui.support;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import javafx.scene.Parent;

public abstract class AbstractView implements View {

    private final Supplier<Parent> parentLoader = Suppliers.memoize(this::loadParent);

    @Override
    public final Parent getParent() {
        return parentLoader.get();
    }

    protected abstract Parent createView();

    protected abstract void configureView();

    private Parent loadParent() {
        final Parent parent = createView();
        configureView();
        return parent;
    }
}
