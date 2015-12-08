package ftg.model.world;

import ftg.model.person.Person;
import javaslang.collection.Seq;
import javaslang.collection.Vector;

public final class Buckets {

    private Seq<Person> allLivingPersons = Vector.empty();
    private Seq<Person> livingMales = Vector.empty();
    private Seq<Person> livingFemales = Vector.empty();
    private Seq<Person> singleMales = Vector.empty();
    private Seq<Person> singleFemales = Vector.empty();

    private Seq<Person> notPregnantFemales = Vector.empty();
    private Seq<Person> pregnantFemales = Vector.empty();

    public Seq<Person> livingPersons() {
        return allLivingPersons;
    }

    public Seq<Person> livingMales() {
        return livingMales;
    }

    public Seq<Person> livingFemales() {
        return livingFemales;
    }

    public Seq<Person> singleMales() {
        return singleMales;
    }

    public Seq<Person> singleFemales() {
        return singleFemales;
    }

    public Seq<Person> notPregnantFemales() {
        return notPregnantFemales;
    }

    public Seq<Person> pregnantFemales() {
        return pregnantFemales;
    }

    Person alive(Person person) {

        allLivingPersons = allLivingPersons.append(person);

        if (person.getSex() == Person.Sex.MALE) {
            livingMales = livingMales.append(person);
        } else {
            livingFemales = livingFemales.append(person);
        }

        return single(notPregnant(person));
    }

    Person dead(Person person) {
        allLivingPersons = allLivingPersons.removeAll(person);

        if (person.getSex() == Person.Sex.MALE) {
            livingMales = livingMales.removeAll(person);
        } else {
            livingFemales = livingFemales.removeAll(person);
            pregnantFemales = pregnantFemales.removeAll(person);
        }

        return single(notPregnant(person));
    }

    Person married(Person person) {
        if (person.getSex() == Person.Sex.MALE) {
            singleMales = singleMales.removeAll(person);
        } else {
            singleFemales = singleFemales.removeAll(person);
        }

        return person;
    }

    Person single(Person person) {
        if (person.getSex() == Person.Sex.MALE) {
            singleMales = singleMales.append(person);
        } else {
            singleFemales = singleFemales.append(person);
        }

        return person;
    }

    Person pregnant(Person person) {
        if (person.getSex() == Person.Sex.FEMALE) {
            notPregnantFemales = notPregnantFemales.removeAll(person);
            pregnantFemales = pregnantFemales.removeAll(person);
        }

        return person;
    }

    Person notPregnant(Person person) {
        if (person.getSex() == Person.Sex.FEMALE) {
            notPregnantFemales = notPregnantFemales.append(person);
            pregnantFemales = pregnantFemales.removeAll(person);
        }

        return person;
    }
}
