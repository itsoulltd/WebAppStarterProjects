package com.infoworks.lab.components.ui;


import com.infoworks.lab.components.crud.Configurator;
import com.infoworks.lab.components.crud.Crud;
import com.infoworks.lab.components.crud.components.datasource.DefaultDataSource;
import com.infoworks.lab.components.crud.components.datasource.GridDataSource;
import com.infoworks.lab.components.crud.components.utils.EditorDisplayType;
import com.infoworks.lab.components.db.source.JsqlDataSource;
import com.infoworks.lab.components.db.source.SqlDataSource;
import com.infoworks.lab.components.presenters.PassengerEditor;
import com.infoworks.lab.config.DatabaseBootstrap;
import com.infoworks.lab.domain.entities.Gender;
import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.jsql.ExecutorType;
import com.infoworks.lab.layouts.AppLayout;
import com.infoworks.lab.layouts.RoutePath;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = RoutePath.PASSENGERS_CRUD_VIEW , layout = AppLayout.class)
@CssImport(value = "./styles/view-styles.css", id = "view-styles")
@CssImport(value = "./styles/shared-styles.css", include = "view-styles")
public class PassengerList extends MainContent {

    public PassengerList(){
        //Create DataSource:
        GridDataSource source = createDataSource(true);

        Configurator configurator = new Configurator(Passenger.class)
                .setDisplayType(EditorDisplayType.EMBEDDED)
                .setDataSource(source)
                .setEditor(PassengerEditor.class)
                .setDialog(PassengerEditor.class)
                .setGridPageSize(8);

        Crud crud = new Crud(configurator);
        add(crud);
    }

    private GridDataSource createDataSource(boolean inmemory){
        if (inmemory){
            //In-Memory DataSource:
            GridDataSource source = new DefaultDataSource();
            getPassengers().stream().forEach(passenger -> source.save(passenger));
            return source;
        }else{
            //Fetching Data From Database:
            DatabaseBootstrap.createTables();
            GridDataSource source = JsqlDataSource.createDataSource(SqlDataSource.class, ExecutorType.SQL);
            //Testing RestExecutor:
            //DataSourceKey sourceKey = JsqlConfig.createDataSourceKey("app.db");
            //((SqlDataSource) source).setExecutor(new RestExecutor(Passenger.class, sourceKey));
            return source;
        }
    }

    private List<Passenger> getPassengers() {
        List<Passenger> personList = new ArrayList<>();
        personList.add(new Passenger("Lucas", Gender.MALE, 68));
        personList.add(new Passenger("Peter", Gender.MALE, 38));
        personList.add(new Passenger("Jack", Gender.MALE, 28));
        personList.add(new Passenger("Samuel", Gender.MALE, 53));
        return personList;
    }
}
