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

public class Board extends JPanel implements ActionListener{
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
	Stack<Integer> closed = new Stack<>();
	Stack<Integer> open = new Stack<>();
	boolean pathFound = false;
	int[] eval;
	
	public Board(List<List<String>> maze){
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
		drawMaze(g);
		//calForbiddenMoves();
		drawOpen(g);
		drawClosed(g);
		drawLines(g);
		if(moveCount > 0){
			fireLaser(g);
		}
	}
	
	public void drawLines(Graphics g){
		//g.setFont(new Font("TimesRoman", Font.PLAIN, offset));
		g.setColor(Color.GRAY);
		//draw m rows
		for(int i = 1; i<=m; i++){
			g.drawLine(0, i*offset, n*offset, i*offset);
			//g.drawString(String.valueOf(i), 0, i*offset);
		}
		//draw n columns
		for(int i = 1; i<=n; i++){
			g.drawLine(i*offset, 0, i*offset, m*offset);
			//g.drawString(String.valueOf(i),  (i-1)*offset, offset);
		}
		
	}
	
	public void drawMaze(Graphics g){
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if(moveCount == 0){
			calForbiddenMoves();
			List<Integer> moves = getNextMoves(start);
			putOpenMoves(moves);
			if(!closed.contains(curr)){
				closed.push(curr);
			}
		}else{
			calForbiddenMoves();
			curr = getValidNextMove();
			System.out.println("working on : " + curr);
			if(curr == -1){
				timer.stop();
				System.out.println(closed);
				System.out.println(open);
			}
			List<Integer> moves = getNextMoves(curr);
			putOpenMoves(moves);
			if(!closed.contains(curr)){
				closed.push(curr);
			}
			
			makeMove();
			if(curr == goal){
				pathFound = true;
				timer.stop();
				System.out.println("timer stop!!");
				System.out.println(closed);
				System.out.println(open);
			}
		}
		repaint();
		moveCount++;
		//eval move
	}
	
	
	public void makeMove(){
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
	
	public void evalMoves(List<Integer> moves){
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
		int x = Math.abs(pos[0] - posG[1]);
		int y = Math.abs(pos[1] - posG[1]);
		return x+y+eval[state];
	}
	
	public void putOpenMoves(List<Integer> moves){
		evalMoves(moves);
		for(int move : moves){
			if(!open.contains(move) && !forbiddenMoves.contains(move)){
				open.push(move);
			}
		}
	}
	
	public int getValidNextMove(){
		while(!open.isEmpty()){
			int state = open.pop();
			if(isValidMove(state)){
				if(curr != state){
					eval[state] = eval[state]+1;
					return state;
				}
			}else{
				closed.push(state);
			}
		}
		
		while(!closed.isEmpty()){
			int state = closed.pop();
			if(isValidMove(state)){
				if(curr != state){
					eval[state] = eval[state]+1;
					return state;
				}
			}
		}
		return -1;
	}
	
	public boolean isValidMove(int state){
		return !forbiddenMoves.contains(state);
	}
	
	public List<Integer> getNextMoves(int state){
		List<Integer> moves = new ArrayList<>();
		int[] pos = getXY(state);
		
		if(pos[0]-1>=0 && maze.get(pos[0] - 1).get(pos[1]).equals(".")){
			int newState = getState(pos[0]-1, pos[1]);
			if(!closed.contains(newState)){
				moves.add(newState);
			}
		}
		
		if(pos[1]-1>=0 && maze.get(pos[0]).get(pos[1]-1).equals(".")){
			int newState = getState(pos[0], pos[1]-1);
			if(!closed.contains(newState)){
				moves.add(newState);
			}
		}
		
		if(pos[0]+1<m && maze.get(pos[0] + 1).get(pos[1]).equals(".")){
			int newState = getState(pos[0]+1, pos[1]);
			if(!closed.contains(newState)){
				moves.add(newState);
			}
		}
		
		if(pos[1]+1<n && maze.get(pos[0]).get(pos[1]+1).equals(".")){
			int newState = getState(pos[0], pos[1]+1);
			if(!closed.contains(newState)){
				moves.add(newState);
			}
		}
		return moves;
	}
	
	public void calForbiddenMoves(){
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
	
	public void fireLaser(Graphics g){
		for(int move : forbiddenMoves){
			int[] pos = getXY(move);
			g.setColor(Color.GREEN);
			g.fillRect((pos[1]*offset) + 3, (pos[0]*offset) + 3, offset-5, offset-5);
		}
	}
	
	public void drawOpen(Graphics g){
		for(int move : open){
			int[] pos = getXY(move);
			g.setColor(Color.PINK);
			g.fillRect((pos[1]*offset) + 2, (pos[0]*offset) + 2, offset-4, offset-4);
		}
	}
	
	public void drawClosed(Graphics g){
		for(int move : closed){
			int[] pos = getXY(move);
			g.setColor(Color.GRAY);
			g.fillRect((pos[1]*offset) + 2, (pos[0]*offset) + 2, offset-4, offset-4);
		}
	}
	
}
