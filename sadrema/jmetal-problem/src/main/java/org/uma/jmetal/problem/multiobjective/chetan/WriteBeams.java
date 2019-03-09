package org.uma.jmetal.problem.multiobjective.chetan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteBeams {
	private static String sep = File.separator;
	private static String path = System.getProperty("user.home") + sep + "gameofcode" + sep + "modelserver" + sep + "io" + sep + "sadrema" + sep + "in" + sep +"data";
	
	public static void main(String [] args)
	{
		String sep = File.separator;
		
		new File(path + sep + "in").mkdirs();
		System.out.println(new File(path + sep + "in").mkdirs());
	}
	
	public static void writeBeamData(String data, String filename) throws IOException
	{
		new File(path).mkdirs();
		BufferedWriter writer = new BufferedWriter(new FileWriter(path + sep + filename));
		writer.write(data);
		writer.close();
	}
}
