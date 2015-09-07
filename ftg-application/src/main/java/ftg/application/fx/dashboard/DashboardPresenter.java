package ftg.application.fx.dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class DashboardPresenter {

    @FXML
    private ComboBox<Integer> simulationDurationField;

    @FXML
    protected void handleRunSimulationButtonAction(ActionEvent event) {
        System.out.println(simulationDurationField.getValue());
    }

}


