package model;

import java.util.ArrayList;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.terminal.Variable;

public class RegressionFitnessFunction extends GPFitnessFunction {

    private ArrayList<Double> _input1;
    private ArrayList<Double> _input2;
    private ArrayList<Double> _output;
    private Variable _xVariable;
    private Variable _yVariable;

    private static Object[] NO_ARGS = new Object[0];

    public RegressionFitnessFunction(ArrayList<Double> input1, ArrayList<Double> input2,ArrayList<Double> output, Variable x, Variable y) {
        _input1 = input1;
        _input2 = input2;
        _output = output;
        _xVariable = x;
        _yVariable = y;
    }
    
   
    @Override
    protected double evaluate(final IGPProgram program) {
        double result = 0.0f;

        double longResult = 0;
        for (int i = 0; i < _input1.size(); i++) {
            // Set the input values
            _xVariable.set(_input1.get(i));
            _yVariable.set(_input2.get(i));
            // Execute the genetically engineered algorithm
            double value =  program.execute_double(0, NO_ARGS);

            // The closer longResult gets to 0 the better the algorithm.
            longResult += Math.abs(value - _output.get(i));
        }

        result = longResult;

        return result;
    }

}