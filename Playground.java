package amstelhaege;

public class Playground {

	int x;
	int y;
	int len1;
	int len2;
	//Coordinates coordinates;
	
	Playground(){
	}
	
	Playground(int x, int y, int len1, int len2){
		this.x = x;
		this.y = y;
		this.len1 = len1;
		this.len2 = len2;

	}
	
	Playground copy() {
		return new Playground(x,y,len1,len2);
	}
	
	
}