package Heuristics;
import java.util.Random;

public class Polder {
	final int POLDER_WIDTH = 1700;
	final int POLDER_HEIGHT = 2000;

	final int TOTAL_NUMBER_OF_HOUSES = 40;
	final double PERC_FAM = 0.5;
	final double PERC_BUNG = 0.3;
	final double PERC_MANS = 0.2;
	final double MIN_CLEAR_FAM = 2;
	final double MIN_CLEAR_BUNG = 3;
	final double MIN_CLEAR_MANS = 6;
	final double PRICE_FAM = 285000;
	final double PRICE_BUNG = 399000;
	final double PRICE_MANS = 610000;
	final double PRICE_INCREASE_FAM = 0.03;
	final double PRICE_INCREASE_BUNG = 0.04;
	final double PRICE_INCREASE_MANS = 0.06;

	final int HOUSE=1;
	final int WATER=2;
	final int NOTHING=0;

	int[][] world_matrix;
	int[][] init_environment;

	Random rand = new Random();

	Polder(){
		int height = POLDER_HEIGHT;
		int width = POLDER_WIDTH;
		world_matrix= new int[width][height];
		//	pop=ORG_POP;
		//	gen=1;
		//	iterations=0;
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				world_matrix[i][j]= 0;
			}
		}
	}

	//	init_environment=copy_world(world_matrix);
	//	generateOrganisms();
	//	PlaceOrganisms();
}

/*
	void PlaceOrganisms(){
		for(int i=0; i<ORG_POP; i++){
		int forCent =(int) Math.floor(Math.random()*(NUMBER_OF_FORESTS));
		int xfirst = all_forests[forCent].x;
		int yfirst = all_forests[forCent].y;
		int xnext = xfirst;
		int ynext = yfirst;
			while(sumofNeighbors(xnext,ynext) > 3) {
				int xAdd =(int) Math.floor(Math.random()*5-2);
				int yAdd =(int) Math.floor(Math.random()*5-2);
				xfirst = xnext;
				yfirst = ynext;
				xnext = mod(xnext + xAdd);
				ynext = mod(ynext + yAdd);
			}
			int xfin = mod((xnext + xfirst)/2);
			int yfin = mod((ynext + yfirst)/2);
			organisms[i].initialize_position(xfin, yfin);
		}


	}
 */


//place houses ofzo
/*

	int mod(int d){
		return Math.floorMod(d, WORLD_SIZE);
	}

	int r(){
		return (int) Math.abs(rand.nextGaussian()*2+1);
	}

	int sumofNeighbors(int x, int y){
		int sum=0;
		for(int i=-1; i<=1;i++){
			for(int j=-1; j<=1;j++){
				sum+=world_matrix[mod(x+i)][mod(y+j)].object;
			}
		}
		return sum;
	}
 */
/*
	Tuple[][] iterate(){
		iterations++;
		pop=0;

		for(int k=0;k<ORG_POP;k++){
			if(organisms[k].alive==1){
				pop++;
				organisms[k].iterate();
			}
		}

		if(pop==0){
			Tuple[][] world_matrix_temp=copy_world(init_environment);
			world_matrix=world_matrix_temp;
			nextGen();
		}


		return world_matrix;
	}
 */
/*
	Tuple[][] copy_world(Tuple[][] original){
		Tuple[][] copy = new Tuple[WORLD_SIZE][WORLD_SIZE];
		for(int i = 0; i<WORLD_SIZE; i++) {
			for(int j = 0; j<WORLD_SIZE; j++) {
				int object=original[i][j].object;
				boolean p=original[i][j].poison;
				copy[i][j] = new Tuple(object,0,p);
			}
		}
		return(copy);
	}
 */

/*
	void nextGen(){
		gen++;
		NeuralNet[] top_orgsnisms= topN(organisms,TOP);
		int j=0;
		// double avg=0;
		double max=0;
		for(int k = 0; k<TOP; k++) {
			// avg+= (500-top_orgsnisms[k].Life);
			double Lifetime=500-top_orgsnisms[k].Life;
			max= (Lifetime>max)? Lifetime:max;
		}
		// System.out.println(avg/TOP); 
		System.out.println(max);
		while(j<ORG_POP){
			organisms[j]=top_orgsnisms[Math.floorMod(j,TOP-1)].mutated_child();
			j++;
		}
		PlaceOrganisms();
	}
}	
 */