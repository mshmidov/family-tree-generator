package ftg.application;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import ftg.application.fx.dashboard.DashboardPresenter;
import ftg.application.fx.dashboard.DashboardView;
import ftg.application.fxsupport.FxSupportModule;
import ftg.application.fxsupport.LoadedView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GuiApplication extends Application {

    private Injector injector;

    @Inject
    private DashboardView dashboardView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        injector = Guice.createInjector(new FxSupportModule(), new ApplicationModule());
        injector.injectMembers(this);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Family tree generator");
        final LoadedView<DashboardPresenter> view = dashboardView.load();
        primaryStage.setScene(new Scene(view.getRoot(), 800, 600));
        primaryStage.show();
    }
}
