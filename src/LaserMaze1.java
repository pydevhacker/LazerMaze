import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;


public class LaserMaze1 {

	int currentIndex = 0;
	int m = 0;
	int n = 0;
	int startState;
	int goalState;
	
	Stack<Integer> openStates;
	Stack<Integer> closedStates;
	
	public LaserMaze1(){
		process();
	}
	
	public static void  main(String[] args){
		new LaserMaze1();
	}
	
	private int getState(int x, int y){
		return (n*x + y);
	}
	
	private int[] getXY(int cell){
		int[] pos = new int[2];
		pos[0] = cell / n;
		pos[1] = cell % n;
		return pos;
	}
	
	public void process(){
		List<String> lines = readFile();
		int cases = Integer.valueOf(lines.get(currentIndex++));
		
		for(int i = 0; i<1; i++){
			openStates = new Stack<>();
			closedStates = new Stack<>();
			
			String[] matrixSize = lines.get(currentIndex++).split(" ");
			m = Integer.valueOf(matrixSize[0]);
			n = Integer.valueOf(matrixSize[1]);
			String[][] matrix = new String[m][n];
			for(int j = 0; j<matrix.length; j++){
				String[] row = lines.get(currentIndex++).split("");
				for(int k = 0; k<row.length; k++){
					matrix[j][k] = row[k];
					if(row[k].equals("S")){
						startState = getState(j, k);
						matrix[j][k] = ".";
						openStates.push(startState);
					}
					if(row[k].equals("G")){
						goalState = getState(j, k);
						matrix[j][k] = ".";
					}
				}
			}
			int count = 0;
			boolean foundGoal = false;
			while(!openStates.isEmpty()){
				int currState = openStates.pop();
				if(count > 0){
					makeMove(matrix);
				}
				if(currState == goalState){
					foundGoal = true;
					break;
				}
				pushMoves(currState, matrix);
				closedStates.push(currState);
				count++;
			}
			if(foundGoal){
				System.out.println("Case #" + (i+1) + ": " + count);
			}else{
				System.out.println("Case #" + (i+1) + ": impossible");
			}
		}
	}
	
	private int evalState(int state){
		int[] gState = getXY(goalState);
		int[] cState = getXY(state);
		int x = Math.abs(gState[0] - cState[0]);
		int y = Math.abs(gState[1] - cState[1]);
		return x+y;
	}
	
	private void pushMoves(int currState, String[][] matrix){
		List<Integer> moves = getMoves(currState, matrix);
		for(int i = 0; i<moves.size(); i++){
			int max = i;
			for(int j = i; j<moves.size(); j++){
				if(evalState(moves.get(i))<evalState(moves.get(j))){
					max = j;
				}
			}
			if(max != i){
				int temp = moves.get(max);
				moves.set(max, moves.get(i));
				moves.set(i, temp);
			}
		}
		
		for(int i = 0; i<moves.size(); i++){
			if(!closedStates.contains(moves.get(i)) && isValidMove(moves.get(i), matrix)){
				openStates.push(moves.get(i));
			}
		}
		if(openStates.isEmpty()){
			while(!closedStates.isEmpty()){
				int state = closedStates.pop();
				if(isValidMove(state, matrix)){
					openStates.push(state);
					break;
				}
			}
		}
		
	}
	
	private void makeMove(String[][] matrix){
		for(int i = 0; i<m; i++){
			for(int j = 0; j<n; j++){
				if(matrix[i][j].equals(">")){
					matrix[i][j] = "v";
					continue;
				}
				if(matrix[i][j].equals("v")){
					matrix[i][j] = "<";
					continue;
				}
				if(matrix[i][j].equals("<")){
					matrix[i][j] = "^";
					continue;
				}
				if(matrix[i][j].equals("^")){
					matrix[i][j] = ">";
					continue;
				}
			}
		}
	}
	
	private boolean isValidMove(int state, String[][] matrix){
		int[] xy = getXY(state);
		return isValidMove(xy[0],xy[1], matrix);
	}
	
	private boolean isValidMove(int x, int y, String[][] matrix){
		//check up
		int up = x-1;
		while(up>=0){
			if(matrix[up][y].equals(".")){
				up = up-1;
			}else if(matrix[up][y].equals(">")){ //in next move it will be V
				return false;
			}else{
				break;
			}
		}
		
		//check down
		int down = x+1;
		while(down<matrix.length){
			if(matrix[down][y].equals(".")){
				down = down+1;
			}else if(matrix[down][y].equals("<")){ // in next move it will be ^
				return false;
			}else{
				break;
			}
		}
		
		//check left
		int left = y-1;
		while(left>=0){
			if(matrix[x][left].equals(".")){
				left = left-1;
			}else if(matrix[x][left].equals("^")){ // in next move it will be >
				return false;
			}else{
				break;
			}
		}
		
		//check right
		int right = y+1;
		while(right<matrix[0].length){
			if(matrix[x][right].equals(".")){
				right = right+1;
			}else if(matrix[x][right].equals("v")){ // in next move it will be <
				return false;
			}else{
				break;
			}
		}
		
		return true;
	}
	
	private List<Integer> getMoves(int state, String[][] matrix){
		int[] xy = getXY(state);
		return getMoves(xy[0],xy[1], matrix);
	}
	
	private List<Integer> getMoves(int x, int y, String[][] matrix){
		List<Integer> moves = new ArrayList<>();
		//up
		if(x -1>=0 && ".".equals(matrix[x-1][y])){
			moves.add(getState(x-1, y));
		}
		//left
		if(y-1>=0&& ".".equals(matrix[x][y-1])){
			moves.add(getState(x, y-1));
		}
		//down
		if(x+1< matrix.length&& ".".equals(matrix[x+1][y])){
			moves.add(getState(x+1, y));
		}
		//right
		if(y+1 < matrix[0].length&& ".".equals(matrix[x][y+1])){
			moves.add(getState(x, y+1));
		}
		return moves;
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
