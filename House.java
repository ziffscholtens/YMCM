package amstelhaege;

public class House {
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

	}
	
	void setValue(double worth) {
		value = worth;
	}
	
	int minClearance() {
		int clearance = 0;
		if(type == InitialPolder.MANSION) {
			clearance = InitialPolder.MIN_CLEAR_MANS;
		} else if (type == InitialPolder.BUNGALOW) {
			clearance = InitialPolder.MIN_CLEAR_BUNG;
		} else if (type == InitialPolder.FAMILY_HOME) {
			clearance = InitialPolder.MIN_CLEAR_FAM;
		}
		return clearance;
	}
	
	House copy() {
		return new House(x,y,len1,len2,type);
	}
	
	void setX(int newX) {
		this.x = newX;
	}
	
	void setY(int newY) {
		this.y = newY;
	}
	
}
