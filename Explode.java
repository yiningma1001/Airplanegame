import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
public class Explode implements Serializable  {
	int eX,eY;
	int eWidth,eHeight;
	int life=10;
	transient Image eimage;
	public Explode(int x,int y){
		super();//在子类的构造方法的第一行，必须是第一行调用super()，这样就调用了父类的构造方法，如果父类有多个不同的构造方法，则根据调用super()时传入的参数调用相应的父类构造方法。
		  eX=x;
		  eY=y;
	}
	public void hit(){
		
	}
	public void moveToTop(){
		
	}
	public void moveToBottom(){
		
	}
	public void moveToleft(){
		
	}
	public void moveToRihgt(){
		
	}
}
