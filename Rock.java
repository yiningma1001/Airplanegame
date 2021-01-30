import java.awt.Image;
import java.io.Serializable;


public class Rock implements Serializable {
	int x,y;
	int width,height;
	int speed=1;
	transient Image image;
	public Rock(int x,int y,int w,int h){
		super();
		  this.x=x;
		  this.y=y;
		  width=w;
		  height=h;
	}

}
