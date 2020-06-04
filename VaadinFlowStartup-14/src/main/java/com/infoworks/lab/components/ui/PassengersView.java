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
import com.infoworks.lab.layouts.RootAppLayout;
import com.infoworks.lab.layouts.RoutePath;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = RoutePath.PASSENGERS_CRUD_VIEW, layout = RootAppLayout.class)
public class PassengersView extends Composite<Div> {

    public PassengersView() {

        //Create DataSource:
        GridDataSource source = createDataSource(true);

        Configurator configurator = new Configurator(Passenger.class)
                .setDisplayType(EditorDisplayType.COMBINED)
                .setDataSource(source)
                .setEditor(PassengerEditor.class)
                .setDialog(PassengerEditor.class)
                .setGridPageSize(8);

        Crud crud = new Crud(configurator);
        getContent().add(crud);
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
        personList.add(new Passenger("Peter", Gender.MALE, 38));
        personList.add(new Passenger("Samuel", Gender.MALE, 53));
        personList.add(new Passenger("Jack", Gender.MALE, 28));
        personList.add(new Passenger("Peter", Gender.MALE, 38));
        personList.add(new Passenger("Samuel", Gender.MALE, 53));
        personList.add(new Passenger("Anton", Gender.MALE, 37));
        personList.add(new Passenger("Samuel", Gender.MALE, 53));
        personList.add(new Passenger("Aaron", Gender.FEMALE, 18));
        personList.add(new Passenger("Jack", Gender.MALE, 28));
        personList.add(new Passenger("Peter", Gender.MALE, 38));
        personList.add(new Passenger("Samuel", Gender.MALE, 53));
        personList.add(new Passenger("Anton", Gender.MALE, 37));
        personList.add(new Passenger("Samuel", Gender.MALE, 53));
        personList.add(new Passenger("Jack", Gender.MALE, 28));
        personList.add(new Passenger("Aaron", Gender.FEMALE, 18));
        personList.add(new Passenger("Jack", Gender.MALE, 28));
        personList.add(new Passenger("Peter", Gender.MALE, 38));
        personList.add(new Passenger("Samuel", Gender.MALE, 53));
        return personList;
    }
}
