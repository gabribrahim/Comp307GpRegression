package model;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.terminal.Variable;

public class SimpleMathTestFitnessFunction extends GPFitnessFunction {

    private Integer[] _input1;
    private Integer[] _input2;
    private int[] _output;
    private Variable _xVariable;
    private Variable _yVariable;

    private static Object[] NO_ARGS = new Object[0];

    public SimpleMathTestFitnessFunction(Integer input1[], Integer input2[],
            int output[], Variable x, Variable y) {
        _input1 = input1;
        _input2 = input2;
        _output = output;
        _xVariable = x;
        _yVariable = y;
    }
    
    public void evaluateVerbose(final IGPProgram program) {
        double result = 0.0f;

        long longResult = 0;
        for (int i = 0; i < _input1.length; i++) {
            // Set the input values
            _xVariable.set(_input1[i]);
            _yVariable.set(_input2[i]);
            // Execute the genetically engineered algorithm
            long value = program.execute_int(0, NO_ARGS);

            // The closer longResult gets to 0 the better the algorithm.
            longResult += Math.abs(value - _output[i]);
        }

        result = longResult;

            	
    }
    
    @Override
    protected double evaluate(final IGPProgram program) {
        double result = 0.0f;

        long longResult = 0;
        for (int i = 0; i < _input1.length; i++) {
            // Set the input values
            _xVariable.set(_input1[i]);
            _yVariable.set(_input2[i]);
            // Execute the genetically engineered algorithm
            long value = program.execute_int(0, NO_ARGS);

            // The closer longResult gets to 0 the better the algorithm.
            longResult += Math.abs(value - _output[i]);
        }

        result = longResult;

        return result;
    }

}