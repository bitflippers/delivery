package org.uma.jmetal.runner.multiobjective.chetan;

import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.multiobjective.chetan.Beam;
import org.uma.jmetal.problem.multiobjective.chetan.Beam_Utils;
import org.uma.jmetal.problem.multiobjective.chetan.SDRM_BinaryMap;
import org.uma.jmetal.problem.multiobjective.chetan.SDRM_GridProperties;
import org.uma.jmetal.problem.multiobjective.chetan.SDRM_ReadProperties;
import org.uma.jmetal.problem.multiobjective.chetan.WriteBeams;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class for configuring and running the NSGA-II algorithm (binary encoding)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class NSGAIIBinaryRunner_SDRM extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.NSGAIIBinaryRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws Exception {
    BinaryProblem problem;
    Algorithm<List<BinarySolution>> algorithm;
    CrossoverOperator<BinarySolution> crossover;
    MutationOperator<BinarySolution> mutation;
    SelectionOperator<List<BinarySolution>, BinarySolution> selection;
    
    JMetalRandom.getInstance().setSeed(10L);
    
    String problemName ;
    String referenceParetoFront = "" ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.chetan.SDRM_BinaryMap";
      referenceParetoFront = "" ;
    }

    //problem = (BinaryProblem) ProblemUtils.<BinarySolution> loadProblem(problemName);
    problem = new SDRM_BinaryMap("/SDRM_files/1552120460216-24ea99c2-f442-4cb4-962b-04a24c87ab84-marker.csv");

    double crossoverProbability = 0.9 ;
    crossover = new SinglePointCrossover(crossoverProbability) ;

    double mutationProbability = 1.0 / problem.getNumberOfBits(0) ;
    mutation = new BitFlipMutation(mutationProbability) ;

    selection = new BinaryTournamentSelection<BinarySolution>() ;

    int populationSize = 100;
    algorithm = new NSGAIIBuilder<BinarySolution>(problem, crossover, mutation, populationSize)
            .setSelectionOperator(selection)
            .setMaxEvaluations(25000)
            .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    List<BinarySolution> population = algorithm.getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    SortnFilterBeams(population);
    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
  
  static void SortnFilterBeams(List<BinarySolution> population) throws IOException
  {
	  Comparator<BinarySolution> c = new Comparator<BinarySolution>() {
		
		@Override
		public int compare(BinarySolution o1, BinarySolution o2) {
			// TODO Auto-generated method stub
			//Objective 0 - Priority
			//Objective 1 - Capacity
			//Objective 2 - Markers
			//Objective 3 - Beams
			int sum = Arrays.stream(SDRM_GridProperties.getInstance().MAX_PRIORITY).sum();
			int total_Markers = SDRM_GridProperties.getInstance().totalMarkers;
			int capacity = Arrays.stream(SDRM_ReadProperties.getInstance().SAT_CAPACITY).sum();
			
			double o1_capacity = -1 * (double)o1.getObjective(1);
			double o2_capacity = -1 * (double)o2.getObjective(1);
			if((o1_capacity - o2_capacity) > capacity/20)
				return 0;
			else if((o2_capacity - o1_capacity) > capacity/20)
				return 1;
			
			double o1_priority = -1 * (double)o1.getObjective(0);
			double o2_priority = -1 * (double)o2.getObjective(0);
			double o1_markers = -1 * (double)o1.getObjective(2);
			double o2_markers = -1 * (double)o2.getObjective(2);
			double o1_extrabeams = (double)o1.getObjective(3);
			double o2_extrabeams = (double)o2.getObjective(3);
			
			double o1_score = o1_priority*12/(sum*sum) + o1_capacity*20/capacity + o1_markers*15/total_Markers - 8*o1_extrabeams;
			double o2_score = o2_priority*12/(sum*sum) + o2_capacity*20/capacity + o2_markers*15/total_Markers - 8*o2_extrabeams;
			if(o1_score > o2_score)
				return 0;
				
			return 1;
		}
	};
	  int i = SolutionListUtils.findIndexOfBestSolution(population, c);
	  System.out.println(population.get(i));
	  BinarySolution soln = population.get(i);
	  StringJoiner joiner = new StringJoiner("\n");
	  for(int j=0; j<3; j++)
	  {
		  BitSet set = soln.getVariableValue(j);
		  Beam_Utils beam = new Beam_Utils();
		  int[][]M = beam.Dto2DArray(set);
		  List<Beam> beams=  beam.countIslands(M,SDRM_BinaryMap.sat_gridcells.get(j),j);
		  int count =1;
		  int sat = j+1;
		  for(Beam b: beams)
		  {			  
			  joiner.add(("Sat_"+sat + " : " + "Beam_"+count +   " Capacity="+b.capacity + ": " + b.size() + " cells " + b).trim());
			  count++;
		  }
	  }
	  String filename = "1552120460216-24ea99c2-f442-4cb4-962b-04a24c87ab84-marker.txt".replace("marker", "beams");
	  WriteBeams.writeBeamData(joiner.toString(), filename);
  }
}