package ftg.commons;

import com.google.common.collect.ForwardingSet;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ObservableSetWrapper<T> extends ForwardingSet<T> {

    private final Set<T> delegate;
    private final Class<T> valueClass;

    private final Set<Consumer<T>> additionListeners = new HashSet<>();
    private final Set<Consumer<T>> removalListeners = new HashSet<>();

    public ObservableSetWrapper(Set<T> delegate, Class<T> valueClass) {
        this.delegate = delegate;
        this.valueClass = valueClass;
    }

    public ObservableSetWrapper(Set<T> delegate, Class<T> valueClass, Consumer<T> additionListener, Consumer<T> removalListener) {
        this.delegate = delegate;
        this.valueClass = valueClass;
        additionListeners.add(additionListener);
        removalListeners.add(removalListener);
    }

    @Override
    protected Set<T> delegate() {
        return delegate;
    }

    @Override
    public boolean add(T element) {
        final boolean added = super.add(element);
        if (added) {
            additionListeners.forEach(listener -> listener.accept(element));
        }
        return added;
    }


    @Override
    public boolean remove(Object object) {
        final boolean removed = super.remove(object);
        if (removed && valueClass.isAssignableFrom(object.getClass())) {
            removalListeners.forEach(listener -> listener.accept(valueClass.cast(object)));
        }
        return removed;
    }

    public void addAdditionListener(Consumer<T> listener) {
        additionListeners.add(listener);
    }

    public void removeAdditionListener(Consumer<T> listener) {
        additionListeners.remove(listener);
    }

    public void addRemovalListener(Consumer<T> listener) {
        removalListeners.add(listener);
    }

    public void removeRemovalListener(Consumer<T> listener) {
        removalListeners.remove(listener);
    }
}
