package org.uma.jmetal.problem.multiobjective.chetan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class Beam {
	public List<GridCell> beam_cells = new ArrayList<>();
	public int capacity = 0;
	public int num_Demands = 0;
	public double priority_Demand_Ratio = 0.0;
	public static int MAX_CAPACITY;
	public static int MAX_CELLS;
	public Set<String> users_beam = new HashSet<>();
	private StringJoiner id_List = new StringJoiner(" , "); 
	/*
	 * Constructor
	 */
	public Beam()
	{
		//Hard Coded
		this(SDRM_ReadProperties.getInstance().SAT_CAPACITY[0],SDRM_ReadProperties.getInstance().MAX_BEAM_SIZE); //Default values of Beam capacity and number of cells
	}
	
	/*
	 * Dynamic constructor
	 */
	public Beam(int max_capacity, int max_cells)
	{
		MAX_CAPACITY = max_capacity;
		MAX_CELLS  = max_cells;
	}
	
	/*
	 * Add cell to a grid
	 */
	public boolean addCell(GridCell cell)
	{		
		if((cell.totalCapacityDemand + capacity > MAX_CAPACITY) || (size() + 1 > MAX_CELLS))
		{
			return false;
		}
		beam_cells.add(cell);
		capacity += cell.totalCapacityDemand;
		num_Demands += cell.num_Demands;
		priority_Demand_Ratio += cell.priority_Demand_Ratio;
		id_List.add(cell.getId());
		users_beam.addAll(cell.set_Users);
		return true;
	}
	
	/*
	 * Return size of the beam
	 */
	public int size()
	{
		return beam_cells.size();
	}
	
	@Override
	public String toString()
	{		
		return id_List.toString() + "\n";	
	}
}
