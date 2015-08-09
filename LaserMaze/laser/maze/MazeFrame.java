package laser.maze;

import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class MazeFrame extends JFrame{
	
	public MazeFrame(){
		init();
	}
	
	public void init(){
		this.setLocation(10, 20);
		this.setSize(1000, 700);
		this.setTitle("Laser Maze");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JScrollPane sp = new JScrollPane(new Board4(generateMaze()));
		sp.setPreferredSize(new Dimension(800, 1080));
		
		this.add(sp);
		this.setVisible(true);
	}
	
	private List<List<String>> generateMaze(){
		List<List<String>> maze = new ArrayList<>();
		List<String> lines = readMaze();
		for(String line : lines){
			List<String> lineList = Arrays.asList(line.split(""));
			maze.add(lineList);
		}
		return maze;
	}
	
	private List<String> readMaze(){
		List<String> lines = null;
		try {
			 lines = Files.readAllLines(Paths.get("LaserMaze/maze5"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
	
	public static void main(String[] args){
		new MazeFrame();
	}
}
