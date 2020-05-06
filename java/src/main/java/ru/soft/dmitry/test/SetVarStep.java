package ru.soft.dmitry.test;

import ru.soft.dmitry.test.annot.Step;
import ru.soft.dmitry.test.annot.StepAttribute;
import ru.soft.dmitry.test.annot.StepAttribute.UIComponent;
import ru.soft.dmitry.test.annot.StepAttribute.UIComponent.FieldType;

@Step(type = "set-var",
        ui = @Step.UI(name = "Set variable", id = "Id of the step", help = "Step Help", info = "Step info", author = "Praveen Kumar Rejeti",
                category = "General", displayname = "Set var", company = "Mantra Pvt. Ltd.", thumbnail = "/png", icon = "/.ico"),
        transformer = SetVarStep.class, executor = SetVarStep.class)
public class SetVarStep extends StepC {
    @StepAttribute(ui = @UIComponent(fieldType = FieldType.TEXT, category = "Category", name = "name", type = "text", required = true,
            label = "Enter Name of the Variable", title = "Variable Name", id = "var_name"))
    private String name;

    @StepAttribute(ui = @UIComponent(fieldType = FieldType.TEXT, category = "Category", name = "value", type = "text", required = true, label = "Value",
            title = "Enter Value", id = "value"))
    private String value;
    @StepAttribute(ui = @UIComponent(fieldType = FieldType.SELECT, category = "Category", name = "value_type", type = "text", required = true,
            label = "Select Template", title = "Variable Name", id = "value", options = " {\"Handlebar\": \"hb\",\"Free Marker Template\": \"ftl\"},"))
    private String value_type;
}