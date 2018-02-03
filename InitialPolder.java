package amstelhaege;

import java.util.Random;

public class InitialPolder {
	final static int POLDER_WIDTH = 340;
	final static int POLDER_HEIGHT = 400;

	final static int TOTAL = 100;
	final static double PERC_FAM = 0.5;
	final static double PERC_BUNG = 0.3;
	final static double PERC_MANS = 0.2;
	final static double PERC_WATER = 0.2;
	final static int MIN_CLEAR_FAM = 4;
	final static int MIN_CLEAR_BUNG = 6;
	final static int MIN_CLEAR_MANS = 12;
	final static int MANSION = 0;
	final static int BUNGALOW = 1;
	final static int FAMILY_HOME = 2;
	final static double PRICE_FAM = 285000;
	final static double PRICE_BUNG = 399000;
	final static double PRICE_MANS = 610000;
	final static double PRICE_PLAY =-500000;
	final static double PRICE_INC_FAM = 0.03;
	final static double PRICE_INC_BUNG = 0.04;
	final static double PRICE_INC_MANS = 0.06;
	final static int LEN1_FAM = 16;
	final static int LEN2_FAM = 16;
	final static int LEN1_BUNG = 20;
	final static int LEN2_BUNG = 15;
	final static int LEN1_MANS = 22;
	final static int LEN2_MANS = 21;
	final static int LEN1_PLAY = 40;
	final static int LEN2_PLAY = 60;

	final static int HOUSE=1;
	final static int WATER=2;
	final static int CLEARANCE = 3;
	final static int PLAYGROUND = 4;
	final static int NOTHING=0;

	final static int MAX_TRIES = 100000;

	int numberOfPlaygrounds = 0;
	int placedNum = 0;
	double totalValue = 0;


	int[][] world_matrix;
	int[][] init_environment;

	House[] houseList;
	Water[] waterList;
	Playground[] playgroundList;

	Random rand = new Random();

	InitialPolder(){
		//creates a new polder, on which the heuristic can be applied
		//generate and place the houses
		generatePlan();
		//calculate the total value
		totalValue = totalValue();
		System.out.printf("The initial value = %.2f \n", totalValue);
		//uncomment next line(s) to apply heuristic
		world_matrix = hillClimberHeuristic();
		simulatedAnnealingHeuristic();
		//print the highest ever found value
		renewClearence();
		printTotalValue();

	}

	void generatePlan() {
		int height = POLDER_HEIGHT;
		int width = POLDER_WIDTH;
		world_matrix= new int[width][height];
		houseList = new House[TOTAL];
		placedNum = 0;

		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				world_matrix[i][j]= NOTHING;
			}
		}
		placingWater();
		placingPlayground();
		generatePlaceHouse();
	}

	void placingPlayground() {
		int playgrounds = 0;
		Playground[] tempPlayground = new Playground[1000];
		int wantedPlaygrounds = 2; //between 1 and 4
		int x1 = 100;
		int y1 = 100;
		int x2 = 180;
		int y2 = 260;
		int x3 = 140;
		int y3 = 70;
		int x4 = 140;
		int y4 = 290;
		int lenx1 = LEN2_PLAY;
		int lenx2 = LEN2_PLAY;
		int lenx3 = LEN2_PLAY;
		int lenx4 = LEN2_PLAY;

		int leny1 = LEN1_PLAY;
		int leny2 = LEN1_PLAY;
		int leny3 = LEN1_PLAY;
		int leny4 = LEN1_PLAY;

		if (wantedPlaygrounds>0){
			if(legalPlaygroundPlace(x1,y1,lenx1,leny1)) {
				tempPlayground[playgrounds] = new Playground(x1,y1,lenx1,leny1);;
				placePlayground(x1,y1,lenx1,leny1);
				playgrounds ++;
			}
		}
		if (wantedPlaygrounds>1){
			if(legalPlaygroundPlace(x2,y2,lenx2,leny2)) {
				tempPlayground[playgrounds] = new Playground(x2,y2,lenx2,leny2);;
				placePlayground(x2,y2,lenx2,leny2);
				playgrounds ++;
			}
		}
		if (wantedPlaygrounds>2){
			if(legalPlaygroundPlace(x3,y3,lenx3,leny3)) {
				tempPlayground[playgrounds] = new Playground(x3,y3,lenx3,leny3);;
				placePlayground(x3,y3,lenx3,leny3);
				playgrounds ++;
			}
		}
		if (wantedPlaygrounds>3){
			if(legalPlaygroundPlace(x4,y4,lenx4,leny4)) {
				tempPlayground[playgrounds] = new Playground(x4,y4,lenx4,leny4);;
				placePlayground(x4,y4,lenx4,leny4);
				playgrounds ++;
			}
		}
		playgroundList = new Playground[playgrounds];
		for(int i =0; i< playgrounds;i++){
			playgroundList[i] = tempPlayground[i];
		}
	}

	int[][] hillClimberHeuristic(){
		System.out.println("The Hillclimber is running");
		int numberOfNoChanges = 0;
		HillClimberHeuristic heuristic = null;
		while(numberOfNoChanges < 100){
			heuristic = new HillClimberHeuristic(world_matrix, houseList, totalValue, waterList, playgroundList);
			world_matrix = copyWorld(heuristic.world_matrix);
			houseList = copyHouseList(heuristic.houseList);
			if(totalValue>=heuristic.totalValue){
				numberOfNoChanges++;
			}
			else{
				numberOfNoChanges=0;
			}
			totalValue = heuristic.totalValue;
		}

		System.out.printf("Max is %.2f after hillc \n", totalValue);
		return heuristic.world_matrix;
	}

	void simulatedAnnealingHeuristic() {
		System.out.println("The Simulated Annealing is running");
		int numberOfNoChanges = 0;
		//		int runs = 0;
		// heuristic is local optimum
		while(numberOfNoChanges < 10000){
			SimulatedAnnealing heuristic = new SimulatedAnnealing(world_matrix, houseList, totalValue, waterList, playgroundList);
			world_matrix = copyWorld(heuristic.world_matrix);
			houseList = copyHouseList(heuristic.houseList);
			totalValue = heuristic.totalValue;
			if(totalValue>=heuristic.totalValue){
				numberOfNoChanges++;
			}
			else{
				numberOfNoChanges=0;
			}
			
			totalValue = heuristic.totalValue;
		}
		System.out.printf("Max is %.2f after sim \n", totalValue);
	}
	House[] copyHouseList(House[] original){
		//copy a houselist
		House[] copy = new House[original.length];
		for(int i = 0; i<original.length; i++) {
			House object = original[i];
			copy[i] = object;
		}
		return(copy);

	}
	int[][] copyWorld(int[][] original){
		//copy a matrix
		int[][] copy = new int[InitialPolder.POLDER_WIDTH][InitialPolder.POLDER_HEIGHT];
		for(int i = 0; i<InitialPolder.POLDER_WIDTH; i++) {
			for(int j = 0; j<InitialPolder.POLDER_HEIGHT; j++) {
				int object = original[i][j];
				copy[i][j] = object;
			}
		}
		return(copy);
	}

	void generatePlaceWater() {
		int bodies = 0;
		double waterBlocks = 0.0;
		double neededWater = POLDER_WIDTH*POLDER_HEIGHT*PERC_WATER;
		int numberOfTries = 0;
		Water[] tempWaterList = new Water[1000];

		while(waterBlocks < neededWater && numberOfTries < InitialPolder.MAX_TRIES) {
			int x = rand.nextInt(POLDER_WIDTH);
			int y = rand.nextInt(POLDER_HEIGHT);
			int size = rand.nextInt(10);
			size+=50;

			if(Math.random() > .5) {
				int xRat = rand.nextInt(4);
				int len1 = size*xRat;
				int len2 = size;

				if(legalWaterplace(x,y,len1,len2)) {
					tempWaterList[bodies] = new Water(x,y,len1,len2);
					placeWater(x,y,len1,len2);
					waterBlocks += (len1*len2);
					bodies ++;
				}
			} else {
				int yRat = rand.nextInt(4);
				int len1 = size;
				int len2 = size*yRat;

				if(legalWaterplace(x,y,len1,len2)) {
					tempWaterList[bodies] = new Water(x,y,len1,len2);;
					placeWater(x,y,len1,len2);
					waterBlocks += (len1*len2);
					bodies ++;
				}
			}
		}
		if(bodies > 4) {
			for(int m=0;m<POLDER_WIDTH;m++){
				for(int n=0;n<POLDER_HEIGHT;n++){
					world_matrix[m][n]= NOTHING;
					bodies =0;
				}
			}
			generatePlaceWater();
		}
		else{
			waterList = new Water[bodies];
			for(int i =0; i< bodies;i++){
				waterList[i] = tempWaterList[i];
			}
		}
	}

	void placingWater(){
		double neededWater = POLDER_WIDTH*POLDER_HEIGHT*PERC_WATER;
		int bodies = 0;
		int placedWater=0;
		Water[] tempWaterList = new Water[1000];
		int wantedWater = 2; //between 1 and 4
		int x1 = 259;
		int y1 = 0;

		int x2 = 0;
		int y2 = 229;

		int x3 = 0;
		int y3 = 314;

		int x4 = 256;
		int y4 = 314;
		int lenx1 = 80;
		int lenx2 = 80;
		int lenx3 = 83;
		int lenx4 = 83;

		int leny1 = 170;
		int leny2 = 170;
		int leny3 = 83;
		int leny4 = 83;
		if (wantedWater>0){
			if(legalWaterplace(x1,y1,lenx1,leny1)) {
				tempWaterList[bodies] = new Water(x1,y1,lenx1,leny1);;
				placeWater(x1,y1,lenx1,leny1);
				placedWater += (lenx1*leny1);
				bodies ++;
			}
		}
		if (wantedWater>1){
			if(legalWaterplace(x2,y2,lenx2,leny2)) {
				tempWaterList[bodies] = new Water(x2,y2,lenx2,leny2);;
				placeWater(x2,y2,lenx2,leny2);
				placedWater += (lenx2*leny2);
				bodies ++;
			}
		}
		if (wantedWater>2){
			if(legalWaterplace(x3,y3,lenx3,leny3)) {
				tempWaterList[bodies] = new Water(x3,y3,lenx3,leny3);;
				placeWater(x3,y3,lenx3,leny3);
				placedWater += (lenx3*leny3);
				bodies ++;
			}
		}
		if (wantedWater>3){
			if(legalWaterplace(x4,y4,lenx4,leny4)) {
				tempWaterList[bodies] = new Water(x4,y4,lenx4,leny4);;
				placeWater(x4,y4,lenx4,leny4);
				placedWater += (lenx4*leny4);
				bodies ++;
			}
		}
		if (placedWater<neededWater){
			System.out.println("more water needed");
		}

		waterList = new Water[bodies];
		for(int i =0; i< bodies;i++){
			waterList[i] = tempWaterList[i];
		}
	}

	void renewClearence(){
		for(int i=0; i<houseList.length; i++){
			placeClearance(houseList[i]);
		}
	}

	boolean legalPlaygroundPlace(int x, int y, int len1, int len2) {
		for(int i = x; i < len1+x; i++) {
			for(int j = y; j < len2+y; j++) {
				if(!playgroundCheck(i,j)) {
					if(world_matrix[i][j] != NOTHING && world_matrix[i][j] != WATER) { 
						return false;
					}
				} else {
					return false;
				}
			}
		}
		return true;
	}

	boolean legalWaterplace(int x, int y, int len1, int len2) {
		for(int i = x; i < len1+x; i++) {
			for(int j = y; j < len2+y; j++) {
				if(!waterCheck(i,j)) {
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

	void placeWater(int x, int y, int len1, int len2) {
		for(int i = x; i < x+len1 ; i++) {
			for(int j = y; j < y+len2; j++) {
				if(!waterCheck(i,j)) {
					world_matrix[i][j] = WATER;		
				}
			}
		}
	}

	void placePlayground(int x, int y, int len1, int len2) {
		for(int i = x; i < x+len1 ; i++) {
			for(int j = y; j < y+len2; j++) {
				world_matrix[i][j] = PLAYGROUND;					
			}
		}
	}

	void generatePlaceHouse() {
		int mansions = 0;
		int bungalows = 0;
		int fam = 0;
		int tries = 0;
		while(placedNum < TOTAL && tries < MAX_TRIES) {
			//gen random coordinate

			int x = rand.nextInt(POLDER_WIDTH);
			int y = rand.nextInt(POLDER_HEIGHT);
			String lining1, lining2;
			//gen random lining of the house
			if(Math.random() > 0.5) {
				lining1 = "vertical";
				lining2 = "horizontal";
			} else {
				lining1 = "horizontal";
				lining2 = "vertical";
			}

			//check if there can still be placed mansions
			if(mansions < TOTAL * PERC_MANS) {
				//check if the generated coordinate is suiting for a mansion and place it if so
				if(genMansion(x,y, lining1)) {
					placedNum ++;
					mansions++;
					tries = 0;
				} else if(genMansion(x,y, lining2)) {
					placedNum ++;
					mansions++;
					tries = 0;
				}
			}
			//do the same for other housetypes
			else if(bungalows < TOTAL * PERC_BUNG) {
				if(genBungalow(x,y, lining1)) {
					placedNum ++;
					bungalows++;
					tries = 0;
				} else if (genBungalow(x,y, lining2)) {
					placedNum ++;
					bungalows++;
					tries = 0;
				}
			}
			else if(fam < TOTAL * PERC_FAM) {
				if(genFamHouse(x,y)) {
					placedNum ++;
					fam++;
					tries = 0;
				} 
			}
			tries++;
		}
		if(tries >= MAX_TRIES) {
			for(int i=0;i<POLDER_WIDTH;i++){
				for(int j=0;j<POLDER_HEIGHT;j++){
					world_matrix[i][j]= NOTHING;
				}
			}
			//generate and place the houses
			houseList = null;
			waterList = null;
			playgroundList = null;
			generatePlan();
		}

	}

	boolean outOfBounds(int x, int y) {
		//check if the coordinate is out of bounds
		return (x < 0 || x > POLDER_WIDTH-1 || y < 0 || y > POLDER_HEIGHT-1);
	}

	boolean notOnHouse(int x, int y) {
		//check if the coordinate contains a house
		if(outOfBounds(x,y)) {
			return false;
		} else {
			return (world_matrix[x][y] != HOUSE || world_matrix[x][y] != PLAYGROUND);
		}
	}

	boolean waterCheck(int x, int y) {
		return (x < 0 || x > POLDER_WIDTH-1 || y < 0 || y > POLDER_HEIGHT-1 || world_matrix[x][y] == WATER);	
	}

	boolean playgroundCheck(int x, int y) {
		return (x < 0 || x > POLDER_WIDTH-1 || y < 0 || y > POLDER_HEIGHT-1 || world_matrix[x][y] == PLAYGROUND);	
	}

	boolean legalProperty(int startX, int startY, int len1, int len2, int minClearance) {
		//check if a house can be placed on the given spot
		for(int i = startX; i < len1+startX; i++) {
			for(int j = startY; j < len2+startY; j++) {
				if(!outOfBounds(i,j) && !clearanceConflict(startX, startY, len1, len2, minClearance)) {
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

	boolean clearanceConflict(int startX, int startY, int len1, int len2, int minClearance) {
		for(int i = startX-minClearance; i < len1+startX+ minClearance; i++) {
			for(int j = startY-minClearance; j < len2+startY+minClearance; j++) {
				if(!outOfBounds(i,j)) {
					if(world_matrix[i][j] == HOUSE || world_matrix[i][j] == PLAYGROUND) {
						return true;
					} 
				}
			}
		}
		return false;
	}


	void placeHouseOnMatrix(House house) {
		//place the house on the matrix
		for(int i = house.x; i < house.len1+house.x; i++) {
			for(int j = house.y; j < house.len2+house.y; j++) {
				if(!outOfBounds(i,j)) {
					world_matrix[i][j] = HOUSE;
				}
			}
		}
	}

	void placeClearance(House house) {
		//place the clearance of the house on the matrix
		for(int h = 0; h < house.minClearance(); h++) {
			for(int k = house.x-h; k < house.x+h+house.len1; k++) {
				if(notOnHouse(k, house.y - h-1)) {
					world_matrix[k][house.y-h-1] = CLEARANCE;
				}
				if(notOnHouse(k, house.y+house.len2+h)) {
					world_matrix[k][house.y+house.len2+h] = CLEARANCE;
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
		//check if placement is possible and if so, place the house and its clearance.
		boolean placed = false;
		int len1;
		int len2;

		if(lining.contains("vertical")) {
			len1 = LEN1_MANS;
			len2 = LEN2_MANS;
		} else {
			len1 = LEN2_MANS;
			len2 = LEN1_MANS;
		}

		if(legalProperty(startX,startY,len1,len2,MIN_CLEAR_MANS)) {
			placed = true;
		}

		if(placed) {
			House newHouse = new House(startX, startY, len1, len2, MANSION);

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
			len1 = LEN1_BUNG;
			len2 = LEN2_BUNG;
		} else {
			len1 = LEN2_BUNG;
			len2 = LEN1_BUNG;
		}

		if(legalProperty(startX,startY,len1,len2,MIN_CLEAR_BUNG)) {
			placed = true;
		}

		if(placed) {
			House newHouse = new House(startX, startY, len1, len2, BUNGALOW);

			placeHouseOnMatrix(newHouse);
			placeClearance(newHouse);

			houseList[placedNum] = newHouse;
		}
		return placed;
	}

	boolean genFamHouse(int startX, int startY) {
		//this type of house is a square so there is no lining
		boolean placed = false;
		int len1 = LEN1_FAM;
		int len2 = LEN2_FAM;

		if(legalProperty(startX,startY,len1,len2,MIN_CLEAR_FAM)) {
			placed = true;
		}

		if(placed) {
			House newHouse = new House(startX, startY, len1, len2, FAMILY_HOME);

			placeHouseOnMatrix(newHouse);
			placeClearance(newHouse);

			houseList[placedNum] = newHouse;
		}
		return placed;
	}

	int countClearance(House house) {
		// count the clearance on top of the minimal clearance
		int clearance = 0;
		boolean clear = true;

		while(clear) {

			for(int k = house.x-clearance; k < house.x+clearance+house.len1; k++) {
				if(!outOfBounds(k, house.y-clearance-1)) {
					if(world_matrix[k][house.y-clearance-1] == HOUSE || world_matrix[k][house.y-clearance-1] == PLAYGROUND) {
						clear = false;
					}
				}
				if(!outOfBounds(k, house.y+house.len2+clearance+1)) {
					if(world_matrix[k][house.y+house.len2+clearance] == HOUSE || world_matrix[k][house.y+house.len2+clearance] == PLAYGROUND) {
						clear = false;
					}
				}
			}

			for(int l = house.y-clearance; l < house.y+clearance+house.len2; l++) {
				if(!outOfBounds(house.x-clearance-1, l)) {
					if(world_matrix[house.x-clearance-1][l] == HOUSE || world_matrix[house.x-clearance-1][l] == PLAYGROUND) {
						clear = false;
					}
				}
				if(!outOfBounds(house.x+house.len1+clearance, l)) {
					if(world_matrix[house.x+house.len1+clearance][l] == HOUSE || world_matrix[house.x+house.len1+clearance][l] == PLAYGROUND) {
						clear = false;
					}
				}
			}
			clearance ++;
		}

		return (int) (clearance-house.minClearance())/2;
	}

	double getValue(House house) {
		//calculate the value of the house
		double value = 0;
		double incWeight = 0;
		if (house.type == MANSION) {
			value += PRICE_MANS;
			incWeight = PRICE_INC_MANS;
		} else if (house.type == BUNGALOW) {
			value += PRICE_BUNG;
			incWeight = PRICE_INC_BUNG;
		} else if (house.type == FAMILY_HOME) {
			value += PRICE_FAM;
			incWeight = PRICE_INC_FAM;
		}

		int clearance = countClearance(house);
		value = value * (clearance * (incWeight)+1);
		house.setValue(value);
		return value;
	}

	void printTotalValue() {
		//System.out.printf("the total value of the project equals: €%.2f milion\n", totalValue/1000000);
	}

	double totalValue() {
		// calculate the total value
		double totalValue = 0;
		for(int i = 0; i < placedNum; i++) {
			totalValue = totalValue + getValue(houseList[i]);
		}
		for(int j =0; j<playgroundList.length; j++){
			totalValue = totalValue + PRICE_PLAY;
		}
		return totalValue;
	}

	void printValues() {
		for(int i = 0; i < placedNum; i++) {
			System.out.println(getValue(houseList[i]));
		}
	}
	//	int[][] iterateHillc() {
	//		int i = 0;
	//		HillClimberHeuristic heuristic = new HillClimberHeuristic(world_matrix, houseList, totalValue, waterList);
	//		heuristic.iterate();
	//		world_matrix = copyWorld(heuristic.world_matrix);
	//		houseList = copyHouseList(heuristic.houseList);
	//		totalValue = heuristic.totalValue;
	//		return world_matrix;
	//	}
	//	
	//	int[][] iterateSimA() {
	//		return world_matrix;
	//	}

}