package dk.mmj.circuit;

import dk.mmj.fhe.interfaces.FHE;

import static dk.mmj.circuit.GateBuilder.GateType.*;

public class CircuitBuilder {
    private final FHE fhe;
    private GateBuilder root;

    public CircuitBuilder(FHE fhe) {
        this.fhe = fhe;
    }

    public CircuitBuilder not() {
        root = new GateBuilder(NOT, fhe);
        CircuitBuilder gb = new CircuitBuilder(fhe);
        root.setGates(gb);
        return gb;
    }

    public MultipleInputGateBuilder and() {
        return handleGate(AND);
    }

    public MultipleInputGateBuilder nand() {
        return handleGate(NAND);
    }

    public MultipleInputGateBuilder or() {
        return handleGate(OR);
    }

    public MultipleInputGateBuilder xor() {
        return handleGate(XOR);
    }

    Gate gateBuild() {
        return root.build();
    }


    public Circuit build() {
        return root.build()::evaluate;
    }

    private MultipleInputGateBuilder handleGate(GateBuilder.GateType type) {
        root = new GateBuilder(type, fhe);

        CircuitBuilder left = new CircuitBuilder(fhe);
        CircuitBuilder right = new CircuitBuilder(fhe);
        root.setGates(
                left, right
        );

        return new MultipleInputGateBuilder(
                () -> left,
                () -> right
        );
    }

    public void input(int i) {
        root = new GateBuilder(INPUT, fhe);
        root.setInputIndex(i);
    }


    private interface SingleInputGateBuilder {
        CircuitBuilder gate();
    }

    /**
     * Wrapper for two builders:
     * <ul>
     *     <li>Builder for the left input gate</li>
     *     <li>Builder for the right input gate</li>
     * </ul>
     */
    public static class MultipleInputGateBuilder {
        private final SingleInputGateBuilder left;
        private final SingleInputGateBuilder right;

        public MultipleInputGateBuilder(SingleInputGateBuilder left, SingleInputGateBuilder right) {
            this.left = left;
            this.right = right;
        }

        CircuitBuilder leftGate() {
            return left.gate();
        }

        CircuitBuilder rightGate() {
            return right.gate();
        }
    }

}
