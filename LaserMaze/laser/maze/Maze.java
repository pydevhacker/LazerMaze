package laser.maze;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Maze {
	List<List<String>> maze = null;
	int m = 0;
	int n = 0;
	Set<Integer> forbidden = new HashSet<>();
	List<Integer> lasers = new ArrayList<>();
	List<Integer> walls = new ArrayList<>();
	int startState = 0;
	int goalState = 0;
	int currentState = 0;
	
	public Maze(List<List<String>> maze){
		createMaze(maze);
		this.m = maze.size();
		this.n = maze.get(0).size();
		processMaze();
		calforbidden();
	}
	
	public Maze(Maze maze){
		this(maze.getMaze());
		this.goalState = maze.getGoalState();
		this.startState = maze.getStartState();
	}
	
	private void createMaze(List<List<String>> maze){
		this.maze = new ArrayList<>();
		for(List<String> str : maze){
			List<String> lst = new ArrayList<>(str);
			this.maze.add(lst);
		}
	}
	
	private void processMaze(){
		for(int i = 0; i<m; i++){
			for(int j = 0; j<n ; j++){
				if(maze.get(i).get(j).equals(">") || maze.get(i).get(j).equals("<") ||
						maze.get(i).get(j).equals("^") || maze.get(i).get(j).equals("v")){
					lasers.add(getState(i, j));
				}
				if(maze.get(i).get(j).equals("S")){
					startState = getState(i, j);
					maze.get(i).set(j,".");
				}
				if(maze.get(i).get(j).equals("G")){
					goalState = getState(i, j);
					maze.get(i).set(j,".");
				}
			}
		}
	}
	
	public int getM(){
		return this.m;
	}
	
	public int getN(){
		return this.n;
	}
	
	public int[] getXY(int state){
		int[] xy = new int[2];
		xy[0] = state/n;
		xy[1] = state%n;
		return xy;
	}
	
	public int getState(int x, int y){
		return (x*n)+y;
	}
	
	public int getCurrentState(){
		return currentState;
	}
	
	public void setCurrentState(int currState){
		this.currentState = currState;
	}
	public int getStartState(){
		return startState;
	}
	
	public int getGoalState(){
		return goalState;
	}
	
	public List<List<String>> getMaze(){
		return maze;
	}
	
	public Set<Integer> getForbidden(){
		return forbidden;
	}
	
	public void calforbidden(){
		forbidden = new HashSet<>();
		for(int laser : lasers){
			int[] pos = getXY(laser);
			int x = pos[0];
			int y = pos[1];
			
			if(maze.get(pos[0]).get(pos[1]).equals(">")){
				x = pos[0];
				y = pos[1];
				y = y+1;
				while(y<n && maze.get(x).get(y).equals(".")){
					forbidden.add(getState(x, y));
					y = y+1;
				}
			}
			
			if(maze.get(pos[0]).get(pos[1]).equals("v")){
				x = pos[0];
				y = pos[1];
				x = x+1;
				while(x<m && maze.get(x).get(y).equals(".")){
					forbidden.add(getState(x, y));
					x++;
				}
			}
			
			if(maze.get(pos[0]).get(pos[1]).equals("<")){
				x = pos[0];
				y = pos[1];
				y--;
				while(y>=0 && maze.get(x).get(y).equals(".")){
					forbidden.add(getState(x, y));
					y--;
				}
			}
			
			if(maze.get(pos[0]).get(pos[1]).equals("^")){
				x = pos[0];
				y = pos[1];
				x--;
				while(x>=0 && maze.get(x).get(y).equals(".")){
					forbidden.add(getState(x, y));
					x--;
				}
			}
		}
	}
	
	
	public static Maze getNextMaze(Maze maze){
		Maze tempMaze = new Maze(maze);
		List<List<String>> maz = tempMaze.getMaze();
		for(int i = 0; i<maz.size(); i++){
			for(int j = 0; j<maz.get(0).size(); j++){
				if(maz.get(i).get(j).equals(">")){
					maz.get(i).set(j, "v");
					continue;
				}
				if(maz.get(i).get(j).equals("v")){
					maz.get(i).set(j, "<");
					continue;
				}
				if(maz.get(i).get(j).equals("<")){
					maz.get(i).set(j, "^");
					continue;
				}
				if(maz.get(i).get(j).equals("^")){
					maz.get(i).set(j, ">");
					continue;
				}
			}
		}
		return new Maze(tempMaze);
	}
	
	public List<Integer> getMoves(int current){
		List<Integer> moves = new ArrayList<>();
		int[] pos = getXY(current);
		
		if(pos[0]-1>=0 && maze.get(pos[0] - 1).get(pos[1]).equals(".")){
			int newState = getState(pos[0]-1, pos[1]);
			if(!forbidden.contains(newState)){
				moves.add(newState);
			}
		}
		
		if(pos[1]-1>=0 && maze.get(pos[0]).get(pos[1]-1).equals(".")){
			int newState = getState(pos[0], pos[1]-1);
			if(!forbidden.contains(newState)){
				moves.add(newState);
			}
		}
		
		if(pos[0]+1<m && maze.get(pos[0] + 1).get(pos[1]).equals(".")){
			int newState = getState(pos[0]+1, pos[1]);
			if(!forbidden.contains(newState)){
				moves.add(newState);
			}
		}
		
		if(pos[1]+1<n && maze.get(pos[0]).get(pos[1]+1).equals(".")){
			int newState = getState(pos[0], pos[1]+1);
			if(!forbidden.contains(newState)){
				moves.add(newState);
			}
		}
		return moves;
		
	}
	
	public String toString(){
		return maze.toString();
	}
}
