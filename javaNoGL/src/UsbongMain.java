/*
 * Copyright 2024 Usbong
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @company: Usbong
 * @author: SYSON, MICHAEL B.
 * @date created: 20240522
 * @last updated: 20240828; from 20240827
 * @website: www.usbong.ph
 *
 */
/*
 Additional Notes:
 1) compile on Windows Machine;
	javac *.java

 2) Execute
	java UsbongMain
*/

/*
current error; 20240608; from 20240606
LWJGL; JVM platform Windows x86
however, Platform available on classpath: windows/x64
Failed to locate library: lwjgl.dll

answer: download Java SE 8 SDK; Windows x64

https://www.oracle.com/ph/java/technologies/javase/javase8-archive-downloads.html; last accessed: 20240608
*/

/*
//Reference:
1) https://docs.oracle.com/javase/tutorial/uiswing/painting/refining.html; last accessed: 20240622
SwingPaintDemo4.java; last accessed: 20240622; from 20240623

2) https://github.com/usbong/game-off-2023; last accessed: 20240623
*/

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import java.util.TimerTask;
import java.util.Timer;

import java.awt.Toolkit;
import java.awt.Dimension;

import java.awt.event.WindowStateListener;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

//sound
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;

public class UsbongMain {

  //edited by Mike, 20240622
  //reference: Usbong Game Off 2023 Main
  //1000/60=16.66; 60 frames per second
  //1000/30=33.33; 30 frames
  //const fFramesPerSecondDefault=16.66;
  //const fFramesPerSecondDefault=33.33;
  private static final int updateDelay = 16;//20;
  private static MyPanel mp;

  private static int iScreenWidth;
  private static int iScreenHeight;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        //System.out.println("Created GUI on EDT? "+
        //SwingUtilities.isEventDispatchThread());

		JFrame f = new JFrame("Usbong Main Demo");		
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//edited by Mike, 20240622
        //f.setSize(250,250);

		//edited by Mike, 20240618
	    //setSize(windowSize, windowSize);
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    double dWidth = screenSize.getWidth();
        double dHeight = screenSize.getHeight();

	    iScreenWidth = (int) Math.round(dWidth);
	    iScreenHeight = (int) Math.round(dHeight);

		//added by Mike, 20240718; debug
		//iScreenHeight=512;
		
/*	//removed by Mike, 20240722		
	    System.out.println("width: "+iScreenWidth);
	    System.out.println("height: "+iScreenHeight);
*/
      //edited by Mike, 20240622
      //macOS still has menu row, etc.
      //f.setSize(iWidth, iHeight);

	  //edited by Mike, 20240722; from 20240719
	  //reminder: in Java21, shall still need to switch between windows;
	  f.setUndecorated(true); //removes close, minimize, maxime buttons in window
/*
	  //f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	  f.addWindowStateListener(new WindowStateListener() {
	    public void windowStateChanged(WindowEvent e) {
		   // minimized
		   if ((e.getNewState() & f.ICONIFIED) == f.ICONIFIED){
		     //f.setUndecorated(false); //displays close, minimize, maxime buttons in window
			 //System.out.println("MINIMIZED!");
		   }
		   // maximized
		   else if ((e.getNewState() & f.MAXIMIZED_BOTH) == f.MAXIMIZED_BOTH){
			 f.setUndecorated(true); //removes close, minimize, maxime buttons in window
			 //System.out.println("MAXIMIZED!");
		   }
	    }
  	  });
*/

      //Reference: https://stackoverflow.com/questions/1155838/how-can-i-do-full-screen-in-java-on-osx; last accessed: 20240622
      //answered by: Michael Myers, 20090720T2105
      GraphicsDevice gd =
                  GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

      //macOS, Windows both have "Full Screen Support"
      if (gd.isFullScreenSupported()) {
          gd.setFullScreenWindow(f);
      } else { //note: in case, no "Full Screen Support"
          f.setSize(iScreenWidth, iScreenHeight);
          f.setVisible(true);
      }

		//edited by Mike, 20240622
		//f.add(new MyPanel());
		//edited by Mike, 20240628
		//mp = new MyPanel(f);
		mp = new MyPanel(f, iScreenWidth, iScreenHeight);

		mp.setFocusable(true); //necessary to receive key presses
		f.add(mp);

    //removed by Mike, 20240622
    //f.setVisible(true);

	//playSound("../assets/audio/usbongGameOff2023AudioEffectsCannon.wav");
	//https://en.wikipedia.org/wiki/File:MIDI_sample.mid?qsrc=3044; last accessed: 20240623
	//midi files playable;
	//however, known warning appears:
	//https://stackoverflow.com/questions/23720446/java-could-not-open-create-prefs-error; last accessed: 20240623
	//...java.util.prefs.WindowsPreferences <init>
	//WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root
    //0x80000002. Windows RegCreateKeyEx(...) returned error code 5.
	//edited by Mike, 20240720
	//playSound("../assets/audio/MIDI_sample.mid");
	playSound("./assets/audio/MIDI_sample.mid");

		//timer.scheduleAtFixedRate(() -> mainRun(), 0, updateDelay, MILLISECONDS);

		TimerTask myTimerTask = new TimerTask(){
		   public void run() {
		     mp.update();

			 mp.repaint();
		   }
		};

		Timer myTimer = new Timer(true);
		myTimer.scheduleAtFixedRate(myTimerTask, 0, updateDelay); //60 * 1000);
    }

	//reference: https://stackoverflow.com/questions/26305/how-can-i-play-sound-in-java;
	//last accessed: 20240623
	//answered by: Andrew Jenkins, 20160608T0441
	//edited by: Sebastian, 20200608T1929
	//note: .mp3 format not available
	private static void playSound(String soundFile) {
		  try {
			Clip clip = AudioSystem.getClip();
			File f = new File("" + soundFile);
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());

			clip.open(audioIn);
			clip.start();
		  } catch (Exception e) {
			System.err.println(e.getMessage());
		  }
	}
}

//note JPanel? JFrame? confusing at the start, without working sample code;
class MyPanel extends JPanel {
	//added by Mike, 20240708
	int iScreenWidth;
	int iScreenHeight;
	int iOffsetScreenWidthLeftMargin;
	int iOffsetScreenHeightTopMargin; //added by Mike, 20240718

    //added by Mike, 20240718
    int iStageWidth;
    int iStageHeight;

	int iTileWidth=64;//default
	int iTileHeight=64;//default

	final int iTileWidthCountMax=13;
	final int iTileHeightCountMax=13;

    RedSquare redSquare;

	Level2D myLevel2D;

	JFrame myJFrameInstance;

	//edited by Mike, 20240628
    //public MyPanel(JFrame f) {
    public MyPanel(JFrame f, int iScreenWidth, int iScreenHeight) {

		//added by Mike, 20240622
		myJFrameInstance = f;

		//added by Mike, 20240708
		this.iScreenWidth=iScreenWidth;
		this.iScreenHeight=iScreenHeight;

		//square screen; make the excess, margins
		System.out.println("iScreenWidth: "+iScreenWidth);
		System.out.println("iScreenHeight: "+iScreenHeight);

		//edited by Mike, 20240722; from 20240718
		//reminder: screen width must be greater than the screen height
		//iScreenWidth will be set to be iScreenHeight
		iOffsetScreenWidthLeftMargin=(iScreenWidth-iScreenHeight)/2;
		iScreenWidth=iScreenHeight;

		iTileWidth=iScreenWidth/iTileWidthCountMax;
		iTileHeight=iScreenHeight/iTileHeightCountMax;

		iOffsetScreenHeightTopMargin=(iScreenHeight-(iTileHeight*iTileHeightCountMax))/2;

		//use this as input parameter, instead of screenWidth and screenHeight
		iStageWidth=iTileWidth*iTileWidthCountMax;
		iStageHeight=iTileHeight*iTileHeightCountMax;

		System.out.println("iStageWidth: "+iStageWidth);
		System.out.println("iStageHeight: "+iStageHeight);

		redSquare  = new RedSquare();
		
		myLevel2D = new Level2D(0+iOffsetScreenWidthLeftMargin,0+iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight); 

		setBorder(BorderFactory.createLineBorder(Color.black));

		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				moveSquare(e.getX(),e.getY());
				
				//added by Mike, 20240825
				myLevel2D.mousePressed(e);
			}
		});

		addMouseMotionListener(new MouseAdapter(){
			public void mouseDragged(MouseEvent e){
				moveSquare(e.getX(),e.getY());
				
				//added by Mike, 20240825
				//myLevel2D.mouseDragged(e);				
			}
		});

		//added by Mike, 20240622
		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent key) {
				//System.out.println("Key pressed.");

				if (key.getKeyCode() == KeyEvent.VK_ESCAPE){
					System.out.println("ESC.");

					//TODO: -add: open exit menu
					myJFrameInstance.dispatchEvent(new WindowEvent(myJFrameInstance, WindowEvent.WINDOW_CLOSING));
				}
				myLevel2D.keyPressed(key);
			}

			public void keyReleased(KeyEvent key) {
				myLevel2D.keyReleased(key);
			}

			public void keyTyped(KeyEvent key) {}
		});
    }

    private void moveSquare(int x, int y){

        // Current square state, stored as final variables
        // to avoid repeat invocations of the same methods.
        final int CURR_X = redSquare.getX();
        final int CURR_Y = redSquare.getY();
        final int CURR_W = redSquare.getWidth();
        final int CURR_H = redSquare.getHeight();
        final int OFFSET = 1;

        if ((CURR_X!=x) || (CURR_Y!=y)) {

            // The square is moving, repaint background
            // over the old square location.
			//removed by Mike, 20240627
			//repaint already done by TimerTask
            //repaint(CURR_X,CURR_Y,CURR_W+OFFSET,CURR_H+OFFSET);

            // Update coordinates.
            redSquare.setX(x);
            redSquare.setY(y);

            // Repaint the square at the new location.
/*			//removed by Mike, 20240627;
			//repaint already done by TimerTask
            repaint(redSquare.getX(), redSquare.getY(),
                    redSquare.getWidth()+OFFSET,
                    redSquare.getHeight()+OFFSET);
*/
        }
    }

	//added by Mike, 20240719
	@Override
    public Dimension getPreferredSize() {
        //edited by Mike, 20240720
		//note iScreenWidth and iScreenHeight may not yet have been updated
		//return new Dimension(250,200);
		return new Dimension(800,800);
    }

	public void update() {
	  myLevel2D.update();
	}

	//added by Mike, 20240719
	@Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

		//entire available screen
		g.setColor(Color.decode("#adb2b6")); //gray
		g.fillRect(0,0,iScreenWidth,iScreenHeight);		
		
		//square screen; make the excess, margins
		//g.setColor(Color.black);
		//g.setColor(Color.gray); //white
		g.setColor(Color.decode("#31363a")); //lubuntu, BreezeModified theme; dark terminal

		g.fillRect(0+iOffsetScreenWidthLeftMargin,0+iOffsetScreenHeightTopMargin,iStageWidth,iStageHeight);

		g.setColor(Color.decode("#33c699")); //lubuntu, BreezeModified theme; dark terminal
		
		//draw horizontal line
		//include the last line
		for (int i=0; i<=iTileHeightCountMax; i++) {		
			g.drawLine(0+iOffsetScreenWidthLeftMargin,iOffsetScreenHeightTopMargin+iTileHeight*i,iOffsetScreenWidthLeftMargin+iStageWidth,iOffsetScreenHeightTopMargin+iTileHeight*i);
		}
		//draw vertical line
		//include the last line
		for (int j=0; j<=iTileWidthCountMax; j++) {
			g.drawLine(iOffsetScreenWidthLeftMargin+iTileWidth*j,0+iOffsetScreenHeightTopMargin,iOffsetScreenWidthLeftMargin+iTileWidth*j,iOffsetScreenHeightTopMargin+iStageHeight);
		}

        //redSquare.paintSquare(g);
		
		myLevel2D.draw(g);
		
		redSquare.paintSquare(g);
    }
}

class RedSquare{

    private int xPos = 50;
    private int yPos = 50;
    private int width = 20;
    private int height = 20;

    public void setX(int xPos){
        this.xPos = xPos;
    }

    public int getX(){
        return xPos;
    }

    public void setY(int yPos){
        this.yPos = yPos;
    }

    public int getY(){
        return yPos;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public void paintSquare(Graphics g){
        g.setColor(Color.RED);
        g.fillRect(xPos,yPos,width,height);
        g.setColor(Color.BLACK);
        g.drawRect(xPos,yPos,width,height);
    }
}

//added by Mike, 20240802
//Reference: UsbongPagong
class Actor {
	//added by Mike, 20240804
	protected final int INITIALIZING_STATE = 0;
	protected final int MOVING_STATE = 1;
	protected final int IN_TITLE_STATE = 2; //TODO: -verify this
	protected final int DYING_STATE = 3;

	protected final int HIDDEN_STATE = 4;
	protected final int ACTIVE_STATE = 5;
	
	protected int currentState=ACTIVE_STATE;
	
	protected boolean isCollidable; 
	
	protected int iWidth=0;
	protected int iHeight=0;
	protected int iXPos=0;
	protected int iYPos=0;
	
	//edited by Mike, 20240827; from 20240819
	protected final int ISTEP_X_DEFAULT=1;//2;
	protected final int ISTEP_Y_DEFAULT=1;//2;
	
	protected int iStepX=ISTEP_X_DEFAULT; //4;
	protected int iStepY=ISTEP_Y_DEFAULT; //4;

	//edited by Mike, 20240816
	protected final int ISTEP_X_MAX=3;//2;
	protected final int ISTEP_Y_MAX=3;//2;
	
	//added by Mike, 20240629
	protected final int KEY_W=0; //same as key UP
	protected final int KEY_S=1; //same as key DOWN
	protected final int KEY_D=2; //same as key RIGHT
	protected final int KEY_A=3; //same as key LEFT
	protected final int iNumOfKeyTypes=4;
	protected boolean myKeysDown[];

	protected int iFrameCount=0;
	protected int iFrameCountMax=4;
	protected int iFrameCountDelay=0;
	protected int iFrameCountDelayMax=20;
	
	//added by Mike, 20240626
	protected int iRotationDegrees=0;
	protected int iFrameWidth=128;
	protected int iFrameHeight=128;

	protected BufferedImage myBufferedImage;

	//added by Mike, 20240708
	protected int iOffsetScreenWidthLeftMargin=0;
	protected int iOffsetScreenHeightTopMargin=0;

	//edited by Mike, 20240719; from 20240628
/*
	private int iScreenWidth=0;
	private int iScreenHeight=0;
*/
	protected int iStageWidth=0;
	protected int iStageHeight=0;

	//added by Mike, 20240714
	protected int iTileWidth=0;
	protected int iTileHeight=0;
	
	//added by Mike, 20240804
	protected int[][] tileMap; 
	protected final int MAX_TILE_MAP_HEIGHT=13;
	protected final int MAX_TILE_MAP_WIDTH=39;//26; 
	
	//added by Mike, 20240820
	protected int iRightMostLevelWidth=0;
	
	//added by Mike, 20240812
	protected final int TILE_BLANK=0;
	protected final int TILE_HERO=1;
	protected final int TILE_AIRCRAFT=2;
	protected final int TILE_WALL=3;
	protected final int TILE_PLASMA=4;
	protected final int TILE_TREE=5;	
	protected final int TILE_BASE=6;	
	
	protected int myTileType=0;
	
	//added by Mike, 20240714
	protected final int FACING_UP=0;
	protected final int FACING_DOWN=1;
	protected final int FACING_LEFT=2;
	protected final int FACING_RIGHT=3;
	protected int currentFacingState=0;
	
	//added by Mike, 20240730
	protected boolean bHasStarted=false;
	
	//added by Mike, 20240806
	protected int iViewPortX=0;
	protected int iViewPortY=0;
	protected int iViewPortWidth=0;
	protected int iViewPortHeight=0;
	
	//added by Mike, 20240809
	protected boolean bIsWallTypeHit=false;
	
    public Actor(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight) {
	  try {		  
		  myBufferedImage = ImageIO.read(new File("./res/robotship.png"));
      } catch (IOException ex) {
      }

	  //added by Mike, 20240708
	  this.iOffsetScreenWidthLeftMargin=iOffsetScreenWidthLeftMargin;
	  this.iOffsetScreenHeightTopMargin=iOffsetScreenHeightTopMargin;

	  //added by Mike, 20240628
	  this.iStageWidth=iStageWidth;
	  this.iStageHeight=iStageHeight;

	  //added by Mike, 20240714
	  this.iTileWidth=iTileWidth;
	  this.iTileHeight=iTileHeight;

	  //added by Mike, 20240629
	  myKeysDown = new boolean[iNumOfKeyTypes];
	   
	  //added by Mike, 20240804
	  tileMap = new int[MAX_TILE_MAP_HEIGHT][MAX_TILE_MAP_WIDTH];

	  //added by Mike, 20240812
	  iViewPortX=iOffsetScreenWidthLeftMargin+0;
	  iViewPortY=iOffsetScreenHeightTopMargin+0;
	  iViewPortWidth=iStageWidth;
	  iViewPortHeight=iStageHeight;	
	  
	  //added by Mike, 20240830
	  iRightMostLevelWidth=0+iOffsetScreenWidthLeftMargin+MAX_TILE_MAP_WIDTH*iTileWidth-iTileWidth;
		
	  reset();
	}

	//added by Mike, 20240628
	public void reset() {
		
		iWidth=iFrameWidth;
		iHeight=iFrameHeight;

		//added by Mike, 20240714
		currentFacingState=FACING_RIGHT;

		//edited by Mike, 20240719
		int iStageCenterWidth=(iStageWidth/2/iTileWidth)*iTileWidth; 
		int iStageCenterHeight=(iStageHeight/2/iTileHeight)*iTileHeight;

		setX(iOffsetScreenWidthLeftMargin+0+iStageCenterWidth);
		setY(iOffsetScreenHeightTopMargin+0+iStageCenterHeight);

		iFrameCount=0;
		iFrameCountDelay=0;
		iRotationDegrees=0;

		//added by Mike, 20240629
		int iMyKeysDownLength = myKeysDown.length;
		for (int i=0; i<iMyKeysDownLength; i++) {
			myKeysDown[i]=false;
		}		
		myKeysDown[KEY_D]=true;
		
		//added by Mike, 20240804
		currentState=ACTIVE_STATE;
		isCollidable=true;
		
		//added by Mike, 20240809
		bIsWallTypeHit=false;
	}

	//added by Mike, 20240804
    public boolean isActive() {
        if (currentState==ACTIVE_STATE) {
            return true;
		}
        else {
			return false;
		}
    }

    public void setX(int iXPos ){
        this.iXPos = iXPos;
    }

	//reminder: includes iOffsetScreenWidthLeftMargin
    public int getX(){
        return iXPos;
    }

    public void setY(int iYPos){
        this.iYPos = iYPos;
    }

	//reminder: includes iOffsetScreenHeightTopMargin
    public int getY(){
        return iYPos;
    }

    public int getWidth(){
        return iWidth;
    }

    public int getHeight(){
        return iHeight;
    }

    public void setWidth(int iWidth){
        this.iWidth = iWidth;
    }
		
    public void setHeight(int iHeight){
        this.iHeight = iHeight;
    }
	
	public int getStepX(){
        return iStepX;
    }

	public int getStepY(){
        return iStepY;
    }
	
	//added by Mike, 20240812
	public int getMyTileType() {
		return myTileType;
	}
	
	//added by Mike, 20240804
	public boolean checkIsCollidable() {
		return isCollidable;
	}

	//added by Mike, 20240804
	public void setCollidable(boolean c) {
		 isCollidable=c;
	}
	
	//added by Mike, 20240805
	public void setCurrentFacingState(int f) {
		currentFacingState=f;
	}
	
	//added by Mike, 20240809
	public int getCurrentFacingState() {
		return currentFacingState;
	}
	
	//added by Mike, 20240818
	public void setCurrentState(int s) {
		currentState=s;
	}

	public int getCurrentState() {
		return currentState;
	}
	
	//added by Mike, 20240809
	public boolean isViewPortStopped() {
		return bIsWallTypeHit;
	}
	
	public boolean getIsViewPortStopped() {
		return bIsWallTypeHit;
	}
	
	public void setViewPortStopped(boolean b) {
		bIsWallTypeHit = b;
	}

	//iVPX and iVPY are Level2D's x and y viewports
	//public void synchronizeViewPort(int iVPX, int iVPY) {
	public void synchronizeViewPort(int iVPX, int iVPY, int iVStepX, int iVStepY) {

		if (getMyTileType()==TILE_HERO) {
			iViewPortX=iVPX;
			iViewPortY=iVPY;
			return;
		}

		if ((iViewPortX==iVPX) && (iViewPortY==iVPY)) {
/*			
			//added by Mike, 20240825		
			//TODO: verify: this
			if (!isIntersectingRect(this.getX(),this.getY(),iViewPortX,iViewPortY)) {
							
				//added by Mike, 20240825
				if (getMyTileType()==TILE_PLASMA) {
					this.setCurrentState(HIDDEN_STATE);
				}			
			}
*/			
			return;
		}
		
		//edited by Mike, 20240821
		//https://www.mathsisfun.com/algebra/distance-2-points.html;
		//last accessed: 20240821
		//distance = sqrt(x^2 + y^2)
/*		
		int iDifferenceInXPos=iViewPortX-iVPX;
		int iDifferenceInYPos=iViewPortY-iVPY;	
*/
		double dDifferenceInXPos=Math.sqrt(Math.pow(iViewPortX-iVPX,2));
		double dDifferenceInYPos=Math.sqrt(Math.pow(iViewPortY-iVPY,2));
		
		int iDifferenceInXPos=(int)dDifferenceInXPos;
		int iDifferenceInYPos=(int)dDifferenceInYPos;
		
		if (iVStepX<0) { //hero X moving left; viewport X moving right
			//System.out.println("HERO MOVING LEFTTTTTTTTTTTTTTTTT");
			iDifferenceInXPos=iDifferenceInXPos*1;
		}
		else {
			//System.out.println("HERO MOVING RIGHTTTTTTTT");
			iDifferenceInXPos=iDifferenceInXPos*(-1);
		}

		if (iVStepY<0) { //hero Y moving down; viewport Y moving up
			iDifferenceInYPos=iDifferenceInYPos*1;
		}
		else {
			iDifferenceInYPos=iDifferenceInYPos*(-1);
		}
		
		System.out.println("iViewPortX: "+iViewPortX);
		System.out.println("iViewPortY: "+iViewPortY);
		
		System.out.println("iVPX: "+iVPX);
		System.out.println("iVPY: "+iVPY);
		
		System.out.println("iDifferenceInXPos: "+iDifferenceInXPos);
		System.out.println("iDifferenceInYPos: "+iDifferenceInYPos);
/*
		System.out.println("dDifferenceInXPos: "+dDifferenceInXPos);
		System.out.println("dDifferenceInYPos: "+dDifferenceInYPos);
*/
		
		System.out.println("iRightMostLevelWidth: "+iRightMostLevelWidth);

		System.out.println("iOffsetScreenWidthLeftMargin: "+iOffsetScreenWidthLeftMargin);

		//added by Mike, 20240821			
		if (!isIntersectingRect(iViewPortX,iViewPortY,iVPX,iVPY)) {
			iViewPortX=iVPX;
			iViewPortY=iVPY;		
/*						
			//added by Mike, 20240825
			//TODO: verify: this			
			if (getMyTileType()==TILE_PLASMA) {
				this.setCurrentState(HIDDEN_STATE);
			}
*/		
			return;
		}
		
		//wrap around;
		//has reached right-most
		if (iOffsetScreenWidthLeftMargin+iDifferenceInXPos+getStepX()>=iRightMostLevelWidth) {
			
			int iDifferentinXPosFromRightMostLevelWidth=(iOffsetScreenWidthLeftMargin+iDifferenceInXPos)-iRightMostLevelWidth;
			
			//System.out.println("!!!! ACTOR has reached right-most!: "+iDifferentinXPosFromRightMostLevelWidth);
			
			setX(0+iOffsetScreenWidthLeftMargin+iDifferentinXPosFromRightMostLevelWidth);
		}
		else {
			setX(this.getX()+iDifferenceInXPos);
			setY(this.getY()+iDifferenceInYPos);			
		}
				
		iViewPortX=iVPX;
		iViewPortY=iVPY;
		
/*		
		//added by Mike, 20240825
		//TODO: verify: this		
		if (getMyTileType()==TILE_PLASMA) {
			this.setCurrentState(ACTIVE_STATE);
		}
*/		
	}
	
	//added by Mike, 20240811
	public boolean isActorInsideViewPort(int iViewPortX, int iViewPortY, int iCurrActorX, int iCurrActorY)
{     
		//added by Mike, 20240818
		if (isActorInsideViewPortStartEnd(iViewPortX, iViewPortY, iCurrActorX, iCurrActorY)) {
			return true;
		}

		//add 1 row before making the tile disappear
		//movement causes increased difference between their positions,
		//because both viewport and the actor are moved
		//TODO: -improve: to use smaller size to classify if actor is NOT in viewport
		if (iCurrActorY+iTileHeight+iViewPortHeight*2 < iViewPortY || //is above the top of iViewPortY?
			iCurrActorY > iViewPortY+iViewPortHeight*2 || //is at the bottom of iViewPortY?
			//add 1 column before making the tile disappear
			iCurrActorX+iTileWidth+iViewPortWidth*2 < iViewPortX || //is at the left of iViewPortX?
			iCurrActorX > iViewPortX+iViewPortWidth*2) { //is at the right of iViewPortX?
			return false;
		}	
		return true;
	}	

	//added by Mike, 20240818
	//TODO: -update: this
	public boolean isActorInsideViewPortStartEnd(int iViewPortX, int iViewPortY, int iCurrActorX, int iCurrActorY) {   
		if (iCurrActorY+iTileHeight+iViewPortHeight*2 < 0+iOffsetScreenHeightTopMargin || //is above the top of iViewPortY?
			iCurrActorY > 0+iOffsetScreenHeightTopMargin+iViewPortHeight*2 || //is at the bottom of iViewPortY?
			//add 1 column before making the tile disappear
			iCurrActorX+iTileWidth+iViewPortWidth*2 < 0+iOffsetScreenWidthLeftMargin || //is at the left of iViewPortX?
			iCurrActorX > 0+iOffsetScreenWidthLeftMargin+iViewPortWidth*2) { //is at the right of iViewPortX?
			return false;
		}	
			
		return true;
	}	

	//added by Mike, 20240809
	public void synchronizeKeys(boolean[] mkd) {
		for(int i=0; i<iNumOfKeyTypes; i++) {
			myKeysDown[i]=mkd[i];
		}	
	}
	
	public void update() {	
		//added by Mike, 20240730
		if (!bHasStarted) {
			myKeysDown[KEY_D]=false;
			bHasStarted=true;
		}
		
		return;

/* 	//edited by Mike, 20240706; OK
		//animation
		if (iFrameCountDelay<iFrameCountDelayMax) {
			iFrameCountDelay++;
		}
		else {
			iFrameCount=(iFrameCount+1)%iFrameCountMax;
			iFrameCountDelay=0;
		}
		iFrameCount=0;
*/		
	}

	public void keyPressed(KeyEvent key) {				
		//added by Mike, 20240629
		//horizontal movement
		if ((key.getKeyCode() == KeyEvent.VK_A) || (key.getKeyCode() == KeyEvent.VK_LEFT)) {						
			myKeysDown[KEY_A]=true;
			setCurrentFacingState(FACING_LEFT);	
			
			//added by Mike, 20240816			
			if (getStepX()==999999) { //if NOT forced to go right 
				myKeysDown[KEY_A]=false;
			}						
		}

		if ((key.getKeyCode() == KeyEvent.VK_D) || (key.getKeyCode() == KeyEvent.VK_RIGHT)) {
			myKeysDown[KEY_D]=true;
			setCurrentFacingState(FACING_RIGHT);	
			
			//added by Mike, 20240816			
			if (getStepX()==-999999) { //if NOT forced to go left 
				myKeysDown[KEY_D]=false;
			}			
		}

		//vertical movement
		if ((key.getKeyCode() == KeyEvent.VK_W) || (key.getKeyCode() == KeyEvent.VK_UP)) {
			myKeysDown[KEY_W]=true;
			setCurrentFacingState(FACING_UP);			
			
			//added by Mike, 20240816			
			if (getStepY()==999999) { //if NOT forced to go down 
				myKeysDown[KEY_W]=false;
			}			
		}

		if ((key.getKeyCode() == KeyEvent.VK_S) || (key.getKeyCode() == KeyEvent.VK_DOWN)) {
			myKeysDown[KEY_S]=true;
			setCurrentFacingState(FACING_DOWN);
			
			//added by Mike, 20240816			
			if (getStepY()==-999999) { //if NOT forced to go up 
				myKeysDown[KEY_S]=false;
			}			
		}
	}

	public void keyReleased(KeyEvent key) {
		
		//added by Mike, 20240629
		//horizontal movement
		if ((key.getKeyCode() == KeyEvent.VK_A) || (key.getKeyCode() == KeyEvent.VK_LEFT)) {
			myKeysDown[KEY_A]=false;			
		}

		if ((key.getKeyCode() == KeyEvent.VK_D) || (key.getKeyCode() == KeyEvent.VK_RIGHT)) {
			myKeysDown[KEY_D]=false;			
		}

		//vertical movement
		if ((key.getKeyCode() == KeyEvent.VK_W) || (key.getKeyCode() == KeyEvent.VK_UP)) {
			myKeysDown[KEY_W]=false;
		}

		if ((key.getKeyCode() == KeyEvent.VK_S) || (key.getKeyCode() == KeyEvent.VK_DOWN)) {
			myKeysDown[KEY_S]=false;
		}
		
//		System.out.println("KEY RELEASED!!!!!!");

		setViewPortStopped(false);		
	}

	//added by Mike, 20240825	
	public void mousePressed(MouseEvent e) {
		//moveSquare(e.getX(),e.getY());
	}
	
	//added by Mike, 20240803	
	//reference: UsbongPagong
	public void hitBy(Actor a) {
	}
	
	//added by Mike, 20240821
	//whole viewPort; not tile only
	public boolean isIntersectingRect(int a1X, int a1Y, int a2X, int a2Y) {
		//iOffsetXPosAsPixel=12; a1.getHeight()=83; 12/83=0.14457
		
		//remove negative sign
		a1X=Math.abs(a1X);
		a1Y=Math.abs(a1Y);

		a2X=Math.abs(a2X);
		a2Y=Math.abs(a2Y);
		
		//object and object; not object and tile
		//iTileWidth
		int iOffsetYPosAsPixel=0;//a1.getHeight()/3;//10;//4; //diagonal hit; image has margin
		//iTileHeight
		int iOffsetXPosAsPixel=0;//a1.getWidth()/3;//10;//4;

		if ((a2Y+iViewPortHeight <= a1Y+iOffsetYPosAsPixel) || //is the bottom of a2 above the top of a1?
			(a2Y >= a1Y+iViewPortHeight-iOffsetYPosAsPixel) || //is the top of a2 below the bottom of a1?
			(a2X+iViewPortWidth <= a1X+iOffsetXPosAsPixel)  || //is the right of a2 to the left of a1?
			(a2X >= a1X+iViewPortWidth-iOffsetXPosAsPixel)) { //is the left of a2 to the right of a1?

			return false;
		}
		
		return true;
	}	
	
	//Example values: iOffsetXPosAsPixel=10; iMyWidthAsPixel=64; 10/64=0.15625
	//note: Hero Actor put in a2
	public boolean isIntersectingRect(Actor a1, Actor a2) {
		//iOffsetXPosAsPixel=12; a1.getHeight()=83; 12/83=0.14457

		//object and object; not object and tile
		//iTileWidth
		int iOffsetYPosAsPixel=a1.getHeight()/3;//10;//4; //diagonal hit; image has margin
		//iTileHeight
		int iOffsetXPosAsPixel=a1.getWidth()/3;//10;//4;

		if ((a2.getY()+a2.getHeight() <= a1.getY()+iOffsetYPosAsPixel-a1.getStepY()) || //is the bottom of a2 above the top of a1?
			(a2.getY() >= a1.getY()+a1.getHeight()-iOffsetYPosAsPixel+a2.getStepY()) || //is the top of a2 below the bottom of a1?
			(a2.getX()+a2.getWidth() <= a1.getX()+iOffsetXPosAsPixel-a2.getStepX())  || //is the right of a2 to the left of a1?
			(a2.getX() >= a1.getX()+a1.getWidth()-iOffsetXPosAsPixel+a2.getStepX())) { //is the left of a2 to the right of a1?

			return false;
		}
		
		return true;
	}
			
	public void collideWith(Actor a) {

		if ((!checkIsCollidable())||(!a.checkIsCollidable()))    
		{
			//not collidable
			return;
		}

		if (isIntersectingRect(this, a))
		{
			//System.out.println("COLLISION!");
			
			this.hitBy(a);
			a.hitBy(this);
		}
	}

//Additional Reference: 	https://docs.oracle.com/javase/tutorial/2d/advanced/examples/ClipImage.java; last accessed: 20240625
  //edited by Mike, 20240811
//  public void drawActor(Graphics g) {
  public void drawActor(Graphics g, int iInputX, int iInputY) {
	
	//TODO: -verify: if clip still has to be cleared
	Rectangle2D rect = new Rectangle2D.Float();

    //added by Mike, 20240623
    AffineTransform identity = new AffineTransform();

    Graphics2D g2d = (Graphics2D)g;
    AffineTransform trans = new AffineTransform();
    trans.setTransform(identity);
    //300 is object position;
    //trans.translate(300-iFrameCount*128, 0);
    //trans.translate(-iFrameCount*128, 0);

	//added by Mike, 20240625
	//note clip rect has to also be updated;
	//trans.scale(2,2); //1.5,1.5
	//put scale after translate position;

/*  //reference: https://stackoverflow.com/questions/8721312/java-image-cut-off; last accessed: 20240623
    //animating image doable, but shall need more computations;
    //sin, cos; Bulalakaw Wars;
    trans.translate(128/2,128/2);
    trans.rotate(Math.toRadians(45)); //input in degrees
    trans.translate(-128/2,-128/2);
*/
	//reminder: when objects moved in x-y coordinates, rotation's reference point also moves with the update;
	//"compounded translate and rotate"
	//https://stackoverflow.com/questions/32513508/rotating-java-2d-graphics-around-specified-point; last accessed: 20240625
	//answered by: MadProgrammer, 20150911T00:48; from 20150911T00:41

	//update x and y positions before rotate
	//object position x=300, y=0;
    //trans.translate(300,0);
    //edited by Mike, 20240628
	//trans.translate(300,300);
	//edited by Mike, 20240714
	//trans.translate(getX(),getY());
	trans.translate(iInputX,iInputY);

	//scales from top-left as reference point
    //trans.scale(2,2);
	//rotates using top-left as anchor
    //trans.rotate(Math.toRadians(45)); //input in degrees

/* //removed by Mike, 20240706; OK
	//rotate at center; put translate after rotate
	iRotationDegrees=(iRotationDegrees+10)%360;
    trans.rotate(Math.toRadians(iRotationDegrees));
    trans.translate(-iFrameWidth/2,-iFrameHeight/2);
*/

	//edited by Mike, 20240714; from 20240708
/*
	System.out.println("iTileWidth: "+iTileWidth);
	System.out.println("iTileHeight: "+iTileHeight);
*/
	//scale to size of iTileWidth and iTileHeight;
	//example: iTileWidth=83
	//iFrameWidth=128;
	//trans.scale(0.5,0.5);
	//double temp = iTileWidth*1.0/iFrameWidth;
	//System.out.println("temp: "+temp);
	//edited by Mike, 20240811
	trans.scale((iTileWidth*1.0)/iFrameWidth,(iTileHeight*1.0)/iFrameHeight);

	//added by Mike, 20240714
	//put this after scale;
	//move the input image to the correct row of the frame

	if (currentFacingState==FACING_RIGHT) {
		//trans.translate(getX(),getY());
	}
	else { //FACING_LEFT
		trans.translate(0,0-iFrameHeight);
	}

	//added by Mike, 20240625
	g2d.setTransform(trans);

	if (currentFacingState==FACING_RIGHT) {
		//no animation yet; 0+iFrameCount*iFrameWidth-iFrameCount*iFrameWidth
	    rect.setRect(0, 0, iFrameWidth, iFrameHeight);
	}
	else { //FACING_LEFT
	   rect.setRect(0, 0+iFrameHeight, iFrameWidth, iFrameHeight);
	}

	Area myClipArea = new Area(rect);

    //edited by Mike, 20240625; from 20240623
    g2d.setClip(myClipArea);
    
	//edited by Mike, 20240714
    g2d.drawImage(myBufferedImage,-iFrameCount*iFrameWidth, 0, null);

	//removed by Mike, 20240711; from 20240625
	//put after the last object to be drawn
	//g2d.dispose();
  }
  
  //added by Mike, 20240811
  public void draw(Graphics g) {
	  drawActor(g,-1,-1); //default
  }
}

//added by Mike, 20240622
class RobotShip extends Actor {

    public RobotShip(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight) {
	  super(iOffsetScreenWidthLeftMargin, iOffsetScreenHeightTopMargin, iStageWidth, iStageHeight, iTileWidth, iTileHeight);
		
	  try {		  
		  myBufferedImage = ImageIO.read(new File("./res/robotship.png"));
      } catch (IOException ex) {
      }
	}

	//added by Mike, 20240628
	@Override
	public void reset() {		
		iWidth=iTileWidth;
		iHeight=iTileHeight;
		
		//added by Mike, 20240714
		currentFacingState=FACING_RIGHT;

		//edited by Mike, 20240719
		int iStageCenterWidth=(iStageWidth/2/iTileWidth)*iTileWidth; 
		int iStageCenterHeight=(iStageHeight/2/iTileHeight)*iTileHeight; 

		setX(iOffsetScreenWidthLeftMargin+0+iStageCenterWidth);
		setY(iOffsetScreenHeightTopMargin+0+iStageCenterHeight);

		iFrameCount=0;
		iFrameCountDelay=0;
		iRotationDegrees=0;

		//added by Mike, 20240629
		int iMyKeysDownLength = myKeysDown.length;
		for (int i=0; i<iMyKeysDownLength; i++) {
			myKeysDown[i]=false;
		}		
		myKeysDown[KEY_D]=true;
		
		currentState=ACTIVE_STATE;
		isCollidable=true;
		
		iStepX=ISTEP_X_DEFAULT;//*2; //faster by 1 than the default
		iStepY=ISTEP_Y_DEFAULT;//*2; //faster by 1 than the default
				
		myTileType=TILE_HERO;
	}
	
	@Override
	public void update() {		
		//setX(getX()+getStepX());

		if (myKeysDown[KEY_A])
		{
			currentFacingState=FACING_LEFT;
			//iStepX=-ISTEP_X_DEFAULT;
		}
		
		if (myKeysDown[KEY_D])
		{
			currentFacingState=FACING_RIGHT;
			//iStepX=ISTEP_X_DEFAULT;
		}	
		
		//edited by Mike, 20240809
		//note: robot ship doesn't not have FACING_UP or FACING_DOWN
		//TODO: -remove this
		
		if (myKeysDown[KEY_W])
		{
			//currentFacingState=FACING_UP;
			iStepY=-ISTEP_Y_DEFAULT;//ISTEP_Y_MAX;
			setY(getY()+getStepY());
		}

		if (myKeysDown[KEY_S])
		{
			//currentFacingState=FACING_DOWN;
			iStepY=ISTEP_Y_DEFAULT;//ISTEP_Y_MAX;
			setY(getY()+getStepY());
		}	

		//added by Mike, 20240730
		if (!bHasStarted) {
			myKeysDown[KEY_D]=false;
			bHasStarted=true;
		}
		
		return;

/* 	//edited by Mike, 20240706; OK
		//animation
		if (iFrameCountDelay<iFrameCountDelayMax) {
			iFrameCountDelay++;
		}
		else {
			iFrameCount=(iFrameCount+1)%iFrameCountMax;
			iFrameCountDelay=0;
		}
		iFrameCount=0;
*/		
	}
	

//Additional Reference: 	https://docs.oracle.com/javase/tutorial/2d/advanced/examples/ClipImage.java; last accessed: 20240625
  //robotship
  @Override
  public void draw(Graphics g) {	  
	if (currentState==HIDDEN_STATE) {
		return;
	}

	int iDifferenceInXPos=this.getX();
	int iDifferenceInYPos=this.getY();
					
    drawActor(g, this.getX(), this.getY());		
  }
}

class EnemyAircraft extends Actor {
	
    public EnemyAircraft(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight) {
	  super(iOffsetScreenWidthLeftMargin, iOffsetScreenHeightTopMargin, iStageWidth, iStageHeight, iTileWidth, iTileHeight);
		
	  try {		  
		  myBufferedImage = ImageIO.read(new File("./res/robotship2.png"));
      } catch (IOException ex) {
      }
	}

	@Override
	public void reset() {		
		iWidth=iTileWidth;
		iHeight=iTileHeight;		

		currentFacingState=FACING_LEFT;//FACING_RIGHT;

		iFrameCount=0;
		iFrameCountDelay=0;
		iRotationDegrees=0;

		//added by Mike, 20240629
		int iMyKeysDownLength = myKeysDown.length;
		for (int i=0; i<iMyKeysDownLength; i++) {
			myKeysDown[i]=false;
		}		
		
		currentState=ACTIVE_STATE;
		isCollidable=true;

		iStepX=-ISTEP_X_DEFAULT;//*2; //faster by 1 than the default
		iStepY=ISTEP_Y_DEFAULT;//*2; //faster by 1 than the default
		
		myTileType=TILE_AIRCRAFT;
	}
	
	@Override
	public void hitBy(Actor a) {
		currentState=HIDDEN_STATE;
		isCollidable=false;

		System.out.println("HIT!!! SET TO HIDDEN STATE");
	}
	
	//added by Mike, 20240805
	@Override
	public void keyPressed(KeyEvent key) {		
	}
	
	@Override
	public void keyReleased(KeyEvent key) {		
	}
		
	@Override
	public void update() {
		setX(getX()+getStepX());
/*		
		System.out.println("EnemyAircraft.get(): "+getX());
		System.out.println("EnemyAircraft.getStepX(): "+getStepX());
*/				
		
/* 	//edited by Mike, 20240706; OK
		//animation
		if (iFrameCountDelay<iFrameCountDelayMax) {
			iFrameCountDelay++;
		}
		else {
			iFrameCount=(iFrameCount+1)%iFrameCountMax;
			iFrameCountDelay=0;
		}
*/
		iFrameCount=0;
	}	

  //added by Mike, 20240811
  @Override
  public void drawActor(Graphics g, int iInputX, int iInputY) {  
	Rectangle2D rect = new Rectangle2D.Float();

    //added by Mike, 20240623
    AffineTransform identity = new AffineTransform();

    Graphics2D g2d = (Graphics2D)g;
    AffineTransform trans = new AffineTransform();
    trans.setTransform(identity);
    
	trans.translate(iInputX,iInputY);

	trans.scale((iTileWidth*1.0)/iFrameWidth,(iTileHeight*1.0)/iFrameHeight);

	if (currentFacingState==FACING_RIGHT) {
		//trans.translate(getX(),getY());
	}
	else { //FACING_LEFT
		trans.translate(0,0-iFrameHeight);
	}

	g2d.setTransform(trans);

	//note: clip rect has to move with object position
    //edited by Mike, 20240623
    //reminder:
    //300 is object position;
    //iFrameCount*128 is the animation frame to show;
    //-iFrameCount*128 is move the object position to the current frame;
    //rect.setRect(300+iFrameCount*128-iFrameCount*128, 0, 128, 128);
	//edited by Mike, 20240714; from 20240714
    //rect.setRect(0+iFrameCount*iFrameWidth-iFrameCount*iFrameWidth, 0, iFrameWidth, iFrameHeight);

	if (currentFacingState==FACING_RIGHT) {
		//no animation yet; 0+iFrameCount*iFrameWidth-iFrameCount*iFrameWidth
	    rect.setRect(0, 0, iFrameWidth, iFrameHeight);
	}
	else { //FACING_LEFT
	   rect.setRect(0, 0+iFrameHeight, iFrameWidth, iFrameHeight);
	}

	Area myClipArea = new Area(rect);

    g2d.setClip(myClipArea);    
    g2d.drawImage(myBufferedImage,-iFrameCount*iFrameWidth, 0, null);

	//removed by Mike, 20240711; from 20240625
	//put after the last object to be drawn
	//g2d.dispose();
  }
  
//Additional Reference: 	https://docs.oracle.com/javase/tutorial/2d/advanced/examples/ClipImage.java; last accessed: 20240625
  @Override
  public void draw(Graphics g) {	  
	if (currentState==HIDDEN_STATE) {
		return;
	}

	drawActor(g, this.getX(), this.getY());
  }
}

//added by Mike, 20240825
class Plasma extends Actor {
	
	private final int ISTEP_X_PLASMA=ISTEP_X_DEFAULT*20;//10;
	private final int ISTEP_Y_PLASMA=ISTEP_Y_DEFAULT*20;//10;
	
	//added by Mike, 20240826
	private int iXInitialDistanceTraveled=0;
	private int iXCurrentDistanceTraveled=0;
	private int iXDistanceTraveledMax;
	
	private int iYInitialDistanceTraveled=0;
	private int iYCurrentDistanceTraveled=0;
	private int iYDistanceTraveledMax;
	
    public Plasma(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight) {
	  super(iOffsetScreenWidthLeftMargin, iOffsetScreenHeightTopMargin, iStageWidth, iStageHeight, iTileWidth, iTileHeight);
		
	  try {		  
		  myBufferedImage = ImageIO.read(new File("./res/plasma.png"));
      } catch (IOException ex) {
      }
	}

	@Override
	public void reset() {		
		iWidth=iTileWidth;
		iHeight=iTileHeight;		

		currentFacingState=FACING_RIGHT;

		iFrameCount=0;
		iFrameCountDelay=0;
		iRotationDegrees=0;

		//added by Mike, 20240629
		int iMyKeysDownLength = myKeysDown.length;
		for (int i=0; i<iMyKeysDownLength; i++) {
			myKeysDown[i]=false;
		}		
		
		//added by Mike, 20240826
		iXDistanceTraveledMax=iViewPortWidth;
		iYDistanceTraveledMax=iViewPortHeight;
		
		//default
		currentState=HIDDEN_STATE; //ACTIVE_STATE;
		isCollidable=true;

		iStepX=0;//ISTEP_X_DEFAULT;//*2; //faster by 1 than the default
		iStepY=0;//ISTEP_Y_DEFAULT;//*2; //faster by 1 than the default
		
		myTileType=TILE_PLASMA;
	}
	
	@Override
	public void hitBy(Actor a) {
		if (a.getMyTileType()==TILE_HERO) {
			return;
		}
		
		currentState=HIDDEN_STATE;
		isCollidable=false;

		System.out.println("HIT!!! SET TO HIDDEN STATE");
	}
	
	//added by Mike, 20240805
	@Override
	public void keyPressed(KeyEvent key) {		
	}
	
	@Override
	public void keyReleased(KeyEvent key) {		
	}
	
	
	//reference: Usbong Game Off 2023
	public void processMouseInput(MouseEvent e, int iHeroX, int iHeroY) {
		
		//if hidden, i.e. not available, for use;
		//if (getCurrentState()!=HIDDEN_STATE) {
		if (getCurrentState()==ACTIVE_STATE) {	
			return;
		}

		setCurrentState(ACTIVE_STATE);
		//added by Mike, 20240828
		setCollidable(true);

		this.setX(iHeroX);
		this.setY(iHeroY);
		
		//added by Mike, 20240826
		iXInitialDistanceTraveled=getX();
		iYInitialDistanceTraveled=getY();
		
		iXCurrentDistanceTraveled=0;
		iYCurrentDistanceTraveled=0;
		
	    int iDeltaX=(e.getX())-(this.getX()+this.getWidth()/2);
        int iDeltaY=(e.getY())-(this.getY()+this.getHeight()/2);

	    iDeltaY*=-1;

	    double dMainImageTileStepAngleRad=Math.atan2(iDeltaX,iDeltaY);

        int iMainImageTileStepAngle=(int)(dMainImageTileStepAngleRad*(180/Math.PI));

	    //clockwise
	    iMainImageTileStepAngle=(iMainImageTileStepAngle)%360;
	  
	    //System.out.println(">>>>>>>>>iMainImageTileStepAngle: "+iMainImageTileStepAngle);
		  
	    //newMainImageTileProjectilePosY=mainImageTileProjectileStepY*Math.cos(fMainImageTileStepAngleRad).toFixed(3);
	    iStepY=(int)(ISTEP_Y_PLASMA*Math.cos(dMainImageTileStepAngleRad));//.toFixed(3);

        //newMainImageTileProjectilePosY*=-1;
	    iStepY*=-1;
	  
        //newMainImageTileProjectilePosX=mainImageTileProjectileStepX*Math.sin(fMainImageTileStepAngleRad).toFixed(3);
        iStepX=(int)(ISTEP_X_PLASMA*Math.sin(dMainImageTileStepAngleRad));//.toFixed(3);
	}
			
	@Override
	public void update() {
		setX(getX()+getStepX());		
		setY(getY()+getStepY());

/*		//also OK		
		//added by Mike, 20240826				
		double dDifferenceInXPos=Math.sqrt(Math.pow((iXInitialDistanceTraveled)-getX(),2));
		double dDifferenceInYPos=Math.sqrt(Math.pow((iYInitialDistanceTraveled)-getY(),2));
*/
		
		iXCurrentDistanceTraveled+=getStepX();
		iYCurrentDistanceTraveled+=getStepY();

		double dDifferenceInXPos=Math.abs(iXCurrentDistanceTraveled);
		double dDifferenceInYPos=Math.abs(iYCurrentDistanceTraveled);
	
		int iDifferenceInXPos=(int)dDifferenceInXPos;
		int iDifferenceInYPos=(int)dDifferenceInYPos;

		//hero always at the center of viewport
		//if (iDifferenceInXPos>iViewPortWidth/2) {
		if (iDifferenceInXPos>iViewPortWidth/2+iTileWidth) {			
			this.setCurrentState(HIDDEN_STATE);
		}

		if (iDifferenceInYPos>iViewPortHeight/2+iTileHeight) {
			this.setCurrentState(HIDDEN_STATE);
		}
/*
		System.out.println("!!!!!!!!!!!!!!!iViewPortWidth: "+iViewPortWidth);
		System.out.println("!!!!!!!!!!!!!!!iViewPortHeight: "+iViewPortHeight);
*/
			
/* 	//edited by Mike, 20240706; OK
		//animation
		if (iFrameCountDelay<iFrameCountDelayMax) {
			iFrameCountDelay++;
		}
		else {
			iFrameCount=(iFrameCount+1)%iFrameCountMax;
			iFrameCountDelay=0;
		}
*/
		iFrameCount=0;
	}	

  //added by Mike, 20240811
  @Override
  public void drawActor(Graphics g, int iInputX, int iInputY) {  
	Rectangle2D rect = new Rectangle2D.Float();

    //added by Mike, 20240623
    AffineTransform identity = new AffineTransform();

    Graphics2D g2d = (Graphics2D)g;
    AffineTransform trans = new AffineTransform();
    trans.setTransform(identity);
    
	trans.translate(iInputX,iInputY);

	trans.scale((iTileWidth*1.0)/iFrameWidth,(iTileHeight*1.0)/iFrameHeight);

	if (currentFacingState==FACING_RIGHT) {
		//trans.translate(getX(),getY());
	}
	else { //FACING_LEFT
		trans.translate(0,0-iFrameHeight);
	}

	g2d.setTransform(trans);

	//note: clip rect has to move with object position
    //edited by Mike, 20240623
    //reminder:
    //300 is object position;
    //iFrameCount*128 is the animation frame to show;
    //-iFrameCount*128 is move the object position to the current frame;
    //rect.setRect(300+iFrameCount*128-iFrameCount*128, 0, 128, 128);
	//edited by Mike, 20240714; from 20240714
    //rect.setRect(0+iFrameCount*iFrameWidth-iFrameCount*iFrameWidth, 0, iFrameWidth, iFrameHeight);

	if (currentFacingState==FACING_RIGHT) {
		//no animation yet; 0+iFrameCount*iFrameWidth-iFrameCount*iFrameWidth
	    rect.setRect(0, 0, iFrameWidth, iFrameHeight);
	}
	else { //FACING_LEFT
	   rect.setRect(0, 0+iFrameHeight, iFrameWidth, iFrameHeight);
	}

	Area myClipArea = new Area(rect);

    g2d.setClip(myClipArea);    
    g2d.drawImage(myBufferedImage,-iFrameCount*iFrameWidth, 0, null);

	//removed by Mike, 20240711; from 20240625
	//put after the last object to be drawn
	//g2d.dispose();
  }
  
//Additional Reference: 	https://docs.oracle.com/javase/tutorial/2d/advanced/examples/ClipImage.java; last accessed: 20240625
  @Override
  public void draw(Graphics g) {	  
	if (currentState==HIDDEN_STATE) {
		return;
	}

	drawActor(g, this.getX(), this.getY());
  }
}

//edited by Mike, 20240805; from 20240802
class Wall extends Actor {

    public Wall(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight) {
	  super(iOffsetScreenWidthLeftMargin, iOffsetScreenHeightTopMargin, iStageWidth, iStageHeight, iTileWidth, iTileHeight);
		
	  try {		  
		  myBufferedImage = ImageIO.read(new File("./res/wall.png"));
      } catch (IOException ex) {
      }
	  
	  myTileType=TILE_WALL;
	}

	//added by Mike, 20240628
	@Override
	public void reset() {
		iWidth=iTileWidth;
		iHeight=iTileHeight;		

		//currentFacingState=FACING_LEFT;
		currentFacingState=FACING_RIGHT;

		iFrameCount=0;
		iFrameCountDelay=0;
		iRotationDegrees=0;

		//added by Mike, 20240629
		int iMyKeysDownLength = myKeysDown.length;
		for (int i=0; i<iMyKeysDownLength; i++) {
			myKeysDown[i]=false;
		}		
		//removed by Mike, 20240805; from 20240730
		//myKeysDown[KEY_D]=true; //false;
		
		//added by Mike, 20240804		
		currentState=ACTIVE_STATE;
		isCollidable=true;

		//added by Mike, 20240806
		iStepX=ISTEP_X_DEFAULT;//*2; //faster by 1 than the default
		iStepY=ISTEP_Y_DEFAULT;//*2; //faster by 1 than the default

	}

	public boolean wallHitBy(Actor aLevel, Actor a) {
		System.out.println("HIT!!! STOP ACTOR FROM PASSING!");

		//if (a.getCurrentFacingState()==FACING_LEFT) {
		if (a.myKeysDown[KEY_A]) {			
			if (aLevel.getStepX()!=999999) { //if NOT should go right
				//push back the viewport
				aLevel.setX(aLevel.getX()+aLevel.getStepX());			
				//System.out.println("STOP! >>>>>>>FACING_LEFT");
				aLevel.iStepX=999999;					
				aLevel.iStepY=ISTEP_Y_MAX;
			}			
		}		
		//else if (a.getCurrentFacingState()==FACING_RIGHT) {
		else if (a.myKeysDown[KEY_D]) {			
			if (aLevel.getStepX()!=-999999) { //if NOT should go left
				//push back the viewport
				aLevel.setX(aLevel.getX()-aLevel.getStepX()); 
				//System.out.println("STOP! >>>>>>>FACING_RIGHT");
				aLevel.iStepX=-999999;				
				aLevel.iStepY=ISTEP_Y_MAX;
			}			
		}		
		
		//if (a.getCurrentFacingState()==FACING_UP) {
		if (a.myKeysDown[KEY_W]) {	
			if (aLevel.getStepY()!=999999) { //if NOT forced to go down 
				//push back the viewport
				aLevel.setY(aLevel.getY()+aLevel.getStepY()); 
				//System.out.println("STOP! >>>>>>>FACING_UP");						
				aLevel.iStepY=999999;	
				aLevel.iStepX=ISTEP_X_MAX;//0;			
			}		
		}		
		//else if (a.getCurrentFacingState()==FACING_DOWN) {
		else if (a.myKeysDown[KEY_S]) {
			if (aLevel.getStepY()!=-999999) { //if NOT should go up
				//push back the viewport
				aLevel.setY(aLevel.getY()-aLevel.getStepY()); 
				//System.out.println("STOP! >>>>>>>FACING_DOWN");			
				aLevel.iStepY=-999999;		
				aLevel.iStepX=ISTEP_X_MAX;//0;	
			}
		}		
		
		currentFacingState=a.getCurrentFacingState();
		
		//System.out.println(">>>>>>>>>> currentFacingState: "+currentFacingState);
		
		return true;
	}
	
	//added by Mike, 20240805
	@Override
	public void keyPressed(KeyEvent key) {		
	}
	
	@Override
	public void keyReleased(KeyEvent key) {		
	}
		
	@Override
	public void update() {		
		//setX(getX()+getStepX());

/* 	//edited by Mike, 20240706; OK
		//animation
		if (iFrameCountDelay<iFrameCountDelayMax) {
			iFrameCountDelay++;
		}
		else {
			iFrameCount=(iFrameCount+1)%iFrameCountMax;
			iFrameCountDelay=0;
		}
*/
		iFrameCount=0;
	}	

@Override
//  public void drawActor(Graphics g) {	  
  public void drawActor(Graphics g, int iInputX, int iInputY) {  
	Rectangle2D rect = new Rectangle2D.Float();
    AffineTransform identity = new AffineTransform();

    Graphics2D g2d = (Graphics2D)g;
    AffineTransform trans = new AffineTransform();
    trans.setTransform(identity);
	trans.translate(iInputX,iInputY);

	//scales from top-left as reference point
    //trans.scale(2,2);
	//rotates using top-left as anchor
    //trans.rotate(Math.toRadians(45)); //input in degrees

/* //removed by Mike, 20240706; OK
	//rotate at center; put translate after rotate
	iRotationDegrees=(iRotationDegrees+10)%360;
    trans.rotate(Math.toRadians(iRotationDegrees));
    trans.translate(-iFrameWidth/2,-iFrameHeight/2);
*/

	//edited by Mike, 20240714; from 20240708
/*
	System.out.println("iTileWidth: "+iTileWidth);
	System.out.println("iTileHeight: "+iTileHeight);
*/
	//scale to size of iTileWidth and iTileHeight;
	//example: iTileWidth=83
	//iFrameWidth=128;
	//trans.scale(0.5,0.5);
	//double temp = iTileWidth*1.0/iFrameWidth;
	//System.out.println("temp: "+temp);
	//edited by Mike, 20240811
	trans.scale((iTileWidth*1.0)/iFrameWidth,(iTileHeight*1.0)/iFrameHeight);

	//put this after scale;
	//move the input image to the correct row of the frame
	if (currentFacingState==FACING_RIGHT) {
		//trans.translate(getX(),getY());
	}
	else { //FACING_LEFT
		trans.translate(0,0-iFrameHeight);
	}

	g2d.setTransform(trans);

	//note: clip rect has to move with object position
    //edited by Mike, 20240623
    //reminder:
    //300 is object position;
    //iFrameCount*128 is the animation frame to show;
    //-iFrameCount*128 is move the object position to the current frame;
    //rect.setRect(300+iFrameCount*128-iFrameCount*128, 0, 128, 128);
	//edited by Mike, 20240714; from 20240714
    //rect.setRect(0+iFrameCount*iFrameWidth-iFrameCount*iFrameWidth, 0, iFrameWidth, iFrameHeight);

	if (currentFacingState==FACING_RIGHT) {
		//no animation yet; 0+iFrameCount*iFrameWidth-iFrameCount*iFrameWidth
	    rect.setRect(0, 0, iFrameWidth, iFrameHeight);
	}
	else { //FACING_LEFT
	   rect.setRect(0, 0+iFrameHeight, iFrameWidth, iFrameHeight);
	}

	Area myClipArea = new Area(rect);

    //edited by Mike, 20240625; from 20240623
    g2d.setClip(myClipArea);

    //g2d.drawImage(image, trans, this);
    //g2d.drawImage(myBufferedImage, trans, null);
    //g2d.drawImage(myBufferedImage,300-iFrameCount*128, 0, null);

	//edited by Mike, 20240714
    g2d.drawImage(myBufferedImage,-iFrameCount*iFrameWidth, 0, null);

	//removed by Mike, 20240711; from 20240625
	//put after the last object to be drawn
	//g2d.dispose();
  }
  
//Additional Reference: 	https://docs.oracle.com/javase/tutorial/2d/advanced/examples/ClipImage.java; last accessed: 20240625
  @Override
  public void draw(Graphics g) {	  
	if (currentState==HIDDEN_STATE) {
		return;
	}

	int iDifferenceInXPos=(this.getX());//-iViewPortX);			
	int iDifferenceInYPos=(this.getY());//-iViewPortY);	

	drawActor(g, this.getX(), this.getY());
  }
}

//added by Mike, 20240711
class BackgroundCanvas extends Actor {
/*	
	private final int TILE_BLANK=0;
	private final int TILE_TREE=1;
*/		
    public BackgroundCanvas(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight) {
	
	super(iOffsetScreenWidthLeftMargin, iOffsetScreenHeightTopMargin, iStageWidth, iStageHeight, iTileWidth, iTileHeight);
		
	//added by Mike, 20240727
	tileMap = new int[MAX_TILE_MAP_HEIGHT][MAX_TILE_MAP_WIDTH];

	  try {
		  myBufferedImage = ImageIO.read(new File("./res/background.png"));
      } catch (IOException ex) {
      }

	  //added by Mike, 20240708
	  this.iOffsetScreenWidthLeftMargin=iOffsetScreenWidthLeftMargin;
	  this.iOffsetScreenHeightTopMargin=iOffsetScreenHeightTopMargin;

	  //added by Mike, 20240628
	  this.iStageWidth=iStageWidth;
	  this.iStageHeight=iStageHeight;

	  //added by Mike, 20240714
	  this.iTileWidth=iTileWidth;
	  this.iTileHeight=iTileHeight;

	  //added by Mike, 20240629
	  myKeysDown = new boolean[iNumOfKeyTypes];

	  reset();
	}

	//added by Mike, 20240628
	@Override
	public void reset() {
		iWidth=iFrameWidth;
		iHeight=iFrameHeight;

		setX(iOffsetScreenWidthLeftMargin+0);
		setY(iOffsetScreenHeightTopMargin+0);

		iFrameCount=0;
		iFrameCountDelay=0;
		iRotationDegrees=0;

		//added by Mike, 20240629
		int iMyKeysDownLength = myKeysDown.length;
		for (int i=0; i<iMyKeysDownLength; i++) {
			myKeysDown[i]=false;
		}
		//added by Mike, 20240730
		myKeysDown[KEY_D]=true;
		
		
		//added by Mike, 20240727
	    for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
		  for (int k=0; k<MAX_TILE_MAP_WIDTH; k++) {
			tileMap[i][k]=TILE_BLANK;
		  }
	    }	
		
		//start values in default view port position;
		tileMap[0][2]=TILE_TREE;		
		tileMap[1][3]=TILE_TREE;	
		tileMap[2][4]=TILE_TREE;	

		//added by Mike, 20240807
		tileMap[5][0]=TILE_TREE;	
		tileMap[6][0]=TILE_TREE;	

		//added by Mike, 20240729
		//tileMap[1][10]=TILE_TREE;	

		tileMap[MAX_TILE_MAP_HEIGHT-1][0]=TILE_TREE;		
		tileMap[MAX_TILE_MAP_HEIGHT-1][1]=TILE_TREE;		
		tileMap[MAX_TILE_MAP_HEIGHT-1][2]=TILE_TREE;		
		
		tileMap[MAX_TILE_MAP_HEIGHT-1][13-1]=TILE_TREE;		
		tileMap[0][13-1]=TILE_TREE;		

		//added by Mike, 20240806
		tileMap[MAX_TILE_MAP_HEIGHT-1][MAX_TILE_MAP_WIDTH-1]=TILE_TREE;		
		tileMap[MAX_TILE_MAP_HEIGHT-1][MAX_TILE_MAP_WIDTH-2]=TILE_TREE;		
		tileMap[MAX_TILE_MAP_HEIGHT-1][MAX_TILE_MAP_WIDTH-3]=TILE_TREE;		

		tileMap[MAX_TILE_MAP_HEIGHT-1][26-1]=TILE_TREE;		
		tileMap[0][26-1]=TILE_TREE;
		
		//added by Mike, 20240823
/*		
	    for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
		  for (int k=MAX_TILE_MAP_WIDTH-10; k<MAX_TILE_MAP_WIDTH; k++) {
			tileMap[i][k]=TILE_TREE;
		  }
	    }	
*/		
/*
		for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
		  for (int k=0; k<MAX_TILE_MAP_WIDTH; k++) {
			tileMap[i][k]=TILE_TREE;
		  }
	    }	
*/
		//added by Mike, 20240827
		tileMap[6][12]=TILE_BASE;
		
		//added by Mike, 20240729
		iViewPortX=0;
		iViewPortY=0;
		iViewPortWidth=iStageWidth;
		iViewPortHeight=iStageHeight;	
		
		//added by Mike, 20240806
		iStepX=ISTEP_X_DEFAULT*2; //faster by 1 than the default
		iStepY=ISTEP_Y_DEFAULT*2; //faster by 1 than the default
	}
	
	public int getTileWidth(){
        return iTileWidth;
    }

    public int getTileHeight(){
        return iTileHeight;
    }
	
	//added by Mike, 20240827
	@Override
	public void hitBy(Actor a) {
/*	//TODO: -update: this		
		if (a.getMyTileType()==TILE_HERO) {
			return;
		}
		
		currentState=HIDDEN_STATE;
		isCollidable=false;

		System.out.println("HIT!!! SET TO HIDDEN STATE");
*/		
	}

	@Override
	public void update() {

/* 	//edited by Mike, 20240706; OK
		//animation
		if (iFrameCountDelay<iFrameCountDelayMax) {
			iFrameCountDelay++;
		}
		else {
			iFrameCount=(iFrameCount+1)%iFrameCountMax;
			iFrameCountDelay=0;
		}
*/
		iFrameCount=0;
	}
	
	//added by Mike, 20240828
	public boolean isActorIntersectingWithTile(Actor a, int tileX, int tileY) {
		//iOffsetXPosAsPixel=12; a1.getHeight()=83; 12/83=0.14457
		
		//remove negative sign
		int a1X=Math.abs(a.getX());
		int a1Y=Math.abs(a.getY());

		tileX=Math.abs(tileX);
		tileY=Math.abs(tileY);
		
		System.out.println(">>>>>>>>>>>> a1X: "+a1X);
		System.out.println(">>>>>>>>>>>> tileX: "+tileX);

		System.out.println(">>>>>>>>>>>> a1Y: "+a1Y);
		System.out.println(">>>>>>>>>>>> tileY: "+tileY);

		System.out.println(">>>>>>>>>>>> iTileWidth: "+iTileWidth);
		System.out.println(">>>>>>>>>>>> iTileHeight: "+iTileHeight);
		
		//object and object; not object and tile
		//iTileWidth
		int iOffsetYPosAsPixel=0;//a1.getHeight()/3;//10;//4; //diagonal hit; image has margin
		//iTileHeight
		int iOffsetXPosAsPixel=0;//a1.getWidth()/3;//10;//4;

		if ((tileY+iTileHeight <= a1Y+iOffsetYPosAsPixel) || //is the bottom of tile above the top of a1?
			(tileY >= a1Y+a.getHeight()-iOffsetYPosAsPixel) || //is the top of tile below the bottom of a1?
			(tileX+iTileWidth <= a1X+iOffsetXPosAsPixel)  || //is the right of tile to the left of a1?
			(tileX >= a1X+a.getWidth()-iOffsetXPosAsPixel)) { //is the left of tile to the right of a1?

			return false;
		}
		
		return true;
	}	
		
	public void collideWithBackgroundTile(Actor a) {
/*
		if ((!checkIsCollidable())||(!a.checkIsCollidable()))    
		{
			//not collidable
			return;
		}		
*/
		//plasma actor
		//if (!a.checkIsCollidable())
		if (!a.isActive())
		{
			//not collidable
			return;
		}	
		
		//TODO: -update: this; iDifferenceInXPos; iDifferenceInYPos
		for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
		  for (int k=0; k<MAX_TILE_MAP_WIDTH; k++) {
				int iDifferenceInXPos=iViewPortX-(iOffsetScreenWidthLeftMargin+iTileWidth*k);
				
				int iDifferenceInYPos=iViewPortY-(iOffsetScreenHeightTopMargin+iTileHeight*i);

				//edited by Mike, 20240807
				if (isTileInsideViewport(iViewPortX,iViewPortY, iOffsetScreenWidthLeftMargin+iTileWidth*k-iDifferenceInXPos,iOffsetScreenHeightTopMargin+iTileHeight*i-iDifferenceInYPos)) {		
	
//System.out.println(">>");

					if (tileMap[i][k]==TILE_BASE) {		
/*
System.out.println(">>>");


System.out.println(">>>>> iTileWidth*k: "+k);
System.out.println(">>>>> iTileHeight*i: "+i);


System.out.println("iViewPortX: "+iViewPortX);
System.out.println("iViewPortY: "+iViewPortY);


System.out.println("iDifferenceInXPos: "+iDifferenceInXPos);
System.out.println("iDifferenceInYPos: "+iDifferenceInYPos);
*/
				int iDifferenceInXPosOfViewPortAndBG=iViewPortX-(iOffsetScreenWidthLeftMargin+iTileWidth*0);
				
				int iDifferenceInYPosOfViewPortAndBG=iViewPortY-(iOffsetScreenHeightTopMargin+iTileHeight*0);


						if (isActorIntersectingWithTile(a,iOffsetScreenWidthLeftMargin+iTileWidth*k-iDifferenceInXPosOfViewPortAndBG,iOffsetScreenHeightTopMargin+iTileHeight*i-iDifferenceInYPosOfViewPortAndBG))
						{
							System.out.println("COLLISION!");
							
							//this.hitBy(a);
							tileMap[i][k]=TILE_BLANK;
							
							a.hitBy(this);
						}
					}
					
				}
		  }
		}
	}	
	
	//added by Mike, 20240811
	//background's x and y are iViewPortX and iViewPortY respectively
	public void synchronizeViewPortWithBackground(int x, int y) {
		setX(x); 
		setY(y); 
	}

	//edited by Mike, 20240806; from 20240729
	private boolean isTileInsideViewport(int iViewPortX, int iViewPortY, int iCurrTileX, int iCurrTileY)
{     
	//System.out.println(">>>iViewPortWidth: "+iViewPortWidth);
	
	//we need to multiply by 2 the viewport width and height; 
	//because both the viewport and the object move,
	//thereby increasing or decreasing the distance by two;
	//TODO: -verify: reduce excess viewport size
	//iViewPortWidth*2; iViewPortHeight*2
	
		//add 1 row before making the tile disappear
		if (iCurrTileY+iTileHeight*2 < iViewPortY || //is above the top of iViewPortY?
			iCurrTileY > iViewPortY+iViewPortHeight*2 || //is at the bottom of iViewPortY?
			//add 1 column before making the tile disappear
			iCurrTileX+iTileWidth*2 < iViewPortX || //is at the left of iViewPortX?
			iCurrTileX > iViewPortX+iViewPortWidth*2) { //is at the right of iViewPortX?
			return false;
		}
		
		return true;
	}

	//added by Mike, 20240726
	public void drawTree(Graphics g, int iInputTileX, int iInputTileY) {
		Rectangle2D rect = new Rectangle2D.Float();
		AffineTransform identity = new AffineTransform();

		Graphics2D g2d = (Graphics2D)g;
		AffineTransform trans = new AffineTransform();
		trans.setTransform(identity);
		trans.translate(iInputTileX,iInputTileY);

		//scales from top-left as reference point	
		//edited by Mike, 20240714; from 20240708
		//scale to size of iTileWidth and iTileHeight;
		//example: iTileWidth=83
		//iFrameWidth=128;
		//trans.scale(0.5,0.5);
		//double temp = iTileWidth*1.0/iFrameWidth;
		//System.out.println("temp: "+temp);
		trans.scale((iTileWidth*1.0)/iFrameWidth,(iTileHeight*1.0)/iFrameHeight);

		//added by Mike, 20240625
		g2d.setTransform(trans);

		//note: clip rect has to move with object position
		//edited by Mike, 20240623
		//reminder:
		//300 is object position;
		//iFrameCount*128 is the animation frame to show;
		//-iFrameCount*128 is move the object position to the current frame;
		//rect.setRect(300+iFrameCount*128-iFrameCount*128, 0, 128, 128);
		rect.setRect(0+iFrameCount*iFrameWidth-iFrameCount*iFrameWidth, 0, iFrameWidth, iFrameHeight);

		Area myClipArea = new Area(rect);

		//edited by Mike, 20240625; from 20240623
		g2d.setClip(myClipArea);
		//g.setClip(myClipArea);

		//g2d.drawImage(image, trans, this);
		//g2d.drawImage(myBufferedImage, trans, null);
		//g2d.drawImage(myBufferedImage,300-iFrameCount*128, 0, null);
		g2d.drawImage(myBufferedImage,-iFrameCount*iFrameWidth, 0, null);

		//removed by Mike, 20240711; from 20240625
		//put after the last object to be drawn
		//g2d.dispose();					
	}

	//added by Mike, 20240827
	public void drawBase(Graphics g, int iInputTileX, int iInputTileY) {
		Rectangle2D rect = new Rectangle2D.Float();
		AffineTransform identity = new AffineTransform();

		Graphics2D g2d = (Graphics2D)g;
		AffineTransform trans = new AffineTransform();
		trans.setTransform(identity);
		trans.translate(iInputTileX,iInputTileY);

		//scales from top-left as reference point	
		//edited by Mike, 20240714; from 20240708
		//scale to size of iTileWidth and iTileHeight;
		//example: iTileWidth=83
		//iFrameWidth=128;
		//trans.scale(0.5,0.5);
		//double temp = iTileWidth*1.0/iFrameWidth;
		//System.out.println("temp: "+temp);
		trans.scale((iTileWidth*1.0)/iFrameWidth,(iTileHeight*1.0)/iFrameHeight);

		//2nd row in background.png
		trans.translate(0,0-iFrameHeight);

		//added by Mike, 20240625
		g2d.setTransform(trans);

		//note: clip rect has to move with object position
		//edited by Mike, 20240623
		//reminder:
		//300 is object position;
		//iFrameCount*128 is the animation frame to show;
		//-iFrameCount*128 is move the object position to the current frame;
		//rect.setRect(300+iFrameCount*128-iFrameCount*128, 0, 128, 128);
		//2nd row in background.png
		rect.setRect(0+iFrameCount*iFrameWidth-iFrameCount*iFrameWidth, 0+iFrameHeight, iFrameWidth, iFrameHeight);

		Area myClipArea = new Area(rect);

		//edited by Mike, 20240625; from 20240623
		g2d.setClip(myClipArea);
		//g.setClip(myClipArea);

		//g2d.drawImage(image, trans, this);
		//g2d.drawImage(myBufferedImage, trans, null);
		//g2d.drawImage(myBufferedImage,300-iFrameCount*128, 0, null);
		g2d.drawImage(myBufferedImage,-iFrameCount*iFrameWidth, 0, null);

		//removed by Mike, 20240711; from 20240625
		//put after the last object to be drawn
		//g2d.dispose();					
	}	

//Additional Reference: 	https://docs.oracle.com/javase/tutorial/2d/advanced/examples/ClipImage.java; last accessed: 20240625
  @Override
  public void draw(Graphics g) {
	//identify the current tile in horizontal axis
	iViewPortX=getX();
	iViewPortY=getY();

	System.out.println("iViewPortX: "+iViewPortX);
	System.out.println("iViewPortY: "+iViewPortY);

	for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
	  for (int k=0; k<MAX_TILE_MAP_WIDTH; k++) {
	
			//edited by Mike, 20240807			
			int iDifferenceInXPos=iViewPortX-(iOffsetScreenWidthLeftMargin+iTileWidth*k);
			
			int iDifferenceInYPos=iViewPortY-(iOffsetScreenHeightTopMargin+iTileHeight*i);

			//edited by Mike, 20240807
			if (isTileInsideViewport(iViewPortX,iViewPortY, iOffsetScreenWidthLeftMargin+iTileWidth*k-iDifferenceInXPos,iOffsetScreenHeightTopMargin+iTileHeight*i-iDifferenceInYPos)) {		
				if (tileMap[i][k]==TILE_TREE) {		
					drawTree(g, iOffsetScreenWidthLeftMargin-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);	
				}
				else if (tileMap[i][k]==TILE_BASE) {		
					drawBase(g, iOffsetScreenWidthLeftMargin-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);	
				}				
			}	  
	  }
	}
	
	//note: noticeable slow-down on i3
	//added by Mike, 20240823; from 20240806
	int iCount=0;
	int iWrapTileCountOffset=10;//6; //10/13=0.7692; 76.92% of the stage width
	for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
	  iCount=0;
	  for (int k=MAX_TILE_MAP_WIDTH-1-iWrapTileCountOffset; k<MAX_TILE_MAP_WIDTH; k++) {
	
		//has reached left-most
		//use distance formula?
		int iDifferenceInXPos=iViewPortX-(iOffsetScreenWidthLeftMargin-iTileWidth*iCount);		
		int iDifferenceInYPos=iViewPortY-(iOffsetScreenHeightTopMargin+iTileHeight*i);
			
		//if (isTileInsideViewport(iViewPortX,iViewPortY, iOffsetScreenWidthLeftMargin+iTileWidth*k-iDifferenceInXPos,iOffsetScreenHeightTopMargin+iTileHeight*i-iDifferenceInYPos)) {	
			if (tileMap[i][k]==TILE_TREE) {						
				drawTree(g, iOffsetScreenWidthLeftMargin-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);								
				iCount++;
			}
		//}			
	  }
	}
	
//System.out.println(">>>>>>>>>>>>>iCount"+iCount);	

	iCount=0;
	//iWrapTileCountOffset=6;
	//edited by Mike, 20240808	
	for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
	  for (int k=0; k<iWrapTileCountOffset; k++) {	
		//has reached right-most

		//edited by Mike, 20240808
		int iDifferenceInXPos=(iOffsetScreenWidthLeftMargin+MAX_TILE_MAP_WIDTH*iTileWidth-iViewPortWidth/2-iTileWidth)-iViewPortX+iViewPortWidth/2+iTileWidth*k; 
		
		int iDifferenceInYPos=iViewPortY-(iOffsetScreenHeightTopMargin+iTileHeight*i);

		if (tileMap[i][k]==TILE_TREE) {								
			drawTree(g, iOffsetScreenWidthLeftMargin+iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);	
			
			iCount++;
		}
	  }
	}
  }
}

//added by Mike, 20240804; from 20240803
//for Actor object positions
class Level2D extends Actor {	
	
	//added by Mike, 20240804
	//private EnemyAircraft myEnemyAircraft;
	private final int MAX_ENEMY_AIRCRAFT_COUNT=1;//1;//2; //5;
	private EnemyAircraft[] myEnemyAircraftContainer;
	
	//added by Mike, 20240809
	private final int MAX_WALL_COUNT=0;//0;//16;//1;//2; 
	private Wall[] myWallContainer;

	//added by Mike, 20240825
	private final int MAX_PLASMA_COUNT=5;//1; 
	private Plasma[] myPlasmaContainer;
	
	//added by Mike, 20240809
	BackgroundCanvas myBackgroundCanvas;
	
	//added by Mike, 20240810
	RobotShip myRobotShip;	
		
    public Level2D(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight) {
	  super(iOffsetScreenWidthLeftMargin, iOffsetScreenHeightTopMargin, iStageWidth, iStageHeight, iTileWidth, iTileHeight);
		
	  //added by Mike, 20240708
	  this.iOffsetScreenWidthLeftMargin=iOffsetScreenWidthLeftMargin;
	  this.iOffsetScreenHeightTopMargin=iOffsetScreenHeightTopMargin;

	  //added by Mike, 20240628
	  this.iStageWidth=iStageWidth;
	  this.iStageHeight=iStageHeight;

	  //added by Mike, 20240714
	  this.iTileWidth=iTileWidth;
	  this.iTileHeight=iTileHeight;
	  
	  //System.out.println("iTileWidth: "+iTileWidth);

	  //added by Mike, 20240629
	  myKeysDown = new boolean[iNumOfKeyTypes];

	  myEnemyAircraftContainer = new EnemyAircraft[MAX_ENEMY_AIRCRAFT_COUNT];
	  
	  for (int i=0; i<MAX_ENEMY_AIRCRAFT_COUNT; i++) {
		  myEnemyAircraftContainer[i] = new EnemyAircraft(iOffsetScreenWidthLeftMargin,iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight);
	  }
	  
	  //added by Mike, 20240809
	  myWallContainer = new Wall[MAX_WALL_COUNT];
	  
	  for (int i=0; i<MAX_WALL_COUNT; i++) {
		  myWallContainer[i] = new Wall(iOffsetScreenWidthLeftMargin,iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight);
	  }
	  	  
	  //added by Mike, 20240825
	  myPlasmaContainer = new Plasma[MAX_PLASMA_COUNT];
	  
	  for (int i=0; i<MAX_PLASMA_COUNT; i++) {
		  myPlasmaContainer[i] = new Plasma(iOffsetScreenWidthLeftMargin,iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight);
	  }
	  
	  //added by Mike, 20240809
	  myBackgroundCanvas = new BackgroundCanvas(0+iOffsetScreenWidthLeftMargin,0+iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight);

	  //added by Mike, 20240810
	  myRobotShip = new RobotShip(iOffsetScreenWidthLeftMargin,iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight);	
	  
	  reset();
	  		
	  //added by Mike, 20240804
	  initLevel();
	}

	//added by Mike, 20240628
	@Override
	public void reset() {
		iWidth=iFrameWidth;
		iHeight=iFrameHeight;
		
		setX(iOffsetScreenWidthLeftMargin+0);
		setY(iOffsetScreenHeightTopMargin+0);

		//added by Mike, 20240811
		setWidth(iStageWidth);
		setHeight(iStageHeight);
	  		
		iFrameCount=0;
		iFrameCountDelay=0;
		iRotationDegrees=0;

		//added by Mike, 20240629
		int iMyKeysDownLength = myKeysDown.length;
		for (int i=0; i<iMyKeysDownLength; i++) {
			myKeysDown[i]=false;
		}
		//edited by Mike, 20240805; from 20240730
		//myKeysDown[KEY_D]=true;
				
		//added by Mike, 20240727
	    for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
		  for (int k=0; k<MAX_TILE_MAP_WIDTH; k++) {
			tileMap[i][k]=TILE_BLANK;
		  }
	    }	
	
		//start values in default view port position;
		//tileMap[1][6]=TILE_AIRCRAFT;	
		tileMap[5][0]=TILE_AIRCRAFT;	
		
		//tileMap[1][7]=TILE_AIRCRAFT;	
		//tileMap[1][MAX_TILE_MAP_WIDTH-10]=TILE_AIRCRAFT;	

		//tileMap[1][11]=TILE_AIRCRAFT;	
/*		
		tileMap[1][14]=TILE_AIRCRAFT;	
*/		
		
		//tileMap[5][1]=TILE_WALL;
		
		//added by Mike, 20240809
/*		
		tileMap[5][5]=TILE_WALL;			
		tileMap[5][6]=TILE_WALL;	

		tileMap[5][8]=TILE_WALL;	

		tileMap[5][4]=TILE_WALL;	
		tileMap[5][3]=TILE_WALL;	
		tileMap[5][2]=TILE_WALL;	
		tileMap[5][1]=TILE_WALL;	
*/		
		//tileMap[5][0]=TILE_WALL;	
/*
		tileMap[5][MAX_TILE_MAP_WIDTH-1]=TILE_WALL;	
		tileMap[5][MAX_TILE_MAP_WIDTH-2]=TILE_WALL;	
		tileMap[5][MAX_TILE_MAP_WIDTH-3]=TILE_WALL;	
		tileMap[5][MAX_TILE_MAP_WIDTH-4]=TILE_WALL;	
		
		tileMap[5][MAX_TILE_MAP_WIDTH-5]=TILE_WALL;	
		tileMap[5][MAX_TILE_MAP_WIDTH-6]=TILE_WALL;	
		tileMap[5][MAX_TILE_MAP_WIDTH-7]=TILE_WALL;	
		tileMap[5][MAX_TILE_MAP_WIDTH-8]=TILE_WALL;	
*/		

/*		//removed by Mike, 20240826		
		//added by Mike, 20240825
		//TODO: -update: this; debug only
		tileMap[5][5]=TILE_PLASMA;
*/
		
		//added by Mike, 20240817; from 20240729
		iViewPortX=iOffsetScreenWidthLeftMargin+0;
		iViewPortY=iOffsetScreenHeightTopMargin+0;
		iViewPortWidth=iStageWidth;
		iViewPortHeight=iStageHeight;	
		
		//added by Mike, 20240806
		//TODO: -add: acceleration; setting iStepX and iStepY to zero after hitting Wall (setting to 1 reduces shake);
		iStepX=ISTEP_X_DEFAULT*2; //faster by 1 than the default
		iStepY=ISTEP_Y_DEFAULT*2; //faster by 1 than the default
	}
	
	//added by Mike, 20240825	
	@Override	
	public void mousePressed(MouseEvent e) {
		//moveSquare(e.getX(),e.getY());
		
	  //myPlasmaContainer[0].processMouseInput(e, myRobotShip.getX(),myRobotShip.getY());
		
	  for (int i=0; i<MAX_PLASMA_COUNT; i++) {		  
		  if (!myPlasmaContainer[i].isActive()) {
			myPlasmaContainer[i].processMouseInput(e, myRobotShip.getX(),myRobotShip.getY());
			break;
		  }
	  }	
	}
	
	//TODO: -fix: collision detection; use TILE_WALL in Background class;
	@Override
	public void update() {		
		
		if (!getIsViewPortStopped()) {
			//added by Mike, 20240827
			boolean bHasPressedAnyKey=false;
			
			if (myKeysDown[KEY_A])
			{
				if (iStepX==999999) { //if should go right, KEY_D
				}
				else {
					iStepX=-ISTEP_X_MAX;				
					//setX(getX()-iStepX);
					setX(getX()+iStepX);
				}
				
				//iStepY=ISTEP_Y_MAX;			
				bHasPressedAnyKey=true;
			}
			else if (myKeysDown[KEY_D])
			{				
				if (iStepX==-999999) { //if should go left, KEY_A
				}
				else {
					iStepX=ISTEP_X_MAX;
					setX(getX()+iStepX);
				}			

				//iStepY=ISTEP_Y_MAX;				
				bHasPressedAnyKey=true;				
			}

			if (myKeysDown[KEY_W])
			{
				if (iStepY==999999) { //if should go down, KEY_S
				}
				else {
					//edited by Mike, 20240827
					iStepY=-ISTEP_Y_DEFAULT;//-ISTEP_Y_MAX;
					
					//edited by Mike, 20240827
					//setY(getY()+iStepY);		

					//1 tile higher than iViewPortHeight					
					if (getY()>0+iOffsetScreenHeightTopMargin-iTileHeight) {
						setY(getY()+iStepY);					
					}					
				}
				
				//iStepX=ISTEP_X_MAX;				
				//bHasPressedAnyKey=true;				
			}
			else if (myKeysDown[KEY_S])
			{
				if (iStepY==-999999) { //if should go up, KEY_W
				}
				else {
					//edited by Mike, 20240827
					iStepY=ISTEP_Y_DEFAULT;//ISTEP_Y_MAX;
					
					//edited by Mike, 20240827					
					//setY(getY()+iStepY);					
					
					//1 tile lower than iViewPortHeight
					if (getY()<iOffsetScreenHeightTopMargin+iTileHeight) {
						setY(getY()+iStepY);					
					}
				}				
				
				//iStepX=ISTEP_X_MAX;				
				//bHasPressedAnyKey=true;
			}

/*			
			//added by Mike, 20240827
			if (!bHasPressedAnyKey) {
				//reset to default
				if (iStepX<0) {
					iStepX=-ISTEP_X_DEFAULT;
				}
				else {
					iStepX=ISTEP_X_DEFAULT;
				}
								
				setX(getX()+iStepX);
			}
*/			
		}

		//added by Mike, 20240807
		iViewPortX=this.getX();
		iViewPortY=this.getY();

		//note: however, if not in viewport, object won't move;
		//horizontal
		//wrap around;
		//left-most
		if (this.getX()+this.getWidth()/2<=0+iOffsetScreenWidthLeftMargin) {
			setX(iViewPortX+MAX_TILE_MAP_WIDTH*iTileWidth-iTileWidth);
		}
		
		//right-most				
		if (this.getX()+this.getWidth()/2>=iRightMostLevelWidth) {	
			setX(iOffsetScreenWidthLeftMargin+0-this.getWidth()/2); //OK;
		}
		
		//added by Mike, 20240730
		if (!bHasStarted) {
			myKeysDown[KEY_D]=false;
			bHasStarted=true;
		}
		
		//added by Mike, 20240810
		iViewPortX=this.getX();
		iViewPortY=this.getY();

		//iViewPortY=0;

		myBackgroundCanvas.synchronizeViewPortWithBackground(iViewPortX,iViewPortY);		
		myRobotShip.synchronizeViewPort(iViewPortX,iViewPortY, getStepX(),getStepY());

		myBackgroundCanvas.synchronizeKeys(myKeysDown);
		myRobotShip.synchronizeKeys(myKeysDown);		
		//-----------------------------------------------------------
		
		for (int i=0; i<MAX_ENEMY_AIRCRAFT_COUNT; i++) {
			
			//added by Mike, 20240811
			myEnemyAircraftContainer[i].synchronizeViewPort(iViewPortX,iViewPortY,getStepX(),getStepY());
			
			//added by Mike, 20240806			
			//if (isActorInsideViewPort(iViewPortX, iViewPortY, myEnemyAircraftContainer[i])) {						
				//System.out.println(">>>>>>>>>>>> UPDATE!!!");
				myEnemyAircraftContainer[i].update();			
			//}
		
			//note: however, if not in viewport, object won't move;
			//System.out.println("myEnemyAircraftContainer[i].getX(): "+myEnemyAircraftContainer[i].getX());
			
			//horizontal
			//wrap around;
			//put this BEFORE "went beyond left-most"
			//went beyond right-most;
			if (myEnemyAircraftContainer[i].getX()+myEnemyAircraftContainer[i].getWidth()>=iRightMostLevelWidth+iTileWidth-myEnemyAircraftContainer[i].getWidth()) {		
				myEnemyAircraftContainer[i].setX(iOffsetScreenWidthLeftMargin+0); //OK;
			}
			
			
			//went beyond left-most			
			if (myEnemyAircraftContainer[i].getX()+myEnemyAircraftContainer[i].getWidth()/2<=0+iOffsetScreenWidthLeftMargin)
			{	
				myEnemyAircraftContainer[i].setX(MAX_TILE_MAP_WIDTH*iTileWidth-myEnemyAircraftContainer[i].getWidth());
			}			
		}
								
		//added by Mike, 20240809
		//TODO: -add: all tile Actor objects in a container
		for (int i=0; i<MAX_WALL_COUNT; i++) {		    
			myWallContainer[i].synchronizeViewPort(iViewPortX,iViewPortY,getStepX(),getStepY());
/*	//edited by Mike, 20240820
			if (isActorInsideViewPort(iViewPortX, iViewPortY, myWallContainer[i])) {		
*/			
				myWallContainer[i].update();			
/*				
			}
*/			
			//note: however, if not in viewport, object won't move;
			//horizontal			
			//wrap around;
			//right-most		
			//put this before "went beyond left-most"
			if (myWallContainer[i].getX()+myWallContainer[i].getWidth()>=iRightMostLevelWidth+iTileWidth-myWallContainer[i].getWidth()) {
				myWallContainer[i].setX(iOffsetScreenWidthLeftMargin+0); //OK;
			}			
			
			//went beyond left-most
			//note error when moving right up to the start; 
			//non-tile object appears
			//current solution: set the non-tile object to hidden;
			if (myWallContainer[i].getX()+myWallContainer[i].getWidth()/2<=0+iOffsetScreenWidthLeftMargin)
			{	
				myWallContainer[i].setX(MAX_TILE_MAP_WIDTH*iTileWidth-myWallContainer[i].getWidth());
			}	
		}
		
		//added by Mike, 20240825
		for (int i=0; i<MAX_PLASMA_COUNT; i++) {
			
			myPlasmaContainer[i].synchronizeViewPort(iViewPortX,iViewPortY,getStepX(),getStepY());			
			myPlasmaContainer[i].update();			
		
			//note: however, if not in viewport, object won't move;
			//System.out.println("myEnemyAircraftContainer[i].getX(): "+myEnemyAircraftContainer[i].getX());
			
			//horizontal
			//wrap around;
			//put this BEFORE "went beyond left-most"
			//went beyond right-most;
			if (myPlasmaContainer[i].getX()+myPlasmaContainer[i].getWidth()>=iRightMostLevelWidth+iTileWidth-myPlasmaContainer[i].getWidth()) {		
				myPlasmaContainer[i].setX(iOffsetScreenWidthLeftMargin+0); //OK;
			}
						
			//went beyond left-most			
			if (myPlasmaContainer[i].getX()+myPlasmaContainer[i].getWidth()/2<=0+iOffsetScreenWidthLeftMargin)
			{	
				myPlasmaContainer[i].setX(MAX_TILE_MAP_WIDTH*iTileWidth-myPlasmaContainer[i].getWidth());
			}			
			
			//added by Mike, 20240825
			for (int k=0; k<MAX_ENEMY_AIRCRAFT_COUNT; k++) {
				myPlasmaContainer[i].collideWith(myEnemyAircraftContainer[k]);
				
				//added by Mike, 20240827
				myBackgroundCanvas.collideWithBackgroundTile(myPlasmaContainer[i]);
			}
		}
			
/* 	//edited by Mike, 20240706; OK
		//animation
		if (iFrameCountDelay<iFrameCountDelayMax) {
			iFrameCountDelay++;
		}
		else {
			iFrameCount=(iFrameCount+1)%iFrameCountMax;
			iFrameCountDelay=0;
		}
*/
		iFrameCount=0;
		
		//added by Mike, 20240809
		myBackgroundCanvas.update();

		//added by Mike, 20240810		
		myRobotShip.update();
		
		this.collideWith(myRobotShip);
	}
	
	//added by Mike, 20240804
	@Override
	public void collideWith(Actor a) {

		for (int i=0; i<MAX_ENEMY_AIRCRAFT_COUNT; i++) {
			
			if (!myEnemyAircraftContainer[i].isActive()) {
				continue;
			}
			
/*			
			if ((!myEnemyAircraftContainer[i].checkIsCollidable())||(!a.checkIsCollidable()))    
			{
				//not collidable
				return;
			}
*/		

			if (isIntersectingRect(myEnemyAircraftContainer[i], a))		
			{
				myEnemyAircraftContainer[i].hitBy(a);
				a.hitBy(myEnemyAircraftContainer[i]);
			}
		}
		
		//added by Mike, 20240809
		//TODO: -add: all tile Actor objects in a container
		for (int i=0; i<MAX_WALL_COUNT; i++) {
			
			if (!myWallContainer[i].isActive()) {
				continue;
			}
			
/*			
			if ((!myEnemyAircraftContainer[i].checkIsCollidable())||(!a.checkIsCollidable()))    
			{
				//not collidable
				return;
			}
*/		
			if (isIntersectingRect(myWallContainer[i], a))		
			{
				if (myWallContainer[i].wallHitBy(this, a)) {
					setViewPortStopped(true);
				}
			}
		}
	}
		
	//pre-set enemy positions, but do not draw them all yet;
	public void initLevel() {
		int iEnemyAircraftCount=0;
		int iWallCount=0;
		int iPlasmaCount=0;		
		
		for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
			for (int k=0; k<MAX_TILE_MAP_WIDTH; k++) {
				if (tileMap[i][k]==TILE_AIRCRAFT) {		

					System.out.println("TILE_AIRCRAFT!");

					if (iEnemyAircraftCount<MAX_ENEMY_AIRCRAFT_COUNT) {

						if (myEnemyAircraftContainer[iEnemyAircraftCount].isActive()) {
							//edited by Mike, 20240818
							myEnemyAircraftContainer[iEnemyAircraftCount].setX(0+iOffsetScreenWidthLeftMargin+iTileWidth*k);			
							myEnemyAircraftContainer[iEnemyAircraftCount].setY(0+iOffsetScreenHeightTopMargin+iTileHeight*i);
										
							tileMap[i][k]=TILE_BLANK;
							
							iEnemyAircraftCount++;		
						}
					}					
				}
				//added by Mike, 20240809
				else if (tileMap[i][k]==TILE_WALL) {		
					System.out.println("TILE_WALL!");

					if (iWallCount<MAX_WALL_COUNT) {
											
						if (myWallContainer[iWallCount].isActive()) {
							
							myWallContainer[iWallCount].setX(0+iOffsetScreenWidthLeftMargin+iTileWidth*k);
							myWallContainer[iWallCount].setY(0+iOffsetScreenHeightTopMargin+iTileHeight*i);
													
							//tileMap[i][k]=TILE_BLANK;
							
							iWallCount++;		
							
							//continue; //stop once an available aircraft is found in the container
						}
					}					
				}
/*				//removed by Mike, 20240826
				//added by Mike, 20240825; debug only
				else if (tileMap[i][k]==TILE_PLASMA) {		
					System.out.println("TILE_PLASMA!");

					if (iPlasmaCount<MAX_PLASMA_COUNT) {
											
						if (myPlasmaContainer[iPlasmaCount].isActive()) {
							
							myPlasmaContainer[iPlasmaCount].setX(0+iOffsetScreenWidthLeftMargin+iTileWidth*k);
							myPlasmaContainer[iPlasmaCount].setY(0+iOffsetScreenHeightTopMargin+iTileHeight*i);
													
							//tileMap[i][k]=TILE_BLANK;
							
							iPlasmaCount++;		
							
							//continue; //stop once an available aircraft is found in the container
						}
					}					
				}
*/				
			}
	  }
	}
		
	//added by Mike, 20240805; from 20240731
	public void drawMargins(Graphics g) {
		Rectangle2D rect = new Rectangle2D.Float();

		//added by Mike, 20240623
		AffineTransform identity = new AffineTransform();

		Graphics2D g2d = (Graphics2D)g;
		AffineTransform trans = new AffineTransform();
		trans.setTransform(identity);
		g2d.setTransform(trans);
		
		//added by Mike, 20240731
		//https://stackoverflow.com/questions/1241253/inside-clipping-with-java-graphics; last accessed: 20240731
		//answer by: Savvas Dalkitsis, 20090806T2154
		g2d.setClip(new Area(new Rectangle2D.Double(0, 0, iOffsetScreenWidthLeftMargin*2+iStageWidth, iOffsetScreenHeightTopMargin+iStageHeight)));

		//paint the margins;
		g.setColor(Color.decode("#adb2b6")); //gray; 
		
		//cover the left margin
		g.fillRect(0,0,iOffsetScreenWidthLeftMargin,iStageHeight);

		//cover the right margin
		g.fillRect(0+iOffsetScreenWidthLeftMargin+iStageWidth,0,0+iOffsetScreenWidthLeftMargin+iStageWidth+iOffsetScreenWidthLeftMargin,iStageHeight);

		//System.out.println(">>>");
		
		//removed by Mike, 20240711; from 20240625
		//put after the last object to be drawn
		//g2d.dispose();		
	}	
	
    @Override
    public void draw(Graphics g) {
		//draw only if in viewport	
		iViewPortX=getX();
		iViewPortY=getY();
		
		//added by Mike, 20240809
		myBackgroundCanvas.draw(g);		
			
		for (int iEnemyAircraftCount=0; iEnemyAircraftCount<MAX_ENEMY_AIRCRAFT_COUNT; iEnemyAircraftCount++) {									
			if (myEnemyAircraftContainer[iEnemyAircraftCount].isActive()) {
				myEnemyAircraftContainer[iEnemyAircraftCount].draw(g);
			}
		}

		//added by Mike, 20240809
		for (int iWallCount=0; iWallCount<MAX_WALL_COUNT; iWallCount++) {	
					
			if (myWallContainer[iWallCount].isActive()) {
				myWallContainer[iWallCount].draw(g);		
			}
		}

		//added by Mike, 20240825
		for (int iPlasmaCount=0; iPlasmaCount<MAX_PLASMA_COUNT; iPlasmaCount++) {	
					
			if (myPlasmaContainer[iPlasmaCount].isActive()) {
				myPlasmaContainer[iPlasmaCount].draw(g);		
			}
		}
		
		myRobotShip.draw(g);

		drawMargins(g);		
	}
}