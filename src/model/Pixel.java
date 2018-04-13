package model;

public class Pixel {
	public int x;
	public int y;
	public boolean sign;
	public int r;
	public int g;
	public int b;
	
	public Pixel(int x, int y, boolean sign) {
		super();
		this.x = x;
		this.y = y;
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "P[x=" + x + ",y=" + y + ",sign=" + sign + "]";
	}
	

}
