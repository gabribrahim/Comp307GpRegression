package model;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Pow;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;

import sun.security.action.GetBooleanAction;

/**
 * @author carlos
 *
 */
public class SimpleMathTest extends GPProblem {
    @SuppressWarnings("boxing")
    private static Integer[] INPUT_1 = { 26, 8, 20, 33, 37 };

    @SuppressWarnings("boxing")
    private static Integer[] INPUT_2 = { 35, 24, 1, 11, 16 };

    private static int[] OUTPUT = { 829, 141, 467, 1215, 1517 };

    private Variable _xVariable;
    private Variable _yVariable;

    public SimpleMathTest() throws InvalidConfigurationException {
        super(new GPConfiguration());

        GPConfiguration config = getGPConfiguration();

        _xVariable = Variable.create(config, "X", CommandGene.IntegerClass);
        _yVariable = Variable.create(config, "Y", CommandGene.IntegerClass);

        config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
        config.setMaxInitDepth(20);
        config.setPopulationSize(1000);
        config.setMaxCrossoverDepth(8);
        config.setMutationProb((float) 0.1);
        config.setCrossoverProb((float)0.75);
        config.setPreservFittestIndividual(true);
        config.setKeepPopulationSizeConstant(true);
        config.setFitnessFunction(new SimpleMathTestFitnessFunction(INPUT_1, INPUT_2, OUTPUT, _xVariable, _yVariable));
        config.setStrictProgramCreation(true);
    }

    @Override
    public GPGenotype create() throws InvalidConfigurationException {
        GPConfiguration config = getGPConfiguration();

        // The return type of the GP program.
        Class[] types = { CommandGene.IntegerClass };

        // Arguments of result-producing chromosome: none
        Class[][] argTypes = { {} };

        // Next, we define the set of available GP commands and terminals to
        // use.
        CommandGene[][] nodeSets = {
            {
                _xVariable,
                _yVariable,
                new Add(config, CommandGene.IntegerClass),
                new Multiply(config, CommandGene.IntegerClass),
                new Terminal(config, CommandGene.IntegerClass, 0.0, 10.0, true)
            }
        };

        GPGenotype result = GPGenotype.randomInitialGenotype(config, types, argTypes,
                nodeSets, 40, true);

        return result;
    }

    public static void main(String[] args) throws Exception {
        GPProblem problem = new SimpleMathTest();

        GPGenotype gp = problem.create();
//        gp.setVerboseOutput(true);
        for (int i=0; i<30;i++) {
        	System.out.println("CYCLE:"+i);
        	gp.evolve(1);
        	gp.outputSolution(gp.getAllTimeBest());
        }

        System.out.println("Formulaiscover: x^2 + 2y + 3x + 5");
        
//        gp.outputSolution(gp.getAllTimeBest());
        IGPProgram bestP 	= gp.getAllTimeBest();
        
        problem.getGPConfiguration().getVariable("X").set(26);
        problem.getGPConfiguration().getVariable("Y").set(35);
        System.out.println(bestP.size()+"\nExecuted INT="+bestP.execute_int(0,new Object[0]));
    }

}