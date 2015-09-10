package ftg.application.cdi;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

final class LoggingSubscriberExceptionHandler implements SubscriberExceptionHandler {

    private final Logger logger;

    /**
     * @param identifier a brief name for this bus, for logging purposes. Should
     *                   be a valid Java identifier.
     */
    public LoggingSubscriberExceptionHandler(String identifier) {
        logger = LogManager.getLogger(EventBus.class.getName() + "." + checkNotNull(identifier));
    }

    @Override
    public void handleException(Throwable exception,
                                SubscriberExceptionContext context) {
        logger.error("Could not dispatch event {} from {} to {}",
                context.getEvent(),
                context.getSubscriber(),
                context.getSubscriberMethod(),
                exception);
    }
}
