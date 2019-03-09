package org.uma.jmetal.problem.multiobjective.chetan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

/**
 * Each variable is one section of the map - 
 * number of '1's and '0's in a binary string.
 */
@SuppressWarnings({ "serial"})
public class SDRM_BinaryMap extends AbstractBinaryProblem {
  private int bits ; //Number of bits per beam
  protected int []        numberOfDemands ; //Number of demands
  public static List<HashMap<String, GridCell>> sat_gridcells;
  private int num_ObjectivesperRegion = 4;
  
  protected SDRM_ReadProperties prop;
  public OverallConstraintViolation<BinarySolution> overallConstraintViolationDegree ;
  public NumberOfViolatedConstraints<BinarySolution> numberOfViolatedConstraints ;
  
  /*
   * configuration Params
   */
  private static int num_rows;
  private static int num_cols;
  private static int num_satellites;
  
  /*
   * Entire GRID
   */ 
  protected static List<BitSet> grid_snapshot; //The entire grid
  
  
  protected double [] minDistMatrix;
  /** Constructor 
 * @throws IOException */
//  public SDRM_BinaryMap(String dataFile) throws JMetalException, IOException {
//	  this(dataFile);
//  }

  /** Constructor 
 * @throws IOException */
  public SDRM_BinaryMap(String dataFile) throws JMetalException, IOException {	
	init(); //Read configuration properties
	  readProblem(dataFile) ;  
    setNumberOfVariables(num_satellites); //Number of Satellites
    setNumberOfObjectives(num_ObjectivesperRegion);
    setNumberOfConstraints(2);
    setName("SDRM_BinaryMap");

    bits = num_rows * num_cols ;
    overallConstraintViolationDegree = new OverallConstraintViolation<BinarySolution>() ;
    numberOfViolatedConstraints = new NumberOfViolatedConstraints<BinarySolution>() ;
    
  }
  
  private void init()
  {
	  prop = SDRM_ReadProperties.getInstance();
	  num_rows = prop.ROW;
	  num_cols = prop.COL;
	  num_satellites = prop.NUM_SAT;
	  sat_gridcells = new ArrayList<HashMap<String,GridCell>>(num_satellites);
	  grid_snapshot = new ArrayList<>(num_satellites);
	  for(int i=0; i<num_satellites; i++)
	  {
		  HashMap<String, GridCell> grid_cells = new HashMap<>();
		  sat_gridcells.add(i, grid_cells);
		  grid_snapshot.add(i, new BitSet(bits));
	  }	  
	  numberOfDemands = new int[num_satellites];
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
	double [] priority = new double[num_satellites]; // Objective 1 - Maximize priority^2/demand
	int [] capacity = new int[num_satellites]; // Objective 2 -Maximize the capacity catered 
    int [] demands = new int[num_satellites]; // Objective 3 - Maximize the number of demands
    int [] num_extraBeams = new int[num_satellites]; //Objective 4 - Minimize the number of extra beams chosen (more than 3)
    int [] extrabits = new int[num_satellites]; //Objective 5 - Maximixe the num_users
    int total_Capacity = 0;
    int total_Demands = 0;
    double total_priority = 0.0;
    int total_num_extraBeams = 0;
    int total_num_extraBits = 0;
    
    double overallConstraintViolation = 0.0;
    int violatedConstraints = 0;
    List<Beam> list = new ArrayList<>();
    for(int satellite=0; satellite<num_satellites; satellite++)
    {    	
    	BitSet bitset = solution.getVariableValue(satellite) ;
    	BitSet bitset_copy = (BitSet)bitset.clone();
    	Beam_Utils beam_utils = new Beam_Utils();
    	bitset.and(grid_snapshot.get(satellite));
    	int[][] arr = Beam_Utils.Dto2DArray(bitset);
    
    	List<Beam> list_Beams = beam_utils.countIslands(arr,sat_gridcells.get(satellite),satellite);
    	list.addAll(list_Beams);
    	int cellsInBeam=0;
	    for(Beam beam: list_Beams)
	    {
	    	cellsInBeam+=beam.size();
	    	capacity[satellite] += beam.capacity;
	    	demands[satellite] += beam.num_Demands;
	    	priority[satellite] += beam.priority_Demand_Ratio;
	    }
	    extrabits[satellite] = bitset_copy.cardinality() - bitset.cardinality();
	    int extrabeams = list_Beams.size()-3;
	    num_extraBeams[satellite] = extrabeams;
	    
	    
    double[] constraint = new double[getNumberOfConstraints()];
    constraint[0] = prop.SAT_CAPACITY[satellite] - capacity[satellite];
    constraint[1] = num_extraBeams[satellite] > 0? -1*num_extraBeams[satellite]:0;
    //constraint[1] = num_extraBeams[satellite] < -3? constraint[1]+num_extraBeams[satellite]:constraint[1];
    
    for (int i = 0; i < getNumberOfConstraints(); i++) {
        if (constraint[i]<0.0){
          overallConstraintViolation+=constraint[i];
          violatedConstraints++;
        }
     }
    total_Capacity+= capacity[satellite];
    total_Demands += demands[satellite];
    total_num_extraBeams+=num_extraBeams[satellite];
    total_priority+=priority[satellite];
    total_num_extraBits+=extrabits[satellite];
    BinarySet binSet = (BinarySet) bitset;
    solution.setVariableValue(satellite, binSet);
//	    solution.setObjective(num_ObjectivesperRegion * satellite + 0, -1.0 * priority[satellite]);
//	    solution.setObjective(num_ObjectivesperRegion * satellite + 1, -1.0 * capacity[satellite]);
//	    solution.setObjective(num_ObjectivesperRegion * satellite + 2, -1.0 * demands[satellite]);   
//	    solution.setObjective(num_ObjectivesperRegion * satellite + 3, 1.0 * num_extraBeams[satellite]);
    }
    solution.setObjective(0, -1.0 * total_priority);
    solution.setObjective(1, -1.0 * total_Capacity);
    solution.setObjective(2, -1.0 * total_Demands);
    solution.setObjective(3, 1.0 * total_num_extraBeams);
    //solution.setObjective(4, 1.0 * total_num_extraBits); 
    
    
    
    overallConstraintViolationDegree.setAttribute(solution, overallConstraintViolation);
    numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);    
  }
  
  private void readProblem(String file) throws IOException {
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
    			int row_idx = Integer.parseInt(lat_long[1]);
    			int col_idx = Integer.parseInt(lat_long[0]);
    			cell_id = String.format("%02d",row_idx)+ "-" + String.format("%02d",col_idx);
    			
    			Demand demand = new Demand(values[3].trim(), Integer.parseInt(values[1].trim()), Integer.parseInt(values[2].trim()));
    			
    			//Find the region where the demand belongs;
    			int SatIdx = 0;
    			for(int i=0; i<num_satellites; i++)
    			{    				
					if(col_idx >=  prop.SAT_MIN_LONGITUDE[i] && col_idx <= prop.SAT_MAX_LONGITUDE[i])
					{
						SatIdx = i;
						break;
					}    						
    			}
    			SDRM_GridProperties.getInstance().setMax_Priority(Integer.parseInt(values[2].trim()), SatIdx);
    			grid_snapshot.get(SatIdx).set((num_rows * (row_idx-1)) + col_idx-(SatIdx*num_cols) - 1); //HARD-CODED
    			
    			HashMap<String, GridCell> gridcells = sat_gridcells.get(SatIdx);
    			int local_ColId = col_idx-(SatIdx*num_cols);
    			String localid = String.format("%02d",row_idx) + "-" + String.format("%02d",local_ColId);//Local ID in the Grid
    			if(gridcells.containsKey(cell_id))
    			{
    				GridCell cell = gridcells.get(cell_id);
    				cell.addDemand(demand);
    			}
    			else
    			{
    				GridCell cell = new GridCell(cell_id, localid);
    				cell.addDemand(demand);
    				gridcells.put(cell_id, cell);
    			}
    			numberOfDemands[SatIdx]++;    			
    		}
    } catch (Exception e) {
      new JMetalException("SDRM.readProblem(): error when reading data file " + e);
    }
  }
}
