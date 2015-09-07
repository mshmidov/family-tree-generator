package ftg.application.gui.dashboard;

import com.google.inject.Inject;
import ftg.simulation.Simulation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class DashboardPresenter {

    @Inject
    private Simulation simulation;

    @FXML
    private ComboBox<Integer> simulationDurationField;

    @FXML
    protected void handleRunSimulationButtonAction(ActionEvent event) {
        System.out.println(simulationDurationField.getValue());
    }

}


