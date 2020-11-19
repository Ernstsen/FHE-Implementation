package dk.mmj;

import dk.mmj.circuit.TestCircuitBuilder;
import dk.mmj.fhe.TestLWE;
import dk.mmj.fhe.TestLWECircuits;
import dk.mmj.matrix.TestLWEUtils;
import dk.mmj.matrix.TestMatrix;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        //FHE
        TestLWE.class,
        TestLWEUtils.class,
        TestLWECircuits.class,

        //Matrix
        TestMatrix.class,

        //Circuit
        TestCircuitBuilder.class
})
public class TestSuite {
}
