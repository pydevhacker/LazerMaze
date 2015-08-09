package laser.maze;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.math.*;
import java.text.*;
import java.util.*;

public class C {
	private static BufferedReader br;
	private static StringTokenizer st;
	private static PrintWriter pw;

	static int[] dx = new int[]{-1,0,1,0};
	static int[] dy = new int[]{0,1,0,-1};
	
	static char[][] grid;
	
	public static void main(String[] args) throws IOException	{
		br = new BufferedReader(new InputStreamReader(System.in));
		pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
		final int MAX_NUM_CASE = readInt();
		for(int qq = 1; qq <= MAX_NUM_CASE; qq++)	{
			pw.print("Case #" + qq + ": ");
			int r = readInt();
			int c = readInt();
			grid = new char[r][c];
			int sx = -1;
			int sy = -1;
			int ex = -1;
			int ey = -1;
			for(int i = 0; i < r; i++) {
				String s = nextToken();
				for(int j = 0; j < c; j++) {
					grid[i][j] = s.charAt(j);
					if(grid[i][j] == 'S')  {
						sx = i;
						sy = j;
					}
					if(grid[i][j] == 'G')  {
						ex = i;
						ey = j;
					}
				}
			}
			boolean[][][] bad = new boolean[r][c][4];
			for(int i = 0; i < r; i++) {
				for(int j = 0; j < c; j++) {
					if(grid[i][j] == '.') continue;
					if(grid[i][j] == '#') continue;
					if(grid[i][j] == 'S') continue;
					if(grid[i][j] == 'G') continue;
					int dir = -1;
					if(grid[i][j] == '^') dir = 0;
					if(grid[i][j] == 'v') dir = 2;
					if(grid[i][j] == '<') dir = 3;
					if(grid[i][j] == '>') dir = 1;
					for(int k = 0; k < 4; k++) {
						int actualDir = (dir + k) % 4;
						int x = i;
						int y = j;
						bad[x][y][k] = true;
						x += dx[actualDir];
						y += dy[actualDir];
						while(valid(x, y)) {
							bad[x][y][k] = true;
							x += dx[actualDir];
							y += dy[actualDir];
						}
					}
				}
			}
			int[][][] dp = new int[r][c][4];
			for(int i = 0; i < r; i++) {
				for(int j = 0; j < c; j++) {
					Arrays.fill(dp[i][j], 1 << 25);
				}
			}
			LinkedList<State> q = new LinkedList<State>();
			dp[sx][sy][0] = 0;
			q.add(new State(sx, sy, 0));
			while(!q.isEmpty()) {
				State curr = q.removeFirst();
				int dist = dp[curr.x][curr.y][curr.d];
				for(int k = 0; k < dx.length; k++) {
					int nx = curr.x + dx[k];
					int ny = curr.y + dy[k];
					if(!valid(nx, ny)) continue;
					if(dp[nx][ny][(dist+1)%4] <= 1 + dist) continue;
					if(bad[nx][ny][(dist+1)%4]) continue;
					dp[nx][ny][(dist+1)%4] = dist + 1;
					q.add(new State(nx, ny, (dist+1)%4));
				}
			}
			int ret = Integer.MAX_VALUE;
			for(int a = 0; a < 4; a++) {
				ret = Math.min(ret, dp[ex][ey][a]);
			}
			if(ret >= 1 << 25) {
				pw.println("impossible");
			}
			else {
				pw.println(ret);
			}
		}
		pw.close();
	}

	static class State {
		public int x,y,d;
		public State(int a, int b, int c) {
			x=a;
			y=b;
			d=c;
		}
		public String toString() {
			return x + " " + y + " " + d;
		}
	}
	
	public static boolean valid(int x, int y) {
		if(x < 0 || x >= grid.length || y < 0 || y >= grid[x].length) return false;
		if(grid[x][y] == '^') return false;
		if(grid[x][y] == '<') return false;
		if(grid[x][y] == 'v') return false;
		if(grid[x][y] == '>') return false;
		if(grid[x][y] == '#') return false;
		return true;
	}
	
	/* NOTEBOOK CODE */

	private static long readLong() throws IOException	{
		return Long.parseLong(nextToken());
	}

	private static double readDouble() throws IOException	{
		return Double.parseDouble(nextToken());
	}

	private static int readInt() throws IOException	{
		return Integer.parseInt(nextToken());
	}

	private static String nextToken() throws IOException	{
		while(st == null || !st.hasMoreTokens())	{
			if(!br.ready())	{
				pw.close();
				System.exit(0);
			}
			st = new StringTokenizer(br.readLine().trim());
		}
		return st.nextToken();
	}
}

