import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
public class Cluster {
	
	static int n,len,i,j;
	static float min_x,max_x,min_y,max_y;
	static float data[][];
	static int cluster_center[],cluster_center_final[];
	static float x[][];
	static float[][][] cluster_elements;
	static int[] cluster_element_len;
	static float[][] cluster_center_initial;
	static String filename = "C:\\Users\\Kousthubha\\workspace\\K-Means Clustering\\src\\k-means.txt";
	
	public static void main(String args[])
	{
		getData(filename);
		n=4;
		randomCenter2(n);
		cluster(cluster_center_initial,0);
	}
	
	public static void randomCenter2(int n)
	{
		float center_x = (min_x+max_x)/2;
		float center_y = (min_y+max_y)/2;
		float radius = distance(min_x,min_y,center_x,center_y);
		cluster_center_initial = new float[n][2];
		float angle = 360/n;
		switch(n)
		{
		case 2:
			cluster_center_initial[0][0] = (center_x+min_x)/2;
			cluster_center_initial[0][1] = (center_y+min_y)/2;
			cluster_center_initial[1][0] = (center_x+max_x)/2;
			cluster_center_initial[1][1] = (center_y+max_y)/2;
			break;
		default:
			System.out.println("Radius: "+radius);
			System.out.println("Angle in degrees: "+angle);
			angle = (float)Math.toRadians(angle/2);
			System.out.println("Angle/2 in radians: "+angle);
			System.out.println("Sine of Angle/2: "+Math.sin(angle));
			double side = 2*radius*Math.sin(angle);
			System.out.println("Side length: "+side);
			float prev_x = min_x;
			float prev_y = min_y;
			cluster_center_initial[0][0] = prev_x;
			cluster_center_initial[0][1] = prev_y;
			int cluster_len=1;
			while(cluster_len<n)
			{
				double c1 = Math.pow(center_x, 2)+Math.pow(center_y, 2)-Math.pow(radius, 2);
				double c2 = Math.pow(prev_x, 2)+Math.pow(prev_y, 2)-Math.pow(side, 2);
				double CA = (c1-c2)/(center_x-prev_x);
				double BA = (center_y-prev_y)/(center_x-prev_x);
				double a = (Math.pow(BA, 2)+1);
				double b = -((CA*BA) + (2*center_y) - (2*BA*center_x));
				double c = (Math.pow(CA, 2)/4) - CA*center_x + Math.pow(center_x, 2) + Math.pow(center_y, 2) - Math.pow(radius, 2);
				float coordinates[] = calculateRoots(a,b,c);
				float x1 = (float)((CA/2)-coordinates[0]*BA);
				float x2 = (float)((CA/2)-coordinates[1]*BA);
				if(!exists(x1,coordinates[0],cluster_center_initial,cluster_len))
				{
					if(cluster_len<n)
					{
						cluster_center_initial[cluster_len][0] = x1;
						cluster_center_initial[cluster_len][1] = coordinates[0];
						cluster_len++;
					}
				}
				else if(!exists(x2,coordinates[1],cluster_center_initial,cluster_len)){
					if(cluster_len<n){
						cluster_center_initial[cluster_len][0] = x2;
						cluster_center_initial[cluster_len][1] = coordinates[1];
						cluster_len++;
					}
				}
				prev_x = cluster_center_initial[cluster_len-1][0];
				prev_y = cluster_center_initial[cluster_len-1][1];
			}
			break;
		}
		System.out.println(distance(cluster_center_initial[0][0],cluster_center_initial[0][1],cluster_center_initial[1][0],cluster_center_initial[1][1]));
		for(int i=0;i<n;i++)
		{
			cluster_center_initial[i][0] = (float) ((center_x+cluster_center_initial[i][0])*0.5);
			cluster_center_initial[i][1] = (float) ((center_y+cluster_center_initial[i][1])*0.5);
		}
	}
	
	private static float[] calculateRoots(double a, double b, double c)
	{
		double determinant, root1=0, root2=0;
		determinant = b*b-4*a*c;
		if(determinant>0)
		{
			root1 = (-b+Math.sqrt(determinant))/(2*a);
			root2 = (-b-Math.sqrt(determinant))/(2*a);
		}else if(determinant==0){
			root1 = root2 = -b/(2*a);
		}
		float[] coordinates = new float[2];
		coordinates[0] = (float)root1;
		coordinates[1] = (float)root2;
		return coordinates;
	}
	
	private static boolean exists(float x, float y, float rep[][],int len){
		for(int i=0;i<len;i++){
			if(rep[i][0]==x && rep[i][1]==y){
				return true;
			}
		}
		return false;
	}
	
	public static void randomCenter(int n)
	{
		if(n==len){ cluster_center_initial = data;
		return;}
		Random rand = new Random(System.currentTimeMillis());
		int range = (int) len-1;
		cluster_center_initial = new float[n][2];
		float improperElements[][]=new float[n][2];
		int improperIndex[] = new int[n]; 
		int improperLen = 0;
		for(int i=0;i<n;i++)
		{
			int random = rand.nextInt(range);
			if(i==0){
				cluster_center_initial[i][0]=data[random][0];
				cluster_center_initial[i][1]=data[random][1];
				improperElements[improperLen][0]=cluster_center_initial[i][0];
				improperElements[improperLen][1]=cluster_center_initial[i][1];
				improperIndex[improperLen]=random;
				improperLen++;
			}else{
				if(exists(data[random][0],data[random][1],improperElements,improperLen,improperIndex,random)){
					i--;
				}else{
					cluster_center_initial[i][0]=data[random][0];
					cluster_center_initial[i][1]=data[random][1];
					//System.out.println("Center "+i+": ("+data[random][0]+", "+data[random][1]+")");
					improperElements[improperLen][0]=cluster_center_initial[i][0];
					improperElements[improperLen][1]=cluster_center_initial[i][1];
					improperIndex[improperLen] = random;
					improperLen++;
				}
			}
		}
	}
	
	private static boolean exists(float x, float y, float rep[][],int len,int[] index,int id){
		for(int i=0;i<len;i++){
			if(rep[i][0]==x && rep[i][1]==y && index[i]==id){
				return true;
			}
		}
		return false;
	}
	
	public static void getData(String filename)
	{
		BufferedReader br = null;
		FileReader fr = null;
		try{
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			String line;
			line = br.readLine();
			data = new float[Integer.parseInt(line)][2];
			while((line = br.readLine())!=null){
				String[] coordinates = line.split(" ");
				data[len][0] = Float.parseFloat(coordinates[0]);
				if(max_x < data[len][0]) max_x = data[len][0];
				if(min_x > data[len][0]) min_x = data[len][0];
				data[len][1] = Float.parseFloat(coordinates[1]);
				if(max_y < data[len][1]) max_y = data[len][1];
				if(min_y > data[len][1]) min_y = data[len][1];
				len++;
			}
			if(fr!=null)
				fr.close();
			if(br!=null)
				br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private static void cluster(float cluster_center_data[][],int iteration)
	{
		float[][] cluster_center_new = new float[n][2];
		cluster_elements = new float[n][len][2]; 
		System.out.println("\n\nIteration: "+iteration);
		//Print initial cluster centers
		for(i=0;i<n;i++)
		{
			System.out.print("\t\tK"+iteration+""+i+": (");
			for(j=0;j<2;j++)
			{
				if(j==0)
				System.out.print(cluster_center_data[i][j]);
				else if(j==1)
				{
					System.out.print(","+cluster_center_data[i][j]+")");
				}
			}
		}
		System.out.println();
		
		//Calculate clusters
		System.out.println();
		cluster_element_len = new int[n];
		for(i=0;i<n;i++) cluster_element_len[i]=0;
		for(i=0;i<len;i++){
			float dist[] = new float[n];
			System.out.print("("+data[i][0]+","+data[i][1]+")\t\t");
			int min=0;
			for(j=0;j<n;j++)
			{
				dist[j]=distance(data[i][0],data[i][1],cluster_center_data[j][0],cluster_center_data[j][1]);
				if(Float.isNaN(dist[j])){
					System.out.println("NaN error!");
					return;
				}
				System.out.print(dist[j]+"\t\t");
				if(dist[j]<dist[min]) min = j; 
			}
			cluster_elements[min][cluster_element_len[min]][0]=data[i][0];
			cluster_elements[min][cluster_element_len[min]][1]=data[i][1];
			cluster_element_len[min]++;
			System.out.println();
			System.out.println();
		}
		for(i=0;i<n;i++){
			float x=0;
			for(j=0;j<cluster_element_len[i];j++){
				x+=cluster_elements[i][j][0];
			}
			x = x/cluster_element_len[i];
			float y=0;
			for(j=0;j<cluster_element_len[i];j++){
				y+=cluster_elements[i][j][1];
			}
			y=y/cluster_element_len[i];
			cluster_center_new[i][0]=x;
			cluster_center_new[i][1]=y;
		} 
		
		//Check if the new cluster center is equal to the old cluster centers
		if(equals(cluster_center_data, cluster_center_new)){
			x=cluster_center_data;
			printCluster();
		}else{
			System.out.println("Cluster centers do not match!");
			for(i=0;i<n;i++){
				System.out.println("Cluster "+i+" data points are: ");
				for(j=0;j<cluster_element_len[i];j++){
					System.out.println("("+cluster_elements[i][j][0]+","+cluster_elements[i][j][1]+")");
				}
				System.out.println();
			}
			cluster(cluster_center_new,++iteration);
		}
	}
	
	private static float distance(float x1,float y1,float x2,float y2){
		return (float) Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2,2));
	}
	
	private static void printCluster(){
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("Cluster centers match!");
		System.out.println("Cluster centers are:");
		for(i=0;i<n;i++){
			System.out.print("("+x[i][0]+", "+x[i][1]+"), ");
		}
		System.out.println();
		for(i=0;i<n;i++){
			System.out.println("Cluster "+i+" data points are: ");
			for(j=0;j<cluster_element_len[i];j++){
				System.out.println("("+cluster_elements[i][j][0]+","+cluster_elements[i][j][1]+")");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println();
	}
	
	private static boolean equals(float[][] x,float[][] y)
	{
		int flag=0;
		for(i=0;i<n;i++)
		{
			for(j=0;j<2;j++)
			{
				if(x[i][j]!=y[i][j]){flag=1;break;}
			}
		}
		if(flag==0) return true;
		else return false;
	}
}
