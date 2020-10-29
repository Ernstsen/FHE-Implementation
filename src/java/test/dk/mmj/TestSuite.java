package dk.mmj;

import dk.mmj.fhe.TestLWE;
import dk.mmj.matrix.TestMatrix;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestLWE.class,
        TestMatrix.class
})
public class TestSuite {
}
