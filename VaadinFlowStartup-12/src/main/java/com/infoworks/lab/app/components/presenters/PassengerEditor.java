package com.infoworks.lab.app.components.presenters;

import com.infoworks.lab.app.entities.Gender;
import com.infoworks.lab.components.crud.components.editor.EmbeddedBeanEditor;
import com.it.soul.lab.sql.query.models.Property;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.util.Arrays;

public class PassengerEditor extends EmbeddedBeanEditor {

    public PassengerEditor(Class beanType) {
        super(beanType);
    }

    @Override
    protected boolean shouldSaveAutofocus() {
        return false;
    }

    @Override
    protected HasValue getValueField(Property prop) {
        if (prop.getKey().equals("sex")){
            ComboBox<String> comboBox = new ComboBox<>(prop.getKey());
            comboBox.setItems(Arrays.stream(Gender.values()).map(gender -> gender.name()));
            return comboBox;
        } else if (prop.getKey().equals("name")) {
            TextField field = (TextField) super.getValueField(prop);
            field.setAutofocus(true);
            return field;
        } else{
            return super.getValueField(prop);
        }
    }

    @Override
    protected Converter getValueConverter(Property prop) {
        if (prop.getKey().equals("sex")){
            return new GenderConverter();
        }else{
            return super.getValueConverter(prop);
        }
    }

    public static class GenderConverter implements Converter<String, String>{

        @Override
        public Result<String> convertToModel(String s, ValueContext valueContext) {
            if (s == null || s.isEmpty()) s = Gender.NONE.name();
            return Result.ok(s);
        }

        @Override
        public String convertToPresentation(String gender, ValueContext valueContext) {
            if (gender == null) return Gender.NONE.name();
            return gender;
        }
    }
}
