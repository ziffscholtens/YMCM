package amstelhaege;

public class Water {

	int x;
	int y;
	int len1;
	int len2;
	//Coordinates coordinates;
	
	Water(){
	}
	
	Water(int x, int y, int len1, int len2){
		this.x = x;
		this.y = y;
		this.len1 = len1;
		this.len2 = len2;

	}
	
	Water copy() {
		return new Water(x,y,len1,len2);
	}

}
