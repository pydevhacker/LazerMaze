import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NewYearResolution {
	
	static int currentIndex = 0;
	static List<String> lines = null; 
	
	public NewYearResolution(){
		lines = readFile();
		int cases = Integer.valueOf(lines.get(currentIndex++));
		for(int i = 0; i<cases; i++){
			String[] expected = lines.get(currentIndex++).split(" ");
			int o = Integer.valueOf(expected[0]);
			int p = Integer.valueOf(expected[1]);
			int q = Integer.valueOf(expected[2]);
			int n = Integer.valueOf(lines.get(currentIndex++));
			int[][] num = new int[n+1][3];
			for(int j = 0; j<3; j++){
				num[0][j] = 0;
			}
			for(int j = 1; j<n+1;j++){
				String[] inputStr = lines.get(currentIndex++).split(" ");
				num[j][0] = Integer.valueOf(inputStr[0]);
				num[j][1] = Integer.valueOf(inputStr[1]);
				num[j][2] = Integer.valueOf(inputStr[2]);
			}
			n = num.length;
			boolean result = cal(num, o, p, q, n);
			System.out.println("Case #" + (i+1) + ": " + (result?"yes":"no"));
		}
		
	}
	
	public static void main(String[] args){
		new NewYearResolution();
	}
	
	private boolean cal(int[][] num, int o, int p, int q, int n){
		List<int[]> lst = new ArrayList<int[]>(1000);
		lst.add(num[0]);
		for(int i = 1; i<n; i++){
			List<int[]> newLst = new ArrayList<>(lst);
			for(int j = 0; j<newLst.size(); j++){
				int[] arr = newLst.get(j);
				int w = arr[0] + num[i][0];
				int x = arr[1] + num[i][1];
				int y = arr[2] + num[i][2];
				if(w == o && x == p && y == q){
					return true;
				}else if(w < o && x < p && y < q){
					int[] newarr = new int[3];
					newarr[0] = w;
					newarr[1] = x;
					newarr[2] = y;
					lst.add(newarr);
				}						
			}
		}
		
		return false;
	}
	
	private static List<String> readFile(){
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get("src/Input"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
}
