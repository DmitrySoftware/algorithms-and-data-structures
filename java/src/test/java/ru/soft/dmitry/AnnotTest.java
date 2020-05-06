package ru.soft.dmitry;

import org.junit.Before;
import org.junit.Test;
import ru.soft.dmitry.test.AnnotationProcessor;
import ru.soft.dmitry.test.Solution;
import ru.soft.dmitry.test.SetVarStep;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.util.Collections;
import java.util.Set;

public class AnnotTest {

    private AnnotationProcessor test;

    @Before
    public void setUp() throws Exception {
        test = new AnnotationProcessor();
    }

    @Test
    public void name() throws Exception {
        System.out.println(test.getJson());
    }

    @Test
    public void name3() throws Exception {
        Solution.test(new int[]{0, 0, 0, 0, 1, 0, 0});
        Solution.test(new int[]{0, 0, 1, 0, 1, 0});
        Solution.test(new int[]{0, 0, 1, 0, 0});
        Solution.test(new int[]{0, 0, 1, 0});
        Solution.test(new int[]{0, 0, 0, 0});
        Solution.test(new int[]{0, 0, 0});
        Solution.test(new int[]{0, 0});
    }

    @Test
    public void name2() throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                null,
                null,
                null,
                Collections.singleton(SetVarStep.class.getName()),
                Collections.emptySet());
        task.setProcessors(Collections.singleton(new AnnotationProcessor.StepAnnotationProcessor()));
        task.call();
    }

    @SupportedSourceVersion(SourceVersion.RELEASE_8)
    @SupportedAnnotationTypes("*")
    private static class AnnotationProcessor2 extends AbstractProcessor {

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            if (roundEnv.processingOver()) return true;
            System.out.println("annot="+annotations);
            System.out.println("roundEnv="+roundEnv);
            return false;
        }
    }
}
