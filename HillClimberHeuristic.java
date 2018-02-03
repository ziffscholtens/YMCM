package amstelhaege;

import java.util.Random;

public class HillClimberHeuristic {

	double totalValue = 0;
	int[][] world_matrix;
	int[][] temp_matrix;
	House[] houseList;
	Water[] waterList;
	Playground[] playgroundList;

	House newHouse;

	Random rand = new Random();

	HillClimberHeuristic(int[][] initialWorldMatrix, House[] initialHouseList, double initialTotalValue, Water[] waterList, Playground[] playgroundList){

		int height = InitialPolder.POLDER_HEIGHT;
		int width = InitialPolder.POLDER_WIDTH;
		world_matrix = copyWorld(initialWorldMatrix);
		houseList = copyHouseList(initialHouseList);
		this.waterList = copyWaterList(waterList);
		this.playgroundList = playgroundList;
		totalValue = initialTotalValue;
		hillClimber();
	}

	void renewWater() {
		for (int i = 0; i < waterList.length; i++) {
			Water w = waterList[i]; 
			placeWater(w.x, w.y, w.len1, w.len2);
		}
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


	void hillClimber(){
		int number = 0;
		for(int i=0; i<houseList.length;i++){
			temp_matrix = copyWorld(world_matrix);
			House tempHouse = houseList[number];
			removeHouseOnMatrix(tempHouse);
			removeClearance(tempHouse);
			renewWater();
			renewClearance(number);
			placeHouse(tempHouse);

			double tempTotalValue = totalValue();
			if(tempTotalValue > totalValue){
				totalValue = tempTotalValue;
				houseList[number] = newHouse;
				world_matrix = copyWorld(temp_matrix);
			}
			number++;
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


	boolean legalProperty(int startX, int startY, int len1, int len2, int minClearance) {
		//check if a house can be placed on the given spot
		for(int i = startX; i < len1+startX; i++) {
			for(int j = startY; j < len2+startY; j++) {
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

		for(int i = startX-minClearance; i < len1+startX+ minClearance; i++) {
			for(int j = startY-minClearance; j < len2+startY+minClearance; j++) {
				if(!outOfBounds(i,j)) {
					if(temp_matrix[i][j] == InitialPolder.HOUSE || temp_matrix[i][j] == InitialPolder.PLAYGROUND) {
						return true;
					} 
				}
			}
		}
		return false;
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

	boolean genBungalow(int startX, int startY, String lining) {
		boolean placed = false;
		int len1;
		int len2;

		if(lining.contains("vertical")) {
			len1 = InitialPolder.LEN1_BUNG;
			len2 = InitialPolder.LEN2_BUNG;
		} else {
			len1 = InitialPolder.LEN2_BUNG;
			len2 = InitialPolder.LEN1_BUNG;
		}

		if(legalProperty(startX,startY,len1,len2,InitialPolder.MIN_CLEAR_BUNG)) {
			placed = true;
		}

		if(placed) {
			House newHouse = new House(startX, startY, len1, len2, InitialPolder.BUNGALOW);

			placeHouseOnMatrix(newHouse);
			placeClearance(newHouse);
			this.newHouse = newHouse;
		}
		return placed;
	}

	boolean genFamHouse(int startX, int startY) {
		boolean placed = false;
		int len1 = InitialPolder.LEN1_FAM;
		int len2 = InitialPolder.LEN2_FAM;

		if(legalProperty(startX,startY,len1,len2,InitialPolder.MIN_CLEAR_FAM)) {
			placed = true;
		}

		if(placed) {
			House newHouse = new House(startX, startY, len1, len2, InitialPolder.FAMILY_HOME);

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
		for(int j =0; j<playgroundList.length; j++){
			totalValue = totalValue + InitialPolder.PRICE_PLAY;
		}
		return totalValue;
	}

	void replaceHouse(House house) {
		int len1 = house.len1;
		int len2 = house.len2;
		int x = house.x;
		int y = house.y;


		House newHouse = new House(x, y, len1, len2, house.type);
		placeHouseOnMatrix(newHouse);
		placeClearance(newHouse);
		this.newHouse = newHouse;

	}

	void placeHouse(House house) {
		boolean notPlaced = true;
		int numberOfRuns = 0;
		while(notPlaced && numberOfRuns < InitialPolder.MAX_TRIES) {
			//gen random coordinate

			int x = rand.nextInt(InitialPolder.POLDER_WIDTH);
			int y = rand.nextInt(InitialPolder.POLDER_HEIGHT);
			String lining1, lining2;
			if(Math.random() > 0.5) {
				lining1 = "vertical";
				lining2 = "horizontal";
			} else {
				lining1 = "horizontal";
				lining2 = "vertical";
			}

			if(house.type == InitialPolder.MANSION) {
				if(genMansion(x,y, lining1)) {
					notPlaced = false;
				} else if(genMansion(x,y, lining2)) {
					notPlaced = false;
				}
			}
			else if((house.type == InitialPolder.BUNGALOW)) {
				if(genBungalow(x,y, lining1)) {
					notPlaced = false;
				} else if (genBungalow(x,y, lining2)) {
					notPlaced = false;
				}
			}
			else if((house.type == InitialPolder.FAMILY_HOME)) {
				if(genFamHouse(x,y)) {
					notPlaced = false;
				}

			}
			numberOfRuns++;
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