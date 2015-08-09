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

public class Board2 extends JPanel implements ActionListener{
	List<List<String>> maze = null;
	int m;
	int n;
	int offset = 8;
	int start;
	int goal;
	int curr;
	Timer timer;
	int animationTimer = 500;
	Graphics dg = null;
	List<Integer> forbiddenMoves = null;
	List<Integer> lasers = new ArrayList<>();
	int moveCount = 0;
	boolean pathFound = false;
	int[] eval;
	Stack<Integer> path = new Stack<>();
	Stack<Integer> opened = new Stack<>();
	int[] visited;
	List<Integer> backtracked = new ArrayList<>();
	
	public Board2(List<List<String>> maze){
		this.maze = maze;
		this.m = maze.size();
		this.n = maze.get(0).size();
		this.setAutoscrolls(true);
		this.setPreferredSize(new Dimension(this.m*offset,this.n*offset));
		this.setBackground(Color.BLACK);
		findStartGoalState();
		timer = new Timer(animationTimer, this);
		timer.start();
		eval = new int[m*n];
		visited = new int[m*n];
	}
	
	private void findStartGoalState(){
		for(int i = 0; i<m; i++){
			for(int j = 0; j<n; j++){
				if(maze.get(i).get(j).equals("S")){
					start = getState(i, j);
					curr = start;
					maze.get(i).set(j, ".");
				}
				if(maze.get(i).get(j).equals("G")){
					goal = getState(i, j);
					maze.get(i).set(j, ".");
				}
			}
		}
	}
	
	private int[] getXY(int state){
		int[] xy = new int[2];
		xy[0] = state/n;
		xy[1] = state%n;
		return xy;
	}
	
	private int getState(int x, int y){
		return (x*n)+y;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		dg = g;
		doDrawing(g);
	}
	
	public void doDrawing(Graphics g){
		//drawEval(g);
		drawMaze(g);
		drawOpen(g);
		drawPath(g);
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
	
	private void drawMaze(Graphics g){
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
					lasers.add(getState(i, j));
				}
				if(getState(i, j) == curr){
					g.setColor(Color.BLUE);
					g.fillRect(j*offset, i*offset, offset, offset);
				}
				if(getState(i, j) == start){
					g.setColor(Color.MAGENTA);
					g.fillRect(j*offset, i*offset, offset, offset);
					g.setColor(Color.WHITE);
					g.drawString("S", (j*offset)+2, (i+1)*offset);
				}
				if(getState(i, j) == goal){
					g.setColor(Color.ORANGE);
					g.fillRect(j*offset, i*offset, offset, offset);
				}
			}
		}
	}
	
	private int getNextMove(List<Integer> moves){
		if(!moves.isEmpty()){
			evalMoves(moves);
			for(int move : moves){
				opened.push(move);
			}
			return moves.get(moves.size()-1);
		}else{
			System.out.println("no valid move... need to backtrack.. on mmove" + curr);
			System.out.println(path);
			if(!path.isEmpty()){
				//int move = -1;
//				if(path.size() >= 5){
//					for(int i = 0; i<4; i++){
//							move = path.remove(path.size()-1);
//							System.out.println("backing move : " + move);
//					}
//					move = path.get(path.size()-1);
//					System.out.println("final backing move : " + move);
//				}
			}
		}
		return -1;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(moveCount == 0){
			curr = start;
		}
		calForbiddenMoves();
		List<Integer> moves = getNextMoves(curr);
		int move = -1;
		if(moves.isEmpty()){
			visited[curr] = visited[curr] + 1;
			eval[curr] = eval[curr] + 4 + visited[curr];
			System.out.println("backing on move : " + curr);
			move = path.remove(path.size() - 1);
			while(curr == move){
				System.out.println(path);
				int preMove = path.remove(path.size() - 1);
				System.out.println("prevMove is : " + preMove);
				backMove();
				calForbiddenMoves();
				List<Integer> newMoves = getNextMoves(preMove);
				System.out.println("newMoves : " + newMoves);
				move = getNextMove(newMoves);
				System.out.println("next Move : " + move);
			}
		}else{
			move = getNextMove(moves);
		}
		
		if(move == -1){
			System.out.println("we are done");
			timer.stop();
		}else{
			curr = move;
			visited[curr] = visited[curr] + 1;
			eval[curr] = eval[curr] + visited[curr];
			
		}
		makeMove();
		path.push(curr);
		if(curr == goal){
			pathFound = true;
			timer.stop();
			System.out.println("timer stop!! Path found in moves : " + moveCount);
		}
		repaint();
		moveCount++;
	}
	
	
	private void makeMove(){
		for(int i = 0; i<m; i++){
			for(int j = 0; j<n; j++){
				if(maze.get(i).get(j).equals(">")){
					maze.get(i).set(j, "v");
					continue;
				}
				if(maze.get(i).get(j).equals("v")){
					maze.get(i).set(j, "<");
					continue;
				}
				if(maze.get(i).get(j).equals("<")){
					maze.get(i).set(j, "^");
					continue;
				}
				if(maze.get(i).get(j).equals("^")){
					maze.get(i).set(j, ">");
					continue;
				}
			}
		}
	}
	
	private void backMove(){
		for(int i = 0; i<m; i++){
			for(int j = 0; j<n; j++){
				if(maze.get(i).get(j).equals(">")){
					maze.get(i).set(j, "^");
					continue;
				}
				if(maze.get(i).get(j).equals("v")){
					maze.get(i).set(j, ">");
					continue;
				}
				if(maze.get(i).get(j).equals("<")){
					maze.get(i).set(j, "v");
					continue;
				}
				if(maze.get(i).get(j).equals("^")){
					maze.get(i).set(j, "<");
					continue;
				}
			}
		}
	}
	
	private void evalMoves(List<Integer> moves){
		for(int i = 0;i <moves.size(); i++){
			int max = i;
			for(int j = i; j<moves.size(); j++){
				if(evalMove(moves.get(max)) < evalMove(moves.get(j))){
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
	
	private int evalMove(int state){
		int[] pos = getXY(state);
		int [] posG = getXY(goal);
		int x = Math.abs(pos[0] - posG[0]);
		int y = Math.abs(pos[1] - posG[1]);
		int result = x+y+eval[state];
		return result;
	}
	
	private boolean isValidMove(int state){
		return !forbiddenMoves.contains(state);
	}
	
	private List<Integer> getNextMoves(int state){
		List<Integer> moves = new ArrayList<>();
		int[] pos = getXY(state);
		
		if(pos[0]-1>=0 && maze.get(pos[0] - 1).get(pos[1]).equals(".")){
			int newState = getState(pos[0]-1, pos[1]);
			if(!forbiddenMoves.contains(newState)){
				moves.add(newState);
			}
		}
		
		if(pos[1]-1>=0 && maze.get(pos[0]).get(pos[1]-1).equals(".")){
			int newState = getState(pos[0], pos[1]-1);
			if(!forbiddenMoves.contains(newState)){
				moves.add(newState);
			}
		}
		
		if(pos[0]+1<m && maze.get(pos[0] + 1).get(pos[1]).equals(".")){
			int newState = getState(pos[0]+1, pos[1]);
			if(!forbiddenMoves.contains(newState)){
				moves.add(newState);
			}
		}
		
		if(pos[1]+1<n && maze.get(pos[0]).get(pos[1]+1).equals(".")){
			int newState = getState(pos[0], pos[1]+1);
			if(!forbiddenMoves.contains(newState)){
				moves.add(newState);
			}
		}
		return moves;
	}
	
	private void calForbiddenMoves(){
		forbiddenMoves = new ArrayList<>();
		for(int laser : lasers){
			int[] pos = getXY(laser);
			int x = pos[0];
			int y = pos[1];
			
			if(maze.get(pos[0]).get(pos[1]).equals("^")){
				x = pos[0];
				y = pos[1];
				y = y+1;
				while(y<n && maze.get(x).get(y).equals(".")){
					forbiddenMoves.add(getState(x, y));
					y = y+1;
				}
			}
			
			if(maze.get(pos[0]).get(pos[1]).equals(">")){
				x = pos[0];
				y = pos[1];
				x = x+1;
				while(x<m && maze.get(x).get(y).equals(".")){
					forbiddenMoves.add(getState(x, y));
					x++;
				}
			}
			
			if(maze.get(pos[0]).get(pos[1]).equals("v")){
				x = pos[0];
				y = pos[1];
				y--;
				while(y>=0 && maze.get(x).get(y).equals(".")){
					forbiddenMoves.add(getState(x, y));
					y--;
				}
			}
			
			if(maze.get(pos[0]).get(pos[1]).equals("<")){
				x = pos[0];
				y = pos[1];
				x--;
				while(x>=0 && maze.get(x).get(y).equals(".")){
					forbiddenMoves.add(getState(x, y));
					x--;
				}
			}
		}
	}
	
	private void fireLaser(Graphics g){
		for(int move : forbiddenMoves){
			int[] pos = getXY(move);
			g.setColor(Color.GREEN);
			g.fillRect((pos[1]*offset) + 3, (pos[0]*offset) + 3, offset-5, offset-5);
		}
	}
	
	private void drawEval(Graphics g){
		for(int i = 0; i<eval.length; i++){
			int[] pos = getXY(i);
			g.setColor(new Color(eval[i]));
			g.fillRect((pos[1] * offset)+2, (pos[0] * offset) + 2, offset - 2, offset-2);
		}
	}
	
	private void drawOpen(Graphics g){
		for(int move : opened){
			int[] pos = getXY(move);
			g.setColor(Color.PINK);
			g.fillRect((pos[1]*offset) + 2, (pos[0]*offset) + 2, offset-4, offset-4);
		}
	}
	
	private void drawPath(Graphics g){
		for(int move : path){
			int[] pos = getXY(move);
			g.setColor(Color.GRAY);
			g.fillRect((pos[1]*offset) + 2, (pos[0]*offset) + 2, offset-4, offset-4);
		}
	}
	
}
