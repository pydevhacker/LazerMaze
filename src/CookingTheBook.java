import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CookingTheBook {
	
	public CookingTheBook(){
		process();
	}
	
	public static void main(String[] args){
		new CookingTheBook();
	}
	
	int currentIndex = 0;
	public void process(){
		List<String> lines = readFile();
		int cases = Integer.valueOf(lines.get(currentIndex++));
		for(int i = 0; i<cases; i++){
			String[] line = lines.get(currentIndex++).split("");
				int max = 0;
				int min = 0;
				for(int j = 0; j<line.length; j++){
					if(Integer.valueOf(line[max]) < Integer.valueOf(line[j])){
						max = j;
					}
					if(Integer.valueOf(line[min]) > Integer.valueOf(line[j])){
						min = j;
					}
				}
				
				String[] lineMax = null;
				String[] lineMin = null;
				
				if(max != 0 && Integer.valueOf(line[max]) != 0){
					lineMax = Arrays.copyOf(line, line.length);
					//swap
					String temp = lineMax[0];
					lineMax[0] = lineMax[max];
					lineMax[max] = temp;
				}else{
					lineMax = line;
				}
				
				if(min!=0 && Integer.valueOf(line[min]) != 0){
					lineMin = Arrays.copyOf(line, line.length);
					//swap
					String temp = lineMin[0];
					lineMin[0] = lineMin[min];
					lineMin[min] = temp;
				}else{
					lineMin = line;
				}
				
				StringBuilder sbMin = new StringBuilder();
				for(int k = 0; k<lineMin.length ; k ++){
					sbMin.append(lineMin[k]);
				}
				String minStr = sbMin.toString();
				
				StringBuilder sbMax = new StringBuilder();
				for(int k = 0; k<lineMax.length ; k ++){
					sbMax.append(lineMax[k]);
				}
				String maxStr = sbMax.toString();
				
				System.out.println("Case #" + (i+1) + ": " + minStr + " " + maxStr);
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
