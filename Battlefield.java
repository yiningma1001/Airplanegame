import java.awt.*;//监听器类
import java.awt.image.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.applet.*;
import java.net.*;
import javax.swing.*;
//两个线程，一个负责画，一个负责展示
class Flag {
	int f1 = 0, f2 = 0;
	public Flag() { }
	public synchronized void putf1begin() {//synchronized表示锁，只允许一个线程运行
		while (f1 == 1)
			try { wait(); } catch (Exception e) { }//信号量（f1，f2）和锁加起来才能保证并发程序稳定和逻辑正确
	}
	public synchronized void putf1end() {
		f1 = 1;
		notifyAll();
	}
	public synchronized void getf1begin() {
		while (f1 == 0)
			try { wait(); } catch (Exception e) { }
	}
	public synchronized void getf1end() {
		f1 = 0;
		notifyAll();
	}
	public synchronized void putf2begin() {
		while (f2 == 1)
			try { wait(); } catch (Exception e) { }
	}
	public synchronized void putf2end() {
		f2 = 1;
		notifyAll();
	}
	public synchronized void getf2begin() {
		while (f2 == 0)
			try { wait(); } catch (Exception e) { }
	}
	public synchronized void getf2end() {
		f2 = 0;
		notifyAll();
	}
}

public class Battlefield extends JFrame {
	int level;
	Image OffScreen1, OffScreen2, O2;
	Graphics2D drawOffScreen1, drawOffScreen2, g;
	Image myplane, eplane1, eplane2, protectplane,bullet,controlbullet, explode, backgroud, a1, a2, a3,
			a4, a5, a6, a7,gameoverimage, winimage, rockImage,tempplane,end;
	int key;
	Airplane Controlplane;
	ArrayList bulletsList;
	static CopyOnWriteArrayList<Airplane> planeList;
	ArrayList explodeList;
	ArrayList accessoryList;//附件列表
	static CopyOnWriteArrayList<Rock> rockList;//是ArrayList的线程安全版本，在有写操作的时候会copy一份数据，然后写完再设置成新的数据。
	TextField t1, t2, t3, t4, t5,t6,t7;
	Panel p1, p2;
	Button start, save, load,pause,stoppause;
	Timer timer, timer2, timer3,timer4;
	Drawer d1;
	Displayer d2;
	Backgroudmusic m1;
	Scenemusic m2;
	int delay = 1000;
	float backy = 638;
	boolean fire = false;
	boolean goon = true;
	int gameover = 0;
	boolean hasAccessory = false;
	boolean addplane = false;
	Controlplane controlplane;
	Bullettype controltype,enemytype;
	int score=0;
	boolean showstory=false;
	static int clearnum;

	Flag flag;

	////////////////////////////改了位置
//	class Startaction implements ActionListener {
//		public void actionPerformed(ActionEvent event) {
//			level=0;
////			while(level<1||level>10){
//			try{
//				JFrame frame = new JFrame("Level Choose");
//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //关闭窗口
//				level = Integer.parseInt(JOptionPane.showInputDialog(frame, "Please choose the game level：1-10"));
//				goon = true;
//				gameover = 0;
//				start.disable();
//				gamebegin();
//			}catch(Exception e){
////					continue;
//			}
////			}
////			goon = true;
////			gameover = 0;
////			start.disable();
////			gamebegin();
//
//		}
//	}

	//////////////////////////////////////////////////////////////////战斗背景
	public Battlefield() {
		setTitle("～～～～～～～～～～～～ 欢迎收看我们组的面向对象作业：雷霆战机增强版！！～～～～～～～～～～～～～～～～～～～");
//		setSize( 1000, 1900 );
		OffScreen1 = new BufferedImage(1000, 900, BufferedImage.TYPE_INT_RGB);//两个缓冲区（即画板）
		drawOffScreen1 = (Graphics2D) OffScreen1.getGraphics();//细节实现方法，抓手
		OffScreen2 = new BufferedImage(1000, 900, BufferedImage.TYPE_INT_RGB);//两个缓冲区（即画板）
		drawOffScreen2 = (Graphics2D) OffScreen2.getGraphics();
		flag = new Flag();
		if(level<=4) {
		protectplane=getToolkit().getImage("Airplanes/protect-plane.png");
		}
		if(level>4 && level<=6) {
			protectplane=getToolkit().getImage("Airplanes/protect-plane1.png");
			}
		if(level>6) {
			protectplane=getToolkit().getImage("Airplanes/protect-plane2.png");
			}
		a1 = getToolkit().getImage("accessory/lives.gif");//几个附件，都需要除了图标外都是透明的，图片需要透明化
		a2 = getToolkit().getImage("accessory/box1.gif");
		a3 = getToolkit().getImage("accessory/oil.gif");
		a4 = getToolkit().getImage("accessory/Invincible.gif");
		a5 = getToolkit().getImage("accessory/fireLevel.gif");
		a6 = getToolkit().getImage("accessory/cleanscreen.gif");
		a7 = getToolkit().getImage("accessory/protect.png");
		rockImage = getToolkit().getImage("rock/Small_Rock_Icon.png");
		end = getToolkit().getImage("story/cover.jpg");
		Accessory.aimage1 = a1;//形成空列表（容器）
		Accessory.aimage2 = a2;
		Accessory.aimage3 = a3;
		Accessory.aimage4 = a4;
		Accessory.aimage5 = a5;
		Accessory.aimage6 = a6;
		Accessory.aimage7 = a7;
		Airplane.eplane1 = eplane1;
		Airplane.eplane2 = eplane2;

		explode = getToolkit().getImage("Bullets/explode.gif");
		//backgroud = getToolkit().getImage("Backgrounds/sandroad.jpg");
		gameoverimage = getToolkit().getImage("accessory/gameover.gif");
		winimage = getToolkit().getImage("accessory/win.gif");

		planeList = new CopyOnWriteArrayList<Airplane>();
		bulletsList = new ArrayList();
		explodeList = new ArrayList();
		accessoryList = new ArrayList();
		rockList = new CopyOnWriteArrayList<Rock>();
		controlplane=new Controlplane();
//		controltype=new Bullettype(20,"control",controlbullet);
//		enemytype=new Bullettype(20,level,bullet);
	}

	public void gameperpare() {
		Controlplane = new Airplane(500, 750, 80, 66,controlplane,controltype);//根据长宽等。。产生新飞机
		////////////////////////////////////////////////飞机速度
		p2.addKeyListener(new MultiKeyPressListener());//在panel中才能加入监听器，p2活动时，按下按钮就调用就会执行，加入panel的消息队列
		m2 = new Scenemusic();//产生场景音乐
	}
//新增部分？
	public void gamebegin() {
		// 初始化
		TimerTask task = new TimerTask() {
			public void run() {
				hasAccessory = true;
				m2.beepclip.loop();
			}
		};
		timer = new Timer();
		timer.schedule(task, 0, delay);

		TimerTask task2 = new TimerTask() {
			public void run() {
				Controlplane.oil -= 1;
				t3.setText(Controlplane.oil + "");
			}
		};
		timer2 = new Timer();
		timer2.schedule(task2, 3000, 3000);

		TimerTask task3 = new TimerTask() {
			public void run() {
				addplane = true;
			}
		};
		timer3 = new Timer();
		timer3.schedule(task3, 2000, 8000);

		//////////控制初始状态与选择level的关系
		Controlplane.pX = 480;
		Controlplane.pY = 600;
		Controlplane.life = 100 +50* (level-1);
		Controlplane.bulletnum = 100 * level;
		Controlplane.oil = 100 * level;
		Controlplane.speed = 15;

		switch(level){
			case 1:
				backgroud = getToolkit().getImage("Backgrounds/bg1.png");
				myplane = getToolkit().getImage("Airplanes/myplane1.png");
				eplane1 = getToolkit().getImage("Airplanes/plane1.png");
				//eplane2 = getToolkit().getImage("Airplanes/plane1.png");
				bullet = getToolkit().getImage("Bullets/bullet1.png");
				controlbullet=getToolkit().getImage("Bullets/mybullet1.png");
				break;
			
			case 2:
				backgroud = getToolkit().getImage("Backgrounds/bg1.png");
				myplane = getToolkit().getImage("Airplanes/myplane1.png");
				eplane1 = getToolkit().getImage("Airplanes/plane1.png");
				//eplane2 = getToolkit().getImage("Airplanes/plane1.png");
				bullet = getToolkit().getImage("Bullets/bullet1.png");
				controlbullet=getToolkit().getImage("Bullets/mybullet1.png");
			    break;
			case 3:
				backgroud = getToolkit().getImage("Backgrounds/bg1.png");
				myplane = getToolkit().getImage("Airplanes/myplane1.png");
				eplane1 = getToolkit().getImage("Airplanes/plane1.png");
				//eplane2 = getToolkit().getImage("Airplanes/plane1.png");
				bullet = getToolkit().getImage("Bullets/bullet1.png");
				controlbullet=getToolkit().getImage("Bullets/mybullet1.png");
				break;
			case 4:
				backgroud = getToolkit().getImage("Backgrounds/bg1.png");
				myplane = getToolkit().getImage("Airplanes/myplane1.png");
				eplane1 = getToolkit().getImage("Airplanes/plane1.png");
				//eplane2 = getToolkit().getImage("Airplanes/plane1.png");
				bullet = getToolkit().getImage("Bullets/bullet1.png");
				controlbullet=getToolkit().getImage("Bullets/mybullet1.png");
				break;
			case 5:
				backgroud = getToolkit().getImage("Backgrounds/bg2.jpg");
				myplane = getToolkit().getImage("Airplanes/myplane2.png");
				eplane1 = getToolkit().getImage("Airplanes/plane2.png");
				//eplane2 = getToolkit().getImage("Airplanes/敌机2.png");
				bullet = getToolkit().getImage("Bullets/bullet2.png");
				controlbullet=getToolkit().getImage("Bullets/mybullet2.png");
				break;
			case 6:
				backgroud = getToolkit().getImage("Backgrounds/bg2.jpg");
				myplane = getToolkit().getImage("Airplanes/myplane2.png");
				eplane1 = getToolkit().getImage("Airplanes/plane2.png");
				//eplane2 = getToolkit().getImage("Airplanes/敌机2.png");
				bullet = getToolkit().getImage("Bullets/bullet2.png");
				controlbullet=getToolkit().getImage("Bullets/mybullet2.png");
				break;
			case 7:
				backgroud = getToolkit().getImage("Backgrounds/bg3.png");
				myplane = getToolkit().getImage("Airplanes/myplane3.png");
				eplane1 = getToolkit().getImage("Airplanes/plane3.png");
				//eplane2 = getToolkit().getImage("Airplanes/敌机3.png");
				bullet = getToolkit().getImage("Bullets/bullet3.png");
				controlbullet=getToolkit().getImage("Bullets/mybullet3.png");
				break;
			case 8:
				backgroud = getToolkit().getImage("Backgrounds/bg3.png");
				myplane = getToolkit().getImage("Airplanes/myplane3.png");
				eplane1 = getToolkit().getImage("Airplanes/plane3.png");
				//eplane2 = getToolkit().getImage("Airplanes/敌机3.png");
				bullet = getToolkit().getImage("Bullets/bullet3.png");
				controlbullet=getToolkit().getImage("Bullets/mybullet3.png");
				break;
			case 9:
				backgroud = getToolkit().getImage("Backgrounds/bg3.png");
				myplane = getToolkit().getImage("Airplanes/myplane3.png");
				eplane1 = getToolkit().getImage("Airplanes/plane3.png");
				//eplane2 = getToolkit().getImage("Airplanes/药品盒子.png");
				bullet = getToolkit().getImage("Bullets/bullet3.png");
				controlbullet=getToolkit().getImage("Bullets/mybullet3.png");
				break;
			case 10:
				backgroud = getToolkit().getImage("Backgrounds/bg3.png");
				myplane = getToolkit().getImage("Airplanes/myplane3.png");
				eplane1 = getToolkit().getImage("Airplanes/plane3.png");
				//eplane2 = getToolkit().getImage("Airplanes/药品盒子.png");
				bullet = getToolkit().getImage("Bullets/bullet3.png");
				controlbullet=getToolkit().getImage("Bullets/mybullet3.png");
				break;
		}

		g = (Graphics2D) this.p2.getGraphics();
		planeList.clear();
		bulletsList.clear();
		explodeList.clear();
		accessoryList.clear();
		rockList.clear();
		controltype=new Bullettype(20,"control",controlbullet);
		enemytype=new Bullettype(20,level,bullet);
		for (int i = 1; i <= 8; i++) {//产生8个敌机
			Airplane p1 = new Airplane(90 * i, 50, 78, 68,enemytype,level);
			planeList.add(p1);
			p1.intervel = p1.getRandomIntNum(0, 6);
			p1.eplane = 1;
		}
		for (int i = 1; i <= 4; i++) {//产生4个rock
			Rock rock = new Rock(200 * i, 0, 100, 100);
			rockList.add(rock);
		}
		p2.requestFocus();
		m1 = new Backgroudmusic();
		m1.run();
//		d1 = new Drawer();//开始画布上画
//		d2 = new Displayer();
//		d1.start();
//		d2.start();
	}
	
	/* 故事线 */
	public void storyContrl(Graphics2D drawOffScreen) {
		Image story = getToolkit().getImage("story/01.jpg");
	    m2.storyclip.play();
		switch(level){
			case 2:
				story = getToolkit().getImage("story/01.jpg");
				m2.storyclip.play();
				break;
			case 3:
				story = getToolkit().getImage("story/02.jpg");
				break;
			case 4:
				story = getToolkit().getImage("story/03.jpg");
				break;
			case 5:
				story = getToolkit().getImage("story/04.jpg");
				break;
			case 6:
				story = getToolkit().getImage("story/05.jpg");
				break;
			case 7:
				story = getToolkit().getImage("story/06.jpg");
				break;
			case 8:
				story = getToolkit().getImage("story/07.jpg");
				break;
			case 9:
				story = getToolkit().getImage("story/08.jpg");
				break;
			case 10:
				story = getToolkit().getImage("story/09.jpg");
				break;
		}
		drawOffScreen.drawImage(story, 0, 0, 1000, 900,0, 20,
				364, 400, null);
	}
	
	public void gameContrl(Graphics2D drawOffScreen) throws InterruptedException {
		// drawOffScreen.fillRect(0, 0, 1000, 900);
		drawOffScreen.drawImage(backgroud, 0, 0, 1000, 900, 0, (int) backy,
				360, 320 + (int) backy, null);//全部屏幕抹黑，清空
		t1.setText(Controlplane.bulletnum + "");
		t2.setText(Controlplane.life + "");
		t3.setText(Controlplane.oil + "");
		t4.setText(Controlplane.speed + "");
		t5.setText(Controlplane.pHeight + "*" + Controlplane.pWidth + "");
		t6.setText(String.valueOf(score));
		t7.setText(String.valueOf(clearnum));
		for (Rock rock : rockList) {
			rock.y+=rock.speed;
			if(rock.y>900){//控制rock消失
				rockList.remove(rock);
			}
//			rockList.add(new Rock(new Random().nextInt(1000), 0, 100, 100));
		}
		if(rockList.size()==0){
			for(int i=0;i<4;i++){
				rockList.add(new Rock(new Random().nextInt(1000), 0, 100, 100));
			}
		}
		for (Rock rock : rockList) {
		drawOffScreen.drawImage(rockImage, rock.x, rock.y, null);
		}
		backy -= .2;
		// System.out.println((int)backy+"");
		if (backy < 0)
			backy = 638;
		// drawOffScreen.drawImage(backgroud,0,0,1000,900,null);
		if (addplane) {
			if (planeList.size() < 8)//飞机数量小于8就加飞机
				planeList.add(new Airplane(level));
			addplane = false;
		}
		for(Airplane p : planeList){
			p.fly();
			drawOffScreen.drawImage(eplane1, p.pX, p.pY, null);
			//发射子弹
			if ((p.getRandomIntNum(0, 300)) == 2) {
				Bullet b2 = new Bullet(p.pX + p.pWidth / 2 - 3, p.pY
						+ p.pHeight, 13, 13,enemytype,level);
				b2.speed = -3;
				bulletsList.add(b2);
			}
			// 判断是否被击中?
			Iterator bnums = bulletsList.iterator();
			while (bnums.hasNext()) {
				Bullet b = (Bullet) bnums.next();
				if (p.hit(b)) {
					b = null;
					bnums.remove();
					m2.hitclip.play();
				}
				;
				// 判断是否撞击控制飞机
				if (p.hit(Controlplane))
					m2.explodeclip.play();
			}
			// 判断是否撞击附件
			Iterator anums = accessoryList.iterator();
			while (anums.hasNext()) {
				Accessory a = (Accessory) anums.next();
				if (p.hit(a)) {
					a = null;
					anums.remove();
					m2.beepclip.stop();
					m2.eatclip.play();
				}
				;
			}

			// 撞到石头
			for (Rock rock : rockList) {
				if (Controlplane.hit(rock)) {
					m2.beepclip.stop();
				}
			}

			if (p.life < 0) {
				explodeList.add(new Explode(p.pX, p.pY));
				planeList.remove(p);
				m2.explodeclip.play();
				score += 1;
				t1.setText(Controlplane.bulletnum + "");
				t2.setText(Controlplane.life + "");
				t3.setText(Controlplane.oil + "");
				t4.setText(Controlplane.speed + "");
				t5.setText(Controlplane.pHeight + "*" + Controlplane.pWidth + "");
				t6.setText(String.valueOf(score));
				
			}
		}

		// 附件
		if (hasAccessory) {
			accessoryList.add(new Accessory());
			hasAccessory = false;

		}
		Iterator anums = accessoryList.iterator();
		while (anums.hasNext()) {
			Accessory a = (Accessory) anums.next();
			if (a.aimage == 1)
				drawOffScreen.drawImage(a1, a.aX, a.aY, null);
			if (a.aimage == 2)
				drawOffScreen.drawImage(a2, a.aX, a.aY, null);
			if (a.aimage == 3)
				drawOffScreen.drawImage(a3, a.aX, a.aY, null);
			if (a.aimage == 4)
				drawOffScreen.drawImage(a4, a.aX, a.aY, null);
			if (a.aimage == 5)
				drawOffScreen.drawImage(a5, a.aX, a.aY, null);
			if (a.aimage == 6) 
				drawOffScreen.drawImage(a6, a.aX, a.aY, null);
			if (a.aimage == 7) 
				drawOffScreen.drawImage(a7, a.aX, a.aY, null);
			
			a.aY += a.speed;//运行轨迹的规划
			if (a.aY > 900) {
				a = null;
				anums.remove();
				m2.beepclip.stop();
				continue;
				// t2.setText(Controlplane.life+"");
			}
			;
			//撞到附件
			if (Controlplane.hit(a)) {
				if(a.typeint==7) {
					tempplane=myplane;
		            myplane=protectplane;
				}
				a = null;
				anums.remove();
				m2.beepclip.stop();
				m2.eatclip.play();
				t1.setText(Controlplane.bulletnum + "");
				t2.setText(Controlplane.life + "");
				t3.setText(Controlplane.oil + "");
				t4.setText(Controlplane.speed + "");
				t5.setText(Controlplane.pHeight + "*" + Controlplane.pWidth + "");
				t6.setText(String.valueOf(score));
				
				TimerTask task4 = new TimerTask() {
					public void run() {
						myplane=tempplane;
					}
			};
			Timer timer4 = new Timer();
		    timer4.schedule(task4, 5000);
								continue;
				// t2.setText(Controlplane.life+"");
			}
			;
			// 判断是否被击中?
			Iterator bnums = bulletsList.iterator();
			while (bnums.hasNext()) {
				Bullet b = (Bullet) bnums.next();
				if (a.hit(b)) {
					b = null;
					bnums.remove();
					m2.hitclip.play();
				}
			}//子弹击中飞机不一定会爆炸
			if (a.life < 0) {
				explodeList.add(new Explode(a.aX, a.aY));
				a = null;
				m2.beepclip.stop();
				anums.remove();
				m2.explodeclip.play();
			}
		
		 //撞到石头
        for(Rock rock:rockList){
   		   if(Controlplane.hit(rock)){
   			 m2.beepclip.stop();
   		   }
   	   } 

		// 子弹
		if (fire) {
			bulletsList.add(new Bullet(Controlplane.pX + Controlplane.pWidth
					/ 2 - 3, Controlplane.pY, 13, 13,controltype,controlplane));
			for (int i = 0; i < (Controlplane.fireLevel - 1) / 2; i++) {
				bulletsList.add(new Bullet(Controlplane.pX
						- Controlplane.pWidth/2 * (i - 1) - 3, Controlplane.pY,
						13, 13,controltype,controlplane));
				bulletsList.add(new Bullet(Controlplane.pX + Controlplane.pWidth/2
								* i - 3, Controlplane.pY, 13, 13,controltype,controlplane));
			}
			fire = false;
			t1.setText(Controlplane.bulletnum + "");
		}
		}

		Iterator bnums = bulletsList.iterator();
		while (bnums.hasNext()) {
			Bullet b = (Bullet) bnums.next();
			if(b.bullettype==enemytype) {
			drawOffScreen.drawImage(bullet, b.bX, b.bY, null);
			}
			if(b.bullettype==controltype) {
				drawOffScreen.drawImage(controlbullet, b.bX, b.bY, null);
				}
			b.bY -= b.speed;
			if ((b.bY < 0) || (b.bY > 900)) {
				b = null;
				bnums.remove();
				continue;
			}
			if ((Controlplane.hit(b))) {
				b = null;
				bnums.remove();
				m2.hitclip.play();
				t1.setText(Controlplane.bulletnum + "");
				t2.setText(Controlplane.life + "");
				t3.setText(Controlplane.oil + "");
				t4.setText(Controlplane.speed + "");
				t5.setText(Controlplane.pHeight + "*" + Controlplane.pWidth);
				t6.setText(String.valueOf(score));
				
			}
			;
		}
		if (gameover == 0)
			drawOffScreen.drawImage(myplane, Controlplane.pX, Controlplane.pY,
					null);
		if (gameover == -1)
			drawOffScreen.drawImage(gameoverimage, 500,
					450, null);
		if (gameover == 1)
			drawOffScreen.drawImage(end, 0, 0, 1000, 900,0, 20,
					364, 400, null);

		// 
		if ((Controlplane.life < 0) || (Controlplane.oil < 0)) {
			explodeList.add(new Explode(Controlplane.pX, Controlplane.pY));
			gameover = -1;
			Controlplane.life = 0;
			Controlplane.oil = 0;
			m2.explodeclip.play();//发出碰撞声音
		}
		;

		// 判断切换
		if (score > (level * 5 - 1)) {
			level = level + 1;
			showstory = true;
		}

		// 判断是结局
		if (score == 45)
			gameover = 1;

		//
		if ((explodeList.size() == 0) && (gameover != 0)) {
			goon = false;
		}

		Iterator enums = explodeList.iterator();
		while (enums.hasNext()) {
			Explode e = (Explode) enums.next();//爆炸？
			drawOffScreen.drawImage(explode, e.eX, e.eY, null);
			e.life--;

			if (e.life < 0) {
				e = null;
				enums.remove();
			}
			;
		}
	}
		// g.drawImage(OffScreen1,0,0,this.p2);
	

	class MultiKeyPressListener implements KeyListener {   
		// 存储按下的键，KeyEvent即为键盘事件类
		private final Set<Integer> pressed = new HashSet<Integer>();  //新建一个集合，命名为pressed
		@Override
		public void keyTyped(KeyEvent e) {  //敲击键盘时发生，即在按键按下后，按键放开前
		}

		@Override
		public synchronized void keyPressed(KeyEvent e) {   //按下按键时发生
			pressed.add(e.getKeyCode());  //把键盘上按下的字符码添加至pressed集合中
//			key = e.getKeyCode();  //把键盘上按下的字符码赋值给key

			//控制按键，前后左右的偏移控制，VK_RIGHT表示键盘“右”，VK_LEFT表示“左”，VK_UP表示键盘“上”，VK_DOWN表示键盘“下”
//			if (key == KeyEvent.VK_RIGHT) {  
			if (pressed.contains(KeyEvent.VK_RIGHT)) {  //如果按下的是右键，且当前飞机横坐标小于915像素，飞机位置向右偏移speed个像素
				if (Controlplane.pX < 915)    
					Controlplane.pX += Controlplane.speed; 
			}
			if (pressed.contains(KeyEvent.VK_LEFT)) {  //如果按下的是左键，且当前飞机横坐标大于5像素，飞机位置向左偏移speed个像素
				if(Controlplane.pX > 5)  
					Controlplane.pX -= Controlplane.speed;  
			}
			if (pressed.contains(KeyEvent.VK_UP)) {  //如果按下的是上键，且当前飞机纵坐标大于-5像素，飞机位置向上偏移speed个像素
				if(Controlplane.pY > 0)  
					Controlplane.pY -= Controlplane.speed;  
			}
			if (pressed.contains(KeyEvent.VK_DOWN)) {  //如果按下的是上键，且飞机纵坐标小于710像素，飞机位置向下偏移speed个像素
				if(Controlplane.pY < 600) 
					Controlplane.pY += Controlplane.speed; 
			}
			if (pressed.contains(KeyEvent.VK_SPACE)) {   //如果按下的是空格键
				if (Controlplane.bulletnum - Controlplane.fireLevel >= 0)  //当子弹数量大于火力值时，子弹数量减去火力值，得到剩余子弹数量
					Controlplane.bulletnum-=Controlplane.fireLevel;  
					fire = true;    //将开火fire置为true并播放场景音乐“射击”
				m2.gunshotclip.play();  
			}  //后续为加减速控制
			if (pressed.contains(KeyEvent.VK_1)) {  //如果按下的是1键，且飞机速度小于50，则飞机速度加10，并将文本框t3上面的文字设置为当前速度
				if (Controlplane.speed < 50) { 
					Controlplane.speed += 10;
					t4.setText(Controlplane.speed + "");
				}
			}
			if (pressed.contains(KeyEvent.VK_2)) {  //如果按下的是21键，且飞机速度大于150，则飞机速度减10，并将文本框t43上面的文字设置为当前速度
				if (Controlplane.speed > 10) {
					Controlplane.speed -= 10;
					t4.setText(Controlplane.speed + "");
				}
			}
			if(pressed.contains(KeyEvent.VK_C)){
				if(clearnum > 0){
					clearnum--;
					for (Rock rock : Battlefield.rockList) {
						if(rock.y>0){
							Battlefield.rockList.remove(rock);
						}
					}
					for (Airplane plane : Battlefield.planeList) {
						if(plane.pY>0){
							Battlefield.planeList.remove(plane);
						}
					}
				}
			}
		}

		@Override
		public synchronized void keyReleased(KeyEvent e) {  //放开按键时
			pressed.remove(e.getKeyCode());   //把放开的按键从pressed集合中移除
		}
	}

//		public void keyReleased(KeyEvent e) {
//		}



	public static Font loadFont(String fontFileName, float fontSize){  //加载字体； fontFileName字体文件名称；fontSize字体大小
		try
		{
			Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontFileName));
			font = font.deriveFont(Font.BOLD, fontSize);  //加粗并将字体大小设置为fontSize
			return font;
		}
		catch(Exception e)//异常处理
		{
			return new java.awt.Font(Font.MONOSPACED, Font.BOLD, 14);
//			return null;
		}
	}


	//选择初始状态
	public void showcomponent() {
		MenuBar m_MenuBar = new MenuBar();  //创建菜单条
//		Menu menuFile = new Menu("文件"); // 创建菜单
//		m_MenuBar.add(menuFile); // 将菜单加入菜单条
//		MenuItem f1 = new MenuItem("打开"); // 创建各菜单项
//		MenuItem f2 = new MenuItem("关闭");
//		menuFile.add(f1); // 加入菜单
//		menuFile.add(f2);
//		setMenuBar(m_MenuBar);
		//设置当前页面布局
		p1 = new Panel(); //新建面板容器p1
		add(p1, "North");  //把North加入p1面板中
		p1.setLayout(new GridLayout(2,8)); //把面板设置为一个1行10列的网状布局

		p1.add(new Label("  Bullet"), 0);  //在第1列创建子弹标签Bullet，再创建一个3个字符长度的文本框，并置于第2列
		t1 = new TextField(3);  
		p1.add(t1, 1);
		p1.add(new Label("  Health"), 2); //在第3列创建生命值标签Health，再创建一个3个字符长度的文本框，并置于第4列
		t2 = new TextField(3);  
		p1.add(t2, 3);
		p1.add(new Label("    Oil"), 4);  //在第5列创建油量标签Oil，再创建一个3个字符长度的文本框，并置于第6列
		t3 = new TextField(3);
		p1.add(t3, 5);
		p1.add(new Label("   Speed"), 6);  //在第7列创建速度标签Speed，再创建一个3个字符长度的文本框，并置于第8列
		t4 = new TextField(3);
		p1.add(t4, 7);
		p1.add(new Label("  Volumn"), 8);  //在第9列创建音量标签Volumn，再创建一个3个字符长度的文本框，并置于第10列
		t5 = new TextField(3);
		p1.add(t5, 9);
		p1.add(new Label("  Score"), 10);  //在第9列创建音量标签Volumn，再创建一个3个字符长度的文本框，并置于第10列
		t6 = new TextField(3);
		p1.add(t6,11);
		p1.add(new Label("  ClearNum"), 10);  //在第9列创建音量标签Volumn，再创建一个3个字符长度的文本框，并置于第10列
		t7 = new TextField(3);
		p1.add(t7,11);
		p1.add(new Label(""), 12);         //结束面板设置
		start = new Button("Start");       //创建一个Start按钮，并置于第12列位置,当鼠标单击改键可开始游戏
		p1.add(start, 13);                 
		start.addActionListener(new Startaction()); 
		save = new Button("Save");         //创建一个Save按钮，并置于第13列位置，当鼠标单击改键可保存游戏
		p1.add(save, 14);
//		save.addActionListener(new Startaction());
		save.addActionListener(new Saveaction());  
		load = new Button("Load");         //创建一个Load按钮，并置于第14列位置，当鼠标单击改键可加载游戏
		p1.add(load, 15);
//		load.addActionListener(new Startaction());
		load.addActionListener(new Loadaction());
        pause=new Button("Pause");
        p1.add(pause,16);
        pause.addActionListener(new paaction());
		stoppause=new Button("resume");
		p1.add(stoppause,17);
		stoppause.addActionListener(new stopPauseaction());

		//
		p2 = new Panel();      //创建面板容器p2，并向其中添加Center字符串

		add(p2, "Center");     
		Font font = loadFont("font/girl.ttc",15);   //将字体变量font设置为文件夹中的字体girl.ttc，大小为15号
		if(font != null) {     //如果找到了该字体，就把菜单条和面板p1的字体该为该字体
			m_MenuBar.setFont(font);
			p1.setFont(font);
		}
		/*
		 * Choice ColorChooser = new Choice(); ColorChooser.add("Green");
		 * ColorChooser.add("Red"); ColorChooser.add("Blue");
		 * p.add(ColorChooser); t1 = new TextField(3); p.add(t1);
		 * ColorChooser.addItemListener(new ItemListener(){ public void
		 * itemStateChanged(ItemEvent e){ String s= e.getItem().toString();
		 * t1.setText(s);} });
		 */
	}

	public static void main(String args[]) {
		Battlefield f = new Battlefield();  //创建战场f
		f.showcomponent();   //显示初始状态
		f.setSize(1000, 900);  //设置窗口大小
		f.setVisible(true);   //显示窗口
		//f.gameperpare();     //进入游戏准备阶段
		// f.gamebegin();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //设置关闭选项
	}

	class Drawer extends Thread {
		public void run() {
			while (goon) {
				if(!showstory) {
					flag.putf1begin();
					try {
						gameContrl(drawOffScreen1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					flag.putf1end();
					flag.putf2begin();
					try {
						gameContrl(drawOffScreen2);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					flag.putf2end();
				}else {
					if (timer != null) timer.cancel();
					timer = null;
					if (timer2 != null) timer2.cancel();
					timer2 = null;
					m2.beepclip.stop();
					m1.clip.stop();
					m1 = null;
					TimerTask task4 = new TimerTask() {
						public void run() {
							gameperpare();
							gamebegin();
							showstory = false;
						}
					};
					Timer timer3 = new Timer();
					timer3.schedule(task4, 2000);
					while(showstory) {
						flag.putf1begin();
						storyContrl(drawOffScreen1);
						flag.putf1end();
						flag.putf2begin();
						storyContrl(drawOffScreen2);
						flag.putf2end();
					}
				}
			}
		}
	}

	class Displayer extends Thread {  //线程2-展示
		public void run() {
			while (goon) {
				flag.getf1begin(); 
				g.drawImage(OffScreen1, 0, 0, Battlefield.this.p2);//搬第一个
				flag.getf1end(); 
				flag.getf2begin();
				g.drawImage(OffScreen2, 0, 0, Battlefield.this.p2);//搬第二个
				flag.getf2end();
			}
//			System.out.println("Game Over");
			timer.cancel();      
			timer = null;
			timer2.cancel();
			timer2 = null;
			m2.beepclip.stop();  //beepclip音乐片段停止
			m1.clip.stop();      //clip音乐片段停止
			m1 = null;
			start.enable();

		}
	}

 //	class Startaction implements ActionListener {
//		public void actionPerformed(ActionEvent event) {
//			level=0;
//			while(level<1||level>10){
//				try{
//					level = Integer.parseInt(JOptionPane.showInputDialog( this,
//		                     "请选择难度：1-10"));
//				}catch(Exception e){
//					continue;
//				}
//			}
//			goon = true;
//			gameover = 0;
//			start.disable();
//			gamebegin();
//
//		}
//	}
class Startaction implements ActionListener{
	public void actionPerformed(ActionEvent event) {
		level = 1;
		score = 0;
		goon=true;
		gameover=0;
		start.disable();
		gameperpare();
		gamebegin();
		d1 = new Drawer();
		d2 = new Displayer();
		d1.start();
		d2.start();
	}
}

	class Pauseaction implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			d1.suspend();
			d2.suspend();
		}
	}

	class paaction implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			goon=false;
			new Saveaction();
		}
	}
	class stopPauseaction implements ActionListener {
		public void actionPerformed(ActionEvent event) {

			ObjectInputStream ios;

			try {
				ios = new ObjectInputStream(
						new FileInputStream("save/save.dat"));
				Controlplane = (Airplane) ios.readObject();
				planeList = (CopyOnWriteArrayList<Airplane>) ios.readObject();
				bulletsList = (ArrayList) ios.readObject();
				accessoryList = (ArrayList) ios.readObject();
				explodeList = (ArrayList) ios.readObject();
				rockList = (CopyOnWriteArrayList<Rock>) ios.readObject();
				ios.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//这是在干啥？
			TimerTask task = new TimerTask() {
				public void run() {
					hasAccessory = true;
					m2.beepclip.loop();
				}
			};
			timer = new Timer();
			timer.schedule(task, 0, delay);

			TimerTask task2 = new TimerTask() {
				public void run() {
					Controlplane.oil -= 5;
					t3.setText(Controlplane.oil + "");
				}
			};
			timer2 = new Timer();
			timer2.schedule(task2, 3000, 3000);
			TimerTask task3 = new TimerTask() {
				public void run() {
					addplane = true;
				}
			};
			timer3 = new Timer();
			timer3.schedule(task3, 2000, 40000);
			goon = true;
			gameover = 0;
			p2.requestFocus();

			d1 = new Drawer();
			d2 = new Displayer();
			d1.start();
			d2.start();
			m1 = new Backgroudmusic();
			m1.run();

		}
	}
//	}
    //保存，以输出流的形式保存
	class Saveaction implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			d1.suspend();
			d2.suspend();
			ObjectOutputStream oos;
			try {
				File f = new File("save/save.dat");//产生文件
				if (f.exists())
					f.delete();//原来有就删掉

				oos = new ObjectOutputStream(new FileOutputStream(
						"save/save.dat"));//建立output管道
				oos.writeObject(Controlplane);
				oos.writeObject(planeList);
				oos.writeObject(bulletsList);
				oos.writeObject(accessoryList);
				oos.writeObject(explodeList);
				oos.writeObject(rockList);
				oos.close();
				goon=false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			d1.resume();
			d2.resume();
		}
	}
    //加载，用读文件的形式
	class Loadaction implements ActionListener {
		public void actionPerformed(ActionEvent event) {

			ObjectInputStream ios;

			try {
				ios = new ObjectInputStream(
						new FileInputStream("save/save.dat"));
				Controlplane = (Airplane) ios.readObject();
				planeList = (CopyOnWriteArrayList<Airplane>) ios.readObject();
				bulletsList = (ArrayList) ios.readObject();
				accessoryList = (ArrayList) ios.readObject();
				explodeList = (ArrayList) ios.readObject();
				rockList = (CopyOnWriteArrayList<Rock>) ios.readObject();
				ios.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//这是在干啥？
			TimerTask task = new TimerTask() {
				public void run() {
					hasAccessory = true;
					m2.beepclip.loop();
				}
			};
			timer = new Timer();
			timer.schedule(task, 0, delay);

			TimerTask task2 = new TimerTask() {
				public void run() {
					Controlplane.oil -= 5;
					t3.setText(Controlplane.oil + "");
				}
			};
			timer2 = new Timer();
			timer2.schedule(task2, 3000, 3000);
			TimerTask task3 = new TimerTask() {
				public void run() {
					addplane = true;
				}
			};
			timer3 = new Timer();
			timer3.schedule(task3, 2000, 40000);
			goon = true;
			gameover = 0;
			p2.requestFocus();

			d1 = new Drawer();
			d2 = new Displayer();
			d1.start();
			d2.start();
			m1 = new Backgroudmusic();
			m1.run();

		}
	}

	class Backgroudmusic {
		AudioClip clip;

		public void run() {
			File backmusic = new File("music/Tobu - Seven.mid");
			//File backmusic = new File("music/periodolder.mp3");
			try {
				clip = Applet.newAudioClip(backmusic.toURL());
				clip.loop();
			} catch (Exception e) {
			}
			;
		}
	}

	class Scenemusic {
		File gunshot, explode, beep, hit, eat,story;
		AudioClip gunshotclip, explodeclip, beepclip, hitclip, eatclip,storyclip;

		public Scenemusic() {
			super();
			gunshot = new File("music/bullet.mp3");
			explode = new File("music/explode.mp3");
			beep = new File("music/beep.wav");
			hit = new File("music/hit.wav");
			eat = new File("music/eat.mp3");
			story=new File("music/periodolder.mp3");
			try {
				gunshotclip = Applet.newAudioClip(gunshot.toURL());
				explodeclip = Applet.newAudioClip(explode.toURL());
				beepclip = Applet.newAudioClip(beep.toURL());
				hitclip = Applet.newAudioClip(hit.toURL());
				eatclip = Applet.newAudioClip(eat.toURL());
				storyclip = Applet.newAudioClip(story.toURL());

			} catch (Exception e) {
			}
			;
		}
		/*
		 * public void run() { while (true) { if (gunshot_voice>0)
		 * {gunshotclip.play();gunshot_voice--;}; if (explode_voice>0)
		 * {explodeclip.play();explode_voice--;}; if (accessory_voice>0)
		 * {beepclip.play(); accessory_voice--;}; } }
		 */
	}
}
