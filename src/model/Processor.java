package model;

import java.io.File;

import org.joone.engine.FullSynapse;
import org.joone.engine.LinearLayer;
import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.SigmoidLayer;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.FileInputSynapse;
import org.joone.net.NeuralNet;

public class Processor implements org.joone.engine.NeuralNetListener{
	
	public void buildNNTest() {
		LinearLayer input = new LinearLayer();
		SigmoidLayer hidden = new SigmoidLayer();
		SigmoidLayer output = new SigmoidLayer();
		
		input.setRows(2);
		hidden.setRows(3);
		output.setRows(1);
		
		FullSynapse synapse_IH = new FullSynapse(); /* Input  -> Hidden conn. */
		FullSynapse synapse_HO = new FullSynapse(); /* Hidden -> Output conn. */
		
		input.addOutputSynapse(synapse_IH);
		hidden.addInputSynapse(synapse_IH);
		hidden.addOutputSynapse(synapse_HO);
		output.addInputSynapse(synapse_HO);
		
		NeuralNet nnet = new NeuralNet();
		nnet.addLayer(input, NeuralNet.INPUT_LAYER);
		nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
		nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);	
		
		Monitor monitor = nnet.getMonitor();
		monitor.setLearningRate(0.7);
//		monitor.setMomentum(0.5);
		
		monitor.addNeuralNetListener(this);		
		
		FileInputSynapse inputStream = new FileInputSynapse();
		/* The first two columns contain the input values */
		inputStream.setAdvancedColumnSelector("1,2");
		/* This is the file that contains the input data */
		String trainingFilePath		= System.getProperty("user.dir").replace('\\', '/') + "/test.txt";
		inputStream.setInputFile(new File(trainingFilePath));		
		
		input.addInputSynapse(inputStream);
		
		TeachingSynapse trainer = new TeachingSynapse();
		/* Setting of the file containing the desired responses, provided by a FileInputSynapse */
		FileInputSynapse samples = new FileInputSynapse();
		
		samples.setInputFile(new File(trainingFilePath));
		trainer.setDesired(samples);
		/* The output values are on the third column of the file */
		samples.setAdvancedColumnSelector("3");
		/* We add it to the neural network */
		nnet.setTeacher(trainer);
		
		output.addOutputSynapse(trainer);
		monitor.setTrainingPatterns(4); /* # of rows contained in the input file */
		monitor.setTotCicles(10000); /* How many times the net must be trained on the input patterns */
		monitor.setLearning(true); /* The net must be trained */
		System.out.println(monitor.getGlobalError());
		nnet.go(); /* The network starts the training job */
		System.out.println(monitor.getGlobalError());
		
	}
	@Override
	public void netStarted(NeuralNetEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cicleTerminated(NeuralNetEvent e) {
		Monitor m = (Monitor)e.getSource();
		
		System.out.println(m.getGlobalError()+","+m.getCurrentCicle());
		
	}

	@Override
	public void netStopped(NeuralNetEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void errorChanged(NeuralNetEvent e) {
		
		
	}

	@Override
	public void netStoppedError(NeuralNetEvent e, String error) {
		// TODO Auto-generated method stub
		
	}
}
