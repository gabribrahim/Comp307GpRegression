package model;

import java.util.ArrayList;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.terminal.Variable;

public class RegressionFitnessFunction extends GPFitnessFunction {

    private ArrayList<Double> _input1;    
    private ArrayList<Double> _output;
    private Variable _xVariable;    

    private static Object[] NO_ARGS = new Object[0];

    public RegressionFitnessFunction(ArrayList<Double> input1, ArrayList<Double> output, Variable x) {
        _input1 = input1;        
        _output = output;
        _xVariable = x;        
    }
    
   
    @Override
    protected double evaluate(final IGPProgram program) {
        double result = 0.0f;

        double longResult = 0;
        for (int i = 0; i < _input1.size(); i++) {
            // Set the input values
            _xVariable.set(_input1.get(i));            
            // Execute the genetically engineered algorithm
            double value =  program.execute_double(0, NO_ARGS);

            // The closer longResult gets to 0 the better the algorithm.
            longResult += Math.abs(value - _output.get(i));
        }

        result = longResult;

        return result;
    }

}