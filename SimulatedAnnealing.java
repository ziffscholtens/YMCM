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

	Random rand = new Random();

	SimulatedAnnealing(int[][] initialWorldMatrix, House[] initialHouseList, double initialTotalValue){

		int height = Polder.POLDER_HEIGHT;
		int width = Polder.POLDER_WIDTH;
		world_matrix = copyWorld(initialWorldMatrix);
		houseList = copyHouseList(initialHouseList);
		totalValue = initialTotalValue;

		oldVal = initialTotalValue;
		old_mat = initialWorldMatrix;
		oldList = initialHouseList;
	}

	void iterate() {
		itNum = rand.nextInt(houseList.length);
		temp_matrix = copyWorld(world_matrix);
		House tempHouse = houseList[itNum];
		removeHouseOnMatrix(tempHouse);
		removeClearance(tempHouse);
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
					if(temp_matrix[i][j] != Polder.NOTHING) {
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
					if(temp_matrix[i][j] == Polder.HOUSE) {
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
					temp_matrix[i][j] = Polder.HOUSE;
				}
			}
		}
	}

	void replaceOld(House house) {
		for(int i = house.x; i < house.len1+house.x; i++) {
			for(int j = house.y; j < house.len2+house.y; j++) {
				//		System.out.println("place house on matrix OOB? " + outOfBounds(i,j));
				if(!outOfBounds(i,j)){
					temp_matrix[i][j] = Polder.HOUSE;
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
			double probability = logarithm(totalValue - oldVal, Graphics.c);
			double randVal = Math.random();
			double realProb = 1 - probability;
			System.out.println("Probability of Accepting = " + probability);
			System.out.println("RandVal = " + randVal);

			if(randVal > realProb) {
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
				if(Graphics.c > 500) {
					Graphics.c -= 500;
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

	double logarithm (double diff, double c) {
		System.out.println("diff: " + diff);
		System.out.println("c: " + c);
		double param = diff/c;
		System.out.println("param: " + param);
		double prob = Math.exp(param);
		System.out.println("gives: " + prob);

		if(prob > 0 && prob < 1) {
			return prob;
		} else {
			return 0;
		}
	}


	void simulatedAnnealing() {
		int number = 0;
		for(int i=0; i<Polder.TOTAL;i++){
		
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

	int[][] copyWorld(int[][] original){
		int[][] copy = new int[Polder.POLDER_WIDTH][Polder.POLDER_HEIGHT];
		for(int i = 0; i<Polder.POLDER_WIDTH; i++) {
			for(int j = 0; j<Polder.POLDER_HEIGHT; j++) {
				int object = original[i][j];
				copy[i][j] = object;
			}
		}
		return(copy);
	}

	boolean outOfBounds(int x, int y) {
		return (x < 0 || x > Polder.POLDER_WIDTH-1 || y < 0 || y > Polder.POLDER_HEIGHT-1);
	}

	boolean notOnHouse(int x, int y) {
		if(outOfBounds(x,y)) {
			return false;
		} else {
			return (temp_matrix[x][y] != Polder.HOUSE);
		}
	}


	void removeHouseOnMatrix(House house) {
		for(int i = house.x; i < house.len1+house.x; i++) {
			for(int j = house.y; j < house.len2+house.y; j++) {
				if(!outOfBounds(i,j)) {
					temp_matrix[i][j] = Polder.NOTHING;
				}
			}
		}
	}

	void placeClearance(House house) {
		for(int h = 0; h < house.minClearance(); h++) {
			for(int k = house.x-h; k < house.x+h+house.len1; k++) {
				if(!outOfBounds(k, house.y - h-1)){ 

					if(notOnHouse(k, house.y - h-1)) {
						temp_matrix[k][house.y-h-1] = Polder.CLEARANCE;
					}
				}
				if(!outOfBounds(k, house.y+house.len2+h)){ 	
					if(notOnHouse(k, house.y+house.len2+h)) {
						temp_matrix[k][house.y+house.len2+h] = Polder.CLEARANCE;
					}
				}
			}

			for(int l = house.y-h; l < house.y+h+house.len2; l++) {
				if(!outOfBounds(house.x-h-1, l)){ 
					if(notOnHouse(house.x-h-1, l)) {
						temp_matrix[house.x-h-1][l] = Polder.CLEARANCE;
					}
				}
				if(!outOfBounds(house.x+house.len1+h, l)){ 
					if(notOnHouse(house.x+house.len1+h, l)) {
						temp_matrix[house.x+house.len1+h][l] = Polder.CLEARANCE;
					}
				}
			}

		}
	}

	void removeClearance(House house) {
		for(int h = 0; h < house.minClearance(); h++) {
			for(int k = house.x-h; k < house.x+h+house.len1; k++) {
				if(notOnHouse(k, house.y - h-1)) {
					temp_matrix[k][house.y-h-1] = Polder.NOTHING;
				}
				if(notOnHouse(k, house.y+house.len2+h)) {
					temp_matrix[k][house.y+house.len2+h] = Polder.NOTHING;
				}
			}

			for(int l = house.y-h; l < house.y+h+house.len2; l++) {
				if(notOnHouse(house.x-h-1, l)) {
					temp_matrix[house.x-h-1][l] = Polder.NOTHING;
				}
				if(notOnHouse(house.x+house.len1+h, l)) {
					temp_matrix[house.x+house.len1+h][l] = Polder.NOTHING;
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
					if(temp_matrix[k][house.y-clearance-1] == Polder.HOUSE) {
						clear = false;
					}
				}
				if(!outOfBounds(k, house.y+house.len2+clearance+1)) {
					if(temp_matrix[k][house.y+house.len2+clearance+1] == Polder.HOUSE) {
						clear = false;
					}
				}
			}

			for(int l = house.y-clearance; l < house.y+clearance+house.len2; l++) {
				if(!outOfBounds(house.x-clearance-1, l)) {
					if(temp_matrix[house.x-clearance-1][l] == Polder.HOUSE) {
						clear = false;
					}
				}
				if(!outOfBounds(house.x+house.len1+clearance, l)) {
					if(temp_matrix[house.x+house.len1+clearance][l] == Polder.HOUSE) {
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
		if (house.type == Polder.MANSION) {
			value += Polder.PRICE_MANS;
			incWeight = Polder.PRICE_INC_MANS;
		} else if (house.type == Polder.BUNGALOW) {
			value += Polder.PRICE_BUNG;
			incWeight = Polder.PRICE_INC_BUNG;
		} else if (house.type == Polder.FAMILY_HOME) {
			value += Polder.PRICE_FAM;
			incWeight = Polder.PRICE_INC_FAM;
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
