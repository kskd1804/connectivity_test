import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
public class MinimumDataCell {
	
	static int len,i,j;
	static float data[][];
	static float x[][],cluster_center_initial[][];
	static float[][][] cluster_elements;
	static int[] cluster_element_len;
	static int ideal=0;
	static float max_x=0,min_x=0,max_y=0,min_y=0;
	static double distance_measure = Math.sqrt(2);
	static String filename = "C:\\Users\\Kousthubha\\workspace\\K-Means Clustering\\src\\fisher.txt";
	public static void main(String args[])
	{
		getData(filename);
		randomCenter(2);
		cluster(cluster_center_initial,0,2);
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
	
	private static void randomCenter(int n){
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("Entered randomCenter");
		System.out.println("Number of clusters: "+n);
		System.out.println();System.out.println();
		Random rand = new Random(System.currentTimeMillis());
		//int rangeX = (int) max_x-min_x+1;
		//int rangeY = (int) max_y-min_y+1;
		int range = (int) len-1;
		cluster_center_initial = new float[n][2];
		//int randomX = 0,randomY=0;
		/*for(i=0;i<n;i++)
		{
			for(j=0;j<2;j++)
			{
				if(j==0){
					randomX = rand.nextInt(rangeX);
					cluster_center_initial[i][j]=randomX;
				} else if(j==1){
					int currentIteration=i;
					randomY = rand.nextInt(rangeY);
					cluster_center_initial[i][j]=randomY;
					for(int l=0;l<i;l++){
						if(distance(cluster_center_initial[i][0],cluster_center_initial[i][1],cluster_center_initial[i][0],cluster_center_initial[i][1])>min_dist){
							i--;
							break;
						} 
					}
					if(currentIteration==i){
						for(int f=0;f<i;f++){
							if(cluster_center_initial[f][0]==cluster_center_initial[i][0] && cluster_center_initial[f][1]==cluster_center_initial[i][1])
								j--;
						}
					}
				}
			}
		}*/
		float improperElements[][]=new float[len][2];
		int improperIndex[] = new int[len]; 
		int improperLen = 0;
		int count=0;
		for(i=0;i<n;i++){
			if(count>500){
				System.out.println("Count greater than 500\n Assigning a value using Linear Search");
				System.out.println(improperLen);
				for(int g=0;g<improperLen;g++) System.out.print(improperElements[g][0]+", "+improperElements[g][1]+"   "); 
				for(int s=0;s<len;s++){
					System.out.println("Data point: ("+data[s][0]+", "+data[s][1]+")");
					int flag=0;
					if(!exists(data[s][0],data[s][1],improperElements,improperLen,improperIndex,s)){
						int currentImproperLen = improperLen;
						cluster_center_initial[i][0]=data[s][0];
						cluster_center_initial[i][1]=data[s][1];
						flag=1;
						improperElements[improperLen][0]=data[s][0];
						improperElements[improperLen][1]=data[s][1];
						improperIndex[improperLen]=s;
						improperLen++;
						System.out.println("Center "+i+": ("+data[s][0]+", "+data[s][1]+")");
							for(int j=currentImproperLen;j<improperLen;j++){
								for(int k=0;k<len;k++)
								if(distance(improperElements[j][0],improperElements[j][1],data[k][0],data[k][1])<=distance_measure){
									if(!exists(data[k][0],data[k][1],improperElements,improperLen,improperIndex,k)){
										improperElements[improperLen][0]=data[k][0];
										improperElements[improperLen][1]=data[k][1];
										improperIndex[improperLen] = k;
										improperLen++;
									}
								}
							}
					}
					System.out.println("false");
					if(flag==1){
						break;
					}
				}
			}else{
			int random = rand.nextInt(range);
			int currentImproperLen = improperLen;
			if(i==0){
				cluster_center_initial[i][0]=data[random][0];
				cluster_center_initial[i][1]=data[random][1];
				improperElements[improperLen][0]=cluster_center_initial[i][0];
				improperElements[improperLen][1]=cluster_center_initial[i][1];
				improperIndex[improperLen]=random;
				improperLen++;
				System.out.println("Center "+i+": ("+data[random][0]+", "+data[random][1]+")");
			}else{
				if(exists(data[random][0],data[random][1],improperElements,improperLen,improperIndex,random)){
					i--;
					count++;
				}else{
					cluster_center_initial[i][0]=data[random][0];
					cluster_center_initial[i][1]=data[random][1];
					System.out.println("Center "+i+": ("+data[random][0]+", "+data[random][1]+")");
					improperElements[improperLen][0]=cluster_center_initial[i][0];
					improperElements[improperLen][1]=cluster_center_initial[i][1];
					improperIndex[improperLen] = random;
					improperLen++;
				}
			}
			if(currentImproperLen<improperLen || i==0)
			for(int j=0;j<improperLen;j++){
				for(int k=0;k<len;k++)
				if(distance(improperElements[j][0],improperElements[j][1],data[k][0],data[k][1])<=distance_measure){
					if(!exists(data[k][0],data[k][1],improperElements,improperLen,improperIndex,k)){
						improperElements[improperLen][0]=data[k][0];
						improperElements[improperLen][1]=data[k][1];
						improperIndex[improperLen] = k;
						improperLen++;
					}
				}
			}
			}
		}
	}
	
	private static void cluster(float cluster_center_data[][],int iteration,int n)
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
				System.out.print("("+cluster_center_data[j][0]+","+cluster_center_data[j][1]+"):"+dist[j]+"\t\t");
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
		if(equals(cluster_center_data, cluster_center_new,n)){
			x=cluster_center_data;
			printCluster(n);
			if(!checkConnectivity(n,0)){
				randomCenter(++n);
				cluster(cluster_center_initial,0,n);
			}else{
				ideal=n;
				System.out.println("\n");
				System.out.println("Found the correct number of clusters for the given data sets is: "+n);
				printCluster(n);
				return;
			}
		}else{
			System.out.println("Cluster centers do not match!");
			for(i=0;i<n;i++){
				System.out.println("Cluster "+i+" data points are: ");
				for(j=0;j<cluster_element_len[i];j++)
				{
					System.out.println("("+cluster_elements[i][j][0]+","+cluster_elements[i][j][1]+")");
				}
				System.out.println();
			}
			cluster(cluster_center_new,++iteration,n);
		}
	}
	
	private static float distance(float x1,float y1,float x2,float y2){
		return (float) Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2,2));
	}
	
	private static void printCluster(int n){
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
	
	private static boolean equals(float[][] x,float[][] y,int n)
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
	
	private static boolean equals(float[][][] x,float[][][] y,int n,int[] len)
	{
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<len[i];j++)
			{
				if(x[i][j][0]!=y[i][j][0]||x[i][j][1]!=y[i][j][1]){return false;}
			}
		}
		return true;
	}
	
	private static boolean checkConnectivity(int l,int call){
		int disconnectedClusters[] = new int[l];
		int disconnectedClusterLen=0;
		float cluster_elements_initial[][][] = cluster_elements;
		int i=0,j=0;
		System.out.println("Entered checkConnectivity");
		System.out.println("Length: "+l);
		for(i=0;i<l;i++)
		System.out.println("Cluster "+i+" length: "+cluster_element_len[i]);
		for(i=0;i<l;i++){
			float rep[][]=new float[cluster_element_len[i]][2];
			int repIndex[] = new int[cluster_element_len[i]];
			int repLen=0;
			rep[repLen][0]=cluster_elements[i][0][0];
			rep[repLen][1]=cluster_elements[i][0][1];
			repIndex[repLen]=0;
			repLen++;
			for(j=0;j<repLen;j++){
				for(int k=0;k<cluster_element_len[i];k++){
					if(distance(rep[j][0],rep[j][1],cluster_elements[i][k][0],cluster_elements[i][k][1])<=distance_measure){
						if(!exists(cluster_elements[i][k][0],cluster_elements[i][k][1],rep,repLen,repIndex,k)){
							rep[repLen][0]=cluster_elements[i][k][0];
							rep[repLen][1]=cluster_elements[i][k][1];
							repIndex[repLen]=k;
							repLen++;
						}
					}
				}
			}
			if(repLen!=cluster_element_len[i]){
				disconnectedClusters[disconnectedClusterLen] = i;
				disconnectedClusterLen++;
			}
		}
		if(disconnectedClusterLen==0){
			System.out.println("Clusters are connected!");
			return true;
		}else{
			System.out.println("Clusters are disconnected!\nDisconnected Clusters are: ");
			for(int x=0;x<disconnectedClusterLen;x++){
				System.out.print(disconnectedClusters[x]+" ");
			}
			for(int x=0;x<disconnectedClusterLen;x++){
				for(int d=0;d<cluster_element_len[disconnectedClusters[x]];d++){
					float connectedMembers[][] = new float[cluster_element_len[disconnectedClusters[x]]][2];
					int connectedLen=0;
					int connectedIndex[] = new int[cluster_element_len[disconnectedClusters[x]]];
					connectedMembers[connectedLen][0]=cluster_elements[disconnectedClusters[x]][d][0];
					connectedMembers[connectedLen][1]=cluster_elements[disconnectedClusters[x]][d][1];
					connectedIndex[connectedLen]=d;
					connectedLen++;
					for(int ld=0;ld<connectedLen;ld++){
						for(int lf=0;lf<cluster_element_len[disconnectedClusters[x]];lf++)
						{
							if(distance(connectedMembers[ld][0],connectedMembers[ld][1],cluster_elements[disconnectedClusters[x]][lf][0],cluster_elements[disconnectedClusters[x]][lf][1])<=distance_measure)
							{
								if(!exists(cluster_elements[disconnectedClusters[x]][lf][0],cluster_elements[disconnectedClusters[x]][lf][1],connectedMembers,connectedLen,connectedIndex,lf))
								{
									connectedMembers[connectedLen][0]=cluster_elements[disconnectedClusters[x]][lf][0];
									connectedMembers[connectedLen][1]=cluster_elements[disconnectedClusters[x]][lf][1];
									connectedIndex[connectedLen]=lf;
									connectedLen++;
								}
								
							}
						}
					}
					System.out.println("Connectivity for: ("+cluster_elements[disconnectedClusters[x]][d][0]+" ,"+cluster_elements[disconnectedClusters[x]][d][1]+")");
					for(int c=0;c<l;c++){
						int flag=0;
						if(c==disconnectedClusters[x]){
							continue;
						}else{
							for(int f=0;f<cluster_element_len[c];f++){
								for(int ld=0;ld<connectedLen;ld++)
								{
									if(distance(cluster_elements[c][f][0],cluster_elements[c][f][1],connectedMembers[ld][0],connectedMembers[ld][1])<distance_measure){
										flag=1;
										break;
									}
								}
								if(flag==1){
									for(int ld=0;ld<connectedLen;ld++)
									{
										System.out.println("Pushing element ("+connectedMembers[ld][0]+" ,"+connectedMembers[ld][1]+") from cluster "+disconnectedClusters[x]+" to "+c);
										cluster_elements[c][cluster_element_len[c]][0]=connectedMembers[ld][0];
										cluster_elements[c][cluster_element_len[c]][1]=connectedMembers[ld][1];
										cluster_element_len[c]++;
										removeElement(disconnectedClusters[x],connectedMembers[ld][0],connectedMembers[ld][1]);
									}
									break;
								}
							}
						}
						if(flag==1) break;
					}
				}
			}
		}
		if(!equals(cluster_elements,cluster_elements_initial,l,cluster_element_len)){
			System.out.println("Clusters do not match!");
			int actualLen =0;
			for(int h=0;h<l;h++)
			{
				if(cluster_element_len[h]!=0)actualLen++;
				else{
					for(int g=h;g<l-1;g++){
						cluster_elements[g]=cluster_elements[g+1];
					}
					cluster_elements[l-1]=null;
					l--;
					h--;
				}
			}
			return checkConnectivity(actualLen,++call);
		}else{
			if(connected(l)){
				System.out.println("Clusters are connected after jumbling!");
				return true;
			}else{
				System.out.println("Clusters are disconnected even after jumbling!");
				printCluster(l);
				return false;
			}
		}
	}
	
	private static boolean connected(int l){
			for(i=0;i<l;i++){
				float rep[][]=new float[cluster_element_len[i]][2];
				int repIndex[] = new int[cluster_element_len[i]];
				int repLen=0;
				rep[repLen][0]=cluster_elements[i][0][0];
				rep[repLen][1]=cluster_elements[i][0][1];
				repIndex[repLen]=0;
				repLen++;
				for(j=0;j<repLen;j++){
					for(int k=0;k<cluster_element_len[i];k++){
						if(distance(rep[j][0],rep[j][1],cluster_elements[i][k][0],cluster_elements[i][k][1])<=distance_measure){
							if(!exists(cluster_elements[i][k][0],cluster_elements[i][k][1],rep,repLen,repIndex,k)){
								rep[repLen][0]=cluster_elements[i][k][0];
								rep[repLen][1]=cluster_elements[i][k][1];
								repIndex[repLen]=k;
								repLen++;
							}
						}
					}
				}
				if(repLen!=cluster_element_len[i]){
					return false;
				}
			}
			return true;
	}
	
	private static void removeElement(int clusterIndex,float x,float y){
		System.out.println("Removing element ("+x+" ,"+y+") from cluster "+clusterIndex);
		int elementIndex=0;
		for(int h=0;h<cluster_element_len[clusterIndex];h++){
			if(cluster_elements[clusterIndex][h][0]==x && cluster_elements[clusterIndex][h][1]==y){elementIndex=h;break;}
		}
		for(int h=elementIndex;h<cluster_element_len[clusterIndex]-1;h++){
			cluster_elements[clusterIndex][h][0]=cluster_elements[clusterIndex][h+1][0];
			cluster_elements[clusterIndex][h][1]=cluster_elements[clusterIndex][h+1][1];
		}
		cluster_element_len[clusterIndex]--;
		System.out.println("New len of cluster "+clusterIndex+" is "+cluster_element_len[clusterIndex]);
		for(int h=0;h<cluster_element_len[clusterIndex];h++){
			System.out.println("("+cluster_elements[clusterIndex][h][0]+" ,"+cluster_elements[clusterIndex][h][1]+")");
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
}