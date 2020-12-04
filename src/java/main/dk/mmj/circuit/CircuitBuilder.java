package dk.mmj.circuit;

import dk.mmj.fhe.interfaces.Ciphertext;
import dk.mmj.fhe.interfaces.FHE;

import java.util.ArrayList;
import java.util.List;

import static dk.mmj.circuit.GateType.*;

public class CircuitBuilder {
    private final FHE<?> fhe;
    private GateBuilder root;
    private final List<Observer> observers = new ArrayList<>();

    public CircuitBuilder(FHE<?> fhe) {
        this.fhe = fhe;
    }

    public CircuitBuilder(FHE<?> fhe, List<Observer> observers) {
        this.fhe = fhe;
        this.observers.addAll(observers);
    }

    public CircuitBuilder addObserver(Observer observer) {
        observers.add(observer);
        return this;
    }

    public CircuitBuilder not() {
        root = new GateBuilder(NOT, fhe, observers);
        CircuitBuilder gb = new CircuitBuilder(fhe, observers);
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

    Gate gateBuild(DepthCounter cnt) {
        return root.build(cnt);
    }


    public Circuit build() {
        return root.build((i) -> {})::evaluate;
    }

    private MultipleInputGateBuilder handleGate(GateType type) {
        root = new GateBuilder(type, fhe, observers);

        CircuitBuilder left = new CircuitBuilder(fhe, observers);
        CircuitBuilder right = new CircuitBuilder(fhe, observers);
        root.setGates(
                left, right
        );

        return new MultipleInputGateBuilder(
                () -> left,
                () -> right
        );
    }

    public void input(int i) {
        root = new GateBuilder(INPUT, fhe, observers);
        root.setInputIndex(i);
    }


    private interface SingleInputGateBuilder {
        CircuitBuilder gate();
    }

    /**
     * ObserverPattern interface for use in Circuits
     * <br/>
     * <code>register</code> methods will be called during circuit <b>evaluation</b>
     */
    public interface Observer {
        void register(GateType type, Ciphertext inputValue, Ciphertext eval);

        void register(GateType type, Ciphertext leftValue, Ciphertext rightValue, Ciphertext eval, String comment);
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

        public CircuitBuilder leftGate() {
            return left.gate();
        }

        public CircuitBuilder rightGate() {
            return right.gate();
        }
    }

    /**
     *
     */
    interface DepthCounter {
        void registerDepth(int depth);
    }

}
