package org.uma.jmetal.problem.multiobjective.chetan.backup;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.uma.jmetal.problem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing a bi-objective TSP (Traveling Salesman Problem) problem.
 * It accepts data files from TSPLIB:
 *   http://www.iwr.uni-heidelberg.de/groups/comopt/software/TSPLIB95/tsp/
 */
@SuppressWarnings("serial")
public class MultiobjectiveSDRM extends AbstractIntegerPermutationProblem {
  protected int         numberOfDemands ;
  protected ArrayList<Double> latitudeMatrix ;
  protected ArrayList<Double> longitudeMatrix ;
  protected ArrayList<Integer> demandMatrix ;
  protected ArrayList<Integer> priorityMatrix;
  protected ArrayList<String> usersMatrix;
  protected double [] minDistMatrix;

  /**
   * Creates a new MultiobjectiveTSP problem instance
   */
  public MultiobjectiveSDRM(String dataFile) throws IOException {
    readProblem(dataFile) ;
    //costMatrix     = readProblem(costFile);

    setNumberOfVariables(numberOfDemands);
    setNumberOfObjectives(4);
    //setNumberOfConstraints(1);
    setName("MultiobjectiveSDRM");
  }

  /** Evaluate() method */
  public void evaluate(PermutationSolution<Integer> solution){
    double fitness1   ;
    double fitness2   ;
    double fitness3   ;
    double fitness4   ; //distance

    fitness1 = 0.0 ;
    fitness2 = 0.0 ;
    fitness3 = 0.0 ;
    fitness4 = 0.0 ;
     
   int allotedDemand = 0;
   int totalDemand = 15;
   Set<String> users = new HashSet<>();

    for (int i = 0; i < (numberOfDemands - 1); i++) {
      int x ;
      int y ;
      int idx = solution.getVariableValue(i);
      x = demandMatrix.get(idx) ;
      y = priorityMatrix.get(idx);
      
      //fitness1 += (numberOfDemands -i) * x;
      //fitness2 += (numberOfDemands -i) * y;
      
      allotedDemand += x;
      if(allotedDemand <= totalDemand)
      {
    	  fitness1 +=  y * y / x;
    	  users.add(usersMatrix.get(idx));
    	  fitness3 += y;
    	  fitness4+= minDistMatrix[idx];
      }
      else
      {
    	  break;
      }
    }
    
    fitness2 = users.size();
    solution.setObjective(0, -1 * fitness1);
    solution.setObjective(1, -1 * fitness2);
    solution.setObjective(2, -1 * fitness3);
    solution.setObjective(3, fitness4);
  }

  private void readProblem(String file) throws IOException {
	  latitudeMatrix = new ArrayList<>();
	  longitudeMatrix = new ArrayList<>();
	  demandMatrix = new ArrayList<>();
	  priorityMatrix = new ArrayList<>();
	  usersMatrix = new ArrayList<>();
	  
    InputStream in = getClass().getResourceAsStream(file);
    InputStreamReader isr = new InputStreamReader(in);
    BufferedReader br = new BufferedReader(isr);
    try {
    	String line;
    	int idx = 0;
    		while((line = br.readLine()) !=null)
    		{
    			String[] values = line.split(",");
    			//System.out.println(Integer.parseInt(values[0]));
    			//int idx = Integer.parseInt(values[0]);
    			
    			latitudeMatrix.add(idx , Double.parseDouble(values[1].trim()));
    			longitudeMatrix.add(idx , Double.parseDouble(values[2].trim()));
    			usersMatrix.add(idx, values[3].trim());
    			demandMatrix.add(idx , Integer.parseInt(values[4].trim()));
    			priorityMatrix.add(idx , Integer.parseInt(values[5].trim()));    
    			idx++;
    		}
    		numberOfDemands = idx;
    		minDistMatrix = calcMinDistance();
    } catch (Exception e) {
      new JMetalException("SDRM.readProblem(): error when reading data file " + e);
    }
  }
  
  private double [] calcMinDistance()
  {
	  double [] minDistMatrix = new double[numberOfDemands];
	  double dist;
	  
	  for(int i =0; i<numberOfDemands; i++)
	  {
		  double min_dist = 10000.0;
		  for(int j=0; j<numberOfDemands; j++)
		  {
			  if(i==j)
			  {
				  continue;
			  }
			  double y1 = latitudeMatrix.get(i);
			  double x1 = longitudeMatrix.get(i);
			  double y2 = latitudeMatrix.get(j);
			  double x2 = longitudeMatrix.get(j);
			  dist = Math.sqrt(Math.pow(y1-y2, 2) + Math.pow(x1-x2,2));
			  if(min_dist > dist)
			  {
				  min_dist = dist;
			  }
		  }
		  minDistMatrix[i] = min_dist;
	  }
	  return minDistMatrix;
  }

  @Override public int getPermutationLength() {
    return numberOfDemands ;
  }
}
