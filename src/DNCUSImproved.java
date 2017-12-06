import java.io.*;
import java.util.Random;

public class DNCUSImproved {
	
	static float[][] data;
	static int len=0,dim=0;
	static float min_x,min_y,max_x,max_y;
	static String filename = "C:\\Users\\Kousthubha\\workspace\\K-Means Clustering\\src\\glass.txt";
	static String output = "K:\\Papers\\K-Means Clustering (AI)\\Outputs\\glass-id.txt";
	static float max[],min[];
	static float cluster_elements[][][];
	static int cluster_element_len[];
	static float cluster_center_initial[][],x[][];
	static double distance_measure = Math.sqrt(2);
	static double upper_threshold = 0;
	
	public static void main(String[] args)
	{
		getData(filename);
		randomCenter3(2);
		setUpperThreshold();
		k_means(cluster_center_initial,0,2);
	}
	
	public static void setUpperThreshold(){
		distance_measure = Math.sqrt(dim);
		upper_threshold =  2*Math.sqrt(dim);
		System.out.println("Upper Threshold: "+upper_threshold);
	}
	
	public static void getData(String filename)
	{
		BufferedReader br = null;
		FileReader fr = null;
		int lno =0;
		try{
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			String line;
			line = br.readLine();
			lno++;
			int l = Integer.parseInt(line);
			line = br.readLine();
			lno++;
			dim = Integer.parseInt(line);
			data = new float[l][dim];
			max = new float[dim];
			min = new float[dim];
			while((line = br.readLine())!=null){
				lno++;
				String[] coordinates = line.split(" ");
				for(int i=0;i<dim;i++)
				{
					data[len][i] = Float.parseFloat(coordinates[i]);
					if(max[i]<data[len][i])max[i] = data[len][i];
					if(min[i]>data[len][i])min[i] = data[len][i];
				}
				len++;
			}
			if(fr!=null)
				fr.close();
			if(br!=null)
				br.close();
		}catch(Exception e){
			System.out.println(lno);
		}
		//normalizeData();
	}
	
	private static void normalizeData()
	{
		for(int i=0;i<len;i++)
		{
			for(int j=0;j<dim;j++)
			{
				data[i][j] = (data[i][j]/max[j])*10;
			}
		}
	}
	
	public static void k_means(float[][] cluster_center_data, int iteration, int numOfClusters){
		float[][] cluster_center_new = new float[numOfClusters][dim];
		cluster_elements = new float[numOfClusters][len][dim]; 
		System.out.println("\n\nIteration: "+iteration);
		//Print initial cluster centers
		for(int i=0;i<numOfClusters;i++)
		{
			System.out.print("\t\tK"+iteration+""+i+": (");
			for(int j=0;j<dim;j++)
			{
				if(j!=dim-1)
				System.out.print(cluster_center_data[i][j]+", ");
				else if(j==dim-1)
				{
					System.out.print(cluster_center_data[i][j]+")");
				}
			}
		}
		System.out.println();
		
		//Calculate clusters
		System.out.println();
		cluster_element_len = new int[numOfClusters];
		for(int i=0;i<len;i++){
			double dist[] = new double[numOfClusters];
			System.out.print("(");
			for(int d=0;d<dim;d++)
			{
				if(d!=dim-1)
				System.out.print(data[i][d]+",");
				else
					System.out.print(data[i][d]+")\t\t");
			}
			int min=0;
			for(int j=0;j<numOfClusters;j++)
			{
				dist[j]=distance(data[i],cluster_center_data[j]);
				System.out.print(dist[j]+"\t\t");
				if(dist[j]<dist[min]) min = j;
			}
			for(int d=0;d<dim;d++)
			cluster_elements[min][cluster_element_len[min]][d]=data[i][d];
			cluster_element_len[min]++;
			System.out.println();
			System.out.println();
		}
		
		for(int i=0;i<numOfClusters;i++){
			if(cluster_element_len[i]==0){
				System.out.println("Cannot calculte "+numOfClusters+" for the given initial centers and data set combination.");
			}
			float avg[] = new float[dim];
			for(int j=0;j<cluster_element_len[i];j++){
				for(int d=0;d<dim;d++){
					avg[d]+=cluster_elements[i][j][d];
					if(j==cluster_element_len[i]-1) avg[d]=avg[d]/cluster_element_len[i];
				}
			}
			cluster_center_new[i]=avg;
		}
		
		//Check if the new cluster center is equal to the old cluster centers
		if(equals(cluster_center_data, cluster_center_new,numOfClusters)){
			x=cluster_center_data;
			printCluster(numOfClusters);
			if(!connectivityTest(numOfClusters)){
				distance_measure = Math.sqrt(dim);
				randomCenter(++numOfClusters);
				k_means(cluster_center_initial,0,numOfClusters);
			}else{
				System.out.println("\n");
				System.out.println("Found the correct number of clusters for the given data sets is: "+numOfClusters);
				printCluster(numOfClusters);
				printOutput(numOfClusters);
				return; 
			}
		}else{
			System.out.println("Cluster centers do not match!");
			for(int i=0;i<numOfClusters;i++){
				System.out.println("Cluster "+i+" data points are: ");
				for(int j=0;j<cluster_element_len[i];j++)
				{
					System.out.println("("+cluster_elements[i][j][0]+","+cluster_elements[i][j][1]+")");
				}
				System.out.println();
			}
			k_means(cluster_center_new,++iteration,numOfClusters);
		}
	}
	
	private static void printOutput(int n){
		BufferedWriter br = null;
		FileWriter fw = null;
		try{
			fw = new FileWriter(output);
			br = new BufferedWriter(fw);
			br.write("Threshold Distance: "+distance_measure+"\n");
			br.write("Cluster centers match!\n");
			br.write("Cluster centers are:\n");
			for(int i=0;i<n;i++){
				br.write("(");
				for(int d=0;d<dim;d++)
				br.write(x[i][d]+", ");
				br.write("), ");
			}
			br.write("\n");
			for(int i=0;i<n;i++){
				br.write("Cluster "+i+" data points are: \n");
				for(int j=0;j<cluster_element_len[i];j++){
					br.write("(");
					for(int d=0;d<dim;d++)
					br.write(cluster_elements[i][j][d]+", ");
					br.write(")\n");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void printCluster(int n){
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("Threshold Distance: "+distance_measure);
		System.out.println("Cluster centers match!");
		System.out.println("Cluster centers are:");
		for(int i=0;i<n;i++){
			System.out.print("(");
			for(int d=0;d<dim;d++)
			System.out.print(x[i][d]+", ");
			System.out.print("), ");
		}
		
		System.out.println();
		for(int i=0;i<n;i++){
			System.out.println("Cluster "+i+" data points are: ");
			for(int j=0;j<cluster_element_len[i];j++){
				System.out.print("(");
				for(int d=0;d<dim;d++)
				System.out.print(cluster_elements[i][j][d]+", ");
				System.out.println(")");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println();
	}
	
	private static boolean connectivityTest(int n){
		if(checkConnectivity(n))
		{
			return true;
		} else {
			if(distance_measure < upper_threshold){
				distance_measure += Math.sqrt(dim);
				return connectivityTest(n);
			} else return false;
		}
	}
	
	private static boolean checkConnectivity(int n)
	{
		for(int i=0;i<n;i++)
		{
			float rep[][]=new float[cluster_element_len[i]+1][dim];
			int repIndex[] = new int[cluster_element_len[i]+1];
			int repLen=0;
			try{
			rep[repLen]=cluster_elements[i][0];
			}catch(ArrayIndexOutOfBoundsException e)
			{
				System.out.println(repLen);
				System.out.println("Improper Indexing");
				break;
			}
			repIndex[repLen]=0;
			repLen++;
			for(int j=0;j<repLen;j++)
			{
				for(int k=0;k<cluster_element_len[i];k++){
					if(distance(rep[j],cluster_elements[i][k])<=distance_measure){
						if(!exists(cluster_elements[i][k],rep,repLen,repIndex,k)){
							rep[repLen]=cluster_elements[i][k];
							repIndex[repLen]=k;
							repLen++;
						}
					}
				}
			}
			if(repLen!=cluster_element_len[i]) return false;
		}
		return true;
	}
	
	private static boolean equals(float[][] x,float[][] y,int n)
	{
		int flag=0;
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<dim;j++)
			{
				if(x[i][j]!=y[i][j]){flag=1;break;}
			}
		}
		if(flag==0) return true;
		else return false;
	}
	
	private static boolean exists(float x[],float rep[][],int len,int[] index,int id){
		for(int i=0;i<len;i++){
			int flag=0;
			for(int q=0;q<dim;q++)
			{
				if(rep[i][q] == x[q]) flag++;
			}
			if(flag==dim){
				if(index[i]==id) return true;
			}
		}
		return false;
	}
	
	private static boolean exists(float x[],float rep[][],int len){
		for(int i=0;i<len;i++){
			int flag=0;
			for(int q=0;q<dim;q++)
			{
				if(rep[i][q] == x[q]) flag++;
			}
			if(flag==dim){
				return true;
			}
		}
		return false;
	}
	
	public static double distance(float x[], float y[])
	{
		double dist=0;
		for(int i=0;i<dim;i++)
		{
			dist += Math.pow(x[i]-y[i],2);
		}
		dist = Math.sqrt(dist);
		return dist;
	}
	
	public static void randomCenter(int n)
	{
		System.out.println();
		System.out.println();
		System.out.println("Entered randomCenter");
		System.out.println("Number of clusters: "+n);
		System.out.println();
		if(n==len){ cluster_center_initial=data;return;}
		Random rand = new Random(System.currentTimeMillis());
		int range = (int) len-1;
		cluster_center_initial = new float[n][dim];
		float improperElements[][]=new float[n][dim];
		int improperIndex[] = new int[n]; 
		int improperLen = 0;
		for(int i=0;i<n;i++)
		{
			int random = rand.nextInt(range);
			if(i==0){
				cluster_center_initial[i]=data[random];
				improperElements[improperLen]=cluster_center_initial[i];
				improperIndex[improperLen]=random;
				improperLen++;
				System.out.print("Center "+i+": (");
				for(int d=0;d<dim;d++)
					System.out.print(data[random][d]+", ");
				System.out.println(")");
			}else{
				if(exists(data[random],improperElements,improperLen,improperIndex,random)){
					i--;
				}else{
					cluster_center_initial[i]=data[random];
					System.out.print("Center "+i+": (");
					for(int d=0;d<dim;d++)
						System.out.print(data[random][d]+", ");
					System.out.println(")");
					improperElements[improperLen]=cluster_center_initial[i];
					improperIndex[improperLen] = random;
					improperLen++;
				}
			}
		}
	}
	
	public static void randomCenter3(int n)
	{
		System.out.println();
		System.out.println();
		System.out.println("Entered randomCenter");
		System.out.println("Number of clusters: "+n);
		System.out.println();
		if(n==len){ cluster_center_initial=data;return;}
		Random rand[] = new Random[dim];
		for(int d=0;d<dim;d++)
			rand[d] = new Random(System.currentTimeMillis());
		cluster_center_initial = new float[n][dim];
		float improperElements[][]=new float[n][dim];
		int improperLen = 0;
		for(int i=0;i<n;i++)
		{
			float arr[] = new float[dim];
			for(int d=0;d<dim;d++){
				float range = max[d]-min[d];
				arr[d] = rand[d].nextFloat()*range+min[d];
			}
			if(i==0){
				cluster_center_initial[i]=arr;
				improperElements[improperLen]=cluster_center_initial[i];
				improperLen++;
				System.out.print("Center "+i+": (");
				for(int d=0;d<dim;d++)
					System.out.print(cluster_center_initial[i][d]+", ");
				System.out.println(")");
			}else{
				if(exists(arr,improperElements,improperLen)){
					i--;
				}else{
					cluster_center_initial[i]=arr;
					System.out.print("Center "+i+": (");
					for(int d=0;d<dim;d++)
						System.out.print(cluster_center_initial[i][d]+", ");
					System.out.println(")");
					improperElements[improperLen]=cluster_center_initial[i];
					improperLen++;
				}
			}
		}
	}
}
