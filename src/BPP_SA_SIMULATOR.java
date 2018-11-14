import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class BPP_SA_SIMULATOR {
	
	private static int capacity;
	private static int no_items;
	private static Item items[];
	private static int bin_capacity[];
	private static int iter;
	private static double temp=1000.0;
	private static double temp_tar=0.1;
	private static double cooling_factor=0.95;
	private static int current_fx;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Random ran = new Random();
		
		System.out.println("********  Simulator Start ********");
		System.out.println();
		
		System.out.print("Insert Bin Capacity : ");
		capacity = sc.nextInt();
		sc.nextLine();
		System.out.println();
		
		System.out.println("==> DATA Set :");
		System.out.println("A> Random");
		System.out.println("B> Manual");
		System.out.print("Insert Choise : ");
		char ch = sc.nextLine().charAt(0);
		System.out.println();
		
		switch(ch){
		case 'A' | 'a' : 
			randomDataGenerator();
			break;
		case 'B' | 'b' :
			manualDataGenerator();
			break;
		default :
			System.out.println("Insert Valid Choise.");
			sc.close();
			return ;
		}
		
		System.out.print("Lower Bound : ");
		System.out.println(calculateLowerBound());
		System.out.println();
		
		FirstFitDecreasing ffd = new FirstFitDecreasing();
		bin_capacity = ffd.getMinimumBins(items , capacity);
		
		for(int i=0;i<bin_capacity.length;i++){
			System.out.print(bin_capacity[i] + "  ");
		}
		System.out.println();
		
		System.out.print("Insert No. Of Iteration (>=100): ");
		iter = sc.nextInt();
		
		current_fx = F();
		while(iter>0){
			iter--;
			temp=1000.0;
			double threshold = Math.log(0.00001)/Math.log(cooling_factor);
			threshold = 1000*Math.pow(cooling_factor,Math.ceil(threshold/2));
//			System.out.println(threshold);
			while(temp > temp_tar){
				
				if(temp>threshold){
					int rand = ran.nextInt(100);
					if(rand<=70){
						swap10();
					}else{
						swap11();
					}
				}else{
					int rand = ran.nextInt(100);
					if(rand<=70){
						swap11();
					}else{
						swap10();
					}
				}
				temp = cooling_factor*temp;
				for(int id=0;id<bin_capacity.length;id++){
					System.out.print(bin_capacity[id] + "  ");
				}
				System.out.println();
			}
		}
		for(int i=0;i<bin_capacity.length;i++){
			System.out.print(bin_capacity[i] + "  ");
		}
		
	}
	
	private static void swap11(){
		Random ran=new Random();
		int i,j;
		i=ran.nextInt(no_items);
		j=ran.nextInt(no_items);
		if(i!=j && items[i].size != items[j].size && bin_capacity[items[i].bin_no]+items[i].size>=items[j].size && bin_capacity[items[j].bin_no]+items[j].size>=items[i].size ){
			bin_capacity[items[i].bin_no]+=(items[i].size-items[j].size);
			bin_capacity[items[j].bin_no]+=(items[j].size-items[i].size);
			int bno=items[i].bin_no;
			items[i].bin_no=items[j].bin_no;
			items[j].bin_no=bno;
			int next_fx=F();
			if(next_fx>current_fx){
				current_fx=next_fx;
			}else{
				double exp=Math.exp((double)(next_fx-current_fx)/temp);
				exp=1.0-exp;
				double prob=ran.nextDouble();
				if(prob < exp){
					current_fx = next_fx;
				}else{
					bin_capacity[items[i].bin_no]+=(items[i].size-items[j].size);
					bin_capacity[items[j].bin_no]+=(items[j].size-items[i].size);
					int bn0=items[i].bin_no;
					items[i].bin_no=items[j].bin_no;
					items[j].bin_no=bn0;
				}
			}
		}
	}
	
	private static void swap10(){
		System.out.print("swap10 => ");
		Random ran = new Random();
		int ri = ran.nextInt(no_items);
		int bno = -1;
		int max_fx = Integer.MIN_VALUE;
		for(int i=0;i<bin_capacity.length;i++){
			if(i!=items[ri].bin_no && bin_capacity[i] >= items[ri].size){
				bin_capacity[items[ri].bin_no]+=(items[ri].size);
				bin_capacity[i]-=(items[ri].size);
				int temp_bno=items[ri].bin_no;
				items[ri].bin_no=i;
				int next_fx = F();
				if(next_fx > max_fx){
					bno = i;
					max_fx = next_fx;
				}
				items[ri].bin_no=temp_bno;
				bin_capacity[items[ri].bin_no]-=(items[ri].size);
				bin_capacity[i]+=(items[ri].size);
			}
		}
		
		
		if(max_fx>current_fx){
			current_fx=max_fx;
			if(bno!=-1){
				System.out.print("ch : ");
				bin_capacity[items[ri].bin_no]+=(items[ri].size);
				bin_capacity[bno]-=(items[ri].size);
				items[ri].bin_no=bno;
			}
		}
		else if(max_fx != Integer.MIN_VALUE){
			double exp=Math.exp((double)(max_fx-current_fx)/temp);
			exp=1.0-exp;
			double prob=ran.nextDouble();
			if(prob < exp){
				current_fx = max_fx;
				if(bno!=-1){
					bin_capacity[items[ri].bin_no]+=(items[ri].size);
					bin_capacity[bno]-=(items[ri].size);
					items[ri].bin_no=bno;
				}
			}
		}
		for(int i=0;i<bin_capacity.length;i++){
			if(bin_capacity[i]==capacity){
				int temp_capaity[] = new int[bin_capacity.length-1];
				int index=0;
				for(int j=0;j<bin_capacity.length;j++){
					if(j!=i){
						temp_capaity[index] = bin_capacity[j];
						index++;
					}
				}
				bin_capacity=temp_capaity;
				for(int j=0;j<no_items;j++){
					if(items[j].bin_no>i){
						items[j].bin_no--;
					}
				}
			}
		}
	}
	
	private static int F(){
		int ans=0;
		
		HashMap<Integer, Integer> hm = new HashMap<>();
		for(int i=0;i<no_items;i++){
			if(hm.containsKey(items[i].bin_no)){
				hm.put(items[i].bin_no, hm.get(items[i].bin_no)+items[i].size);
			}else{
				hm.put(items[i].bin_no, items[i].size);
			}
		}
		
		Set entrySet = hm.entrySet();
		Iterator it = entrySet.iterator();
		
		while(it.hasNext()){
			Map.Entry me = (Map.Entry)it.next();
			ans += ((int)me.getValue() * (int)me.getValue());
		}
		
		return ans;
	}
	
	private static void randomDataGenerator(){
		Scanner sc = new Scanner(System.in);
		Random ran = new Random();
		
		System.out.println("==> Random Data Generator");
		
		System.out.print("No. Of Items : ");
		no_items = sc.nextInt();
		items = new Item[no_items];
		
		for(int i=0;i<no_items;i++){
			items[i] = new Item(ran.nextInt(capacity)+1);
			System.out.print(items[i].size + "  ");
		}
		System.out.println();
		System.out.println();
	}
	
	private static void manualDataGenerator(){
		Scanner sc = new Scanner(System.in);
		
		System.out.println("==> Manual Data Generator");
		
		System.out.print("No. Of Items : ");
		no_items = sc.nextInt();
		items = new Item[no_items];
		System.out.print("Insert Items' Size : ");
		for(int i=0;i<no_items;i++){
			items[i] = new Item(sc.nextInt());
		}
		System.out.println();
	}
	
	private static int calculateLowerBound(){
		int sum=0;
		for(int i=0;i<no_items;i++){
			sum+=items[i].size;
		}
		return (int)Math.ceil(sum/capacity);
	}

}
