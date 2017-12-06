import java.util.Scanner;
public class ClusteringDFS {
	static int len;
	static float[][] data;
	static float[][][] clusterElements;
	static int[] clusterElementsLen;
	static int clusterNumber=0;
	public static void main(String arhs[]){
		Scanner s = new Scanner(System.in);
		System.out.println("Enter the number of data points:");
		len = s.nextInt();
		data = new float[len][2];
		if(len<=0){
			s.close();
			return;
		}else{
			System.out.println("Enter the data points:");
			for(int i=0;i<len;i++){
				System.out.print("Enter data point "+i+": ");
				data[i][0]=s.nextInt();
				data[i][1]=s.nextInt();
			}
		}
		s.close();
		clusterElements = new float[len][len][2];
		clusterElementsLen = new int[len];
		for(int i=0;i<len;i++)
		{
			int flag=0;
			for(int j=0;j<clusterNumber;j++){
				if(exists(data[i][0],data[i][1],clusterElements[j],clusterElementsLen[j])){
					flag=1;
					break;
				}
			}
			if(flag==1) continue;
			cluster(i);
		}
		printClusterInfo();
	}
	private static boolean exists(float x, float y, float rep[][],int len,int[] index,int id){
		for(int i=0;i<len;i++){
			if(rep[i][0]==x && rep[i][1]==y && index[i]==id){
				return true;
			}
		}
		return false;
	}
	private static boolean exists(float x, float y, float rep[][],int len){
		for(int i=0;i<len;i++){
			if(rep[i][0]==x && rep[i][1]==y){
				return true;
			}
		}
		return false;
	}
	
	private static float distance(float x1,float y1,float x2,float y2){
		return (float) Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2,2));
	}
	private static void cluster(int index){
		float[][] improperMembers = new float[len][2];
		int improperLen=0;
		int[] improperIndex = new int[len];
		improperMembers[improperLen][0] = data[index][0];
		improperMembers[improperLen][1] = data[index][1];
		improperIndex[improperLen]=index;
		improperLen++;
		for(int i=0;i<improperLen;i++){
			for(int j=0;j<len;j++){
				if(!exists(data[j][0],data[j][1],improperMembers,improperLen,improperIndex,j))
				if(distance(improperMembers[i][0],improperMembers[i][1],data[j][0],data[j][1])<=2*Math.sqrt(2)){
					improperMembers[improperLen][0]=data[j][0];
					improperMembers[improperLen][1]=data[j][1];
					improperIndex[improperLen]=j;
					improperLen++;
				}
			}
		}
		clusterElements[clusterNumber] = improperMembers;
		clusterElementsLen[clusterNumber] = improperLen;
		clusterNumber++;
	}
	private static void printClusterInfo()
	{
		for(int i=0;i<clusterNumber;i++){
			System.out.println("Cluster "+i+" data points are: ");
			for(int j=0;j<clusterElementsLen[i];j++){
				System.out.println("("+clusterElements[i][j][0]+", "+clusterElements[i][j][1]+")");
			}
		}
		System.out.println("Found the correct number of clusters as: "+clusterNumber);
	}
}