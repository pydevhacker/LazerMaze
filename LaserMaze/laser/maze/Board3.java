package laser.maze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Board3 extends JPanel implements ActionListener{
	int offset = 8;
	Timer timer;
	int animationTimer = 100;
	int moveCount = 0;
	boolean pathFound = false;
	
	Stack<Integer> path = new Stack<>();
	Stack<Integer> opened = new Stack<>();
	int[] visited;
	int[] eval;
	
	List<Maze> mazes = new ArrayList<>();
	int currentState;
	int m,n;
	int distinctStates = 4;
	int goalState;
	Maze currentMaze = null;
	
	public Board3(List<List<String>> maze){
		processMaze(maze);
		this.setAutoscrolls(true);
		this.setPreferredSize(new Dimension(800,800));
		this.setBackground(Color.BLACK);
		timer = new Timer(animationTimer, this);
		timer.start();
	}
	
	public void processMaze(List<List<String>> maze){
		Maze maz = new Maze(maze);
		mazes.add(maz);
		path.push(maz.getStartState());
		m = maze.size();
		n = maze.get(0).size();
		for(int i = 1; i<distinctStates; i++){
			maz = Maze.getNextMaze(maz);
			mazes.add(maz);
		}
		
//		for(Maze ma : mazes){
//			System.out.println(ma);
//			System.out.println(ma.getForbidden());
//		}
		
		eval = new int[m*n];
		visited = new int[m*n];
		goalState = maz.getGoalState();
		
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		doDrawing(g);
	}
	
	public void doDrawing(Graphics g){
		currentMaze = mazes.get((moveCount)%distinctStates);
		drawMaze(g);
		drawOpen(g);
		drawPath(g);
		drawCurrent(g);
		drawLines(g);
		
		if(moveCount > 0){
			fireLaser(g);
		}
	}
	
	private void drawLines(Graphics g){
		g.setColor(Color.GRAY);
		//draw m rows
		for(int i = 1; i<=m; i++){
			g.drawLine(0, i*offset, n*offset, i*offset);
		}
		//draw n columns
		for(int i = 1; i<=n; i++){
			g.drawLine(i*offset, 0, i*offset, m*offset);
		}
		
	}
	
	private Maze getMaze(int value){
		return mazes.get(value%distinctStates);
	}
	
	private int getCurrentState(){
		return path.peek();
	}
	
	private void drawMaze(Graphics g){
		List<List<String>> maze = currentMaze.getMaze();
		g.setFont(new Font("TimesRoman", Font.BOLD, offset));
		
		for(int i = 0; i<m; i++){
			for(int j = 0; j<n; j++){
				if(maze.get(i).get(j).equals(".")){
					g.setColor(Color.WHITE);
					g.fillRect(j*offset, i*offset, offset, offset);
				}else if(maze.get(i).get(j).equals("#")){
					g.setColor(Color.BLACK);
					g.fillRect(j*offset, i*offset, offset, offset);
				}else if(maze.get(i).get(j).equals(">") || maze.get(i).get(j).equals("<") ||
						maze.get(i).get(j).equals("^") || maze.get(i).get(j).equals("v")){
					g.setColor(Color.RED);
					g.fillRect(j*offset, i*offset, offset, offset);
					g.setColor(Color.GREEN);
					g.drawString(maze.get(i).get(j), (j*offset)+2, (i+1)*offset);
				}
				if(currentMaze.getState(i, j) == getCurrentState()){
					g.setColor(Color.BLUE);
					g.fillRect(j*offset, i*offset, offset, offset);
				}
				if(currentMaze.getState(i, j) == currentMaze.getStartState()){
					g.setColor(Color.MAGENTA);
					g.fillRect(j*offset, i*offset, offset, offset);
					g.setColor(Color.WHITE);
					g.drawString("S", (j*offset)+2, (i+1)*offset);
				}
				if(currentMaze.getState(i, j) == currentMaze.getGoalState()){
					g.setColor(Color.ORANGE);
					g.fillRect(j*offset, i*offset, offset, offset);
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int nextState = getNextMove(path.peek());
		if(nextState == -1){
			System.out.println("we are done");
			timer.stop();
		}else if(nextState == goalState){
			pathFound = true;
			timer.stop();
			System.out.println("timer stop!! Path found in moves : " + moveCount);
		}
		moveCount++;
		repaint();
	}
	
	private int getNextMove(int current){
		int nextMove = -1;
		List<Integer> moves = getNextMovesList(current);
		
			//backtrack
			while(moves.isEmpty() && !path.isEmpty()){
				//System.out.println("backtrack on " + current);
				//System.out.println("current maze " + path);
				int prevState = path.pop();
				//System.out.println("prevState " + prevState);
				visited[current] = visited[current] +1;
				eval[current] = eval[current] + visited[current];
				if(prevState != current){
					moveCount--;
				}
				moves = getNextMovesList(prevState);
			}
			
			nextMove = moves.get(moves.size() - 1);
			
			
			visited[nextMove] = visited[nextMove] +1;
			eval[nextMove] = eval[nextMove] + visited[nextMove];
			path.push(nextMove);
		return nextMove;
	}

	private List<Integer> getNextMovesList(int current) {
		Maze nextMaze = getMaze((moveCount+1)%distinctStates);
		List<Integer> moves = nextMaze.getMoves(current);
		evalMoves(moves, nextMaze);
		opened.addAll(moves);
		return moves;
	}
	
	private void evalMoves(List<Integer> moves, Maze maze){
		for(int i = 0;i <moves.size(); i++){
			int max = i;
			for(int j = i; j<moves.size(); j++){
				if(evalMove(moves.get(max), maze) < evalMove(moves.get(j), maze)){
					max = j;
				}
			}
			if(max != i){
				int temp = moves.get(max);
				moves.set(max, moves.get(i));
				moves.set(i, temp);
			}
		}
	}
	
	private int evalMove(int state, Maze maze){
		int[] pos = maze.getXY(state);
		int [] posG = maze.getXY(maze.getGoalState());
		int x = Math.abs(pos[0] - posG[0]);
		int y = Math.abs(pos[1] - posG[1]);
		int result = x+y+eval[state];
		return result;
	}
	
	private void fireLaser(Graphics g){
		for(int move : currentMaze.getForbidden()){
			int[] pos = currentMaze.getXY(move);
			g.setColor(Color.GREEN);
			g.fillRect((pos[1]*offset) + 3, (pos[0]*offset) + 3, offset-5, offset-5);
		}
	}
	
	private void drawOpen(Graphics g){
		for(int move : opened){
			int[] pos = currentMaze.getXY(move);
			g.setColor(Color.PINK);
			g.fillRect((pos[1]*offset) + 2, (pos[0]*offset) + 2, offset-4, offset-4);
		}
	}
	
	private void drawPath(Graphics g){
		for(int move : path){
			int[] pos = currentMaze.getXY(move);
			g.setColor(Color.BLUE);
			g.fillRect((pos[1]*offset) + 2, (pos[0]*offset) + 2, offset-4, offset-4);
		}
	}
	
	private void drawCurrent(Graphics g){
		int cur = getCurrentState();
		int[] pos = currentMaze.getXY(cur);
		g.setColor(Color.BLUE);
		g.fillRect(pos[1]*offset, pos[0]*offset, offset, offset);
	}
	
}
