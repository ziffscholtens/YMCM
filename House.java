package Heuristics;

public class House {
	/*
	 * Coordinates kan Linksboven rechtsonder of Alle Hoeken
	 */
	int value;
	int type;
	Coordinates coordinates;
	
	House(){
	}
	
	House(int value, int type, Coordinates coordinates){
		this.value = value;
		this.type = type;
		this.coordinates = coordinates;
	}
	
	int getValue(){
		return value;
	}
	
	int getType(){
		return type;
	}
	
	Coordinates getCoordinates(){
		return coordinates;
	}
}
