import java.util.Arrays;
import java.util.Comparator;

class SortBySize implements Comparator<Item> {

	@Override
	public int compare(Item o1, Item o2) {
		return o2.size - o1.size;
	}
	
}

public class FirstFitDecreasing{
	
	public int [] getMinimumBins(Item items[] , int capacity){
		
		Arrays.sort(items,new SortBySize());
		
		int bins_space[] = new int[items.length];
		int bins=0;
		
		for(int i=0;i<bins_space.length;i++){
			bins_space[i] = capacity;
		}
		
		for(int i=0;i<items.length;i++){
			int j;
			for(j=0;j<bins;j++){
				if(bins_space[j] >= items[i].size){
					bins_space[j] -= items[i].size;
					items[i].bin_no=j;
					break;
				}
			}
			if(j==bins){
				bins++;
				bins_space[bins-1] -= items[i].size;
				items[i].bin_no=bins-1;
			}
		}
		int bin_cap[]=new int[bins];
		for(int i=0;i<bins;i++){
			bin_cap[i]=bins_space[i];
		}
		
		return bin_cap;
	}
	
}