package org.uma.jmetal.problem.multiobjective.chetan.backup;

import java.util.ArrayList;
import java.util.List;

public class GridCell {
	private String id;
	public List<Demand> cell_Demands = new ArrayList<>();
	public int maxPriority = 0;
	public double avgPriority = 0.0;
	public int totalCapacityDemand = 0;
	public int totalPriorityDemand = 0;
	public int num_Demands = 0;
	public double priority_Demand_Ratio = 0.0; 
	
	public GridCell(String id)
	{
		this.id = id;
	}
		
	public String getId() {
		return id;
	}

	public void addDemand(Demand demand)
	{
		cell_Demands.add(demand);
		maxPriority = Math.max(maxPriority, demand.getPriority());
		totalCapacityDemand +=  demand.getCapacity();
		totalPriorityDemand += demand.getPriority();
		avgPriority = totalPriorityDemand / (double)cell_Demands.size();
		num_Demands = cell_Demands.size();
		priority_Demand_Ratio += Math.pow(demand.getPriority(), 2) / (double)demand.getCapacity();
	}	
}
