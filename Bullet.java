import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
public class Bullet implements Serializable {
	int bX;
	int bY;
	int bWidth,bHeight;
	int speed=5;
	Bullettype bullettype;
	int power;
	transient Image bimage;//在序列化保存的时候忽略这个图片
	public Bullet(int x,int y,int w,int h,Bullettype btype,int level){//根据子弹的位置设置构造函数
		super();
		  bX=x;
		  bY=y;
		  bWidth=w;
		  bHeight=h;
		  bullettype = btype;
		  speed=5+2*(level-1);
		  power=btype.power;
	}
	

    public Bullet(int x, int y, int w, int h, Bullettype btype, Controlplane controller) {
        super();
        bX = x;
        bY = y;
        bWidth = w;
        bHeight = h;
        bullettype = btype;
        power = bullettype.power + controller.baseDamage;
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
