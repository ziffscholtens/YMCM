package amstelhaege;

import java.util.Random;

public class SimulatedAnnealing {

	double totalValue = 0;
	int[][] world_matrix;
	int[][] temp_matrix;
	House[] houseList;
	int itNum = 0;
	House newHouse;
	double oldVal;
	int[][] old_mat;
	House[] oldList;
	Water[] waterList;
	Playground[] playgroundList;

	Random rand = new Random();

	SimulatedAnnealing(int[][] initialWorldMatrix, House[] initialHouseList, double initialTotalValue, Water[] waterList, Playground[] playgroundList){

		int height = InitialPolder.POLDER_HEIGHT;
		int width = InitialPolder.POLDER_WIDTH;
		world_matrix = copyWorld(initialWorldMatrix);
		houseList = copyHouseList(initialHouseList);
		totalValue = initialTotalValue;
		this.playgroundList = playgroundList;
		this.waterList = copyWaterList(waterList);

		oldVal = initialTotalValue;
		old_mat = initialWorldMatrix;
		oldList = initialHouseList;
		iterate();
	}

	void iterate() {
		itNum = rand.nextInt(houseList.length);
		temp_matrix = copyWorld(world_matrix);
		House tempHouse = houseList[itNum];
		removeHouseOnMatrix(tempHouse);
		removeClearance(tempHouse);
		renewWater();
		renewClearance(itNum);

		slideHouse(tempHouse);

		totalValue = totalValue();	
		houseList[itNum] = newHouse;
		world_matrix = temp_matrix;

	}

	int[] direction(double d) {
		int x = 0;
		int y = 0;

		if(d <= .25) {
			x = -5;
		} else if (d > .25 && d <= .5) {
			x = 5;
		} else if (d > .5 && d <= .75) {
			y = -5;
		} else if (d > .75 && d <=1) {
			y = 5;
		}
		/*
		if(d <= .125) {
			x = -10;
			y = -10;
		} else if (d <= .25) {
			y = -10;
		} else if(d < .375) {
			x = 10;
			y = -10;
		} else if(d < .5) {
			x = -10;
		} else if(d < .625) {
			x = 10;
		} else if(d < .75) {
			x = -10;
			y = 10;
		} else if(d < .875) {
			y = 10;
		} else if (d < 1) {
			x = 10;
			y = 10;
		}		
		 */

		return new int[] {x,y};
	}


	boolean legalProperty(int startX, int startY, int len1, int len2, int minClearance) {
		//check if a house can be placed on the given spot
		for(int i = startX; i < len1+startX; i++) {
			for(int j = startY; j < len2+startY; j++) {
				//	System.out.println("Legal Property OOB geeft> " + outOfBounds(i,j));
				//	System.out.println("Legal Property CC geeft> " + clearanceConflict(startX, startY, len1, len2, minClearance));
				if(!outOfBounds(i,j) && !clearanceConflict(startX, startY, len1, len2, minClearance)) {
					if(temp_matrix[i][j] != InitialPolder.NOTHING && temp_matrix[i][j] != InitialPolder.PLAYGROUND) {
						return false;
					}
				} else {
					return false;
				}
			}
		}
		return true;
	}

	boolean clearanceConflict(int startX, int startY, int len1, int len2, int minClearance) {

		boolean conflict = false;

		for(int i = startX-minClearance; i < len1+startX+ minClearance; i++) {
			for(int j = startY-minClearance; j < len2+startY+minClearance; j++) {
				if(!outOfBounds(i,j)) {
					if(temp_matrix[i][j] == InitialPolder.HOUSE || temp_matrix[i][j] == InitialPolder.PLAYGROUND) {
						conflict = true;
					} 
				}
			}
		}
		return conflict;
	}


	void placeHouseOnMatrix(House house) {
		for(int i = house.x; i < house.len1+house.x; i++) {
			for(int j = house.y; j < house.len2+house.y; j++) {
				//	System.out.println("place house on matrix OOB? " + outOfBounds(i,j));
				if(!outOfBounds(i,j)){
					temp_matrix[i][j] = InitialPolder.HOUSE;
				}
			}
		}
	}

	void replaceOld(House house) {
		for(int i = house.x; i < house.len1+house.x; i++) {
			for(int j = house.y; j < house.len2+house.y; j++) {
				//		System.out.println("place house on matrix OOB? " + outOfBounds(i,j));
				if(!outOfBounds(i,j)){
					temp_matrix[i][j] = InitialPolder.HOUSE;
				}
			}
		}
	}

	void slideHouse(House house) {
		boolean notPlaced = true;
		int tries = 0;
		House old = house.copy();
		int a = house.len1;
		int b = house.len2;

		while(notPlaced && tries < 10) {
			int[] cord = direction(Math.random());
			house.setX(old.x+cord[0]);
			house.setY(old.y+cord[1]);
			if(Math.random() > 0.5) {
				house.len2 = a;
				house.len1 = b;
			}
			if(genHouse(house)) {
				//		System.out.println("SLIDEHOUSE");
				placeHouseOnMatrix(house);
				placeClearance(house);
				newHouse = house;
				notPlaced = false;
			}
			tries ++;
			if(tries == 10 && notPlaced) {
				//		System.out.println("OLD");
				replaceOld(old);
				placeClearance(old);
				newHouse = old;
				return;
			}
		}
		totalValue = totalValue();
		if(totalValue < oldVal) {
			double probability = expo(totalValue - oldVal, Graphics.c);
			double randVal = Math.random();
			double realProb = 1 - probability;

			if(randVal < realProb) {
				houseList[itNum] = newHouse;
				world_matrix = temp_matrix;
				removeHouseOnMatrix(house);
				removeClearance(house);
				renewClearance(itNum);
				replaceOld(old);
				placeClearance(old);
				newHouse = old;
				houseList[itNum] = old;
				
				world_matrix = temp_matrix;
				
				if(Graphics.c > 1500) {
					Graphics.c -= 1500;
				} else if (Graphics.c <= 500 && Graphics.c > 100) {
					Graphics.c -= 200;
				} else if (Graphics.c <= 100 & Graphics.c > 10) {
					Graphics.c -= 20;
				}
			}
		}

	}

	boolean genHouse(House house) {
		boolean placed = false;

		if(legalProperty(house.x,house.y,house.len1,house.len2,house.minClearance())) {
			placed = true;
		}

		if(placed) {
			House newHouse = new House(house.x,house.y,house.len1,house.len2,house.minClearance());
			this.newHouse = newHouse;
		}

		return placed;
	}

	double expo (double diff, double c) {
		double param = diff/c;
		double prob = Math.exp(param);
		if(prob > 0 && prob < 1) {
			return prob;
		} else {
			return 0;
		}
	}


	void simulatedAnnealing() {
		int number = 0;
		for(int i=0; i<InitialPolder.TOTAL;i++){
		
			oldVal = totalValue;
			old_mat = world_matrix;
			oldList = houseList;
			
			temp_matrix = copyWorld(world_matrix);
			House tempHouse = houseList[number];

			removeHouseOnMatrix(tempHouse);
			removeClearance(tempHouse);
			renewClearance(number);		

			slideHouse(tempHouse);
			
			totalValue = totalValue();	
			houseList[itNum] = newHouse;
			world_matrix = temp_matrix;

			number++;
			
			System.out.println("number "+ number);
		}
	}



	House[] copyHouseList(House[] original){
		House[] copy = new House[original.length];
		for(int i = 0; i<original.length; i++) {
			House object = original[i];
			copy[i] = object;
		}
		return(copy);

	}
	
	Water[] copyWaterList(Water[] original){
		Water[] copy = new Water[original.length];
		for(int i = 0; i<original.length; i++) {
			Water object = original[i];
			copy[i] = object;
		}
		return(copy);

	}

	int[][] copyWorld(int[][] original){
		int[][] copy = new int[InitialPolder.POLDER_WIDTH][InitialPolder.POLDER_HEIGHT];
		for(int i = 0; i<InitialPolder.POLDER_WIDTH; i++) {
			for(int j = 0; j<InitialPolder.POLDER_HEIGHT; j++) {
				int object = original[i][j];
				copy[i][j] = object;
			}
		}
		return(copy);
	}
	
	boolean waterCheck(int x, int y) {
		return (x < 0 || x > InitialPolder.POLDER_WIDTH-1 || y < 0 || y > InitialPolder.POLDER_HEIGHT-1 || world_matrix[x][y] == InitialPolder.WATER);	
	}
	
	void placeWater(int x, int y, int len1, int len2) {
		for(int i = x; i < x+len1 ; i++) {
			for(int j = y; j < y+len2; j++) {
				if(!waterCheck(i,j)){
					world_matrix[i][j] = InitialPolder.WATER;
				}
			}
		}
	}

	void renewWater() {
		for (int i = 0; i < waterList.length; i++) {
			Water w = waterList[i]; 
			placeWater(w.x, w.y, w.len1, w.len2);
		}
	}

	boolean outOfBounds(int x, int y) {
		return (x < 0 || x > InitialPolder.POLDER_WIDTH-1 || y < 0 || y > InitialPolder.POLDER_HEIGHT-1);
	}

	boolean notOnHouse(int x, int y) {
		if(outOfBounds(x,y)) {
			return false;
		} else {
			return (temp_matrix[x][y] != InitialPolder.HOUSE && temp_matrix[x][y] != InitialPolder.WATER);
		}
	}


	void removeHouseOnMatrix(House house) {
		for(int i = house.x; i < house.len1+house.x; i++) {
			for(int j = house.y; j < house.len2+house.y; j++) {
				if(!outOfBounds(i,j)) {
					temp_matrix[i][j] = InitialPolder.NOTHING;
				}
			}
		}
	}

	void placeClearance(House house) {
		for(int h = 0; h < house.minClearance(); h++) {
			for(int k = house.x-h; k < house.x+h+house.len1; k++) {
				if(!outOfBounds(k, house.y - h-1)){ 

					if(notOnHouse(k, house.y - h-1)) {
						temp_matrix[k][house.y-h-1] = InitialPolder.CLEARANCE;
					}
				}
				if(!outOfBounds(k, house.y+house.len2+h)){ 	
					if(notOnHouse(k, house.y+house.len2+h)) {
						temp_matrix[k][house.y+house.len2+h] = InitialPolder.CLEARANCE;
					}
				}
			}

			for(int l = house.y-h; l < house.y+h+house.len2; l++) {
				if(!outOfBounds(house.x-h-1, l)){ 
					if(notOnHouse(house.x-h-1, l)) {
						temp_matrix[house.x-h-1][l] = InitialPolder.CLEARANCE;
					}
				}
				if(!outOfBounds(house.x+house.len1+h, l)){ 
					if(notOnHouse(house.x+house.len1+h, l)) {
						temp_matrix[house.x+house.len1+h][l] = InitialPolder.CLEARANCE;
					}
				}
			}

		}
	}

	void removeClearance(House house) {
		for(int h = 0; h < house.minClearance(); h++) {
			for(int k = house.x-h; k < house.x+h+house.len1; k++) {
				if(notOnHouse(k, house.y - h-1)) {
					temp_matrix[k][house.y-h-1] = InitialPolder.NOTHING;
				}
				if(notOnHouse(k, house.y+house.len2+h)) {
					temp_matrix[k][house.y+house.len2+h] = InitialPolder.NOTHING;
				}
			}

			for(int l = house.y-h; l < house.y+h+house.len2; l++) {
				if(notOnHouse(house.x-h-1, l)) {
					temp_matrix[house.x-h-1][l] = InitialPolder.NOTHING;
				}
				if(notOnHouse(house.x+house.len1+h, l)) {
					temp_matrix[house.x+house.len1+h][l] = InitialPolder.NOTHING;
				}
			}
		}
	}



	int countClearance(House house) {
		int clearance = 0;
		boolean clear = true;

		while(clear) {

			for(int k = house.x-clearance; k < house.x+clearance+house.len1; k++) {
				if(!outOfBounds(k, house.y-clearance-1)) {
					if(temp_matrix[k][house.y-clearance-1] == InitialPolder.HOUSE) {
						clear = false;
					}
				}
				if(!outOfBounds(k, house.y+house.len2+clearance+1)) {
					if(temp_matrix[k][house.y+house.len2+clearance+1] == InitialPolder.HOUSE) {
						clear = false;
					}
				}
			}

			for(int l = house.y-clearance; l < house.y+clearance+house.len2; l++) {
				if(!outOfBounds(house.x-clearance-1, l)) {
					if(temp_matrix[house.x-clearance-1][l] == InitialPolder.HOUSE) {
						clear = false;
					}
				}
				if(!outOfBounds(house.x+house.len1+clearance, l)) {
					if(temp_matrix[house.x+house.len1+clearance][l] == InitialPolder.HOUSE) {
						clear = false;
					}
				}
			}
			clearance ++;
		}

		return (int) (clearance-house.minClearance())/2;
	}

	double getValue(House house) {
		double value = 0;
		double incWeight = 0;
		if (house.type == InitialPolder.MANSION) {
			value += InitialPolder.PRICE_MANS;
			incWeight = InitialPolder.PRICE_INC_MANS;
		} else if (house.type == InitialPolder.BUNGALOW) {
			value += InitialPolder.PRICE_BUNG;
			incWeight = InitialPolder.PRICE_INC_BUNG;
		} else if (house.type == InitialPolder.FAMILY_HOME) {
			value += InitialPolder.PRICE_FAM;
			incWeight = InitialPolder.PRICE_INC_FAM;
		}

		int clearance = countClearance(house);
		value = value * (clearance * (incWeight)+1);

		house.setValue(value);
		return value;
	}

	void printTotalValue() {
		System.out.printf("the total value of the project equals: €%.2f milion\n", totalValue/1000000);
	}

	double totalValue() {
		double totalValue = 0;
		for(int i = 0; i < houseList.length; i++) {
			totalValue = totalValue + getValue(houseList[i]);
		}
		for(int j =0; j<playgroundList.length; j++){
			totalValue = totalValue + InitialPolder.PRICE_PLAY;
		}
		return totalValue;
	}




	void renewClearance(int number){
		for(int i=0;i<houseList.length;i++){
			if(number!=i){
				placeClearance(houseList[i]);
			}
		}
	}
}