package ftg.application.cdi;

import com.google.common.collect.ImmutableList;
import com.google.inject.spi.ProvisionListener;

import java.util.List;
import java.util.function.Consumer;

final class CustomProvisionListener implements ProvisionListener {

    private final List<Consumer<Object>> consumers;

    @SafeVarargs
    public CustomProvisionListener(Consumer<Object>... consumers) {
        this.consumers = ImmutableList.copyOf(consumers);
    }

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {
        final T object = provision.provision();
        consumers.forEach(consumer -> consumer.accept(object));
    }
}
