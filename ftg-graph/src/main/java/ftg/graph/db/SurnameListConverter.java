package ftg.graph.db;

import static com.google.common.base.Preconditions.checkArgument;

import ftg.graph.model.person.Surname;
import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SurnameListConverter implements AttributeConverter<List<Surname>, String> {

    @Override public String toGraphProperty(List<Surname> value) {
        return value.stream()
            .map(surname -> surname.getMaleForm() + "/" + surname.getFemaleForm())
            .collect(Collectors.joining(","));
    }

    @Override public List<Surname> toEntityAttribute(String value) {

        final LinkedList<Surname> result = new LinkedList<>();

        Stream.of(value.split(","))
            .map(s -> {
                final String[] parts = value.split("/");
                checkArgument(parts.length == 2);
                return new Surname(parts[0], parts[1]);
            })
            .forEach(result::add);


        return result;

    }
}
