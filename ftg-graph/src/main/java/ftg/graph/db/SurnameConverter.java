package ftg.graph.db;

import static com.google.common.base.Preconditions.checkArgument;

import ftg.graph.model.person.Surname;
import org.neo4j.ogm.typeconversion.AttributeConverter;

public final class SurnameConverter implements AttributeConverter<Surname, String> {

    @Override public String toGraphProperty(Surname value) {
        return value.toString();
    }

    @Override public Surname toEntityAttribute(String value) {
        final String[] parts = value.split("\\s/\\s");
        checkArgument(parts.length == 2);
        return new Surname(parts[0], parts[1]);
    }
}
