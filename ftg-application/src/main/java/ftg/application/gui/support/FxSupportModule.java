package ftg.application.gui.support;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import javafx.fxml.FXMLLoader;

public class FxSupportModule extends AbstractModule {

    @Override
    protected void configure() {

    }

    @Provides
    public FXMLLoader getFxmlLoader(Injector injector) {
        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(injector::getInstance);
        return fxmlLoader;
    }
}
