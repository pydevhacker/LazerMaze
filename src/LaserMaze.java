import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;


public class LaserMaze {
	
	int[] startState = new int[3];
	int[] goalState = new int[3];
	
	Stack<int[]> openMoves = new Stack<>();
	
	public LaserMaze(){
		
		process();
	}
	
	public static void main(String[] args){
		new LaserMaze();
	}
	
	int currentIndex = 0;
	
	private void process(){
		List<String> lines = readFile();
		int cases = Integer.valueOf(lines.get(currentIndex++));
		
		for(int i = 0; i<1; i++){
			String[] matrixSize = lines.get(currentIndex++).split(" ");
			String[][] matrix = new String[Integer.valueOf(matrixSize[0])][Integer.valueOf(matrixSize[1])];
			for(int j = 0; j<matrix.length; j++){
				String[] row = lines.get(currentIndex++).split("");
				for(int k = 0; k<row.length; k++){
					matrix[j][k] = row[k];
					if(row[k].equals("S")){
						startState[0] = j;
						startState[1] = k;
						matrix[j][k] = ".";
						openMoves.push(startState);
					}else if(row[k].equals("G")){
						goalState[0] = j;
						goalState[1] = k;
						matrix[j][k] = ".";
					}else{
						matrix[j][k] = row[k];
					}
				}
				
				while(!openMoves.isEmpty()){
					int[] state = openMoves.pop();
					boolean isValid = isValidMove(state[0], state[1], matrix);
					matrix[state[0]][state[1]] = "1";//explored
					if(!isValid){
						continue;
					}
					List<int[]> posMoves = movePos(state[0], state[1], matrix);
					if(!posMoves.isEmpty()){
						for(int[] posMove : posMoves){
							if(posMoves.size()>1 && matrix[posMove[0]][posMove[1]].equals("1")){
								continue;
							}
							openMoves.push(posMove);
						}
					}
					printMoves(posMoves);
				}
			}
			System.out.println("case : " + i);
			printArray(matrix);
		}
	}
	
	private boolean isValidMove(int x, int y, String[][] matrix){
		//check up
		int up = x-1;
		while(up>=0 && matrix[up][y].equals(".")){
			if(matrix[up][y].equals(".")){
				up = up-1;
			}else if(matrix[up][y].equals("V")){
				return false;
			}
		}
		
		//check down
		int down = x+1;
		while(down<matrix.length && matrix[down][y].equals(".")){
			if(matrix[down][y].equals(".")){
				down = down+1;
			}else if(matrix[down][y].equals("^")){
				return false;
			}
		}
		
		//check left
		int left = y-1;
		while(left>=0 && matrix[x][left].equals(".")){
			if(matrix[x][left].equals(".")){
				left = left-1;
			}else if(matrix[left][y].equals(">")){
				return false;
			}
		}
		
		//check right
		int right = y+1;
		while(right<matrix[0].length&& matrix[x][right].equals(".")){
			if(matrix[x][right].equals(".")){
				right = right+1;
			}else if(matrix[right][y].equals("<")){
				return false;
			}
		}
		
		return true;
	}
	
	private void printMoves(List<int[]> lst){
		for(int[] i : lst){
			System.out.println(Arrays.toString(i));
		}
	}
	
	
	private List<int[]> movePos(int x, int y, String[][] matrix){
		List<int[]> moves = new ArrayList<>();
		//left move
		if(x -1>=0 && ".".equals(matrix[x-1][y])){
			int[] move = new int[3];
			move[0] = x-1;
			move[1] = y;
			moves.add(move);
		}
		//up move
		if(y-1>=0&& ".".equals(matrix[x][y-1])){
			int[] move = new int[3];
			move[0] = x;
			move[1] = y-1;
			moves.add(move);
		}
		//right move
		if(x+1< matrix.length&& ".".equals(matrix[x+1][y])){
			int[] move = new int[3];
			move[0] = x+1;
			move[1] = y;
			moves.add(move);
		}
		//down move
		if(y+1 < matrix[0].length&& ".".equals(matrix[x][y+1])){
			int[] move = new int[3];
			move[0] = x;
			move[1] = y+1;
			moves.add(move);
		}
		return moves;
	}
	
	private void printArray(String[][] arr){
		for(int i = 0; i<arr.length; i++){
			System.out.println(Arrays.toString(arr[i]));
		}
	}

	private List<String> readFile(){
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get("src/Input"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
	
}
