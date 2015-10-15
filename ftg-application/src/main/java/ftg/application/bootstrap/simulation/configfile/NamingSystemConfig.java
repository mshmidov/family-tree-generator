package ftg.application.bootstrap.simulation.configfile;

import java.util.List;

public interface NamingSystemConfig {

    String getNamingLogic();

    List<String> getMaleNames();

    List<String> getFemaleNames();

    List<String> getCommonSurnames();

    List<String> getUniqueSurnames();

}
