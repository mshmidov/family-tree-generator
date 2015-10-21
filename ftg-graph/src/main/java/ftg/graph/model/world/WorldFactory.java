package ftg.graph.model.world;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import ftg.commons.cdi.Identifier;
import ftg.commons.generator.Generator;
import ftg.commons.generator.RandomElementGenerator;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public final class WorldFactory {

    private final Supplier<String> id;

    private final Generator<String> worldName = new RandomElementGenerator<>(getWorldCodenames());

    @Inject
    public WorldFactory(@Identifier Supplier<String> id) {
        this.id = id;
    }

    public World newWorld() {
        final Population population = new Population();

        return new World(worldName + id.get(), LocalDateTime.now().toString(), population);
    }

    public Country newCountry(String name) {
        return new Country(id.get(), name);
    }

    private ImmutableList<String> getWorldCodenames() {
        return ImmutableList.copyOf(new String[] {
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
        });
    }
}
