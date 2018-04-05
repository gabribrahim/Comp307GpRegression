package model;

import java.util.ArrayList;
import java.util.HashMap;

import prefuse.data.Node;
import prefuse.data.Tree;

public class DtNode {
	ArrayList<LabelledDataInstance> containedInstances ;
	String nodeName;
	DataSetsLoader parentDataSet;
	
	boolean debug;
	ArrayList <DtNode> children					  	= new ArrayList<>();
	ArrayList<LabelledDataInstance> instancesTrue 	= new ArrayList<>();
	ArrayList<LabelledDataInstance> instancesFalse 	= new ArrayList<>();
	ArrayList<String> attrsList;
	ArrayList<String> originalAttrslist;
	String bestAttr     						  	= "";
	String attributeToSplitOn						= "";
	Node visualRepNode;
	
	public String getBestAttr() {
		
		if (isPure()) {
			return containedInstances.get(0).labelName + "\n"+computeNodeProbabilities();
		}
		if (bestAttr.equals("")){return nodeName;}
		return  bestAttr +"\n" + instancesTrue.size() + "<>" + instancesFalse.size()+"\n"+computeNodeProbabilities()+
				"\n"+attrsList.size()+"/"+originalAttrslist.size();
	}

	@Override
	public String toString() {
		return getBestAttr()+" [containedInstances=" + containedInstances.size() + ", nodeName=" + nodeName + ", children=" + children.size()
				+ ", instancesTrue=" + instancesTrue.size() + ", instancesFalse=" + instancesFalse.size() + "]";
	}

	public DtNode(ArrayList<LabelledDataInstance> containedInstances,ArrayList<String>attrsList, ArrayList<String>originalAttrslist) {
		super();
		this.containedInstances		= containedInstances;
		this.attrsList				= attrsList;
		this.debug					= false;
		this.originalAttrslist		= originalAttrslist;
	}
	
	private void pp(String message) {
//		if (containedInstances.size()<49) {System.out.println(message);}
		if (this.debug) {System.out.println(message);}
	}
	
	public String computeNodeProbabilities() {
		int liveCount 				= 0;
		int dieCount				= 0;
		for (LabelledDataInstance instance : containedInstances) {
			if (instance.labelName.equals("live")) {liveCount++;}
			if (instance.labelName.equals("die")) {dieCount++;}
		}
		double plive				= (double)liveCount / (double)containedInstances.size();
		double pdie					= (double)dieCount / (double)containedInstances.size();
		String probs				="L"+String.format("%.2f", plive)+" D"+String.format("%.2f", pdie)+"\n";
		probs						+= "#L"+ liveCount +" #D" + dieCount+" #Total"+ containedInstances.size();
		return probs;
	}
	public double computeGeniImpurity(String attribute){
		int indexOfAttribute = originalAttrslist.indexOf(attribute);
		instancesTrue.clear();
		instancesFalse.clear();
		
		pp("Checking Attr:. "+attribute);
		
		for (LabelledDataInstance instance :containedInstances) {
			if (instance.featureListAsValues.get(indexOfAttribute)) {
				instancesTrue.add(instance);
			}
			else {
				instancesFalse.add(instance);
			}
		}
		
		pp("True Instances = "+instancesTrue.size());
		pp("False Instances = "+instancesFalse.size());
		
		int liveCount 				= 0;
		int dieCount				= 0;
		for (LabelledDataInstance instance:instancesTrue) {
			if (instance.labelName.equals("live")) {liveCount ++;}
			if (instance.labelName.equals("die")) {dieCount ++;}
		}
		
		double weightOfTrueInstances		= (double) instancesTrue.size() /(double)containedInstances.size();
		double trueImpurity					= weightOfTrueInstances*((2*(double)liveCount *(double)dieCount)
												/Math.pow((liveCount+dieCount),2)) ;
//		System.out.print(weightOfTrueInstances + " ");
		pp("True Impurity = "+trueImpurity + "LiveCount:"+liveCount+" DieCount:"+dieCount);

		liveCount 							= 0;
		dieCount							= 0;
		for (LabelledDataInstance instance:instancesFalse) {
			if (instance.labelName.equals("live")) {liveCount ++;}
			if (instance.labelName.equals("die")) {dieCount ++;}
		}
		double weightOfFalseInstances		= (double) instancesFalse.size() /(double)containedInstances.size();
		double falseImpurity				= weightOfFalseInstances*(2*(double)liveCount *(double)dieCount)
												/Math.pow((liveCount+dieCount),2) ;		
		pp("False Impurity = "+falseImpurity+ "LiveCount:"+liveCount+" DieCount:"+dieCount);
		
		double nodeImpurity			= (trueImpurity + falseImpurity)/2.0;
		pp("Avg Impurity = "+nodeImpurity);
		return nodeImpurity;
				
	}
	
	public boolean isEmpty() {
		if (containedInstances.size()==0) {return true;}
		return false;
		
	}
	
	public boolean isPure() {
		String label						= containedInstances.get(0).labelName;
		for (LabelledDataInstance instance: containedInstances) {
			if (!label.equals(instance.labelName)) {return false;}
		}
		return true;
	}
		
	public void decideBestAttribute() {
		double bestImpurity = 1.0 ;
		
		double impurtiyForAttribute;
		HashMap<String, Double> impurities  = new HashMap<String, Double>();
		for (String attribute : attrsList) {
			impurtiyForAttribute					= computeGeniImpurity(attribute);
			if (impurtiyForAttribute==0) {
				bestImpurity						= impurtiyForAttribute;
				bestAttr							= attribute;
				impurities.put(attribute, impurtiyForAttribute);
				break;
			}
			
			if (impurtiyForAttribute<bestImpurity) {
				bestImpurity						= impurtiyForAttribute;
				bestAttr							= attribute;
				impurities.put(attribute, impurtiyForAttribute);
			}
			
			
		}
		
		attrsList.remove(attrsList.indexOf(bestAttr));
		computeGeniImpurity(bestAttr);
		attributeToSplitOn							=(String)bestAttr;
		nodeName									= bestAttr;
		System.out.println("!!!"+ bestAttr + " " + containedInstances.size() +
							" Split into True: "+ instancesTrue.size() + " & False: "+ instancesFalse.size() 
							+ " Imp:"+bestImpurity);
		System.out.println("\t"+impurities);
		
			
	}
	public String report() {
		String reportString = "";
		System.out.println(this);
		reportString += toString()+"\n";
			
		for (DtNode child : this.children) {
			reportString += child.report();
		}
		
		return reportString;
				
	}
	public void visualNode(Node startNode, Tree decisionTreeModel) {
		startNode.setString("label", getBestAttr());
//		visualRepNode							= startNode;
		for (DtNode child :this.children) {
			Node childVisualNode				= decisionTreeModel.addChild(startNode);
			childVisualNode.setString("label", child.getBestAttr());
			child.visualNode(childVisualNode, decisionTreeModel);			
		}
		
	}
	
	public DtNode branchNode() {
		if (isEmpty()) {return this;}
		
		if(isPure()) {return this;}
		
		if (attrsList.size()==0) {return this;}
		
		decideBestAttribute();
		
		ArrayList<String> attrsClone= (ArrayList<String>) attrsList.clone();
		DtNode leftSideNode			= new DtNode(instancesTrue,attrsClone,originalAttrslist);
		DtNode rightSideNode		= new DtNode(instancesFalse,attrsClone,originalAttrslist);
		leftSideNode.branchNode();
		rightSideNode.branchNode();
		
		this.children.add(leftSideNode);
		this.children.add(rightSideNode);
		leftSideNode.nodeName		= this.nodeName+"_TRUE";
		rightSideNode.nodeName		= this.nodeName+"_FALSE";
		return this;
		
	}

}
