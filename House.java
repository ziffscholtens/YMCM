package amstelhaege;

public class House {
	/*
	 * Coordinates kan Linksboven rechtsonder of Alle Hoeken
	 */
	double value;
	int type; //0 for Mansion, 1 for Bungalow, 2 for Family House
	int x;
	int y;
	int len1;
	int len2;
	
	
	House(){
	}
	
	House(int x, int y, int len1, int len2, int type){
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
	
	double clearance() {
		double clearance = 0;
		if(type == InitialPolder.MANSION) {
			clearance = InitialPolder.MIN_CLEAR_MANS;
		} else if (type == InitialPolder.BUNGALOW) {
			clearance = InitialPolder.MIN_CLEAR_BUNG;
		} else if (type == InitialPolder.FAMILY_HOME) {
			clearance = InitialPolder.MIN_CLEAR_FAM;
		}
		return clearance;
	}
	
//	Coordinates getCoordinates(){
//		return coordinates;
//	}
}
