package org.uma.jmetal.problem.multiobjective.chetan.backup;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Beam {
	public List<GridCell> beam_cells = new ArrayList<>();
	public int capacity = 0;
	public int num_Demands = 0;
	public double priority_Demand_Ratio = 0.0;
	public static int MAX_CAPACITY = 15;
	public static int MAX_CELLS = 4;
	private StringJoiner id_List = new StringJoiner(" , "); 
	
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
		return true;
	}
	
	public int size()
	{
		return beam_cells.size();
	}
	
	@Override
	public String toString()
	{		
		return id_List.toString();	
	}
}
