package ru.soft.dmitry.test.annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

public @interface StepAttribute {

    UIComponent ui();

    /**
     * UI Component annotation describes the annotation information
     *
     * @author Giridhar <giridhar@agilecrm.com>
     * @author Praveen Rejeti<praveen.rejeti@agilecrm.com>
     */
    @Target(value = ElementType.ANNOTATION_TYPE)
//    @AppletAnnotation
    @interface UIComponent {
        /**
         * The name of the attribute which is used to display in UI
         */
        String label();

        /**
         * Readable name, which is used as a key in config.json
         */
        String category();

        /**
         * The default value for the key can be used as a help text
         */
        String name() default "";

        /**
         * The type of the variable "text", "textarea", "checkbok"
         */
        String id();

        /**
         * Title of the field
         */
        String title() default "";

        /**
         * Field Type to render in workflow designer, default textbox
         */
        FieldType fieldType() default FieldType.TEXT;

        /**
         * Define its a mandatory field or not
         */
        boolean required() default false;

        String type();

        /**
         * Options for the annotation type
         */
        String options() default "";

        /**
         * FieldType referes the type of UI Component
         *
         * @author Giridhar <giridhar@agilecrm.com>
         * @author Praveen Rejeti<praveen.rejeti@agilecrm.com>
         */
        public enum FieldType {

            DATE_PICKER("datePicker"), GRID("grid"), DYNAMIC_SELECT("dynamicselect"), TEXT("text"), SELECT("select"), HIDDEN("hidden"), TEXT_AREA(
                    "textarea"), PASSWORD("password"), BUTTON("button"), LINK("link"), INPUT("input"), LABEL("label");

            private String returnValue;

            FieldType(String text)
            {
                this.returnValue = text;
            }

            public String getReturnValue()
            {
                return this.returnValue;
            }
        }
    }
}