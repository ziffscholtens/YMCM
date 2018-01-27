package amstelhaege;

import java.util.Random;

public class SimulatedAnnealing {

	double totalValue = 0;
	int[][] world_matrix;
	int[][] temp_matrix;
	House[] houseList;
	Water[] waterList;

	House newHouse;

	Random rand = new Random();

	SimulatedAnnealing(int[][] initialWorldMatrix, House[] initialHouseList, double initialTotalValue, Water[] waterList){

		int height = InitialPolder.POLDER_HEIGHT;
		int width = InitialPolder.POLDER_WIDTH;
		world_matrix = copyWorld(initialWorldMatrix);
		houseList = copyHouseList(initialHouseList);
		this.waterList = waterList;
		totalValue = initialTotalValue;
		simulatedAnnealing();
	}
	void renewWater() {
		for (int i = 0; i < waterList.length; i++) {
			Water w = waterList[i];
			System.out.print(w.x + " " + w.y);
			if(!waterCheck(w.x,w.y)) {
				placeWater(w.x, w.y, w.len1, w.len2);
			}
		}
	}

	boolean waterCheck(int x, int y) {
		return (x < 0 || x > InitialPolder.POLDER_WIDTH-1 || y < 0 || y > InitialPolder.POLDER_HEIGHT-1 || world_matrix[x][y] == InitialPolder.WATER);	
	}

	void placeWater(int x, int y, int len1, int len2) {
		for(int i = x; i < x+len1 ; i++) {
			for(int j = y; j < y+len2; j++) {
				world_matrix[i][j] = InitialPolder.WATER;		
			}
		}
	}
	void simulatedAnnealing() {
		int number = 0;
		//	double c = 3;
		for(int i=0; i<houseList.length;i++){
			temp_matrix = copyWorld(world_matrix);
			House tempHouse = houseList[number];

			House copy = tempHouse.copy();

			removeHouseOnMatrix(tempHouse);
			removeClearance(tempHouse);
			renewClearance(number);		

			slideHouse(copy);

			double tempTotalValue = totalValue();
			if(tempTotalValue > totalValue){
				totalValue = tempTotalValue;
				houseList[number] = newHouse;
				world_matrix = copyWorld(temp_matrix);
			}
			/*else {
								double probability = logarithm(totalValue - tempTotalValue, c);
								double randVal = Math.random();
								if (randVal < probability) {
									tempHouse = copy;
									storedValue = tempTotalValue;
									c += 1;
								}
							}
			 */
			number++;
		}
	}



	int[] direction(double d) {
		int x = 0;
		int y = 0;

		if(d < .125) {
			x = -1;
			y = -1;
		} else if (d < .25) {
			y = -1;
		} else if(d < .375) {
			x = 1;
			y = -1;
		} else if(d < .5) {
			x = -1;
		} else if(d < .625) {
			x = 1;
		} else if(d < .75) {
			x = -1;
			y = 1;
		} else if(d < .875) {
			y = 1;
		} else if (d < 1) {
			x = 1;
			y = 1;
		}		

		return new int[] {x,y};
	}

	double logarithm (double diff, double c) {
		double param = diff/c;
		if(param > 1) {
			return Math.log(diff / c);
		} else {
			return 0;
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
		int[][] copy = new int[InitialPolder.POLDER_WIDTH][InitialPolder.POLDER_HEIGHT];
		for(int i = 0; i<InitialPolder.POLDER_WIDTH; i++) {
			for(int j = 0; j<InitialPolder.POLDER_HEIGHT; j++) {
				int object = original[i][j];
				copy[i][j] = object;
			}
		}
		return(copy);
	}

	boolean outOfBounds(int x, int y) {
		return (x < 0 || x > InitialPolder.POLDER_WIDTH-1 || y < 0 || y > InitialPolder.POLDER_HEIGHT-1);
	}

	boolean notOnHouse(int x, int y) {
		if(outOfBounds(x,y)) {
			return false;
		} else {
			return (temp_matrix[x][y] != InitialPolder.HOUSE);
		}
	}

	boolean legalProperty(int startX, int startY, int len1, int len2, int minClearance) {
		len1+= minClearance;
		len2+= minClearance;
		for(int i = startX; i < len1+startX; i++) {
			for(int j = startY; j < len2+startY; j++) {
				if(!outOfBounds(i,j)) {
					if(temp_matrix[i][j] != InitialPolder.NOTHING) {
						return false;
					}
				} else {
					return false;
				}
			}
		}
		return true;
	}

	void placeHouseOnMatrix(House house) {
		for(int i = house.x; i < house.len1+house.x; i++) {
			for(int j = house.y; j < house.len2+house.y; j++) {
				if(!outOfBounds(i,j)) {
					temp_matrix[i][j] = InitialPolder.HOUSE;
				}
			}
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
				if(notOnHouse(k, house.y - h-1)) {
					temp_matrix[k][house.y-h-1] = InitialPolder.CLEARANCE;
				}
				if(notOnHouse(k, house.y+house.len2+h)) {
					temp_matrix[k][house.y+house.len2+h] = InitialPolder.CLEARANCE;
				}
			}

			for(int l = house.y-h; l < house.y+h+house.len2; l++) {
				if(notOnHouse(house.x-h-1, l)) {
					temp_matrix[house.x-h-1][l] = InitialPolder.CLEARANCE;
				}
				if(notOnHouse(house.x+house.len1+h, l)) {
					temp_matrix[house.x+house.len1+h][l] = InitialPolder.CLEARANCE;
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

	boolean genMansion(int startX, int startY, String lining) {
		boolean placed = false;
		int len1;
		int len2;

		if(lining.contains("vertical")) {
			len1 = InitialPolder.LEN1_MANS;
			len2 = InitialPolder.LEN2_MANS;
		} else {
			len1 = InitialPolder.LEN2_MANS;
			len2 = InitialPolder.LEN1_MANS;
		}

		if(legalProperty(startX,startY,len1,len2,InitialPolder.MIN_CLEAR_MANS)) {
			placed = true;
		}

		if(placed) {
			House newHouse = new House(startX, startY, len1, len2, InitialPolder.MANSION);

			placeHouseOnMatrix(newHouse);
			placeClearance(newHouse);
			this.newHouse = newHouse;
		}
		return placed;
	}

	boolean genHouse(House house) {
		boolean placed = false;

		if(legalProperty(house.x,house.y,house.len1,house.len2,house.minClearance())) {
			placed = true;
		}

		if(placed) {
			House newHouse = new House(house.x,house.y,house.len1,house.len2,house.minClearance());

			placeHouseOnMatrix(newHouse);
			placeClearance(newHouse);
			this.newHouse = newHouse;
		}
		return placed;
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
		return totalValue;
	}


	void slideHouse(House house) {
		boolean notPlaced = true;
		int tries = 0;
		House reserve = house.copy();
		while(notPlaced && tries < 11) {

			int[] cord = direction(Math.random());

			house.setX(house.x+cord[0]);
			house.setY(house.y+cord[1]);

			int a = house.len1;
			int b = house.len2;

			if(Math.random() > 0.5) {
				house.len2 = a;
				house.len1 = b;
			}

			if(genHouse(house)) {
				notPlaced = false;
				tries = 0;
			}
			tries ++;
		}
		if(tries > 10) {
			genHouse(reserve);
		}

	}



	void renewClearance(int number){
		for(int i=0;i<houseList.length;i++){
			if(number!=i){
				placeClearance(houseList[i]);
			}
		}
	}
}