package org.uma.jmetal.problem.multiobjective.chetan;

import java.util.HashSet;
import java.util.Set;

public class SDRM_GridProperties {

	public int[] MAX_PRIORITY;
	public int totalMarkers;
	public Set<String> setUsers = new HashSet<>();
	
	private static final SDRM_GridProperties instance = new SDRM_GridProperties();
	
	private SDRM_GridProperties()
	{
		MAX_PRIORITY = new int[3];
		totalMarkers = 0;
	}
	
	public void setMax_Priority(int priority, int satIdx, String user)
	{
		if(MAX_PRIORITY[satIdx] < priority)
			MAX_PRIORITY[satIdx] = priority;
		totalMarkers++;
		setUsers.add(user);
	}
	
	public static SDRM_GridProperties getInstance()
	{
		return instance;
	}
	
	public static void main(String [] args)
	{
		SDRM_GridProperties prop = new SDRM_GridProperties();
	}

}
