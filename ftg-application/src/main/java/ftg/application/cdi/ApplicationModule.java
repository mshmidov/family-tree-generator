package ftg.application.cdi;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import ftg.application.bootstrap.simulation.SimulationConfigLoader;
import ftg.commons.cdi.Identifier;
import ftg.commons.cdi.Namespace;
import ftg.simulation.configuration.SimulationConfiguration;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;


public class ApplicationModule extends AbstractModule {

    private final AtomicLong identifier = new AtomicLong(0);

    @Override
    protected void configure() {

        bind(SimulationConfiguration.class)
                .toProvider(() -> new SimulationConfigLoader("./config/").loadConfiguration("simulation-config.yml"))
                .in(Singleton.class);

        bind(new TypeLiteral<Supplier<String>>() {}).annotatedWith(Identifier.class)
            .toInstance(() -> String.valueOf(identifier.incrementAndGet()));

        bind(String.class).annotatedWith(Namespace.class)
            .toInstance(getRandomNamespace() + String.valueOf(ThreadLocalRandom.current().nextLong(10_000, 100_000)));
    }

    private String getRandomNamespace() {
        final String[] namespaces = new String[] {
            "Aardvark",
            "Albatross",
            "Alligator",
            "Alpaca",
            "Ant",
            "Anteater",
            "Antelope",
            "Ape",
            "Armadillo",
            "Baboon",
            "Badger",
            "Barracuda",
            "Bat",
            "Bear",
            "Beaver",
            "Bee",
            "Bison",
            "Boar",
            "Buffalo",
            "Butterfly",
            "Camel",
            "Caribou",
            "Cassowary",
            "Cat",
            "Caterpillar",
            "Chamois",
            "Cheetah",
            "Chicken",
            "Chimpanzee",
            "Chinchilla",
            "Chough",
            "Coati",
            "Cobra",
            "Cockroach",
            "Cod",
            "Cormorant",
            "Coyote",
            "Crab",
            "Crane",
            "Crocodile",
            "Crow",
            "Curlew",
            "Deer",
            "Dinosaur",
            "Dog",
            "Dogfish",
            "Dolphin",
            "Donkey",
            "Dotterel",
            "Dove",
            "Dragonfly",
            "Duck",
            "Dugong",
            "Dunlin",
            "Eagle",
            "Echidna",
            "Eel",
            "Eland",
            "Elephant",
            "Elk",
            "Emu",
            "Falcon",
            "Ferret",
            "Finch",
            "Flamingo",
            "Fly",
            "Fox",
            "Frog",
            "Gaur",
            "Gazelle",
            "Gerbil",
            "Giraffe",
            "Gnat",
            "Gnu",
            "Goat",
            "Goldfinch",
            "Goosander",
            "Goose",
            "Gorilla",
            "Goshawk",
            "Grasshopper",
            "Grouse",
            "Guanaco",
            "Gull",
            "Hamster",
            "Hare",
            "Hawk",
            "Hedgehog",
            "Heron",
            "Herring",
            "Hippopotamus",
            "Hornet",
            "Horse",
            "Hummingbird",
            "Hyena",
            "Ibex",
            "Ibis",
            "Jackal",
            "Jaguar",
            "Jay",
            "Jellyfish",
            "Kangaroo",
            "Kinkajou",
            "Koala",
            "Kouprey",
            "Kudu",
            "Lapwing",
            "Lark",
            "Lemur",
            "Leopard",
            "Lion",
            "Llama",
            "Lobster",
            "Locust",
            "Loris",
            "Louse",
            "Lyrebird",
            "Magpie",
            "Manatee",
            "Mandrill",
            "Mink",
            "Mole",
            "Mongoose",
            "Monkey",
            "Moose",
            "Mosquito",
            "Mouse",
            "Narwhal",
            "Newt",
            "Nightingale",
            "Octopus",
            "Okapi",
            "Opossum",
            "Ostrich",
            "Otter",
            "Owl",
            "Oyster",
            "Panda",
            "Panther",
            "Parrot",
            "Partridge",
            "Peafowl",
            "Pelican",
            "Penguin",
            "Pheasant",
            "Pig",
            "Pigeon",
            "Pony",
            "Porcupine",
            "Porpoise",
            "Quail",
            "Quelea",
            "Quetzal",
            "Rabbit",
            "Raccoon",
            "Ram",
            "Rat",
            "Raven",
            "Reindeer",
            "Rhinoceros",
            "Rook",
            "Salamander",
            "Salmon",
            "Sandpiper",
            "Sardine",
            "Seahorse",
            "Seal",
            "Shark",
            "Sheep",
            "Shrew",
            "Skunk",
            "Sloth",
            "Snail",
            "Snake",
            "Spider",
            "Squirrel",
            "Starling",
            "Swan",
            "Tapir",
            "Tarsier",
            "Termite",
            "Tiger",
            "Toad",
            "Turkey",
            "Turtle",
            "Vicuña",
            "Wallaby",
            "Walrus",
            "Wasp",
            "Weasel",
            "Whale",
            "Wolf",
            "Wolverine",
            "Wombat",
            "Wren",
            "Yak",
            "Zebra"
        };

        return namespaces[ThreadLocalRandom.current().nextInt(namespaces.length)];
    }

}
