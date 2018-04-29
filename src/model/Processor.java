package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.jgap.Configuration;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.Abs;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Divide;
import org.jgap.gp.function.Log;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Subtract;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Constant;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;

import application.MainController;

public class Processor  extends GPProblem {
    @SuppressWarnings("boxing")
    public static ArrayList<Double> INPUT_1 = new ArrayList<>(Arrays.asList(26.0d, 8.0d, 20.0d, 33.0d, 37.0d));

    @SuppressWarnings("boxing")
    public static  ArrayList<Double>INPUT_2 = new ArrayList<>(Arrays.asList( 35.0d, 24.0d, 1.0d, 11.0d, 16.0d));

    public static  ArrayList<Double> OUTPUT = new ArrayList<>(Arrays.asList( 829.0d, 141.0d, 467.0d, 1215.0d, 1517.0d));
    public GPConfiguration config = getGPConfiguration();
    private Variable _xVariable;
    private Variable _yVariable;
    private GPGenotype gp;
    public MainController uiWin	;
    public int epochCounter;

    public Processor() throws InvalidConfigurationException {
        super(new GPConfiguration());
        Configuration.reset();
        
    	getInputsFromFile();
        _xVariable = Variable.create(config, "X", CommandGene.DoubleClass);
        

        config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
        config.setMinInitDepth(5);
        config.setMaxInitDepth(7);
        config.setPopulationSize(1000);
        config.setMaxCrossoverDepth(7);
        config.setMutationProb((float) 0.3);
        config.setCrossoverProb((float)0.9);
        config.setPreservFittestIndividual(true);
        config.setKeepPopulationSizeConstant(true);
//        config.setProgramCreationMaxTries(-1);
        config.setFitnessFunction(new RegressionFitnessFunction(INPUT_1, OUTPUT, _xVariable));
        config.setStrictProgramCreation(true);
    }

    @Override
    public GPGenotype create() throws InvalidConfigurationException {
//        GPConfiguration config = getGPConfiguration();

        // The return type of the GP program.
        Class[] types = { CommandGene.DoubleClass};

        // Arguments of result-producing chromosome: none
        Class[][] argTypes = { {} };

        // Next, we define the set of available GP commands and terminals to
        // use.
        CommandGene[][] nodeSets = {
            {
                _xVariable,                
                new Add(config, CommandGene.DoubleClass),
                new Multiply(config, CommandGene.DoubleClass),
//                new Abs(config,CommandGene.DoubleClass),
                new Divide(config,CommandGene.DoubleClass),
                new Subtract(config,CommandGene.DoubleClass),
                new Log(config,CommandGene.DoubleClass),
                new Constant(config, CommandGene.DoubleClass,1.0),
                new Terminal(config, CommandGene.DoubleClass, 0.0, 50, true),
                new Terminal(config, CommandGene.DoubleClass, 0.0, 50, false)
            }
        };

        GPGenotype result = GPGenotype.randomInitialGenotype(config, types, argTypes,
                nodeSets, 15, true);

        return result;
    }
    
    public void initialisePopulation() {
        try {
			gp 					= create();
			epochCounter		= 0;
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        gp.setVerboseOutput(true);
    	
    }
    public void evolveForNEpochs(int epochs) {
      for (int i=0; i<epochs;i++) {
    	epochCounter++;
    	System.out.println("Epoch:"+epochCounter);
    	gp.evolve(1);
//    	Platform.runLater(()->uiWin.drawDataSet());
//    	Platform.runLater(()->uiWin.drawPredictedSet());    	
		uiWin.drawDataSet();
		uiWin.drawPredictedSet();    	
//    	gp.outputSolution(gp.getAllTimeBest());
    }
    	
    }
    public ArrayList<Double> generateCheckPoints() {
        
    	IGPProgram bestP 			= gp.getAllTimeBest();
    	ArrayList<Double> predictOutputs	= new ArrayList<>();
    	for (Double input:INPUT_1) {
            this.getGPConfiguration().getVariable("X").set(input);        
            predictOutputs.add(bestP.execute_double(0,new Object[0]));
    	}
    	return predictOutputs;
    	
    }
	public void getInputsFromFile() {
		String trainingFilePath		= System.getProperty("user.dir").replace('\\', '/') + "/regression.txt";
		File fileObj 								= new File(trainingFilePath);
		String line									="";
		ArrayList<Double> outputs					= new ArrayList<>();
		ArrayList<Double> inputs 					= new ArrayList<>();
		try (FileReader fileReader = new FileReader(fileObj);
				BufferedReader bufferedReader		= new BufferedReader(fileReader);){
				line								= bufferedReader.readLine();
				while (line!=null) {				
					String[] lineParts				= line.split(";");
					outputs.add(Double.parseDouble(lineParts[1]));
					inputs.add(Double.parseDouble(lineParts[0]));
				 	line							= bufferedReader.readLine();
				}
		
		} catch (IOException e) {
			System.out.println("FILE NOT FOUND !!");
		}		
		INPUT_1										= inputs;
		OUTPUT										= outputs;
		if (uiWin !=null) {
				uiWin.appendToStatusText("Inputs= " + INPUT_1+"\nOutput= "+OUTPUT);
			}
	}
    public static void main(String[] args) throws Exception {
        GPProblem problem = new Processor();
        
        GPGenotype gp = problem.create();
        gp.setVerboseOutput(true);
//        for (int i=0; i<30;i++) {
//        	System.out.println("CYCLE:"+i);
//        	gp.evolve(1);
//        	gp.outputSolution(gp.getAllTimeBest());
//        }
        gp.evolve(200);
        gp.outputSolution(gp.getAllTimeBest());
        
//        gp.outputSolution(gp.getAllTimeBest());
        IGPProgram bestP 	= gp.getAllTimeBest();
        
        problem.getGPConfiguration().getVariable("X").set(2.75);        
        System.out.println("\nExecuted 2.75= " + bestP.execute_double(0,new Object[0]));
        problem.getGPConfiguration().getVariable("X").set(-2.0);        
        System.out.println("\nExecuted -2= " + bestP.execute_double(0,new Object[0]));        
        System.out.println("Settings " + gp.getGPConfiguration().getInitStrategy());
    }

}