package ftg.application.bootstrap.configfile;

import java.util.List;

public interface NamingSystemYAML {

    String getNamingLogic();

    List<String> getMaleNames();

    List<String> getFemaleNames();

    List<String> getCommonSurnames();

    List<String> getUniqueSurnames();

}
