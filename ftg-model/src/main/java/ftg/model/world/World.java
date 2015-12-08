package ftg.model.world;

import static ftg.commons.functional.BooleanLogic.not;

import ftg.commons.functional.Checked;
import ftg.model.event.BirthEvent;
import ftg.model.event.ConceptionEvent;
import ftg.model.event.DeathEvent;
import ftg.model.event.Event;
import ftg.model.event.MarriageEvent;
import ftg.model.event.PersonIntroductionEvent;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.person.relation.Marriage;
import ftg.model.person.relation.Parentage;
import ftg.model.person.relation.RelationFactory;
import ftg.model.person.relation.Widowhood;
import ftg.model.person.state.Death;
import ftg.model.person.state.Pregnancy;
import ftg.model.time.TredecimalDate;
import ftg.model.world.country.Country;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.collection.Set;
import javaslang.collection.Stream;
import javaslang.collection.Vector;
import javaslang.control.Option;

public final class World {

    private final Set<Country> countries;

    private final PersonFactory personFactory;
    private final RelationFactory relationFactory;
    private TredecimalDate currentDate = new TredecimalDate(-1);

    private Vector<Event> events = Vector.empty();

    private Map<String, Person> persons = HashMap.empty();

    private final Buckets buckets = new Buckets();


    public World(Set<Country> countries, PersonFactory personFactory, RelationFactory relationFactory) {
        this.countries = countries;
        this.personFactory = personFactory;
        this.relationFactory = relationFactory;
    }

    public Set<Country> countries() {
        return countries;
    }

    public TredecimalDate getCurrentDate() {
        return currentDate;
    }

    public TredecimalDate nextDay() {
        currentDate = currentDate.plusDays(1);
        return currentDate;
    }

    public Seq<Event> events() {
        return events;
    }

    public Stream<Person> persons() {
        return Stream.ofAll(persons.values());
    }

    public Buckets buckets() {
        return buckets;
    }

    public Person getPerson(String id) {
        return persons.get(id).orElseThrow(() -> new IllegalArgumentException("No person found by id " + id));
    }

    public Person submitPersonIntroduction(PersonIntroductionEvent event) {
        events = events.append(event);
        final Person person = event.apply(this, personFactory, relationFactory);
        persons = persons.put(
            Checked.argument(person.getId(), not(persons::containsKey), "Person %s is already added"),
            buckets.alive(person));

        return person;
    }

    public Option<Parentage> submitBirth(Option<BirthEvent> event) {
        return event.isEmpty() ? Option.none() : Option.of(submitBirth(event.get()));
    }

    public Parentage submitBirth(BirthEvent event) {

        events = events.append(event);
        final Parentage parentage = event.apply(this, personFactory, relationFactory);

        persons = persons.put(
            Checked.argument(parentage.getChild().getId(), not(persons::containsKey), "Person %s is already added"),
            buckets.alive(parentage.getChild()));

        buckets.notPregnant(parentage.getMother());

        return parentage;
    }

    public Marriage submitMarriage(MarriageEvent event) {
        events = events.append(event);
        final Marriage marriage = event.apply(this, personFactory, relationFactory);

        buckets.married(marriage.getHusband());
        buckets.married(marriage.getWife());

        return marriage;
    }

    public Option<Pregnancy> submitPregnancy(Option<ConceptionEvent> event) {
        return event.isEmpty() ? Option.none() : Option.of(submitPregnancy(event.get()));
    }

    public Pregnancy submitPregnancy(ConceptionEvent event) {
        events = events.append(event);
        buckets.pregnant(getPerson(event.getMotherId()));

        return event.apply(this, personFactory, relationFactory);
    }

    public Tuple2<Death, Set<Widowhood>> submitDeath(DeathEvent event) {
        events = events.append(event);
        final Tuple2<Death, Set<Widowhood>> result = event.apply(this, personFactory, relationFactory);

        buckets.dead(getPerson(event.getDeceasedId()));

        for (Widowhood widowhood : result._2) {
            buckets.single(widowhood.getWidow());
        }

        return result;
    }


}
