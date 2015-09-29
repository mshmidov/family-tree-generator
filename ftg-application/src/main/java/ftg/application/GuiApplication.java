package ftg.application;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import ftg.application.cdi.ApplicationModule;
import ftg.application.cdi.FxSupportModule;
import ftg.application.gui.dashboard.DashboardController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiApplication extends Application {

    private static final Logger LOGGER = LogManager.getLogger(GuiApplication.class);

    private final Injector injector = Guice.createInjector(new FxSupportModule(), new ApplicationModule());

    @Inject
    private DashboardController dashboardController;

    public static void main(String[] args) {
        LOGGER.entry();
        launch(args);
        LOGGER.exit();
    }

    @Override
    public void init() throws Exception {
        LOGGER.entry();
        injector.injectMembers(this);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        LOGGER.entry();
        primaryStage.setTitle("Family tree generator");
        final Scene dashboardScene = new Scene(dashboardController.getViewRoot(), 800, 600);
        dashboardScene.getStylesheets().add("fx/dashboard.css");
        primaryStage.setScene(dashboardScene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(200);
        primaryStage.show();
    }
}
