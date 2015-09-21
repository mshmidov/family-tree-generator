package ftg.application.gui.dashboard;

import com.google.inject.Inject;
import ftg.commons.cdi.PersonCounter;
import ftg.commons.range.IntegerRange;
import ftg.model.time.TredecimalDate;
import ftg.model.world.PersonIntroductionEvent;
import ftg.simulation.RandomModel;
import ftg.simulation.configuration.Configuration;
import ftg.simulation.configuration.Country;
import ftg.simulation.configuration.naming.NamingSystem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;

public final class PeopleGeneratorService extends Service<List<PersonIntroductionEvent>> {

    private final AtomicLong personCounter;

    private final List<Country> countries;

    private final ObjectProperty<WorkOrder> workOrder = new SimpleObjectProperty<>();

    @Inject
    public PeopleGeneratorService(Configuration configuration, @PersonCounter AtomicLong personCounter) {
        this.personCounter = personCounter;
        this.countries = configuration.getCountries();
        workOrder.addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        checkState(!isRunning());
                        reset();
                        start();
                    }
                }
        );
    }

    public ObjectProperty<WorkOrder> workOrderProperty() {
        return workOrder;
    }

    @Override
    protected Task<List<PersonIntroductionEvent>> createTask() {
        return new IntroduceRandomPeopleTask(personCounter::incrementAndGet, countries, workOrder.getValue());
    }

    public static final class WorkOrder {
        private final int count;
        private final TredecimalDate currentDate;

        public WorkOrder(int count, TredecimalDate currentDate) {
            this.count = count;
            this.currentDate = currentDate;
        }

        public int getCount() {
            return count;
        }

        public TredecimalDate getCurrentDate() {
            return currentDate;
        }
    }

    private static final class IntroduceRandomPeopleTask extends Task<List<PersonIntroductionEvent>> {

        private static final Logger LOGGER = LogManager.getLogger(IntroduceRandomPeopleTask.class);

        private final RandomModel randomModel = new RandomModel();

        private final Supplier<Long> personCounter;
        private final List<Country> countries;
        private final WorkOrder workOrder;

        public IntroduceRandomPeopleTask(Supplier<Long> personCounter, List<Country> countries, WorkOrder workOrder) {
            this.personCounter = personCounter;
            this.countries = countries;
            this.workOrder = workOrder;
        }

        @Override
        public List<PersonIntroductionEvent> call() throws Exception {

            final IntegerRange age = IntegerRange.inclusive(17, 50);

            final List<PersonIntroductionEvent> events = new ArrayList<>(countries.size() * workOrder.getCount());

            for (Country country : countries) {
                final NamingSystem namingSystem = country.getNamingSystem();

                events.addAll(
                        namingSystem.getUniqueSurnames().stream()
                                .limit(workOrder.getCount())
                                .map(surname -> randomModel.newPersonData(personCounter.get(), country, surname, age, workOrder.getCurrentDate()))
                                .map(personData -> new PersonIntroductionEvent(workOrder.getCurrentDate(), personData))
                                .collect(Collectors.toList()));

            }

            LOGGER.debug("Generated {} events", events.size());

            return events;
        }
    }
}
