package ftg.graph.model.world;

import com.google.common.collect.ImmutableList;
import ftg.graph.model.DomainObject;
import ftg.graph.model.person.Family;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@NodeEntity
public class Population extends DomainObject {

    private static final List<String> POSSIBLE_SURNAME_GROUPS = ImmutableList.<String>builder()
        .add("АБВ")
        .add("ГДЕ")
        .add("ЖЗИЙ")
        .add("КЛМ")
        .add("НОП")
        .add("РСТ")
        .add("УФХ")
        .add("ЦЧШЩ")
        .add("ЫЭЮЯ")
        .add("ABC")
        .add("DEFG")
        .add("HIJK")
        .add("LMN")
        .add("OPQ")
        .add("PQR")
        .add("STUV")
        .add("WXYZ")
        .build();

    private static final Map<Character, String> SURNAME_GROUPS_INDEX = buildGroupsIndex(POSSIBLE_SURNAME_GROUPS);


    @Relationship
    private Set<FamilyGroup> familyGroups = new HashSet<>();

    @Property
    private String label = "Population";

    @Relationship
    private AllPeople allPeople;

    public Population() {
    }

    Population(String id, String namespace, AllPeople allPeople) {
        super(id, namespace);
        this.allPeople = allPeople;
    }

    private static Map<Character, String> buildGroupsIndex(Collection<String> groups) {
        final Map<Character, String> result = new HashMap<>();
        for (String group : groups) {
            for (char c : group.toCharArray()) {
                result.put(c, group);
            }
        }
        return result;
    }

    public Set<FamilyGroup> getFamilyGroups() {
        return familyGroups;
    }

    public AllPeople getAllPeople() {
        return allPeople;
    }

    public void addFamily(Family family) {
        final char firstLetter = family.getSurname().getMaleForm().toUpperCase().charAt(0);
        final String groupName = SURNAME_GROUPS_INDEX.get(firstLetter);

        findFamilyGroup(groupName).orElseGet(() -> addFamilyGroup(groupName))
            .getFamilies().add(family);

    }

    private Optional<FamilyGroup> findFamilyGroup(String groupName) {
        for (FamilyGroup group : familyGroups) {
            if (Objects.equals(group.getName(), groupName)) {
                return Optional.of(group);
            }
        }
        return Optional.empty();
    }

    private FamilyGroup addFamilyGroup(String name) {
        final FamilyGroup familyGroup = new FamilyGroup(getNamespace() + name, getNamespace(), name);
        familyGroups.add(familyGroup);
        return familyGroup;
    }
}
