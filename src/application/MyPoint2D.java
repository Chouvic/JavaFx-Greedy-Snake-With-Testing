package application;

/*This is a simple class to contain two numbers as the coordinate
* It can set/get/move the number 
*/
public class MyPoint2D {
	private double x;
	private double y;
	
	MyPoint2D(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	double getX(){
		return x;
	}
	
	double getY() {
		return y;
	}
	
	void setX(double x) {
		this.x = x;
	}
	
	void setY(double y) {
		this.y = y;
	}
	
	void moveX(double dis) {
		 x += dis;
	}
	
	void moveY(double dis) {
		y += dis;
	}
	
	void moveXY(double disX, double disY) {
		x += disX;
		y += disY;
	}
	
}
