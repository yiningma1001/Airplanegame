import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;


public class Airplane implements Serializable //通过序列化一个对象，将其写入磁盘，以后在程序再次调用时重新恢复那个对象，就能圆满实现一种“持久”效果。
{
int pX,pY;//飞机的横纵坐标
int pWidth,pHeight;//飞机的宽度高度
int speed=1;//速度
int oil=100,life=100;//油量，生命值
int Xoffset=0;  //飞机横坐标偏移量
int intervel;//？是间隔interval？
int count=0;
int bulletnum=100;//子弹量
int fireLevel=1;//火力值
int eplane;
static Image eplane1 ;;
static Image eplane2 ; 
boolean controlling;
Controlplane controlplane;
Bullettype btype;
public Airplane(int x,int y,int w,int h,Controlplane control,Bullettype b_type){ //有参构造方法，构造我方飞机
	super();
  pX=x;
  pY=y;
  pWidth=w;
  pHeight=h;
  controlling=true;
  btype=b_type;
  controlplane=control;
}

public Airplane(int x,int y,int w,int h,Bullettype b_type,int level){ //有参构造方法,构造敌机飞机
	super();
  pX=x;
  pY=y;
  pWidth=w;
  pHeight=h;
  btype=b_type;
  speed=level;
  controlling=false;
}

public Airplane(int level){  //随机位置构造方法
	super();
  pX=getRandomIntNum(50,950);  //随机产生横坐标，并设置纵坐标、宽度、高度，敌机种类暂设为第一种；
  pY=50;                     
  pWidth=78;            
  pHeight=68;
  intervel=getRandomIntNum(0,6);
  eplane=1;  
  speed=level;
  controlling=false;
}
public boolean hit(Bullet b){  //定义“子弹击中”：子弹坐标在飞机左右横坐标之间，且子弹纵坐标在飞机上下坐标之间，即为击中，生命值减20
	if ((pX<b.bX) && (b.bX<pX+pWidth) && (pY<b.bY) && (b.bY<pY+pHeight)){
		if(b.bullettype.bulletFrom.equals("enemy") & controlling) {		
			life = life - b.power + controlplane.baseDefense;   
		}
	if(b.bullettype.bulletFrom.equals("control") & !controlling) {
		life-=b.power;     
	}
		return true;
	} else return false;
}

public boolean hit(Airplane p){    //定义“飞机碰撞”：飞机横坐标在敌机左右横坐标间。飞机纵坐标在敌机上下坐标间，飞机生命值和敌机生命值同时减20
	if ((pX-pWidth<p.pX) && (p.pX<pX+pWidth) && (pY<p.pY) && (p.pY<pY+pHeight)){
		life-=20;
		p.life-=20;
		return true;
	} else return false;
	
}
public boolean hit(Accessory a){    //定义“碰撞其他物品”：同上
	if ((pX<a.aX) && (a.aX<pX+pWidth) && (pY<a.aY) && (a.aY<pY+pHeight)){
		if(controlling) {
	       if (a.typeint==1) life+=100;     //碰撞到lives.gif，则生命值加100
	       if (a.typeint==2) bulletnum+=100;  //碰撞到box1.gif，则子弹量加100
	       if (a.typeint==3) oil+=100;   //碰撞到oil.gif，则油量加100
	       if (a.typeint==4) {   	   
	    	   pWidth*=0.9;        //碰撞到Invincible.gif，则宽度和高度均减小
	    	   pHeight*=0.9;
	       }
	       if (a.typeint==5) {     //碰撞到fireLevel.gif，则火力加2
	    	   fireLevel+=2;
	       }
	       if(a.typeint==6){     //��ײ��cleanscreen.gif��������rock����ʧ
	           Battlefield.clearnum += 1;
	       }
	       
	       if(a.typeint==7) {
	    	   controlplane.tempDefense = controlplane.baseDefense;
               controlplane.baseDefense = 20;
               Timer timer = new Timer(true);
               TimerTask task = new TimerTask() {
                   public void run() {
                       controlplane.baseDefense = controlplane.tempDefense;
                   }
               };
               timer.schedule(task, 5000);
	       }
		}
	       return true;
		} else return false;
}

public boolean hit(Rock rock){	   //定义“撞击rock”
	if ((pX<rock.x) && (rock.x<pX+pWidth) && (pY<rock.y) && (rock.y<pY+pHeight)){ //同上，且生命值-1
		life=-1;  
		return true;
		} else return false;
}
public void fly(){   //定义“飞”，即敌机的行动轨迹
    count++;
	 if (pY%200==0) {  //每当飞机纵坐标到了200的倍数，飞机偏移量从-2、-1、0中产生，其他时候遵循下面的if
  	  Xoffset=(getRandomIntNum(0, 3)-2);    
    }
    if  (pX<50)  Xoffset=1; //飞机横坐标小于50像素，偏移量设为1；大于950像素，偏移量设为-1；飞机向左或右偏移
    if  (pX>950)  Xoffset=-1;
	 pX+=Xoffset;	
  if (count>=intervel){         //因为interval是在0-5间随机产生的数字，count不断增加，当大于interval时，飞机纵坐标增加，并将count重新置于0；不大于interval时，纵坐标不变
	     if (pY>500) eplane=2;   //如果飞机纵坐标大于500，下一个要出来的敌机是第一种；小于50，则出来第二种
	     if (pY<50) eplane=1;
	     if ((pY>500)||(pY<50)) speed=-speed;   //飞机纵坐标超过500或小于50，飞机前进方向调转
     pY+=speed;                 //
     count=0;
     }
}

public void moveToTop(){
	
}
public void moveToBottom(){
	
}
public void moveToleft(){
	
}
public void moveToRihgt(){
	
}
public int getRandomIntNum(int a, int b)
{
  Random random = new Random();
	
  int c = random.nextInt();
//这里用到了Random里的nextInt()方法，这个方法会随机产生一个 int 型的数；
  if(c<0)
  {
    c = -c ;
  }

  int d = ((c %(b-a)) + a + 1);

//这里是让变量d变成a和b之中的数， % 是取余运算，请认真的读者自己算一下；
  
return d;

}

}
