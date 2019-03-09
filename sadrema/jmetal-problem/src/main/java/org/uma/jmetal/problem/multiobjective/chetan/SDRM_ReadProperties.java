package org.uma.jmetal.problem.multiobjective.chetan;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.uma.jmetal.util.JMetalException;

public class SDRM_ReadProperties {
	public int NUM_SAT;
	public int[] SAT_MIN_LONGITUDE;
	public int[] SAT_MAX_LONGITUDE;
	public int[] SAT_CAPACITY;
	public int MAX_BEAMS;
	public int MAX_BEAM_SIZE;
	public int ROW;
	public int COL;
	
	private static final SDRM_ReadProperties instance = new SDRM_ReadProperties();
	
	private SDRM_ReadProperties()
	{
		String file = "/SDRM_files/config.properties";
		InputStream in = getClass().getResourceAsStream(file);
	    InputStreamReader isr = new InputStreamReader(in);
	    BufferedReader br = new BufferedReader(isr);
	    Properties prop = new Properties();
	    try {
	    		prop.load(br);
	    		NUM_SAT = Integer.parseInt(prop.getProperty("num_satellites"));
	    		SAT_MIN_LONGITUDE = new int[NUM_SAT];
	    		SAT_MAX_LONGITUDE = new int[NUM_SAT];
	    		SAT_CAPACITY = new int[NUM_SAT];
	    		MAX_BEAMS = Integer.parseInt(prop.getProperty("Max_beams"));
	    		MAX_BEAM_SIZE = Integer.parseInt(prop.getProperty("Max_beam_size")); //The number of grid cells allowed in a beam
	    		ROW = Integer.parseInt(prop.getProperty("Rows"));
	    		COL = Integer.parseInt(prop.getProperty("Cols"));
	    		
	    		for(int i=1; i<=NUM_SAT; i++)
	    		{
	    			SAT_MIN_LONGITUDE[i-1] = Integer.parseInt(prop.getProperty("SAT_"+ i + "_minLong"));
	    			SAT_MAX_LONGITUDE[i-1] = Integer.parseInt(prop.getProperty("SAT_"+ i + "_maxLong"));
	    			SAT_CAPACITY[i-1] = Integer.parseInt(prop.getProperty("SAT_"+ i + "_capacity"));
	    		}	    	
	    }
	    catch (Exception e) {
	        new JMetalException("SDRM_ReadProperties(): error when reading data file " + e);
	      }
	}
	
	public static SDRM_ReadProperties getInstance()
	{
		return instance;
	}
	
	public static void main(String [] args)
	{
		SDRM_ReadProperties prop = new SDRM_ReadProperties();
	}

}
