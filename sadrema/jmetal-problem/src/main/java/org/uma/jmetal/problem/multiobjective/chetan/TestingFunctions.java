package org.uma.jmetal.problem.multiobjective.chetan;

import java.util.Arrays;
import java.util.BitSet;

public class TestingFunctions {
	
	public static void main(String [] args)
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
		
	}

}
