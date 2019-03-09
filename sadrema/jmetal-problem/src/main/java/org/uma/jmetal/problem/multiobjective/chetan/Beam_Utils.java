package org.uma.jmetal.problem.multiobjective.chetan;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Beam_Utils 
{ 
    //No of rows and columns 
    static int ROW;
    static int COL;
  
    public Beam_Utils()
    {
    	ROW = SDRM_ReadProperties.getInstance().ROW;
    	COL = SDRM_ReadProperties.getInstance().COL;
    }
    // A function to check if a given cell (row, col) can 
    // be included in DFS 
    boolean isSafe(int M[][], int row, int col, 
                   boolean visited[][]) 
    { 
        // row number is in range, column number is in range 
        // and value is 1 and not yet visited 
        return (row >= 0) && (row < ROW) && 
               (col >= 0) && (col < COL) && 
               (M[row][col]==1 && !visited[row][col]); 
    } 
  
    // A utility function to do DFS for a 2D boolean matrix. 
    // It only considers the 8 neighbors as adjacent vertices 
    void DFS(int M[][], int row, int col, boolean visited[][], Beam island, HashMap<String, GridCell> gridcells, int region) 
    { 
    	// These arrays are used to get row and column numbers 
        // of 4 neighbors of a given cell 
        int rowNbr[] = new int[] {-1, 1,  0, 0}; 
        int colNbr[] = new int[] {0,  0, -1, 1};
        
        // TODO: remove invalid moves
        //new Random().nextInt()
  
        // Mark this cell as visited        
        String id = String.format("%02d", row+1) + "-" + String.format("%02d", region*COL+col+1);
        GridCell cell = gridcells.get(id);
        if(!island.addCell(cell))
        {
        	return;
        }
        visited[row][col] = true;        
        
  
        // Recur for all connected neighbours 
        for (int k = 0; k < 4; ++k) 
            if (isSafe(M, row + rowNbr[k], col + colNbr[k], visited) ) 
                DFS(M, row + rowNbr[k], col + colNbr[k], visited,island,gridcells,region); 
    } 
  
    // The main function that returns count of islands in a given 
    //  boolean 2D matrix 
    public List<Beam> countIslands(int M[][], HashMap<String, GridCell> gridcells, int region) 
    { 
        // Make a bool array to mark visited cells. 
        // Initially all cells are unvisited 
        boolean visited[][] = new boolean[ROW][COL]; 
  
  
        // Initialize count as 0 and travese through the all cells 
        // of given matrix 
        int count = 0;
        List<Beam> list_Islands = new ArrayList<>();
        
        for (int i = 0; i < ROW; ++i) 
            for (int j = 0; j < COL; ++j) 
                if (M[i][j]==1 && !visited[i][j]) // If a cell with 
                {                                 // value 1 is not 
                    // visited yet, then new island found, Visit all 
                    // cells in this island and increment island count 
                	Beam island = new Beam();
                    DFS(M, i, j, visited,island,gridcells,region); 
                    ++count; 
                    list_Islands.add(island);
                    //System.out.println("Island " + count + ":" + island);
                }
        return list_Islands; 
    } 
    
    public static int[][] Dto2DArray(BitSet set)
    {
    	int M[][]=  new int[ROW][COL];
    	int idx = 0;
    	for (int i = 0; i < ROW; ++i) 
            for (int j = 0; j < COL; ++j) 
            {
            	int myInt = set.get(idx) ? 1 : 0;
            	M[i][j] = myInt;
            	idx++;
            }
    	return M;
    }
  
    // Driver method 
    public static void main (String[] args) throws java.lang.Exception 
    { 
        int M[][]=  new int[][] {{1, 1, 0, 0, 0}, 
                                 {0, 1, 0, 0, 1}, 
                                 {1, 1, 0, 1, 1}, 
                                 {0, 0, 0, 0, 0}, 
                                 {1, 0, 1, 0, 1}
                                };
        int D[][]=  new int[][] {{4, 5, 0, 0, 0}, 
            					 {0, 7, 0, 0, 9}, 
        					 	 {3, 2, 0, 4, 8}, 
        					 	 {0, 0, 0, 0, 0}, 
        					 	 {2, 0, 11, 0, 1} 
           						};
        HashMap<String, GridCell>gridcells = new HashMap<>();  						
        for(int i=0; i<ROW; i++)
        {
        	for(int j=0; j<COL; j++)
        	{
        		 String id = String.format("%02d", i) + "-" + String.format("%02d", j);
        		GridCell cell = new GridCell(id, id);
        		Demand demand = new Demand("User", D[i][j], 2);
        		cell.addDemand(demand);
        		gridcells.put(id, cell);
        	}
        }
        Beam_Utils I = new Beam_Utils();
        //System.out.println("Number of islands is: "+ I.countIslands(M));
        System.out.println("Beams: "+ I.countIslands(M,gridcells,0).size());
        
    } 
}