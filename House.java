package amstelhaege;

public class House {
	/*
	 * Coordinates kan Linksboven rechtsonder of Alle Hoeken
	 */
	double value;
	String type; //0 for Mansion, 1 for Bungalow, 2 for Family House
	int x;
	int y;
	int len1;
	int len2;
	//Coordinates coordinates;
	
	House(){
	}
	
	House(int x, int y, int len1, int len2, String type){
		this.x = x;
		this.y = y;
		this.len1 = len1;
		this.len2 = len2;
		this.type = type;
//		this.coordinates = coordinates;
	}
	
	void setValue(double worth) {
		value = worth;
	}
	
	int clearance() {
		int clearance = 0;
		if(type.equals("mansion")) {
			clearance = 60;
		} else if (type.equals("bungalow")) {
			clearance = 30;
		} else if (type.equals("famHouse")) {
			clearance = 20;
		}
		return clearance;
	}
	
//	Coordinates getCoordinates(){
//		return coordinates;
//	}
}
