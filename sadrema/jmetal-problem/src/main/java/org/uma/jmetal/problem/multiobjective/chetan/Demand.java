package org.uma.jmetal.problem.multiobjective.chetan;

public class Demand {
	private String user;
	private int capacity;
	private int priority;
	
	public Demand(String user, int demand, int priority)
	{
		this.capacity = demand;
		this.user = user;
		this.priority = priority;
	}

	public int getCapacity() {
		return capacity;
	}

	public String getUser() {
		return user;
	}

	public int getPriority() {
		return priority;
	}

}
