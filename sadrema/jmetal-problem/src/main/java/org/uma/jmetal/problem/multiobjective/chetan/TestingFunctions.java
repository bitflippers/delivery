package org.uma.jmetal.problem.multiobjective.chetan;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.List;

public class TestingFunctions {
	
	public static void main(String [] args) throws IOException
	{
		//System.out.println(String.format("%02d", 101));
//		Beam_Utils I = new Beam_Utils();
//		BitSet set = new BitSet(25);
//		set.flip(0);
//		set.flip(2);
//		set.flip(5);
//		set.flip(21);
//		System.out.println(set.length());
//		System.out.println(Arrays.deepToString(Beam_Utils.Dto2DArray(set)));
		System.out.println("\"\"HELLO\"\"".trim().replaceAll("\"", ""));
		BitSet set = new BitSet(10);
		set.set(1);
		set.set(9);
		for(int i=0; i<100; i++)
		{
			System.out.println((int) (Math.random() * (5)));
		}
		//InputStream in = TestingFunctions.class.getResourceAsStream("/Users/chetan.arora/gameofcode/modelserver/io/sadrema/out/data/1552126543994-1eed99e7-061e-4e28-9203-c647428cd632-marker.csv");
		List<String> str = Files.readAllLines(Paths.get("/Users/chetan.arora/gameofcode/modelserver/io/sadrema/out/data/1552126543994-1eed99e7-061e-4e28-9203-c647428cd632-marker.csv"));
	    //InputStreamReader isr = new InputStreamReader(in);
		
	}

}
