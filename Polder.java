package amstelhaege;

import java.util.Random;

public class Polder {
	// NOT IN USE IN THE CURRENT VERSION
	final int POLDER_WIDTH = 1700;
	final int POLDER_HEIGHT = 2000;

	final int TOTAL = 100;
	final double PERC_FAM = 0.5;
	final double PERC_BUNG = 0.3;
	final double PERC_MANS = 0.2;
	final static int MIN_CLEAR_FAM = 20;
	final static int MIN_CLEAR_BUNG = 30;
	final static int MIN_CLEAR_MANS = 60;
	final double PRICE_FAM = 285000;
	final double PRICE_BUNG = 399000;
	final double PRICE_MANS = 610000;
	final double PRICE_INC_FAM = 0.03;
	final double PRICE_INC_BUNG = 0.04;
	final double PRICE_INC_MANS = 0.06;


	final int HOUSE=1;
	final int WATER=2;
	final int CLEARANCE = 3;
	final int NOTHING=0;

	int placedNum = 0;

	int[][] world_matrix;
	int[][] init_environment;

	House[] houseList;

	Random rand = new Random();

	Polder(){
		//	int size=WORLD_SIZE;
		int height = POLDER_HEIGHT;
		int width = POLDER_WIDTH;
		world_matrix= new int[width][height];
		houseList = new House[TOTAL];
		placedNum = 0;
		//	pop=ORG_POP;
		//	gen=1;
		//	iterations=0;
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				world_matrix[i][j]= 0;
			}
		}
		generatePlaceHouse();
		printTotalValue();

		//	generateFood();
	}

	//	init_environment=copy_world(world_matrix);
	// Heuristic, check one house
// update


	int[][] copy_world(int[][] original){
		int[][] copy = new int[POLDER_WIDTH][POLDER_HEIGHT];
		for(int i = 0; i<POLDER_WIDTH; i++) {
			for(int j = 0; j<POLDER_HEIGHT; j++) {
				int object = original[i][j];
				copy[i][j] = object;
			}
		}
		return(copy);
	}

	void generatePlaceHouse() {
		int mansions = 0;
		int bungalows = 0;
		int fam = 0;
		while(placedNum < TOTAL) {
			//gen random coordinate

			int x = rand.nextInt(POLDER_WIDTH);
			int y = rand.nextInt(POLDER_HEIGHT);
			String lining1, lining2;
			if(Math.random() > 0.5) {
				lining1 = "vertical";
				lining2 = "horizontal";
			} else {
				lining1 = "horizontal";
				lining2 = "vertical";
			}

			if(mansions < TOTAL * PERC_MANS) {
				if(genMansion(x,y, lining1)) {
					placedNum ++;
					mansions++;
				} else if(genMansion(x,y, lining2)) {
					placedNum ++;
					mansions++;
				}
			}
			else if(bungalows < TOTAL * PERC_BUNG) {
				if(genBungalow(x,y, lining1)) {
					placedNum ++;
					bungalows++;
				} else if (genBungalow(x,y, lining2)) {
					placedNum ++;
					bungalows++;
				}
			}
			else if(fam < TOTAL * PERC_FAM) {
				if(genFamHouse(x,y)) {
					placedNum ++;
					fam++;
				}
				// IMPLEMENT PLACEMENT OF WATER
			}
		}

	}


	boolean outOfBounds(int x, int y) {
		return (x < 0 || x > POLDER_WIDTH-1 || y < 0 || y > POLDER_HEIGHT-1);
	}

	boolean notOnHouse(int x, int y) {
		if(outOfBounds(x,y)) {
			return false;
		} else {
			return (world_matrix[x][y] != HOUSE);
		}
	}

	boolean legalProperty(int startX, int startY, int len1, int len2) {
		for(int i = startX; i < len1+startX; i++) {
			for(int j = startY; j < len2+startY; j++) {
				if(!outOfBounds(i,j)) {
					if(world_matrix[i][j] != NOTHING) {
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
					world_matrix[i][j] = HOUSE;
				}
			}
		}
	}

	void placeClearance(House house) {
		for(int h = 0; h < house.clearance(); h++) {
			for(int k = house.x-h; k < house.x+h+house.len1; k++) {
				if(notOnHouse(k, house.y - h-1)) {
					world_matrix[k][house.y-h-1] = CLEARANCE;
				}
				if(notOnHouse(k, house.y+house.len2+h+1)) {
					world_matrix[k][house.y+house.len2+h+1] = CLEARANCE;
				}
			}

			for(int l = house.y-h; l < house.y+h+house.len2; l++) {
				if(notOnHouse(house.x-h-1, l)) {
					world_matrix[house.x-h-1][l] = CLEARANCE;
				}
				if(notOnHouse(house.x+house.len1+h, l)) {
					world_matrix[house.x+house.len1+h][l] = CLEARANCE;
				}
			}
		}
	}

	boolean genMansion(int startX, int startY, String lining) {
		boolean placed = false;
		int len1;
		int len2;

		if(lining.contains("vertical")) {
			len1 = 105;
			len2 = 110;
		} else {
			len1 = 110;
			len2 = 105;
		}

		if(legalProperty(startX,startY,len1,len2)) {
			placed = true;
		}

		if(placed) {
			House newHouse = new House(startX, startY, len1, len2, "mansion");

			placeHouseOnMatrix(newHouse);
			placeClearance(newHouse);

			houseList[placedNum] = newHouse;
		}
		return placed;
	}

	boolean genBungalow(int startX, int startY, String lining) {
		boolean placed = false;
		int len1;
		int len2;

		if(lining.contains("vertical")) {
			len1 = 75;
			len2 = 100;
		} else {
			len1 = 100;
			len2 = 75;
		}

		if(legalProperty(startX,startY,len1,len2)) {
			placed = true;
		}

		if(placed) {
			House newHouse = new House(startX, startY, len1, len2, "bungalow");

			placeHouseOnMatrix(newHouse);
			placeClearance(newHouse);

			houseList[placedNum] = newHouse;
		}
		return placed;
	}

	boolean genFamHouse(int startX, int startY) {
		boolean placed = false;
		int len1 = 80;
		int len2 = 80;

		if(legalProperty(startX,startY,len1,len2)) {
			placed = true;
		}

		if(placed) {
			House newHouse = new House(startX, startY, len1, len2, "famHouse");

			placeHouseOnMatrix(newHouse);
			placeClearance(newHouse);

			houseList[placedNum] = newHouse;
		}
		return placed;
	}

	boolean checkProperty(int startX, int startY, int len1, int len2) {
		// vertically outlined

		for(int i = startX; i < startX+len1; i++) {
			for(int j = startY; i < startY+len2; j++) {
				if(world_matrix[i][j] == HOUSE || world_matrix[i][j] == WATER || world_matrix[i][j] == CLEARANCE) {
					return false;
				}
			}

		}
		return true;
	}

	int countClearance(House house) {
		int clearance = 0;
		boolean clear = true;

		while(clear) {

			for(int k = house.x-clearance; k < house.x+clearance+house.len1; k++) {
				if(!outOfBounds(k, house.y-clearance-1)) {
					if(world_matrix[k][house.y-clearance-1] == HOUSE) {
						clear = false;
					}
				}
				if(!outOfBounds(k, house.y+house.len2+clearance+1)) {
					if(world_matrix[k][house.y+house.len2+clearance+1] == HOUSE) {
						clear = false;
					}
				}
			}

			for(int l = house.y-clearance; l < house.y+clearance+house.len2; l++) {
				if(!outOfBounds(house.x-clearance-1, l)) {
					if(world_matrix[house.x-clearance-1][l] == HOUSE) {
						clear = false;
					}
				}
				if(!outOfBounds(house.x+house.len1+clearance, l)) {
					if(world_matrix[house.x+house.len1+clearance][l] == HOUSE) {
						clear = false;
					}
				}
			}
			clearance ++;
		}

		return (int) (clearance-house.clearance())/10;
	}

	double getValue(House house) {
		double value = 0;
		double incWeight = 0;
		if (house.type.contains("mansion")) {
			value += PRICE_MANS;
			incWeight = PRICE_INC_MANS;
		} else if (house.type.contains("bungalow")) {
			value += PRICE_BUNG;
			incWeight = PRICE_INC_BUNG;
		} else if (house.type.contains("famHouse")) {
			value += PRICE_FAM;
			incWeight = PRICE_INC_FAM;
		}

		int clearance = countClearance(house);
		value = value + (clearance * (incWeight+1));

		house.setValue(value);
		return value;
	}

	void printTotalValue() {
		double totalValue = totalValue();
		System.out.printf("the total value of the project equals: %d milion", (int) totalValue/1000);
	}

	double totalValue() {
		double totalValue = 0;
		for(int i = 0; i < placedNum; i++) {
			totalValue = totalValue + getValue(houseList[i]);
		}
		return totalValue;
	}
	
	void printValues() {
		for(int i = 0; i < placedNum; i++) {
			System.out.println(getValue(houseList[i]));
		}
	}

}


