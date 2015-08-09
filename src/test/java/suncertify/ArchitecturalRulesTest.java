package suncertify;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import org.junit.Before;
import org.junit.Test;

public class ArchitecturalRulesTest {

    private static final String DEFAULT_CLASSES_DIR = "target/classes";
    private static final String APPLICATION_PACKAGE = "suncertify";
    private static final String PRESENTATION_PACKAGE = "suncertify.presentation";
    private static final String SERVICE_PACKAGE = "suncertify.service";
    private static final String DATA_PACKAGE = "suncertify.db";

    private JDepend jdepend;
    private String classesDir;

    @Before
    public void setUp() throws IOException {
        jdepend = new JDepend();
        classesDir = System.getProperty("build.classes.dir", DEFAULT_CLASSES_DIR);
        jdepend.addDirectory(classesDir);
    }

    @Test
    public void presentationLayer() {
        Collection<String> violations = new ArrayList<String>();
        violations.add(DATA_PACKAGE);
        assertLayering(PRESENTATION_PACKAGE, violations);
    }

    @Test
    public void serviceLayer() {
        Collection<String> violations = new ArrayList<String>();
        violations.add(APPLICATION_PACKAGE);
        violations.add(PRESENTATION_PACKAGE);
        assertLayering(SERVICE_PACKAGE, violations);
    }

    @Test
    public void dataLayer() {
        Collection<String> violations = new ArrayList<String>();
        violations.add(APPLICATION_PACKAGE);
        violations.add(PRESENTATION_PACKAGE);
        violations.add(SERVICE_PACKAGE);
        assertLayering(DATA_PACKAGE, violations);
    }

    private void assertLayering(String layer, Collection<String> rules) {
        StringBuilder failureMessage = new StringBuilder();
        if (!isLayeringValid(layer, rules, failureMessage)) {
            fail(failureMessage.toString());
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isLayeringValid(String layer, Collection<String> rules,
            StringBuilder failureMessage) {
        boolean rulesCorrect = true;
        Collection allPackages = jdepend.analyze();
        for (Iterator packIter = allPackages.iterator(); packIter.hasNext();) {
            JavaPackage jPackage = (JavaPackage) packIter.next();
            Collection efferents = jPackage.getEfferents();
            for (Iterator effIter = efferents.iterator(); effIter.hasNext();) {
                JavaPackage efferentPackage = (JavaPackage) effIter.next();
                for (String rule : rules) {
                    if (jPackage.getName().equals(layer) && efferentPackage.getName().equals(rule)) {
                        rulesCorrect = false;
                        failureMessage.append(jPackage.getName() + " should not depend on "
                                + efferentPackage.getName() + ", ");
                    }
                }
            }
        }
        return rulesCorrect;
    }
}
