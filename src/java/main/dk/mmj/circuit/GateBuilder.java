package dk.mmj.circuit;

import dk.mmj.fhe.interfaces.Ciphertext;
import dk.mmj.fhe.interfaces.FHE;
import dk.mmj.fhe.interfaces.PublicKey;

import java.util.List;

/**
 * Builder for a gate.
 * <br/>
 * Note that a gate must have input being EITHER gates, or circuit-input
 */
class GateBuilder {
    private final FHE fhe;
    private final List<CircuitBuilder.Observer> observers;
    GateType type;
    CircuitBuilder[] inputGates;
    int input;

    GateBuilder(GateType type, FHE fhe, List<CircuitBuilder.Observer> observers) {
        this.type = type;
        inputGates = new CircuitBuilder[type.inDegree];
        this.fhe = fhe;
        this.observers = observers;
    }

    Gate build() {
        Gate left = null;
        Gate right = null;
        if (inputGates.length > 0) {
            left = inputGates[0].gateBuild();
        }

        if (inputGates.length > 1) {
            right = inputGates[1].gateBuild();
        }

        switch (type) {
            case NOT:
                return evaluate(type, fhe::not, left);
            case OR:
                return evaluate(type, fhe::or, left, right);
            case AND:
                return evaluate(type, fhe::and, left, right);
            case NAND:
                return evaluate(type, fhe::nand, left, right);
            case XOR:
                return evaluate(type, fhe::xor, left, right);
            case INPUT:
                return (pk, inputArray) -> inputArray[input];
            default:
                throw new RuntimeException("Invalid type");
        }
    }

    private Gate evaluate(GateType type, IndegreeOneFunction func, Gate inputGate) {
        return (pk, input) -> {
            Ciphertext inputValue = inputGate.evaluate(pk, input);
            Ciphertext eval = func.eval(inputValue, pk);
            registerWithObservers(type, inputValue, eval);
            return eval;
        };
    }

    private Gate evaluate(GateType type, IndegreeTwoFunction func, Gate leftGate, Gate rightGate) {
        return (pk, input) -> {
            Ciphertext leftInput = leftGate.evaluate(pk, input);
            Ciphertext rightInput = rightGate.evaluate(pk, input);

            Ciphertext eval = func.eval(leftInput, rightInput, pk);
            registerWithObservers(type, leftInput, rightInput, eval);
            return eval;
        };
    }

    void setGates(CircuitBuilder... gates) {
        if (type == GateType.INPUT) {
            throw new RuntimeException("Cannot define gates as input, on an input gate");
        }
        if (gates.length != type.inDegree) {
            throw new RuntimeException("Number of gates as input, must match indegree");
        }
        inputGates = gates;
    }

    void setInputIndex(int inputIndex) {
        input = inputIndex;
    }

    /**
     * Registers usage of gate with indegree two, with observers
     *
     * @param type       type of gate
     * @param inputValue input value
     * @param eval       the result of the evaluation
     */
    private void registerWithObservers(GateType type, Ciphertext inputValue, Ciphertext eval) {
        try {
            for (CircuitBuilder.Observer observer : observers) {
                observer.register(type, inputValue, eval);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Registers usage of gate with indegree two, with observers
     *
     * @param type       type of gate
     * @param leftValue  left input gate
     * @param rightValue right input gate
     * @param eval       the result of the evaluation
     */
    private void registerWithObservers(GateType type, Ciphertext leftValue, Ciphertext rightValue, Ciphertext eval) {
        try {
            for (CircuitBuilder.Observer observer : observers) {
                observer.register(type, leftValue, rightValue, eval);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    enum GateType {
        NOT(1),
        AND(2),
        NAND(2),
        OR(2),
        XOR(2),
        INPUT(0);
        int inDegree;

        GateType(int inDegree) {
            this.inDegree = inDegree;
        }
    }

    private interface IndegreeTwoFunction {
        Ciphertext eval(Ciphertext left, Ciphertext right, PublicKey pk);
    }

    private interface IndegreeOneFunction {
        Ciphertext eval(Ciphertext input, PublicKey pk);
    }

}


