package ru.soft.dmitry.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ru.soft.dmitry.test.annot.Step;
import ru.soft.dmitry.test.annot.StepAttribute;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Searches for all the classes which are marked with the {@link Step} annotations,
 * read its attributes and generates a JSON from it.
 */
public class AnnotationProcessor {

    /** Class file extension string */
    private static final String CLASS = ".class";

    /**
     * Does annotation processing on classes implementing following interfaces:
     * {@link Step} and {@link StepAttribute}. <br />
     * Retrieves metadata and creates JSON string.
     *
     * @return {@link String} in JSON format
     * @throws IOException in case of failure on listing class files
     */
    public String getJson() throws IOException {
        final List<Path> classPaths = getClassPaths();
        final List<String> classNames = listClassNames(classPaths);
        final JsonArray json = processClasses(classNames, new StepAnnotationProcessor());
        return json.toString();
    }

    /**
     * Compiles given classes applying given annotation processor.
     * @param classFiles {@link List} with class file names
     * @param processor annotation processor
     * @param <T> result of annotation processor execution
     * @return execution result of the given annotation processor
     */
    private <T> T processClasses(final List<String> classFiles, final GenericAnnotationProcessor<T> processor) {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                null,
                null,
                null,
                classFiles,
                Collections.emptySet());
        task.setProcessors(Collections.singleton(processor.getProcessor()));
        task.call();
        return processor.getResult();
    }

    /**
     * Searches for java classes in given paths.
     * @param classPaths {@link List} with class paths
     * @return {@link List} with class names found
     * @throws IOException in case of failure listing a directory
     */
    private List<String> listClassNames(final List<Path> classPaths) throws IOException {
        if (null == classPaths) return Collections.emptyList();
        final List<String> result = new ArrayList<>();
        for (Path classPath : classPaths) {
            // list sub-directories of a class path
            final List<Path> pathList = listPaths(classPath);
            // create queue for processing sub-directories without recursion
            final LinkedList<Path> pathQueue = new LinkedList<>(pathList);
            while (!pathQueue.isEmpty()) {
                final Path path = pathQueue.pollFirst();
                // get list with sub-directories or with a single file
                final List<Path> paths = listPaths(path);
                for (final Path each : paths) {
                    final File file = each.toFile();
                    if (file.isDirectory()) {
                        // delay directory listing
                        pathQueue.offerLast(each);
                    } else if (isClassFile(file)) {
                        // add to result
                        final String className = extractClassName(file, classPath.toString());
                        result.add(className);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Subtracts java class name from a given file using given class path.
     * @param file java class file
     * @param classPath class path of the java class file
     * @return {@link String} java class name
     */
    private String extractClassName(final File file, final String classPath) {
        if (null==file || null==classPath) return "";
        final StringBuilder result = new StringBuilder();
        final String fileName = file.getPath();
        int from = fileName.startsWith(classPath) ? classPath.length() : 0;
        if (fileName.codePointAt(from) == '/') from++;
        final int to = fileName.length() - CLASS.length();
        for (int i = from; i < to; i++) {
            int codePoint = fileName.codePointAt(i);
            if (codePoint == '/') codePoint = '.';
            result.appendCodePoint(codePoint);
        }
        return result.toString();
    }

    /**
     * Verifies if given {@link File} is a java class file.
     * @param file {@link File} to verify
     * @return true if file is a java class file
     */
    private boolean isClassFile(final File file) {
        if (null == file) return false;
        final String path = file.getAbsolutePath();
        return path.endsWith(CLASS) && !path.endsWith(".package-info" + CLASS);
    }

    /**
     * Searches given path for sub-directories
     * or returns empty list if it's a file.
     * @param path path to search for sub-directories
     * @return {@link List} with sub-directories
     * @throws IOException in case of failure on listing a directory
     */
    private List<Path> listPaths(final Path path) throws IOException {
        if (null == path) return Collections.emptyList();
        final File file = path.toFile();
        if (file.isDirectory()) return Files.list(path).collect(Collectors.toList());
        // jar files are ignored to avoid hassle in the test job
        return Collections.singletonList(path);
    }

    /**
     * Does a look-up for class paths.
     * @return {@link List} with class paths
     */
    private List<Path> getClassPaths() {
        final String pathSeparator = System.getProperty("path.separator");
        final String classPath = System.getProperty("java.class.path");
        final String[] paths = classPath.split(pathSeparator);
        return Stream.of(paths).map(Paths::get).collect(Collectors.toList());
    }

    /**
     * Use with care: avoid instance leaking to prevent problems with GC. <br />
     * Interface for the purpose of abstracting annotation processing result type
     * from annotation processing execution.
     * @param <T>
     */
    private interface GenericAnnotationProcessor<T> {
        /**
         * Returns generic result of the annotation processor execution.
         * @return T execution result
         */
        T getResult();

        /**
         * Returns {@link AbstractProcessor} implementation.
         * @return {@link AbstractProcessor} implementation
         */
        AbstractProcessor getProcessor();
    }

    /**
     * Use with care: avoid instance leaking to prevent problems with GC. <br />
     * Does processing for {@link Step} and {@link StepAttribute} annotations.
     */
    @SupportedSourceVersion(SourceVersion.RELEASE_8)
    @SupportedAnnotationTypes("*")
    public static class StepAnnotationProcessor extends AbstractProcessor implements GenericAnnotationProcessor<JsonArray> {

        /** resulting JSON */
        private final JsonArray result = new JsonArray();

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
            if (roundEnv.processingOver()) return true;
            final Set<? extends Element> steps = roundEnv.getElementsAnnotatedWith(Step.class);
            for (Element step : steps) {
                final JsonObject jsonObject = new JsonObject();
                result.add(jsonObject);
                addStep(step, jsonObject);
                step.getEnclosedElements().stream()
                        .filter(this::isField)
                        .forEach(stepAttr -> addStepAttr(stepAttr, jsonObject));
            }
            return false;
        }

        /**
         * Adds {@link Step} metadata to {@link JsonObject}
         * @param stepElement {@link Step} element
         * @param jsonObject JSON object
         */
        private void addStep(final Element stepElement, final JsonObject jsonObject) {
            final Step step = stepElement.getAnnotation(Step.class);
            jsonObject.addProperty("id", step.ui().id());
            jsonObject.addProperty("type", step.type());
        }

        /**
         * Returns true if given element is a field.
         * @param element {@link Element}
         * @return true if element is a field
         */
        private boolean isField(final Element element) {
            return null != element && element.getKind().isField();
        }

        /**
         * Adds {@link StepAttribute} metadata to {@link JsonObject}
         * @param element {@link StepAttribute} element
         * @param jsonObject JSON object
         */
        private void addStepAttr(final Element element, final JsonObject jsonObject) {
            final StepAttribute stepAttribute = element.getAnnotation(StepAttribute.class);
            jsonObject.addProperty("name", stepAttribute.ui().name());
            jsonObject.addProperty("value", stepAttribute.ui().id());
            jsonObject.addProperty("value_type", stepAttribute.ui().type());
        }

        /** {@inheritDoc} */
        @Override
        public JsonArray getResult() {
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public AbstractProcessor getProcessor() {
            return this;
        }
    }

}
