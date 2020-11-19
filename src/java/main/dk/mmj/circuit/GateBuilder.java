package dk.mmj.circuit;

import dk.mmj.fhe.interfaces.FHE;

/**
 * Builder for a gate.
 * <br/>
 * Note that a gate must have input being EITHER gates, or circuit-input
 */
class GateBuilder {
    private final FHE fhe;
    GateType type;
    CircuitBuilder[] inputGates;
    int input;

    GateBuilder(GateType type, FHE fhe) {
        this.type = type;
        inputGates = new CircuitBuilder[type.inDegree];
        this.fhe = fhe;
    }

    Gate build() {
        Gate left = null;
        Gate right = null;
        if (inputGates.length > 0) {
            left = inputGates[0].gateBuild();
        }
        if(inputGates.length > 1){
            right = inputGates[1].gateBuild();
        }
        final Gate leftF = left;
        final Gate rightF = right;
        switch (type) {
            case NOT:
                return (pk, input) -> fhe.not(leftF.evaluate(pk, input), pk);
            case OR:
                return (pk, input) -> fhe.or(leftF.evaluate(pk, input), rightF.evaluate(pk, input), pk);
            case AND:
                return (pk, input) -> fhe.and(leftF.evaluate(pk, input), rightF.evaluate(pk, input), pk);
            case NAND:
                return (pk, input) -> fhe.nand(leftF.evaluate(pk, input), rightF.evaluate(pk, input), pk);
            case XOR:
                return (pk, input) -> fhe.xor(leftF.evaluate(pk, input), rightF.evaluate(pk, input), pk);
            case INPUT:
                return (pk, inputArray) -> inputArray[input];
            default:
                throw new RuntimeException("Invalid type");
        }
    }

    void setGates(CircuitBuilder ...gates){
        if(type == GateType.INPUT){
            throw new RuntimeException("Cannot define gates as input, on an input gate");
        }
        if(gates.length != type.inDegree){
            throw new RuntimeException("Number of gates as input, must match indegree");
        }
        inputGates = gates;
    }

    void setInputIndex(int inputIndex){
        input = inputIndex;
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

}


