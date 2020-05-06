package ru.soft.dmitry.test.annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface Step {
    /**
     * Subtypes of the {@link UI} annotated type (annotated class associated with the annotated
     * method). These types can be defined by only including direct UI types.
     */
    UI ui();

    /**
     * Define type of the step
     */
    String type();

    /**
     * Provide the class information about the {@link StepExecutor} for the annotated step class
     */
    Class executor();

    /**
     * Provide the class information about the {@link Transformer} for the annotated step class
     */
    Class transformer();

    /**
     * Step UI annotation describes about the UI to display in marketplace and application portal.
     *
     * @author Praveen Rejeti<praveen.rejeti@agilecrm.com>
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface UI {
        /**
         * Name of the annotated step class
         */
        String name();

        /**
         * Thumbnail of an annotated step class, used to dispaly in marketplace
         */
        String thumbnail();

        /**
         * Icon of an annotated step class used to display in workflow toolbar
         *
         */
        String icon();

        /**
         * Step Description in case user required to know more about the step.
         */
        String info();

        /**
         * Step documentation, HTML can be used.
         */
        String help();

        /**
         * Name of the author for the annotated step class
         */
        String author();

        /**
         * Name of the company that belongs to the step
         */
        String company() default "Mantra";

        /**
         * Describes various output options handled in annotated step class
         */
        String branches() default "success,failure";

        /**
         * Categorise the annotated step in the UI based on the uniqueness
         */
        String category();

        /**
         * Name of an annotated step class to display in marketplace
         */
        String displayname();

        /**
         * Id of the step.
         */
        String id();
    }

}
