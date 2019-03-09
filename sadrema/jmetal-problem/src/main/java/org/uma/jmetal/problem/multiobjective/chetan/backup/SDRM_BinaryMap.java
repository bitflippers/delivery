package org.uma.jmetal.problem.multiobjective.chetan.backup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

/**
 * Each variable is one section of the map - 
 * number of '1's and '0's in a binary string.
 */
@SuppressWarnings({ "serial"})
public class SDRM_BinaryMap extends AbstractBinaryProblem {
  private int bits ;
  protected int         numberOfDemands ;
  protected ArrayList<Integer> demandMatrix ;
  protected ArrayList<Integer> priorityMatrix;
  protected ArrayList<String> usersMatrix;
  public static HashMap<String, GridCell> gridcells;
  private static int num_rows = 5;
  private static int num_cols = 5;
  protected static BitSet grid_snapshot;
  public OverallConstraintViolation<BinarySolution> overallConstraintViolationDegree ;
  public NumberOfViolatedConstraints<BinarySolution> numberOfViolatedConstraints ;
  
  protected double [] minDistMatrix;
  /** Constructor 
 * @throws IOException */
  public SDRM_BinaryMap(String dataFile) throws JMetalException, IOException {
	  this(num_rows*num_cols);
	  //dataFile = "/SDRM_files/Data_20190306_0106.csv";
	  readProblem(dataFile) ;
  }

  /** Constructor */
  public SDRM_BinaryMap(Integer numberOfBits) throws JMetalException {
    setNumberOfVariables(1);
    setNumberOfObjectives(4);
    setNumberOfConstraints(2);
    setName("SDRM_BinaryMap");

    bits = numberOfBits ;
    overallConstraintViolationDegree = new OverallConstraintViolation<BinarySolution>() ;
    numberOfViolatedConstraints = new NumberOfViolatedConstraints<BinarySolution>() ;
  }

  @Override
  protected int getBitsPerVariable(int index) {
//  	if (index != 0) {
//  		throw new JMetalException("Problem OneZeroMax has only a variable. Index = " + index) ;
//  	}
  	return bits ;
  }

  @Override
  public BinarySolution createSolution() {
    return new DefaultBinarySolution(this) ;
  }

  /** Evaluate() method */ 
  @Override
    public void evaluate(BinarySolution solution) {
	  double priority = 0.0; // Objective 1 - Maximize priority^2/demand
	  int capacity = 0; // Objective 2 -Maximize the capacity catered 
    int demands = 0; // Objective 3 - Maximize the number of demands
    int num_extraBeams = 0; //Objective 4 - Minimize the number of extra beams chosen (more than 3)
    

    BitSet bitset = solution.getVariableValue(0) ;
    Beam_Utils beam_utils = new Beam_Utils();
    bitset.and(grid_snapshot);
    int[][] arr = Beam_Utils.Dto2DArray(bitset);
    
    List<Beam> list_Beams = beam_utils.countIslands(arr);
    for(Beam beam: list_Beams)
    {
    	capacity += beam.capacity;
    	demands += beam.num_Demands;
    	priority += beam.priority_Demand_Ratio;
    }
    num_extraBeams = list_Beams.size() - 3;
    
    double[] constraint = new double[getNumberOfConstraints()];
    constraint[0] = 45 - capacity;
    constraint[1] = num_extraBeams > 0? -1*num_extraBeams:0;

    double overallConstraintViolation = 0.0;
    int violatedConstraints = 0;
    for (int i = 0; i < getNumberOfConstraints(); i++) {
      if (constraint[i]<0.0){
        overallConstraintViolation+=constraint[i];
        violatedConstraints++;
      }
    }
    overallConstraintViolationDegree.setAttribute(solution, overallConstraintViolation);
    numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);

    // For maximization problem: multiply by -1 to minimize
    solution.setObjective(0, -1.0 * priority);
    solution.setObjective(1, -1.0 * capacity);
    solution.setObjective(2, -1.0 * demands);   
    solution.setObjective(3, 1.0 * num_extraBeams);
  }
  
  private void readProblem(String file) throws IOException {
	  
	  demandMatrix = new ArrayList<>();
	  priorityMatrix = new ArrayList<>();
	  usersMatrix = new ArrayList<>();
	  gridcells = new HashMap<>();
	  grid_snapshot = new BitSet(num_cols * num_rows);
	  
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
    			
    			//Pattern - ID, capacity, priority, User
    			
    			String cell_id = values[0].trim().replaceAll("\"", "");
    			String [] lat_long = cell_id.split("-");
    			int row_idx = Integer.parseInt(lat_long[0]);
    			int col_idx = Integer.parseInt(lat_long[1]);
    			grid_snapshot.set((num_rows * (row_idx-1)) + col_idx-1);
    			
    			Demand demand = new Demand(values[3].trim(), Integer.parseInt(values[1].trim()), Integer.parseInt(values[2].trim()));
    			
    			//Find the cell to which the demand belongs 
    			//int lat_idx = (90-(int)(Double.parseDouble(values[0].trim())))/10;
    			//int long_idx = (180+(int)(Double.parseDouble(values[1].trim())))/10;
    			
    			if(gridcells.containsKey(cell_id))
    			{
    				GridCell cell = gridcells.get(cell_id);
    				cell.addDemand(demand);
    			}
    			else
    			{
    				GridCell cell = new GridCell(cell_id);
    				cell.addDemand(demand);
    				gridcells.put(cell_id, cell);
    			}
    			idx++;
    		}
    		numberOfDemands = idx;
    		//minDistMatrix = calcMinDistance();
    } catch (Exception e) {
      new JMetalException("SDRM.readProblem(): error when reading data file " + e);
    }
  }
}
