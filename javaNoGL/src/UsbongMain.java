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
 * @last updated: 20241019; from 20241018
 * @website: www.usbong.ph
 *
 */
/*
 Additional Notes:
 1) compile on Windows Machine;
	compile.bat

 2) Execute
	run.bat

 Known Issue:
 1) Problem when TILE_BASE from Background class is at the other side;
	It's not hit by plasma
	Current solution: Don't put TILE_BASE in the part of the tilemap near 0 or the MAX_TILE_MAP_WIDTH by iViewPortWidth distance;
	Reminder: currently, y or height not wrapped;

 References:
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
import java.awt.Polygon;

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

//added by Mike, 20240828
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

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
	int iOffsetScreenWidthRightMargin;

	//added by Mike, 20240903
	//widescreen
	int iOffsetWidthWideScreen;

    //added by Mike, 20240718
    int iStageWidth;
    int iStageHeight;

	int iTileWidth=64;//default
	int iTileHeight=64;//default

	final int iTileWidthCountMax=15;//13;
	final int iTileHeightCountMax=13;

	boolean bIsHidden=true;//false;

	//added by Mike, 20240905
	boolean bIsMaxedMonitorHeight=false;

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

		//added by Mike, 20240905
		//update later
		iOffsetScreenWidthRightMargin=iScreenWidth;//-iOffsetScreenWidthLeftMargin;//-iStageWidth;

		System.out.println("iOffsetScreenWidthLeftMargin: "+iOffsetScreenWidthLeftMargin);
		System.out.println("iOffsetScreenWidthRightMargin: "+iOffsetScreenWidthRightMargin);


		iScreenWidth=iScreenHeight;
		iTileWidth=iScreenWidth/iTileWidthCountMax;

		//edited by Mike, 20240904
		//iTileHeight=iScreenHeight/iTileHeightCountMax;
		iTileHeight=(iScreenHeight-iTileWidth*2)/iTileHeightCountMax;

		//added by Mike, 20240905
		//bIsMaxedMonitorHeight=true;

		//TODO: -verify: this
		//if not square monitor and has left margin
		if (iOffsetScreenWidthLeftMargin!=0) {

			//System.out.println(">>>>>>>>>>>>>iTileWidth: "+iTileWidth);

			iOffsetScreenWidthLeftMargin-=iTileWidth;
			iScreenWidth=iScreenHeight+iTileWidth*2;
			iTileWidth=iScreenWidth/iTileWidthCountMax;
			iTileHeight=iScreenHeight/iTileHeightCountMax;
			bIsMaxedMonitorHeight=true;
		}

		iOffsetScreenWidthRightMargin-=iOffsetScreenWidthLeftMargin;

		iOffsetScreenHeightTopMargin=(iScreenHeight-(iTileHeight*iTileHeightCountMax))/2;

		//use this as input parameter, instead of screenWidth and screenHeight

		//iStageWidth=iTileWidth*iTileWidthCountMax;
		iStageWidth=iTileWidth*iTileWidthCountMax+iOffsetWidthWideScreen;
		iStageHeight=iTileHeight*iTileHeightCountMax;

		//added by Mike, 20240905
		iOffsetScreenWidthRightMargin-=iStageWidth;
		System.out.println(">>iOffsetScreenWidthRightMargin: "+iOffsetScreenWidthRightMargin);

		//added by Mike, 20240906
		//iOffsetScreenWidthLeftMargin is larger than iOffsetScreenWidthRightMargin; adjust
		int iExcess = (iOffsetScreenWidthLeftMargin-iOffsetScreenWidthRightMargin);///2;

		iOffsetScreenWidthLeftMargin-=iExcess;

		System.out.println(">>iOffsetScreenWidthLeftMargin: "+iOffsetScreenWidthLeftMargin);

		if (bIsMaxedMonitorHeight) {
			iStageHeight=iScreenHeight;
		}

		System.out.println("iStageWidth: "+iStageWidth);
		System.out.println("iStageHeight: "+iStageHeight);

		redSquare  = new RedSquare();

		//edited by Mike, 20240905; from 20240903
		myLevel2D = new Level2D(0+iOffsetScreenWidthLeftMargin,0+iOffsetScreenHeightTopMargin, iStageWidth, iStageHeight, iTileWidth, iTileHeight, bIsMaxedMonitorHeight, iOffsetScreenWidthRightMargin);

		setBorder(BorderFactory.createLineBorder(Color.black));

		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				moveSquare(e.getX(),e.getY());

				//added by Mike, 20240825
				myLevel2D.mousePressed(e);
			}

			//added by Mike, 20240830
			public void mouseReleased(MouseEvent e){
				myLevel2D.mouseReleased(e);

				hideSquare();
			}
		});

		addMouseMotionListener(new MouseAdapter(){
			public void mouseDragged(MouseEvent e){
				moveSquare(e.getX(),e.getY());

				//added by Mike, 20240830; from 20240825
				myLevel2D.mouseDragged(e);
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
		bIsHidden=false;

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
/*			//edited by Mike, 20240831
            redSquare.setX(x);
            redSquare.setY(y);
*/

            redSquare.setX(x-CURR_W/2);
            redSquare.setY(y-CURR_H/2);


            // Repaint the square at the new location.
/*			//removed by Mike, 20240627;
			//repaint already done by TimerTask
            repaint(redSquare.getX(), redSquare.getY(),
                    redSquare.getWidth()+OFFSET,
                    redSquare.getHeight()+OFFSET);
*/
        }
    }

	private void hideSquare() {
		bIsHidden=true;
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
		//edited by Mike, 20240905
		g.setColor(Color.decode("#adb2b6")); //gray
		//g.setColor(Color.BLACK);

		g.fillRect(0,0,iScreenWidth,iScreenHeight);

		//square screen; make the excess, margins
		g.setColor(Color.decode("#31363a")); //lubuntu, BreezeModified theme; dark terminal

		g.fillRect(0+iOffsetScreenWidthLeftMargin,0+iOffsetScreenHeightTopMargin,iStageWidth,iStageHeight);

		myLevel2D.draw(g);

		if (!bIsHidden) {
			redSquare.paintSquare(g);
		}
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
	
	//added by Mike, 20240914
	protected int iInitialXPos=0;
	protected int iInitialYPos=0;

	//added by Mike, 20240831
	protected int iMouseXPos=0;
	protected int iMouseYPos=0;

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
	//edited by Mike, 20240924; aircraft turn animation, faster
	protected int iFrameCountDelayMax=12;//20;
	
	//added by Mike, 20241010
	protected int iDeathFrameCount=0;
	
	//added by Mike, 20240626
	protected int iRotationDegrees=0;
	protected int iFrameWidth=128;
	protected int iFrameHeight=128;
	
	//added by Mike, 20240918
	protected double dImageScaleOffsetWidth=0;
	protected double dImageScaleOffsetHeight=0;

	protected BufferedImage myBufferedImage;
	
	//added by Mike, 20241010
	protected BufferedImage explosionBufferedImage;

	//added by Mike, 20240708
	protected int iOffsetScreenWidthLeftMargin=0;
	protected int iOffsetScreenHeightTopMargin=0;

	//added by Mike, 20240905
	protected int iOffsetScreenWidthRightMargin=0;

	//added by Mike, 20240903
	protected int iOffsetWidthWideScreen=0;

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

	//reminder: the last column overlaps the first column
	protected final int MAX_TILE_MAP_WIDTH=52;//100;//39;//26;

	//added by Mike, 20240820
	//set later
	protected int iRightMostLevelWidth;
	protected int iLeftMostLevelWidth;

	//added by Mike, 20240812
	protected final int TILE_BLANK=0;
	protected final int TILE_HERO=1;
	protected final int TILE_AIRCRAFT=2;
	
	protected final int TILE_TREE=3;
	protected final int TILE_WALL=4;
	protected final int TILE_PLASMA=5;
	protected final int TILE_BASE=6;
	protected final int TILE_TEXT=7;

	protected final int TILE_AIRCRAFT_BOSS=8;
	protected final int TILE_ENEMY_PLASMA=9;
	
	protected int myTileType=0;

	//added by Mike, 20240714
	protected final int FACING_UP=0;
	protected final int FACING_DOWN=1;
	protected final int FACING_LEFT=2;
	protected final int FACING_RIGHT=3;
	protected int currentFacingState=0;

	//added by Mike, 20240730
	protected boolean bHasStarted=false;
	
	//added by Mike, 202340924
	protected boolean bIsChangingDirection=false;

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
		  
		  explosionBufferedImage = ImageIO.read(new File("./res/effects.png"));
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

	  iLeftMostLevelWidth=0+iOffsetScreenWidthLeftMargin+iTileWidth;

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
		
		//added by Mike, 20240924
		bIsChangingDirection=false;
	}

	//added by Mike, 20240804
	//TODO: -update: function name? due to can be true 
	//even if not in "ACTIVE_STATE", example in "DYING_STATE"
    public boolean isActive() {
		//edited by Mike, 20241016
        //if (currentState==ACTIVE_STATE) {
        if (currentState!=HIDDEN_STATE) {

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
	
	//edited by Mike, 20240914; from 20240806
	public boolean isTileInsideViewport(int iViewPortX, int iViewPortY, int iCurrTileX, int iCurrTileY) {
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

	//iVPX and iVPY are Level2D's x and y viewports
	//public void synchronizeViewPort(int iVPX, int iVPY) {
	public void synchronizeViewPort(int iVPX, int iVPY, int iVStepX, int iVStepY) {

		if (getMyTileType()==TILE_HERO) {
			iViewPortX=iVPX;
			iViewPortY=iVPY;
			return;
		}

		if ((iViewPortX==iVPX) && (iViewPortY==iVPY)) {
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
	}
	
	//added by Mike, 20240914
	//public boolean isActorIntersectingWithTile(Actor a, int tileX, int tileY) {
	public boolean isActorIntersectingWithActor(Actor a1, Actor a2, int a2X, int a2Y) {
		
		//iOffsetXPosAsPixel=12; a1.getHeight()=83; 12/83=0.14457

		if (!a1.isActive() || !a2.isActive())
		{
			//not collidable
			return false;
		}

		if (!a1.checkIsCollidable() || !a2.checkIsCollidable())
		{
			//not collidable
			return false;
		}
		
		int a1X=a1.getX();
		int a1Y=a1.getY();

		//object and object; not object and tile
		int iOffsetYPosAsPixel=0;
		int iOffsetXPosAsPixel=0;

		if ((a2Y+a2.getHeight() <= a1Y+iOffsetYPosAsPixel) || //is the bottom of tile above the top of a1?
			(a2Y >= a1Y+a1.getHeight()-iOffsetYPosAsPixel) || //is the top of tile below the bottom of a1?
			(a2X+a2.getWidth() <= a1X+iOffsetXPosAsPixel)  || //is the right of tile to the left of a1?
			(a2X >= a1X+a1.getWidth()-iOffsetXPosAsPixel)) { //is the left of tile to the right of a1?

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
	
	//added by Mike, 20240811
	//background's x and y are iViewPortX and iViewPortY respectively
	//verifying this;
	public void synchronizeViewPortWithBackground(int x, int y) {
		iViewPortX=x;
		iViewPortY=y;
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

	//added by Mike, 20240830
	public void mouseReleased(MouseEvent e) {
	}

	//added by Mike, 20240830
	public void mouseDragged(MouseEvent e) {
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
	
	//added by Mike, 20240914
	public void collideWithEnemy(Actor a) {
						
		//plasma actor
		if (!a.isActive())
		{
			//not collidable
			return;
		}

		if (!a.checkIsCollidable())
		{
			//not collidable
			return;
		}
				
		//System.out.println(">>>>>>>>>>>>>>>COLLIDE WITH ENEMY!!");

		int iDifferenceInXPosOfViewPortAndBG=iViewPortX-(iOffsetScreenWidthLeftMargin);

		int iDifferenceInYPosOfViewPortAndBG=iViewPortY-(iOffsetScreenHeightTopMargin);

		if (isActorIntersectingWithActor(this,a,a.getX()-iDifferenceInXPosOfViewPortAndBG,a.getY()-iDifferenceInYPosOfViewPortAndBG)) {
				System.out.println("COLLISION!");

				this.hitBy(a);
				//tileMap[i][k]=TILE_BLANK;

				a.hitBy(this);			
		}

		//edited by Mike, 20240919; from 20240918
		//based on draw(...) function in class EnemyAircraft

		//when actual viewport is in right side, while enemy aircraft is in left side				
		//based on wrap around; left-most
		int iDifferenceInXPos=(iViewPortX+MAX_TILE_MAP_WIDTH*iTileWidth-iTileWidth)-a.getX();	
		int iDifferenceInYPos=iViewPortY-(a.getY());

		if (isActorIntersectingWithActor(this,a,iOffsetScreenWidthLeftMargin-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos)) {
				//System.out.println("COLLISION AT PART 2!");

				this.hitBy(a);
				a.hitBy(this);			
		}

		//when actual viewport is in left side, while enemy aircraft is in right side
		iDifferenceInXPos=iViewPortX-(iOffsetScreenWidthLeftMargin+(MAX_TILE_MAP_WIDTH-1)*iTileWidth);
		iDifferenceInYPos=iViewPortY-(a.getY());

		if (isActorIntersectingWithActor(this,a,a.getX()-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos)) {
				//System.out.println("COLLISION AT PART 3!");

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
  
  public void drawExplosion(Graphics g, int iInputX, int iInputY) {

	Rectangle2D rect = new Rectangle2D.Float();
    AffineTransform identity = new AffineTransform();

    Graphics2D g2d = (Graphics2D)g;
	
    AffineTransform trans = new AffineTransform();
    trans.setTransform(identity);
	trans.translate(iInputX,iInputY);
	trans.scale((iTileWidth*1.0)/iFrameWidth,(iTileHeight*1.0)/iFrameHeight);

/*	
	//added by Mike, 20240714
	//put this after scale;
	//move the input image to the correct row of the frame

	if (currentFacingState==FACING_RIGHT) {
		//trans.translate(getX(),getY());
	}
	else { //FACING_LEFT
		trans.translate(0,0-iFrameHeight);
	}
*/

	//added by Mike, 20240625
	g2d.setTransform(trans);

/*	
	if (currentFacingState==FACING_RIGHT) {
		//no animation yet; 0+iFrameCount*iFrameWidth-iFrameCount*iFrameWidth
	    rect.setRect(0, 0, iFrameWidth, iFrameHeight);
	}
	else { //FACING_LEFT
	   rect.setRect(0, 0+iFrameHeight, iFrameWidth, iFrameHeight);
	}
*/
	rect.setRect(0, 0+iFrameHeight, iFrameWidth, iFrameHeight);
	
	Area myClipArea = new Area(rect);

    g2d.setClip(myClipArea);
    g2d.drawImage(explosionBufferedImage,-iFrameCount*iFrameWidth, 0, null);

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
			//edited by Mike, 20240924	
			//if already facing left
			if (currentFacingState==FACING_LEFT) {
				//bIsChangingDirection=false;				
			}
			else {
				currentFacingState=FACING_LEFT;
				
				if (bIsChangingDirection) {
					bIsChangingDirection=false;		
				}
				else {
					bIsChangingDirection=true;		
					iFrameCountDelay=0;				
				}
			}
	
			//currentFacingState=FACING_LEFT;
			////iStepX=-ISTEP_X_DEFAULT;
		}
		//edited by Mike, 20240902
		else if (myKeysDown[KEY_D])
		{
			//edited by Mike, 20240924	
			//if already facing left
			if (currentFacingState==FACING_RIGHT) {
				//bIsChangingDirection=false;
			}
			else {
				currentFacingState=FACING_RIGHT;

				if (bIsChangingDirection) {
					bIsChangingDirection=false;		
				}
				else {
					bIsChangingDirection=true;		
					iFrameCountDelay=0;				
				}
			}
			
			//currentFacingState=FACING_RIGHT;
			////iStepX=ISTEP_X_DEFAULT;
		}

		//edited by Mike, 20240809
		//note: robot ship doesn't not have FACING_UP or FACING_DOWN
		//TODO: -remove this

		if (myKeysDown[KEY_W])
		{
			//currentFacingState=FACING_UP;
			iStepY=-ISTEP_Y_DEFAULT;//ISTEP_Y_MAX;

			//setY(getY()+getStepY());

			if (getY()>0+iOffsetScreenHeightTopMargin) {
				setY(getY()+getStepY());
			}
		}

		if (myKeysDown[KEY_S])
		{
			//currentFacingState=FACING_DOWN;
			iStepY=ISTEP_Y_DEFAULT;//ISTEP_Y_MAX;
			//setY(getY()+getStepY());

			//if (getY()+getHeight()<0+iOffsetScreenHeightTopMargin+MAX_TILE_MAP_HEIGHT*iTileHeight) {
			if (getY()+getHeight()<0+iOffsetScreenHeightTopMargin+iStageHeight) {
				setY(getY()+getStepY());
			}
		}

		//added by Mike, 20240730
		if (!bHasStarted) {
			myKeysDown[KEY_D]=false;
			bHasStarted=true;
		}

		//return;

		//edited by Mike, 20241019; from 20240706; OK
		if (getCurrentState()==ACTIVE_STATE) {
			//animation
			if (iFrameCountDelay<iFrameCountDelayMax) {
				iFrameCountDelay++;
			}
			else {
				iFrameCount=(iFrameCount+1)%iFrameCountMax;
				//iFrameCountDelay=0;
				
				bIsChangingDirection=false;				
			}
		}
		//added by Mike, 20241019
		else if (getCurrentState()==DYING_STATE) {
			//animation
			if (iFrameCountDelay<iFrameCountDelayMax/2) {
				iFrameCountDelay++;
			}
			else {
				if (getCurrentState()==DYING_STATE) {
					iDeathFrameCount=(iDeathFrameCount+1)%iFrameCountMax;

					if (iDeathFrameCount==0) {
						setCurrentState(HIDDEN_STATE);
					}
				}
				
				iFrameCountDelay=0;			
			}
		}
		
		//iFrameCount=0;

		return;
	}

	//added by Mike, 20241016
	@Override
	public void hitBy(Actor a) {
/*		
		if (a.getMyTileType()==TILE_HERO) {
			return;
		}
*/

		//edited by Mike, 20241019
		//currentState=HIDDEN_STATE;
		currentState=DYING_STATE;
		
		isCollidable=false;

		//System.out.println("HIT!!! SET TO HIDDEN STATE");
		System.out.println("HIT!!! SET TO DYING STATE");		
	}
		
	//added by Mike, 20241019
  @Override
  public void drawExplosion(Graphics g, int iInputX, int iInputY) {
	Rectangle2D rect = new Rectangle2D.Float();
    AffineTransform identity = new AffineTransform();
    Graphics2D g2d = (Graphics2D)g;
	
    AffineTransform trans = new AffineTransform();
    trans.setTransform(identity);
	trans.translate(iInputX,iInputY);
	trans.scale((iTileWidth*1.0)/iFrameWidth,(iTileHeight*1.0)/iFrameHeight);

	//added by Mike, 20240714
	//put this after scale;
	trans.translate(0-(iDeathFrameCount)*iFrameWidth,0);	
	
	//added by Mike, 20240625
	g2d.setTransform(trans);
	
	rect.setRect(0+(iDeathFrameCount)*iFrameWidth,0, iFrameWidth,iFrameHeight);	
	
	
	Area myClipArea = new Area(rect);

    //edited by Mike, 20240625; from 20240623
    g2d.setClip(myClipArea);

	//edited by Mike, 20240924; from 20240714
    //g2d.drawImage(myBufferedImage,-(iFrameCount)*iFrameWidth, 0, null);
    g2d.drawImage(explosionBufferedImage,-(0)*iFrameWidth, 0, null);

	//removed by Mike, 20240711; from 20240625
	//put after the last object to be drawn
	//g2d.dispose();
  }		
		
  //Additional Reference: 	https://docs.oracle.com/javase/tutorial/2d/advanced/examples/ClipImage.java; last accessed: 20240625
  //edited by Mike, 20240924
  @Override
  public void drawActor(Graphics g, int iInputX, int iInputY) {
	Rectangle2D rect = new Rectangle2D.Float();
    AffineTransform identity = new AffineTransform();
    Graphics2D g2d = (Graphics2D)g;
    AffineTransform trans = new AffineTransform();
    trans.setTransform(identity);
	trans.translate(iInputX,iInputY);

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

/*	//note effect; noticeable lag;
	//reference: https://stackoverflow.com/questions/4248104/applying-a-tint-to-an-image-in-java; last accessed: 20240926
	
	for (int x = 0; x < myBufferedImage.getWidth(); x++) {
        for (int y = 0; y < myBufferedImage.getHeight(); y++) {

            //Color color = new Color(myBufferedImage.getRGB(x, y));
            Color color = new Color(myBufferedImage.getRGB(x, y));

			//reference: https://stackoverflow.com/questions/8978228/java-bufferedimage-how-to-know-if-a-pixel-is-transparent; last accessed: 20240926
			int pixel = myBufferedImage.getRGB(x,y);
			//transparent
			if( (pixel>>24) == 0x00 ) {
		      continue;
			}
			
            // do something with the color :) (change the hue, saturation and/or brightness)
            // float[] hsb = new float[3];
            // Color.RGBtoHSB(color.getRed(), old.getGreen(), old.getBlue(), hsb);

            // or just call brighter to just tint it
            Color brighter = color.brighter();

            myBufferedImage.setRGB(x, y, brighter.getRGB());
       }
    }		
*/	
	iFrameCount=0;//1;

	if (bIsChangingDirection) {		
		iFrameCount=1;
		//bIsChangingDirection=false;
	}
	else {
		//moving up
		if (myKeysDown[KEY_W]) {
			iFrameCount=2;
		}
		//moving down
		else if (myKeysDown[KEY_S]) {
			iFrameCount=3;
		}		
	}
	
	
	if (currentFacingState==FACING_RIGHT) {
		//trans.translate(getX(),getY());
		trans.translate(0-(iFrameCount)*iFrameWidth,0);			
	}
	else { //FACING_LEFT
		//trans.translate(0,0-iFrameHeight);
		trans.translate(0-(iFrameCount)*iFrameWidth,0-iFrameHeight);
	}
	
	//added by Mike, 20240625
	g2d.setTransform(trans);

	
	if (currentFacingState==FACING_RIGHT) {
		//no animation yet; 0+iFrameCount*iFrameWidth-iFrameCount*iFrameWidth
	    //rect.setRect(0, 0, iFrameWidth, iFrameHeight);
		
	    rect.setRect(0+(iFrameCount)*iFrameWidth,0, iFrameWidth,iFrameHeight);		
	}
	else { //FACING_LEFT
	   //rect.setRect(0, 0+iFrameHeight, iFrameWidth, iFrameHeight);
	   rect.setRect(0+(iFrameCount)*iFrameWidth, 0+iFrameHeight, iFrameWidth,iFrameHeight);	   
	}

	Area myClipArea = new Area(rect);

    //edited by Mike, 20240625; from 20240623
    g2d.setClip(myClipArea);

	//edited by Mike, 20240924; from 20240714
    //g2d.drawImage(myBufferedImage,-(iFrameCount)*iFrameWidth, 0, null);
    g2d.drawImage(myBufferedImage,-(0)*iFrameWidth, 0, null);

	//removed by Mike, 20240711; from 20240625
	//put after the last object to be drawn
	//g2d.dispose();
  }

//Additional Reference: 	https://docs.oracle.com/javase/tutorial/2d/advanced/examples/ClipImage.java; last accessed: 20240625
  //robotship
  @Override
  public void draw(Graphics g) {
	if (currentState==HIDDEN_STATE) {
		return;
	}

	//edited by Mike, 20241019
    //drawActor(g, this.getX(), this.getY());
	if (getCurrentState()!=DYING_STATE) {
		drawActor(g, this.getX(), this.getY());
	}
	else {
		drawExplosion(g, this.getX(), this.getY());		
	}
  }
}

class EnemyAircraft extends Actor {
	
	//edited by Mike, 20241018
    //public EnemyAircraft(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight) {
    public EnemyAircraft(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight, int iAircraftType) {
		
	  super(iOffsetScreenWidthLeftMargin, iOffsetScreenHeightTopMargin, iStageWidth, iStageHeight, iTileWidth, iTileHeight);

	  try {
		  if (iAircraftType==TILE_AIRCRAFT) { 
			  myBufferedImage = ImageIO.read(new File("./res/robotship2.png"));
		  }
		  else if (iAircraftType==TILE_AIRCRAFT_BOSS) { 
			  myBufferedImage = ImageIO.read(new File("./res/robotship3.png"));			  
		  }		  
      } catch (IOException ex) {
      }
	  
		
	  //added by Mike, 20241018
	  myTileType=iAircraftType;	  
	}

	@Override
	public void reset() {
		iWidth=iTileWidth;
		iHeight=iTileHeight;

		currentFacingState=FACING_RIGHT;//; //FACING_LEFT

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

		iStepX=ISTEP_X_DEFAULT;//*2; //faster by 1 than the default
		iStepY=ISTEP_Y_DEFAULT;//*2; //faster by 1 than the default

		//removed by Mike, 20241018
		//myTileType=TILE_AIRCRAFT;
	}

	@Override
	public void hitBy(Actor a) {
		//edited by Mike, 20241016
		//currentState=HIDDEN_STATE;
		currentState=DYING_STATE;
		
		isCollidable=false;

		//System.out.println("HIT!!! SET TO HIDDEN STATE");
		System.out.println("HIT!!! SET TO DYING STATE");
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
		//TODO: -update: this; AI

		//setX(getX()+getStepX());

		if (myTileType==TILE_AIRCRAFT) { 
			setX(getX()+getStepX());
		}
		else if (myTileType==TILE_AIRCRAFT_BOSS) { 
			//note: remove this to create stationary turret
			setX(getX()+getStepX());
		}
				
		if (!bHasStarted) {
			iInitialXPos=getX();
			iInitialYPos=getY();
			bHasStarted=true;
		}
		else {		
/*			//edited by Mike, 20241016		
			//going down
			if (iStepY==ISTEP_Y_DEFAULT) {
				if (getY()<iInitialYPos+iTileHeight*2) {
					setY(getY()+getStepY());
				}
				else {
					iStepY=-ISTEP_Y_DEFAULT;
				}
				
				myKeysDown[KEY_W]=false;
				myKeysDown[KEY_S]=true;
			}

			//going up
			if (iStepY==-ISTEP_Y_DEFAULT) {			
				if (getY()>iInitialYPos-iTileHeight*2) {
					setY(getY()+getStepY());
				}
				else {
					iStepY=ISTEP_Y_DEFAULT;						
				}

				myKeysDown[KEY_W]=true;
				myKeysDown[KEY_S]=false;
			}
*/			
		}
		


/*
		System.out.println("EnemyAircraft.get(): "+getX());
		System.out.println("EnemyAircraft.getStepX(): "+getStepX());
*/

		//edited by Mike, 20241010; from 20240706; OK
		//animation
		//System.out.println("iFrameCountDelay: "+iFrameCountDelay);
		//iFrameCountDelayMax=10;
		//if (iFrameCountDelay<iFrameCountDelayMax) {
		if (iFrameCountDelay<iFrameCountDelayMax/2) {
			iFrameCountDelay++;
		}
		else {
			//iFrameCount=(iFrameCount+1)%iFrameCountMax;			
			
			//edited by Mike, 20241016
			//iDeathFrameCount=(iDeathFrameCount+1)%iFrameCountMax;
			if (getCurrentState()==DYING_STATE) {
				iDeathFrameCount=(iDeathFrameCount+1)%iFrameCountMax;

				if (iDeathFrameCount==0) {
					setCurrentState(HIDDEN_STATE);
				}
			}
			
			iFrameCountDelay=0;			
		}

		//iFrameCount=0;
	}

/* //previously...; edited by Mike, 20240929
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
*/  

  //edited by Mike, 20241010
  @Override
  public void drawExplosion(Graphics g, int iInputX, int iInputY) {
	Rectangle2D rect = new Rectangle2D.Float();
    AffineTransform identity = new AffineTransform();
    Graphics2D g2d = (Graphics2D)g;
	
    AffineTransform trans = new AffineTransform();
    trans.setTransform(identity);
	trans.translate(iInputX,iInputY);
	trans.scale((iTileWidth*1.0)/iFrameWidth,(iTileHeight*1.0)/iFrameHeight);

	//added by Mike, 20240714
	//put this after scale;
/*
	iFrameCount=0;//1;

	if (bIsChangingDirection) {		
		iFrameCount=1;
		//bIsChangingDirection=false;
	}
	else {
		//moving up
		if (myKeysDown[KEY_W]) {
			iFrameCount=2;
		}
		//moving down
		else if (myKeysDown[KEY_S]) {
			iFrameCount=3;
		}		
	}
	
	if (currentFacingState==FACING_RIGHT) {
		//trans.translate(getX(),getY());
		trans.translate(0-(iFrameCount)*iFrameWidth,0);			
	}
	else { //FACING_LEFT
		//trans.translate(0,0-iFrameHeight);
		trans.translate(0-(iFrameCount)*iFrameWidth,0-iFrameHeight);
	}
*/
	
/*	
	//animation
	if (iFrameCountDelay<iFrameCountDelayMax) {
		iFrameCountDelay++;
	}
	else {
		iFrameCount=(iFrameCount+1)%iFrameCountMax;
		iFrameCountDelay=0;
	}	
	//iFrameCount=0;
*/	
	
	trans.translate(0-(iDeathFrameCount)*iFrameWidth,0);	
	
	//added by Mike, 20240625
	g2d.setTransform(trans);

/*	
	if (currentFacingState==FACING_RIGHT) {
		//no animation yet; 0+iFrameCount*iFrameWidth-iFrameCount*iFrameWidth
	    //rect.setRect(0, 0, iFrameWidth, iFrameHeight);
		
	    rect.setRect(0+(iFrameCount)*iFrameWidth,0, iFrameWidth,iFrameHeight);		
	}
	else { //FACING_LEFT
	   //rect.setRect(0, 0+iFrameHeight, iFrameWidth, iFrameHeight);
	   rect.setRect(0+(iFrameCount)*iFrameWidth, 0+iFrameHeight, iFrameWidth,iFrameHeight);	   
	}
*/
	
	rect.setRect(0+(iDeathFrameCount)*iFrameWidth,0, iFrameWidth,iFrameHeight);	
	
	//TODO: -update: this; add in translate; make not looped
	//second row in input image
	//rect.setRect(0+(iDeathFrameCount)*iFrameWidth,0+iFrameHeight, iFrameWidth,iFrameHeight);	

	
	Area myClipArea = new Area(rect);

    //edited by Mike, 20240625; from 20240623
    g2d.setClip(myClipArea);

	//edited by Mike, 20240924; from 20240714
    //g2d.drawImage(myBufferedImage,-(iFrameCount)*iFrameWidth, 0, null);
    g2d.drawImage(explosionBufferedImage,-(0)*iFrameWidth, 0, null);

	//removed by Mike, 20240711; from 20240625
	//put after the last object to be drawn
	//g2d.dispose();
  }
  
  //added by Mike, 20241010
 @Override
  public void drawActor(Graphics g, int iInputX, int iInputY) {
	Rectangle2D rect = new Rectangle2D.Float();
    AffineTransform identity = new AffineTransform();
    Graphics2D g2d = (Graphics2D)g;
    AffineTransform trans = new AffineTransform();
    trans.setTransform(identity);
	trans.translate(iInputX,iInputY);

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

	iFrameCount=0;//1;

	if (bIsChangingDirection) {		
		iFrameCount=1;
		//bIsChangingDirection=false;
	}
	else {
		//moving up
		if (myKeysDown[KEY_W]) {
			iFrameCount=2;
		}
		//moving down
		else if (myKeysDown[KEY_S]) {
			iFrameCount=3;
		}		
	}
	
	if (currentFacingState==FACING_RIGHT) {
		//trans.translate(getX(),getY());
		trans.translate(0-(iFrameCount)*iFrameWidth,0);			
	}
	else { //FACING_LEFT
		//trans.translate(0,0-iFrameHeight);
		trans.translate(0-(iFrameCount)*iFrameWidth,0-iFrameHeight);
	}
	
	//added by Mike, 20240625
	g2d.setTransform(trans);

	
	if (currentFacingState==FACING_RIGHT) {
		//no animation yet; 0+iFrameCount*iFrameWidth-iFrameCount*iFrameWidth
	    //rect.setRect(0, 0, iFrameWidth, iFrameHeight);
		
	    rect.setRect(0+(iFrameCount)*iFrameWidth,0, iFrameWidth,iFrameHeight);		
	}
	else { //FACING_LEFT
	   //rect.setRect(0, 0+iFrameHeight, iFrameWidth, iFrameHeight);
	   rect.setRect(0+(iFrameCount)*iFrameWidth, 0+iFrameHeight, iFrameWidth,iFrameHeight);	   
	}

	Area myClipArea = new Area(rect);

    //edited by Mike, 20240625; from 20240623
    g2d.setClip(myClipArea);

	//edited by Mike, 20240924; from 20240714
    //g2d.drawImage(myBufferedImage,-(iFrameCount)*iFrameWidth, 0, null);
    g2d.drawImage(myBufferedImage,-(0)*iFrameWidth, 0, null);

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
	
	//drawActor(g, this.getX(), this.getY());
	
	int iDifferenceInXPos=iViewPortX-(this.getX());	
	int iDifferenceInYPos=iViewPortY-(this.getY());

	if (isTileInsideViewport(iViewPortX,iViewPortY, this.getX()-iDifferenceInXPos,this.getY()-iDifferenceInYPos))	
	{	
		//edited by Mike, 20241016
		if (getCurrentState()!=DYING_STATE) {
			drawActor(g, iOffsetScreenWidthLeftMargin-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);
		}
		else {
			//added by Mike, 20241010	
			drawExplosion(g, iOffsetScreenWidthLeftMargin-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);		
		}
	}

	//when actual viewport is in right side, while enemy aircraft is in left side	
	//iViewPortX negative number
	int iViewPortXTemp=(iOffsetScreenWidthLeftMargin+(MAX_TILE_MAP_WIDTH-6)*iTileWidth);
		
	//based on wrap around; left-most
	iDifferenceInXPos=(iViewPortX+MAX_TILE_MAP_WIDTH*iTileWidth-iTileWidth)-this.getX();	
	

	if (isTileInsideViewport(iViewPortXTemp,iViewPortY, this.getX(),this.getY()))	
	{	
		//edited by Mike, 20241016
		if (getCurrentState()!=DYING_STATE) {
			drawActor(g, iOffsetScreenWidthLeftMargin-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);	
		}
		else {
			//added by Mike, 20241010	
			drawExplosion(g, iOffsetScreenWidthLeftMargin-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);	
		}
	}

	
	//when actual viewport is in left side, while enemy aircraft is in right side
	//iViewPortX positive number		
	iDifferenceInXPos=iViewPortX-(iOffsetScreenWidthLeftMargin+(MAX_TILE_MAP_WIDTH-1)*iTileWidth);
	iViewPortXTemp=0+iOffsetScreenWidthLeftMargin;

	if (isTileInsideViewport(iViewPortXTemp,iViewPortY, this.getX(),this.getY()))	
	{	
		//edited by Mike, 20241016
		if (getCurrentState()!=DYING_STATE) {
			drawActor(g, this.getX()-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);
		}
		else {
			//added by Mike, 20241010	
			drawExplosion(g, this.getX()-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);			
		}
	}	
  }
}

//added by Mike, 20240907
//TODO: -update: actual font; currently using Arial Bold in Gimp Studio
class UsbongFontNumber extends Actor {

    public UsbongFontNumber(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight) {
	  super(iOffsetScreenWidthLeftMargin, iOffsetScreenHeightTopMargin, iStageWidth, iStageHeight, iTileWidth, iTileHeight);

	  try {
	      //512x512; 128x128 each tile;
		  //TODO: -add: letters
		  myBufferedImage = ImageIO.read(new File("./res/count.png"));
      } catch (IOException ex) {
      }

	  reset();
	}

	@Override
	public void reset() {
		iWidth=iTileWidth;
		iHeight=iTileHeight;

		iFrameCount=0;
		iFrameCountDelay=0;
		iRotationDegrees=0;

		currentState=ACTIVE_STATE;

		iFrameWidth=128;
		iFrameHeight=128;

		//OK
		setX(0+iOffsetScreenWidthLeftMargin);//+iTileWidth*2);
		setY(0+iOffsetScreenHeightTopMargin);//+iTileHeight*2);

		myTileType=TILE_TEXT;
	}

	@Override
	public void keyPressed(KeyEvent key) {
	}

	@Override
	public void keyReleased(KeyEvent key) {
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

	int iNumber=8;
	int iRow=iNumber/4;
	int iColumn=(iNumber-iRow*4);

	//iFrameCount=0; //number 1
	//iFrameCount=3; //number 4

	System.out.println("iRow: "+iRow);
	System.out.println("iColumn: "+iColumn);

	iFrameCount=iNumber;

	//trans.translate(0,0-iFrameHeight);
	trans.translate(0,0-iFrameHeight*iRow);

	g2d.setTransform(trans);

	//note: clip rect has to move with object position
    //edited by Mike, 20240623
    //reminder:
    //300 is object position;
    //iFrameCount*128 is the animation frame to show;
    //rect.setRect(300+iFrameCount*128, 0, 128, 128);

	//rect.setRect(0, 0, iFrameWidth, iFrameHeight);
	rect.setRect(0, 0+iFrameHeight*iRow, iFrameWidth, iFrameHeight);

	Area myClipArea = new Area(rect);

    g2d.setClip(myClipArea);
    g2d.drawImage(myBufferedImage,-iColumn*iFrameWidth, 0, null);

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

//added by Mike, 20240908
//TODO: -update: actual font; currently using Arial Bold in Gimp Studio
class UsbongFont extends Actor {

    public UsbongFont(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight) {
	  super(iOffsetScreenWidthLeftMargin, iOffsetScreenHeightTopMargin, iStageWidth, iStageHeight, iTileWidth, iTileHeight);

	  try {
	      //512x512; 128x128 each tile;
		  //TODO: -add: letters
		  myBufferedImage = ImageIO.read(new File("./res/font.png"));
      } catch (IOException ex) {
      }

	  reset();
	}

	@Override
	public void reset() {
		iWidth=iTileWidth;
		iHeight=iTileHeight;

		iFrameCount=0;
		iFrameCountDelay=0;
		iRotationDegrees=0;

		currentState=ACTIVE_STATE;

/*
		iFrameWidth=128;
		iFrameHeight=128;
*/		
	  	
		//512X512 font.png; 40X64; width x height;
		iFrameWidth=40;
		iFrameHeight=64;		

		//OK
		setX(0+iOffsetScreenWidthLeftMargin);//+iTileWidth*2);
		setY(0+iOffsetScreenHeightTopMargin);//+iTileHeight*2);

		myTileType=TILE_TEXT;
	}

	@Override
	public void keyPressed(KeyEvent key) {
	}

	@Override
	public void keyReleased(KeyEvent key) {
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
  
  //reference: Usbong pagongHalang
  //reminder: 512X512 font.png; 40X64; width x height;  
  public void drawChar(Graphics g, int iInputX, int iInputY, char cInput) { 
	Rectangle2D rect = new Rectangle2D.Float();

    //added by Mike, 20240623
    AffineTransform identity = new AffineTransform();

    Graphics2D g2d = (Graphics2D)g;
    AffineTransform trans = new AffineTransform();
    trans.setTransform(identity);

	trans.translate(iInputX,iInputY);
	
	//removed by Mike, 20240908
	//trans.scale((iTileWidth*1.0)/iFrameWidth,(iTileHeight*1.0)/iFrameHeight);
	
/*
	//String sFontString="A"; //output in int: 65
	String sFontString="M"; 

	char cFontChar=sFontString.charAt(0);
*/	
	char cFontChar=cInput;
	
	int iFontCharInAscii=(int)cFontChar;
	
	//System.out.println("iFontCharInAscii: "+iFontCharInAscii);		
	
	int iNumber=iFontCharInAscii-32;
	int iRow=iNumber/12;
	int iColumn=(iNumber-iRow*12);

	//iFrameCount=0; //number 1
	//iFrameCount=3; //number 4
/*
	System.out.println("iRow: "+iRow);
	System.out.println("iColumn: "+iColumn);
*/	
////	System.out.println("iRow: "+iRow);
////	System.out.println("iColumn: "+iColumn);
	
	//iFrameCount=0;

	//trans.translate(0,0-iFrameHeight);
	trans.translate(0,0-iFrameHeight*iRow);

	g2d.setTransform(trans);

	//note: clip rect has to move with object position
    //edited by Mike, 20240623
    //reminder:
    //300 is object position;
    //iFrameCount*128 is the animation frame to show;
    //rect.setRect(300+iFrameCount*128, 0, 128, 128);

	//rect.setRect(0, 0, iFrameWidth, iFrameHeight);
	rect.setRect(0, 0+iFrameHeight*iRow, iFrameWidth, iFrameHeight);

	Area myClipArea = new Area(rect);

    g2d.setClip(myClipArea);
    g2d.drawImage(myBufferedImage,-iColumn*iFrameWidth, 0, null);

	//removed by Mike, 20240711; from 20240625
	//put after the last object to be drawn
	//g2d.dispose();
  }
  
  public void drawString(Graphics g, int iInputX, int iInputY, String sInput) {
	  int iStringInputLength=sInput.length();
	  
	  for (int i=0; i<iStringInputLength; i++) {
		char cFontChar=sInput.charAt(i);	
		//System.out.println(">>>>>>>>>>>>>> cFontChar["+i+"]: "+cFontChar);
	    
		//drawChar(g, iInputX, iInputY, cFontChar);
		drawChar(g, iInputX+iFrameWidth*i, iInputY, cFontChar);
	  }
  }

//Additional Reference: 	https://docs.oracle.com/javase/tutorial/2d/advanced/examples/ClipImage.java; last accessed: 20240625
  @Override
  public void draw(Graphics g) {
	if (currentState==HIDDEN_STATE) {
		return;
	}

	//drawActor(g, this.getX(), this.getY());

/*	//removed by Mike, 20241017	
	String sFontString="INSERT COIN";//0123456789";
	drawString(g, this.getX(), this.getY(), sFontString);
*/	
  }
}


//added by Mike, 20240908
//TODO: -update: actual font; currently using Arial Bold in Gimp Studio
class UsbongText extends Actor {
	UsbongFont myUsbongFont;

    public UsbongText(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight) {
	  super(iOffsetScreenWidthLeftMargin, iOffsetScreenHeightTopMargin, iStageWidth, iStageHeight, iTileWidth, iTileHeight);

	  myUsbongFont = new UsbongFont(iOffsetScreenWidthLeftMargin, iOffsetScreenHeightTopMargin, iStageWidth, iStageHeight, iTileWidth, iTileHeight);

	  reset();
	}

	@Override
	public void reset() {
		iWidth=iTileWidth;
		iHeight=iTileHeight;

		iFrameCount=0;
		iFrameCountDelay=0;
		iRotationDegrees=0;

		currentState=ACTIVE_STATE;

/*
		iFrameWidth=128;
		iFrameHeight=128;
*/		
	  	
		//512X512 font.png; 40X64; width x height;
		iFrameWidth=40;
		iFrameHeight=64;		

		//OK
		setX(0+iOffsetScreenWidthLeftMargin);//+iTileWidth*2);
		setY(0+iOffsetScreenHeightTopMargin);//+iTileHeight*2);

		myTileType=TILE_TEXT;
	}

	@Override
	public void keyPressed(KeyEvent key) {
	}

	@Override
	public void keyReleased(KeyEvent key) {
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


//Additional Reference: 	https://docs.oracle.com/javase/tutorial/2d/advanced/examples/ClipImage.java; last accessed: 20240625
  @Override
  public void draw(Graphics g) {
	if (currentState==HIDDEN_STATE) {
		return;
	}

	//drawActor(g, this.getX(), this.getY());
	
	//edited by Mike, 20241017
	//myUsbongFont.draw(g);
	
	//String sFontString="INSERT COIN";//0123456789";
	//String sFontString="100%";//0123456789";
	//String sFontString="0123456789";//0123456789";
	//String sFontString="0000000000";//0123456789";
	String sFontString=",0%";//0123456789";

	myUsbongFont.drawString(g, this.getX(), this.getY(), sFontString);
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
	
	private boolean bIsChargedPlasma=false;

	//edited by Mike, 20241019
	public Plasma(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight, int iPlasmaType) {
	
    //public Plasma(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight) {
				
	  super(iOffsetScreenWidthLeftMargin, iOffsetScreenHeightTopMargin, iStageWidth, iStageHeight, iTileWidth, iTileHeight);

	  try {
		  myBufferedImage = ImageIO.read(new File("./res/plasma.png"));
      } catch (IOException ex) {
      }
	  
	  myTileType=iPlasmaType;	  
	}
	
	public void setChargedPlasma(boolean b) {
		bIsChargedPlasma=b;
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

		//added by Mike, 20240910; from 20240828
		iXDistanceTraveledMax=iViewPortWidth/2+iTileWidth*2;
		iYDistanceTraveledMax=iViewPortHeight+iTileHeight;

		//added by Mike, 20241019
		if (this.getMyTileType()==TILE_ENEMY_PLASMA) {
			iXDistanceTraveledMax=iViewPortWidth+iTileWidth*2;
		}
		
		//default
		currentState=HIDDEN_STATE; //ACTIVE_STATE;
		isCollidable=true;

		iStepX=0;//ISTEP_X_DEFAULT;//*2; //faster by 1 than the default
		iStepY=0;//ISTEP_Y_DEFAULT;//*2; //faster by 1 than the default

		//removed by Mike, 20241019
		//myTileType=TILE_PLASMA;
	}

	@Override
	public void hitBy(Actor a) {		
		if (this.getMyTileType()==TILE_PLASMA) {
			if (a.getMyTileType()==TILE_HERO) {
				return;
			}
		}
		
		//added by Mike, 20241017
		bIsChargedPlasma=false;

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
	//public void processMouseInput(MouseEvent e, int iHeroX, int iHeroY) {
	public void processMouseInput(int iMouseXPos, int iMouseYPos, int iHeroX, int iHeroY) {

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
/*
	    int iDeltaX=(e.getX())-(this.getX()+this.getWidth()/2);
        int iDeltaY=(e.getY())-(this.getY()+this.getHeight()/2);
*/

	    int iDeltaX=(iMouseXPos)-(this.getX()+this.getWidth()/2);
        int iDeltaY=(iMouseYPos)-(this.getY()+this.getHeight()/2);

	    iDeltaY*=-1;

	    double dMainImageTileStepAngleRad=Math.atan2(iDeltaX,iDeltaY);

        int iMainImageTileStepAngle=(int)(dMainImageTileStepAngleRad*(180/Math.PI));

	    //clockwise
	    iMainImageTileStepAngle=(iMainImageTileStepAngle)%360;

	    //System.out.println(">>>>>>>>>iMainImageTileStepAngle: "+iMainImageTileStepAngle);

	    iStepY=(int)(ISTEP_Y_PLASMA*Math.cos(dMainImageTileStepAngleRad));
	    iStepY*=-1;
		
        iStepX=(int)(ISTEP_X_PLASMA*Math.sin(dMainImageTileStepAngleRad));
		
		//added by Mike, 20241003
		iRotationDegrees=iMainImageTileStepAngle;
	}

	public void processMouseInputForEnemy(int iHeroXPos, int iHeroYPos, int iEnemyX, int iEnemyY) {

		//if hidden, i.e. not available, for use;
		//if (getCurrentState()!=HIDDEN_STATE) {
		if (getCurrentState()==ACTIVE_STATE) {
			return;
		}
		
		setCurrentState(ACTIVE_STATE);
		//added by Mike, 20240828
		setCollidable(true);

		this.setX(iEnemyX);
		this.setY(iEnemyY);

		//added by Mike, 20240826
		iXInitialDistanceTraveled=getX();
		iYInitialDistanceTraveled=getY();

		iXCurrentDistanceTraveled=0;
		iYCurrentDistanceTraveled=0;
/*
	    int iDeltaX=(e.getX())-(this.getX()+this.getWidth()/2);
        int iDeltaY=(e.getY())-(this.getY()+this.getHeight()/2);
*/

	    int iDeltaX=(iHeroXPos)-(this.getX()+this.getWidth()/2);
        int iDeltaY=(iHeroYPos)-(this.getY()+this.getHeight()/2);

	    iDeltaY*=-1;

	    double dMainImageTileStepAngleRad=Math.atan2(iDeltaX,iDeltaY);

        int iMainImageTileStepAngle=(int)(dMainImageTileStepAngleRad*(180/Math.PI));

	    //clockwise
	    iMainImageTileStepAngle=(iMainImageTileStepAngle)%360;

	    System.out.println(">>>>>>>>>iMainImageTileStepAngle: "+iMainImageTileStepAngle);

	    iStepY=(int)(ISTEP_Y_PLASMA*Math.cos(dMainImageTileStepAngleRad));
	    iStepY*=-1;
		
        iStepX=(int)(ISTEP_X_PLASMA*Math.sin(dMainImageTileStepAngleRad));
		
		//added by Mike, 20241003
		iRotationDegrees=iMainImageTileStepAngle;
	}
	
	@Override
	public void update() {
/*		
		//added by Mike, 20241019; debug
		if (this.getMyTileType()==TILE_ENEMY_PLASMA) {
			iFrameCount=0;
			return;
		}
*/
		
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
		
		int iOffsetY=Math.abs(0-iViewPortY);
		int iOffsetYPlasmaGoingUp=Math.abs(0-iYInitialDistanceTraveled);
		int iOffsetYPlasmaGoingDown=Math.abs(iViewPortHeight-iYInitialDistanceTraveled);

		iOffsetY=iOffsetY+iOffsetYPlasmaGoingUp;

		if (iOffsetYPlasmaGoingUp<iOffsetYPlasmaGoingDown) {
			iOffsetY=iOffsetY+iOffsetYPlasmaGoingDown;
		}

		//hero always at the center of viewport
		//if (iDifferenceInXPos>iViewPortWidth/2) {
		//if (iDifferenceInXPos>iViewPortWidth/2+iTileWidth) {
		if (iDifferenceInXPos>iXDistanceTraveledMax) {
			this.setCurrentState(HIDDEN_STATE);
			this.isCollidable=false;
			this.setChargedPlasma(false);

			//TODO: -update: this
			this.setX(-999999);
			this.setY(-999999);
		}

		//edited by Mike, 20240828
		//if (iDifferenceInYPos>iViewPortHeight/2+iTileHeight) {
		//if (iDifferenceInYPos>iViewPortHeight/2+iTileHeight+iOffsetY) {
		if (iDifferenceInYPos>iYDistanceTraveledMax+iOffsetY) {
			this.setCurrentState(HIDDEN_STATE);
			this.isCollidable=false;
			this.setChargedPlasma(false);

			//TODO: -update: this
			this.setX(-999999);
			this.setY(-999999);
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

	//added by Mike, 20241003
	//Part 1.put rotate anchor to CENTER
	trans.translate(getWidth()/2,getHeight()/2);
	
	//rotate at center; put translate after rotate
	//note effect;
	//iRotationDegrees=(iRotationDegrees+10)%360;
	
    trans.rotate(Math.toRadians(iRotationDegrees));

	//Part 2.puts anchor to TOP-LEFT; 
	//combined with Part 1, projectile launched from center
	trans.translate(-getWidth()/2,-getHeight()/2);
	
	
	trans.scale((iTileWidth*1.0)/iFrameWidth,(iTileHeight*1.0)/iFrameHeight);
	
	g2d.setTransform(trans);
	
	//note: clip rect has to move with object position
    
	//added by Mike, 20240714
	//put this after scale;
	//move the input image to the correct row of the frame

	if (!bIsChargedPlasma) {
		//trans.translate(getX(),getY());
	}
	else { //FACING_LEFT
		trans.translate(0,0-iFrameHeight);
	}

	//added by Mike, 20240625
	g2d.setTransform(trans);

	if (!bIsChargedPlasma) {
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

	//edited by Mike, 20241019
	//drawActor(g, this.getX(), this.getY());

	if (this.getMyTileType()==TILE_PLASMA) {
		drawActor(g, this.getX(), this.getY());
	}
	else {
		int iDifferenceInXPos=iViewPortX-(this.getX());	
		int iDifferenceInYPos=iViewPortY-(this.getY());

		if (isTileInsideViewport(iViewPortX,iViewPortY, this.getX()-iDifferenceInXPos,this.getY()-iDifferenceInYPos))	
		{	
			drawActor(g, iOffsetScreenWidthLeftMargin-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);
		}

		//when actual viewport is in right side, while enemy aircraft is in left side	
		//iViewPortX negative number
		int iViewPortXTemp=(iOffsetScreenWidthLeftMargin+(MAX_TILE_MAP_WIDTH-6)*iTileWidth);
			
		//based on wrap around; left-most
		iDifferenceInXPos=(iViewPortX+MAX_TILE_MAP_WIDTH*iTileWidth-iTileWidth)-this.getX();	
		
		if (isTileInsideViewport(iViewPortXTemp,iViewPortY, this.getX(),this.getY()))	
		{	
			drawActor(g, iOffsetScreenWidthLeftMargin-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);	
		}
		
		//when actual viewport is in left side, while enemy aircraft is in right side
		//iViewPortX positive number		
		iDifferenceInXPos=iViewPortX-(iOffsetScreenWidthLeftMargin+(MAX_TILE_MAP_WIDTH-1)*iTileWidth);
		iViewPortXTemp=0+iOffsetScreenWidthLeftMargin;

		if (isTileInsideViewport(iViewPortXTemp,iViewPortY, this.getX(),this.getY()))	
		{	
			drawActor(g, this.getX()-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);
		}	
	}
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

	UsbongUtils myUsbongUtils;

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

	  myUsbongUtils = new UsbongUtils();

	  reset();

	  //added by Mike, 20240828
	  initBackground();
	}

	//added by Mike, 20240828
	public void initBackground() {
		try {
			myUsbongUtils.processBackgroundData(tileMap);
		}
		catch(Exception e) {
			System.out.println("ERROR at initBackground(): "+e);
		}
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

		//added by Mike, 20240828
		//TODO: -put: in initBackground()

		//added by Mike, 20240727
	    for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
		  for (int k=0; k<MAX_TILE_MAP_WIDTH; k++) {
			tileMap[i][k]=TILE_BLANK;
		  }
	    }

		//added by Mike, 20240729
		iViewPortX=0;
		iViewPortY=0;
		iViewPortWidth=iStageWidth;
		iViewPortHeight=iStageHeight;

		//added by Mike, 20240806
		iStepX=ISTEP_X_DEFAULT*2; //faster by 1 than the default
		iStepY=ISTEP_Y_DEFAULT*2; //faster by 1 than the default
	}

	public int getTileWidth() {
        return iTileWidth;
    }

    public int getTileHeight() {
        return iTileHeight;
    }

	//added by Mike, 20240901
	public int[][] getTileMap() {
		return tileMap;
	}

	//added by Mike, 20240827
	@Override
	public void hitBy(Actor a) {
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

		if (!a.isActive())
		{
			//not collidable
			return false;
		}

		if (!a.checkIsCollidable())
		{
			//not collidable
			return false;
		}

		int a1X=a.getX();
		int a1Y=a.getY();

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
		//plasma actor
		//if (!a.checkIsCollidable())
		if (!a.isActive())
		{
			//not collidable
			return;
		}

		if (!a.checkIsCollidable())
		{
			//not collidable
			return;
		}

		for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
		  for (int k=0; k<MAX_TILE_MAP_WIDTH; k++) {
				int iDifferenceInXPos=iViewPortX-(iOffsetScreenWidthLeftMargin+iTileWidth*k);

				int iDifferenceInYPos=iViewPortY-(iOffsetScreenHeightTopMargin+iTileHeight*i);

				//removed by Mike, 20240919; from 20240807
				//if (isTileInsideViewport(iViewPortX,iViewPortY, iOffsetScreenWidthLeftMargin+iTileWidth*k-iDifferenceInXPos,iOffsetScreenHeightTopMargin+iTileHeight*i-iDifferenceInYPos)) {

					if (tileMap[i][k]==TILE_BASE) {
						int iDifferenceInXPosOfViewPortAndBG=iViewPortX-(iOffsetScreenWidthLeftMargin+iTileWidth*0);
						int iDifferenceInYPosOfViewPortAndBG=iViewPortY-(iOffsetScreenHeightTopMargin+iTileHeight*0);

						if (isActorIntersectingWithTile(a,iOffsetScreenWidthLeftMargin+iTileWidth*k-iDifferenceInXPosOfViewPortAndBG,iOffsetScreenHeightTopMargin+iTileHeight*i-iDifferenceInYPosOfViewPortAndBG))
						{
							System.out.println("COLLISION!");

							//this.hitBy(a);
							tileMap[i][k]=TILE_BLANK;

							a.hitBy(this);
						}
						
						//added by Mike, 20240919
						//based on draw(...) function in class EnemyAircraft

						//when actual viewport is in right side, while enemy aircraft is in left side				
						//based on wrap around; left-most
						iDifferenceInXPosOfViewPortAndBG=(iViewPortX+MAX_TILE_MAP_WIDTH*iTileWidth-iTileWidth)-(iOffsetScreenWidthLeftMargin+iTileWidth*0);		
						
						iDifferenceInYPosOfViewPortAndBG=iViewPortY-(iOffsetScreenHeightTopMargin+iTileHeight*0);

						if (isActorIntersectingWithTile(a,iOffsetScreenWidthLeftMargin+iTileWidth*k-iDifferenceInXPosOfViewPortAndBG,iOffsetScreenHeightTopMargin+iTileHeight*i-iDifferenceInYPosOfViewPortAndBG)) {
							
								tileMap[i][k]=TILE_BLANK;

								a.hitBy(this);							
						}											

						//when actual viewport is in left side, while enemy aircraft is in right side
						iDifferenceInXPosOfViewPortAndBG=(iViewPortX-((MAX_TILE_MAP_WIDTH-1)*iTileWidth))-(iOffsetScreenWidthLeftMargin+iTileWidth*0);		
						
						iDifferenceInYPosOfViewPortAndBG=iViewPortY-(iOffsetScreenHeightTopMargin+iTileHeight*0);
						
						iDifferenceInYPos=iViewPortY-(iOffsetScreenHeightTopMargin+iTileHeight*i);

						if (isActorIntersectingWithTile(a,iOffsetScreenWidthLeftMargin+iTileWidth*k-iDifferenceInXPosOfViewPortAndBG,iOffsetScreenHeightTopMargin+iTileHeight*i-iDifferenceInYPosOfViewPortAndBG)) {
							
								tileMap[i][k]=TILE_BLANK;

								a.hitBy(this);							
						}
					}

				//}
		  }
		}
	}

	//added by Mike, 20240811
	//background's x and y are iViewPortX and iViewPortY respectively
	@Override
	public void synchronizeViewPortWithBackground(int x, int y) {
		setX(x);
		setY(y);
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

/*
	System.out.println("iViewPortX: "+iViewPortX);
	System.out.println("iViewPortY: "+iViewPortY);
*/
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

	//note: noticeable slow-down on i3; if multiple browser tabs are opened, etc.
	//added by Mike, 20240823; from 20240806
	int iWrapTileCountOffset=10;//6; //10/13=0.7692; 76.92% of the stage width
	int iCount=iWrapTileCountOffset; //0

	for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
	  iCount=iWrapTileCountOffset;

	  for (int k=MAX_TILE_MAP_WIDTH-1-iWrapTileCountOffset; k<MAX_TILE_MAP_WIDTH; k++) {

//System.out.println(">>>>>>>>>>>>>iCount"+iCount);

		//has reached left-most
		//use distance formula?
		int iDifferenceInXPos=iViewPortX-(iOffsetScreenWidthLeftMargin-iTileWidth*iCount);
		int iDifferenceInYPos=iViewPortY-(iOffsetScreenHeightTopMargin+iTileHeight*i);

		//if (isTileInsideViewport(iViewPortX,iViewPortY, iOffsetScreenWidthLeftMargin+iTileWidth*k-iDifferenceInXPos,iOffsetScreenHeightTopMargin+iTileHeight*i-iDifferenceInYPos)) {
			if (tileMap[i][k]==TILE_TREE) {
				drawTree(g, iOffsetScreenWidthLeftMargin-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);
				//iCount++;
			}
			//added by Mike, 20240829
			else if (tileMap[i][k]==TILE_BASE) {
				drawBase(g, iOffsetScreenWidthLeftMargin-iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);
			}

			//iCount++;
			iCount--;

		//}
	  }
	}

//System.out.println(">>>>>>>>>>>>>iCount"+iCount);

	//int iCount=0;

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
		}
		//added by Mike, 20240829
		else if (tileMap[i][k]==TILE_BASE) {
			drawBase(g, iOffsetScreenWidthLeftMargin+iDifferenceInXPos, iOffsetScreenHeightTopMargin-iDifferenceInYPos);
		}
	  }
	}
  }
}

//added by Mike, 20240828
class UsbongUtils {
	//private static String inputDataFilename = ".\\assets\\background\\bg1";
	private static String inputDataFilename = "./assets/background/bg1"; //.txt
	private static int iRowCount=0;

	public UsbongUtils() {
	}

	//reference: Usbong SLHCC
	//store the existing values from the assets file into Random Access Memory (RAM)
	//private static void processBackgroundData(int fileType) throws Exception {
	//TODO: -update: this
	public static void processBackgroundData(int[][] tileMap) throws Exception {
		File inputDataFile;

		inputDataFile = new File(inputDataFilename+".txt");
		Scanner sc = new Scanner(new FileInputStream(inputDataFile), "UTF-8");
		String s;

		iRowCount=0;

		int iRowLengthMax=tileMap.length;
		int iColumnsLengthMax=tileMap[0].length;

		while (sc.hasNextLine()) {

			if (iRowCount>=iRowLengthMax) {
				break;
			}

			s=sc.nextLine();

			//if the row is blank
			if (s.trim().equals("")) {
				continue;
			}

			String[] inputBgColumns = s.split(",");	//\t

			//System.out.println("s: "+s);

			int iColumnsLength=inputBgColumns.length;

			//System.out.println("iColumnsLength: "+iColumnsLength);

			if (iColumnsLength>iColumnsLengthMax) {
				iColumnsLength=iColumnsLengthMax;//-1;
			}

			for(int iColumnCount=0; iColumnCount<iColumnsLength; iColumnCount++) {
				tileMap[iRowCount][iColumnCount]=Integer.parseInt(inputBgColumns[iColumnCount]);
			}

//			if (isInDebugMode) {
				iRowCount++;
//				System.out.println("iRowCount: "+iRowCount);
//			}

		}
	}
}

//added by Mike, 20240804; from 20240803
//for Actor object positions
class Level2D extends Actor {
	//private EnemyAircraft myEnemyAircraft;
	private final int MAX_ENEMY_AIRCRAFT_COUNT=10; //2;//1;//2; //5;
	private EnemyAircraft[] myEnemyAircraftContainer;

	private final int MAX_ENEMY_AIRCRAFT_BOSS_COUNT=1; 
	private EnemyAircraft[] myEnemyAircraftBossContainer;

	//added by Mike, 20240809
	private final int MAX_WALL_COUNT=0;//0;//16;//1;//2;
	private Wall[] myWallContainer;

	//added by Mike, 20240825
	private final int MAX_PLASMA_COUNT=5;//3;//10;//30;//5;//1;
	private Plasma[] myPlasmaContainer;
	
	//added by Mike, 20241018
	private final int MAX_ENEMY_PLASMA_COUNT=1;//5;
	private Plasma[] myEnemyPlasmaContainer;	

	private boolean bIsFiring=false;
	private boolean bHasPressedFiring=false;
	
	private int iFiringDelay=0;
	private int iFiringDelayMax=300;
	
	//added by Mike, 20240910
	private int iPlasmaCharge=0;
	private int iPlasmaChargeMax=200;//100;
	private boolean bIsPlasmaChargeReleased=true;

	//added by Mike, 20240809
	BackgroundCanvas myBackgroundCanvas;

	//added by Mike, 20240810
	RobotShip myRobotShip;

	//added by Mike, 20240907
	UsbongText myUsbongText;
	
	//added by Mike, 20240908
	//UsbongFont myUsbongFont;

	//added by Mike, 20240905
	boolean bIsMaxedMonitorHeight;
	int iScreenWidth;

	//edited by Mike, 20240905
    //public Level2D(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight) {
    public Level2D(int iOffsetScreenWidthLeftMargin, int iOffsetScreenHeightTopMargin, int iStageWidth, int iStageHeight, int iTileWidth, int iTileHeight, boolean bIsMaxedMonitorHeight, int iOffsetScreenWidthRightMargin) {

	  super(iOffsetScreenWidthLeftMargin, iOffsetScreenHeightTopMargin, iStageWidth, iStageHeight, iTileWidth, iTileHeight);

	  this.bIsMaxedMonitorHeight=bIsMaxedMonitorHeight;
	  this.iOffsetScreenWidthRightMargin=iOffsetScreenWidthRightMargin;

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

	  //added by Mike, 20240905
	  this.iScreenWidth=iScreenWidth;

	  //added by Mike, 20240629
	  myKeysDown = new boolean[iNumOfKeyTypes];

	  myEnemyAircraftContainer = new EnemyAircraft[MAX_ENEMY_AIRCRAFT_COUNT];
	  myEnemyAircraftBossContainer = new EnemyAircraft[MAX_ENEMY_AIRCRAFT_BOSS_COUNT];
			
	  //edited by Mike, 20241018
	  for (int i=0; i<MAX_ENEMY_AIRCRAFT_COUNT; i++) {
		  myEnemyAircraftContainer[i] = new EnemyAircraft(iOffsetScreenWidthLeftMargin,iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight, TILE_AIRCRAFT);
	  }

	  for (int i=0; i<MAX_ENEMY_AIRCRAFT_BOSS_COUNT; i++) {
		  myEnemyAircraftBossContainer[i] = new EnemyAircraft(iOffsetScreenWidthLeftMargin,iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight, TILE_AIRCRAFT_BOSS);
	  }
	  
	  //added by Mike, 20240809
	  myWallContainer = new Wall[MAX_WALL_COUNT];

	  for (int i=0; i<MAX_WALL_COUNT; i++) {
		  myWallContainer[i] = new Wall(iOffsetScreenWidthLeftMargin,iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight);
	  }

	  //added by Mike, 20240825
	  myPlasmaContainer = new Plasma[MAX_PLASMA_COUNT];

	  for (int i=0; i<MAX_PLASMA_COUNT; i++) {
		  myPlasmaContainer[i] = new Plasma(iOffsetScreenWidthLeftMargin,iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight, TILE_PLASMA);
	  }

	  //added by Mike, 20241018
	  myEnemyPlasmaContainer = new Plasma[MAX_ENEMY_PLASMA_COUNT];

	  for (int i=0; i<MAX_ENEMY_PLASMA_COUNT; i++) {
		  myEnemyPlasmaContainer[i] = new Plasma(iOffsetScreenWidthLeftMargin,iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight, TILE_ENEMY_PLASMA);
	  }
	  
	  //added by Mike, 20240809
	  myBackgroundCanvas = new BackgroundCanvas(0+iOffsetScreenWidthLeftMargin,0+iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight);

	  //added by Mike, 20240810
	  myRobotShip = new RobotShip(iOffsetScreenWidthLeftMargin,iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight);

	  //added by Mike, 20240907
	  myUsbongText = new UsbongText(iOffsetScreenWidthLeftMargin,iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight);
	  
	  //added by Mike, 20240908
	  //myUsbongFont = new UsbongFont(iOffsetScreenWidthLeftMargin,iOffsetScreenHeightTopMargin,iStageWidth, iStageHeight, iTileWidth, iTileHeight);

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
		
		//edited by Mike, 20241016
/*		
		tileMap[6][MAX_TILE_MAP_WIDTH-3]=TILE_AIRCRAFT;
		tileMap[5][3]=TILE_AIRCRAFT;
*/
		for (int i=0; i<MAX_ENEMY_AIRCRAFT_COUNT; i++) {
			tileMap[2][i]=TILE_AIRCRAFT;
		}
		
		//boss
		//tileMap[0][MAX_TILE_MAP_WIDTH-1]=TILE_AIRCRAFT_BOSS;
		//MAX_ENEMY_AIRCRAFT_BOSS_COUNT
		//tileMap[0][24]=TILE_AIRCRAFT_BOSS;
		tileMap[0][5]=TILE_AIRCRAFT_BOSS;
		
		//tileMap[5][0]=TILE_WALL;

		//added by Mike, 20240817; from 20240729
		iViewPortX=iOffsetScreenWidthLeftMargin+0;
		iViewPortY=iOffsetScreenHeightTopMargin+0;
		iViewPortWidth=iStageWidth;
		iViewPortHeight=iStageHeight;

		iStepX=ISTEP_X_DEFAULT*2; //faster by 1 than the default
		iStepY=ISTEP_Y_DEFAULT*2; //faster by 1 than the default

		bIsFiring=false;
		bHasPressedFiring=false;
	}

	//added by Mike, 20240825
	@Override
	public void mousePressed(MouseEvent e) {
		//moveSquare(e.getX(),e.getY());

		//System.out.println("Pressed!");

		  //myPlasmaContainer[0].processMouseInput(e, myRobotShip.getX(),myRobotShip.getY());

		//added by Mike, 20240831
		iMouseXPos=e.getX();
		iMouseYPos=e.getY();
		
		//added by Mike, 20241016
		if (!myRobotShip.isActive()) {
			return;
		}		
		
/*
		for (int i=0; i<MAX_PLASMA_COUNT; i++) {
			  if (!myPlasmaContainer[i].isActive()) {
				myPlasmaContainer[i].processMouseInput(iMouseXPos, iMouseYPos, myRobotShip.getX(),myRobotShip.getY());
				
				//removed by Mike, 20240927
				//bIsFiring=true;
	
				break;
			  }
		}
*/		
		//only 1 Boss; red aircraft; smarter AI
		for (int i=0; i<MAX_ENEMY_AIRCRAFT_BOSS_COUNT; i++) {	
			//Boss
			if (myEnemyAircraftBossContainer[i].isActive()) {
				for (int k=0; k<MAX_ENEMY_PLASMA_COUNT; k++) {					
					if (!myEnemyPlasmaContainer[k].isActive()) {
						//TODO: -update: this to include if hero robot ship is at right-most of map; reminder wrap around
						myEnemyPlasmaContainer[k].processMouseInputForEnemy(this.getX()+iViewPortWidth/2,myRobotShip.getY(), myEnemyAircraftBossContainer[i].getX(),myEnemyAircraftBossContainer[i].getY());
				
/*						
						myEnemyPlasmaContainer[k].setX(myEnemyAircraftBossContainer[i].getX());
						
						myEnemyPlasmaContainer[k].setY(myEnemyAircraftBossContainer[i].getY());

						myEnemyPlasmaContainer[k].setCurrentState(ACTIVE_STATE);
						myEnemyPlasmaContainer[k].setCollidable(true);		
						
*/
/*						
System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> PLASMA!");				

System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> myEnemyAircraftBossContainer[i].getX(): "+myEnemyAircraftBossContainer[i].getX());				
System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> myEnemyAircraftBossContainer[i].getY(): "+myEnemyAircraftBossContainer[i].getY());				
System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> myRobotShip.getX(): "+myRobotShip.getX());				
System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> myRobotShip.getY(): "+myRobotShip.getY());			

System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> myEnemyPlasmaContainer[k].getX(): "+myEnemyPlasmaContainer[k].getX());				
System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> myEnemyPlasmaContainer[k].getY(): "+myEnemyPlasmaContainer[k].getY());				
*/						
						break;
					  }
				}
			}
		}

		bIsFiring=false;
		bHasPressedFiring=true;

		//bIsFiring=true;
		bIsPlasmaChargeReleased=false;
	}

	//added by Mike, 20240830
	@Override
	public void mouseReleased(MouseEvent e) {
	  //System.out.println("Released!");

	  //bIsFiring=false;

	  //added by Mike, 20240927
	  iMouseXPos=e.getX();
	  iMouseYPos=e.getY();
	  
	  //added by Mike, 20241016
	  if (!myRobotShip.isActive()) {
		return;
	  }
	  
	  for (int i=0; i<MAX_PLASMA_COUNT; i++) {
		  if (!myPlasmaContainer[i].isActive()) {
			  
			//added by Mike, 20240910
			if (iPlasmaCharge==iPlasmaChargeMax) {
			  myPlasmaContainer[i].setChargedPlasma(true);
			  bIsPlasmaChargeReleased=true;
				  
			  System.out.println("Plasma Charge Released!: "+iPlasmaCharge);  
			}
			  
			myPlasmaContainer[i].processMouseInput(iMouseXPos, iMouseYPos, myRobotShip.getX(),myRobotShip.getY());
				
			bIsFiring=true;				    
			break;
		}
	  }		  	  
	  

/*
	  //added by Mike, 20240927
	  else {
		  bIsFiring=true;
	  }
*/
	  iPlasmaCharge=0;	
	  bHasPressedFiring=false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//moveSquare(e.getX(),e.getY());

		//System.out.println("Dragged!");

		//added by Mike, 20240831
		iMouseXPos=e.getX();
		iMouseYPos=e.getY();

		bIsFiring=false; //true;
		bIsPlasmaChargeReleased=false;
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
		myBackgroundCanvas.synchronizeKeys(myKeysDown);
		
		myRobotShip.synchronizeViewPort(iViewPortX,iViewPortY, getStepX(),getStepY());		
		myRobotShip.synchronizeKeys(myKeysDown);
			
		//-----------------------------------------------------------

		for (int i=0; i<MAX_ENEMY_AIRCRAFT_COUNT; i++) {
			
			if (!myEnemyAircraftContainer[i].isActive()) {
				continue;
			}
			
			myEnemyAircraftContainer[i].synchronizeViewPortWithBackground(iViewPortX,iViewPortY);
			//continuously update, even if not inside viewport
			myEnemyAircraftContainer[i].update();

			//went beyond left-most
			//OK
			if (myEnemyAircraftContainer[i].getX()+myEnemyAircraftContainer[i].getWidth()/2<=0+iOffsetScreenWidthLeftMargin) {
				
				myEnemyAircraftContainer[i].setX(myEnemyAircraftContainer[i].getX()+MAX_TILE_MAP_WIDTH*iTileWidth-iTileWidth);
			}
			
			if (myEnemyAircraftContainer[i].getX()+myEnemyAircraftContainer[i].getWidth()/2>=iRightMostLevelWidth) {
				myEnemyAircraftContainer[i].setX(iOffsetScreenWidthLeftMargin+0-myEnemyAircraftContainer[i].getWidth()/2); //OK;
			}
		}
		
		//added by Mike, 20241018
		for (int i=0; i<MAX_ENEMY_AIRCRAFT_BOSS_COUNT; i++) {
			
			if (!myEnemyAircraftBossContainer[i].isActive()) {
				continue;
			}
			
			myEnemyAircraftBossContainer[i].synchronizeViewPortWithBackground(iViewPortX,iViewPortY);
			//continuously update, even if not inside viewport
			myEnemyAircraftBossContainer[i].update();

			//went beyond left-most
			//OK
			if (myEnemyAircraftBossContainer[i].getX()+myEnemyAircraftBossContainer[i].getWidth()/2<=0+iOffsetScreenWidthLeftMargin) {
				
				myEnemyAircraftBossContainer[i].setX(myEnemyAircraftBossContainer[i].getX()+MAX_TILE_MAP_WIDTH*iTileWidth-iTileWidth);
			}
			
			if (myEnemyAircraftBossContainer[i].getX()+myEnemyAircraftBossContainer[i].getWidth()/2>=iRightMostLevelWidth) {
				myEnemyAircraftBossContainer[i].setX(iOffsetScreenWidthLeftMargin+0-myEnemyAircraftBossContainer[i].getWidth()/2); //OK;
			}
		}		

		//added by Mike, 20240825
		for (int i=0; i<MAX_PLASMA_COUNT; i++) {
			//added by Mike, 20240910
			//put charge
			//System.out.println("CHARGED!");
			
			if (bHasPressedFiring) {
				if (!bIsPlasmaChargeReleased) {
					if (iPlasmaCharge<iPlasmaChargeMax) {
						iPlasmaCharge++;
					}
				}
				else {
					iPlasmaCharge=0;
				}			
			}
			
			if (!myPlasmaContainer[i].isActive()) {
				continue;
			}
			
			myPlasmaContainer[i].synchronizeViewPortWithBackground(iViewPortX,iViewPortY);			
			myPlasmaContainer[i].update();

			//went beyond left-most
			//OK
			if (myPlasmaContainer[i].getX()+myPlasmaContainer[i].getWidth()/2<=0+iOffsetScreenWidthLeftMargin) {
				myPlasmaContainer[i].setX(myPlasmaContainer[i].getX()+MAX_TILE_MAP_WIDTH*iTileWidth-iTileWidth);
			}
			
			if (myPlasmaContainer[i].getX()+myPlasmaContainer[i].getWidth()/2>=iRightMostLevelWidth) {
				myPlasmaContainer[i].setX(iOffsetScreenWidthLeftMargin+0-myPlasmaContainer[i].getWidth()/2); //OK;
			}
			
			//added by Mike, 20240825
			for (int k=0; k<MAX_ENEMY_AIRCRAFT_COUNT; k++) {
				myPlasmaContainer[i].collideWithEnemy(myEnemyAircraftContainer[k]);

				//System.out.println("myPlasmaContainer[i].getX(): "+myPlasmaContainer[i].getX());
				//System.out.println("myEnemyAircraftContainer[k].getX(): "+myEnemyAircraftContainer[k].getX());
/*
				System.out.println("myPlasmaContainer[i].getY(): "+myPlasmaContainer[i].getY());
				System.out.println("myEnemyAircraftContainer[k].getY(): "+myEnemyAircraftContainer[k].getY());
*/				
			}

			//added by Mike, 20241018
			for (int k=0; k<MAX_ENEMY_AIRCRAFT_BOSS_COUNT; k++) {
				myPlasmaContainer[i].collideWithEnemy(myEnemyAircraftBossContainer[k]);
			}
			
			myBackgroundCanvas.collideWithBackgroundTile(myPlasmaContainer[i]);
		}

		//added by Mike, 20241018		
		for (int i=0; i<MAX_ENEMY_PLASMA_COUNT; i++) {
			//added by Mike, 20240910
			//put charge
			//System.out.println("CHARGED!");
						
			if (!myEnemyPlasmaContainer[i].isActive()) {
				continue;
			}
						
			myEnemyPlasmaContainer[i].synchronizeViewPortWithBackground(iViewPortX,iViewPortY);			
			myEnemyPlasmaContainer[i].update();

			//went beyond left-most
			//OK
			if (myEnemyPlasmaContainer[i].getX()+myEnemyPlasmaContainer[i].getWidth()/2<=0+iOffsetScreenWidthLeftMargin) {
				myEnemyPlasmaContainer[i].setX(myEnemyPlasmaContainer[i].getX()+MAX_TILE_MAP_WIDTH*iTileWidth-iTileWidth);
			}
			
			if (myEnemyPlasmaContainer[i].getX()+myEnemyPlasmaContainer[i].getWidth()/2>=iRightMostLevelWidth) {
				myEnemyPlasmaContainer[i].setX(iOffsetScreenWidthLeftMargin+0-myEnemyPlasmaContainer[i].getWidth()/2); //OK;
			}
		
			//removed by Mike, 20241019
			//put in collideWith(...); due to hitting Hero		
			//myEnemyPlasmaContainer[i].collideWithEnemy(myRobotShip);
		}

		//added by Mike, 20240809
		myBackgroundCanvas.update();

		//edited by Mike, 20241016
		if (myRobotShip.isActive()) {
			//added by Mike, 20240810
			myRobotShip.update();
			this.collideWith(myRobotShip);
		}		
	}

	//added by Mike, 20240804
	@Override
	public void collideWith(Actor a) {

		for (int i=0; i<MAX_ENEMY_AIRCRAFT_COUNT; i++) {

			if (!myEnemyAircraftContainer[i].isActive()) {
				continue;
			}

			if ((!myEnemyAircraftContainer[i].checkIsCollidable())||(!a.checkIsCollidable()))
			{
				//not collidable
				return;
			}
			
			int iDifferenceInXPosOfViewPortAndBG=iViewPortX-(iOffsetScreenWidthLeftMargin);

			int iDifferenceInYPosOfViewPortAndBG=iViewPortY-(iOffsetScreenHeightTopMargin);

			if (isActorIntersectingWithActor(a,myEnemyAircraftContainer[i],myEnemyAircraftContainer[i].getX()-iDifferenceInXPosOfViewPortAndBG,myEnemyAircraftContainer[i].getY()-iDifferenceInYPosOfViewPortAndBG))
			
			{
				System.out.println("COLLISION!");

				myEnemyAircraftContainer[i].hitBy(a);
				//tileMap[i][k]=TILE_BLANK;

				a.hitBy(myEnemyAircraftContainer[i]);			
			}	
		}
		
		//added by Mike, 20241019
		for (int i=0; i<MAX_ENEMY_AIRCRAFT_BOSS_COUNT; i++) {

			if (!myEnemyAircraftBossContainer[i].isActive()) {
				continue;
			}

			if ((!myEnemyAircraftBossContainer[i].checkIsCollidable())||(!a.checkIsCollidable()))
			{
				//not collidable
				return;
			}
			
			int iDifferenceInXPosOfViewPortAndBG=iViewPortX-(iOffsetScreenWidthLeftMargin);

			int iDifferenceInYPosOfViewPortAndBG=iViewPortY-(iOffsetScreenHeightTopMargin);

			if (isActorIntersectingWithActor(a,myEnemyAircraftBossContainer[i],myEnemyAircraftBossContainer[i].getX()-iDifferenceInXPosOfViewPortAndBG,myEnemyAircraftBossContainer[i].getY()-iDifferenceInYPosOfViewPortAndBG))
			
			{
				System.out.println("COLLISION!");

				myEnemyAircraftBossContainer[i].hitBy(a);
				a.hitBy(myEnemyAircraftBossContainer[i]);			
			}	
		}		
		
		//added by Mike, 20241019
		for (int i=0; i<MAX_ENEMY_PLASMA_COUNT; i++) {

			if (!myEnemyPlasmaContainer[i].isActive()) {
				continue;
			}

			if ((!myEnemyPlasmaContainer[i].checkIsCollidable())||(!a.checkIsCollidable()))
			{
				//not collidable
				return;
			}
			
			int iDifferenceInXPosOfViewPortAndBG=iViewPortX-(iOffsetScreenWidthLeftMargin);

			int iDifferenceInYPosOfViewPortAndBG=iViewPortY-(iOffsetScreenHeightTopMargin);

			if (isActorIntersectingWithActor(a,myEnemyPlasmaContainer[i],myEnemyPlasmaContainer[i].getX()-iDifferenceInXPosOfViewPortAndBG,myEnemyPlasmaContainer[i].getY()-iDifferenceInYPosOfViewPortAndBG))
			
			{
				System.out.println("COLLISION!");

				myEnemyPlasmaContainer[i].hitBy(a);
				//tileMap[i][k]=TILE_BLANK;

				a.hitBy(myEnemyPlasmaContainer[i]);			
			}	
		}
		

/*
		//added by Mike, 20240809
		//TODO: -add: all tile Actor objects in a container
		for (int i=0; i<MAX_WALL_COUNT; i++) {

			if (!myWallContainer[i].isActive()) {
				continue;
			}

			if (isIntersectingRect(myWallContainer[i], a))
			{
				if (myWallContainer[i].wallHitBy(this, a)) {
					setViewPortStopped(true);
				}
			}
		}
*/		
	}

	//pre-set enemy positions, but do not draw them all yet;
	//during init
	public void initLevel() {
		int iEnemyAircraftCount=0;
		int iEnemyAircraftBossCount=0;
		
		int iWallCount=0;
		int iPlasmaCount=0;
		
		//identify the current tile in horizontal axis
		iViewPortX=getX();
		iViewPortY=getY();
		
		for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
			for (int k=0; k<MAX_TILE_MAP_WIDTH; k++) {
				if (tileMap[i][k]==TILE_AIRCRAFT) {

					System.out.println("TILE_AIRCRAFT!");
/*										
					int iDifferenceInXPos=iViewPortX-(iOffsetScreenWidthLeftMargin+iTileWidth*k);

					int iDifferenceInYPos=iViewPortY-(iOffsetScreenHeightTopMargin+iTileHeight*i);
*/
					//edited by Mike, 20241018
					//if (iEnemyAircraftCount<MAX_ENEMY_AIRCRAFT_COUNT) {
					//TODO: -update: this; Boss types also exist
					if (iEnemyAircraftCount<MAX_ENEMY_AIRCRAFT_COUNT) {

						if (myEnemyAircraftContainer[iEnemyAircraftCount].isActive()) {
							//edited by Mike, 20240818
							
							myEnemyAircraftContainer[iEnemyAircraftCount].setX(0+iOffsetScreenWidthLeftMargin+iTileWidth*k);
							myEnemyAircraftContainer[iEnemyAircraftCount].setY(0+iOffsetScreenHeightTopMargin+iTileHeight*i);
/*														
							myEnemyAircraftContainer[iEnemyAircraftCount].setX(0+iOffsetScreenWidthLeftMargin-iDifferenceInXPos);
							myEnemyAircraftContainer[iEnemyAircraftCount].setY(0+iOffsetScreenHeightTopMargin-iDifferenceInYPos);
*/							
							tileMap[i][k]=TILE_BLANK;

							iEnemyAircraftCount++;
						}
					}
				}
				else if (tileMap[i][k]==TILE_AIRCRAFT_BOSS) {

					System.out.println("TILE_AIRCRAFT BOSS!");

					if (iEnemyAircraftBossCount<MAX_ENEMY_AIRCRAFT_BOSS_COUNT) {
						if (myEnemyAircraftBossContainer[iEnemyAircraftBossCount].getMyTileType()==TILE_AIRCRAFT_BOSS) {							
							if (myEnemyAircraftBossContainer[iEnemyAircraftBossCount].isActive()) {
								myEnemyAircraftBossContainer[iEnemyAircraftBossCount].setX(0+iOffsetScreenWidthLeftMargin+iTileWidth*k);
								myEnemyAircraftBossContainer[iEnemyAircraftBossCount].setY(0+iOffsetScreenHeightTopMargin+iTileHeight*i);

								tileMap[i][k]=TILE_BLANK;

								iEnemyAircraftBossCount++;
							}
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
		//edited by Mike, 20240904
		//g2d.setClip(new Area(new Rectangle2D.Double(0, 0, iOffsetScreenWidthLeftMargin*2+iStageWidth, iOffsetScreenHeightTopMargin+iStageHeight)));
		g2d.setClip(new Area(new Rectangle2D.Double(0, 0, iOffsetScreenWidthLeftMargin+iStageWidth+iOffsetScreenWidthRightMargin, iOffsetScreenHeightTopMargin+iStageHeight+iOffsetScreenHeightTopMargin)));

		//paint the margins;
		//edited by Mike, 20240905
		//g.setColor(Color.decode("#adb2b6")); //gray
		g.setColor(Color.BLACK);

		//cover the left margin
		//g.fillRect(0,0,iOffsetScreenWidthLeftMargin,iStageHeight);

		g.fillRect(0,0,iOffsetScreenWidthLeftMargin,0+iOffsetScreenHeightTopMargin+iStageHeight+iOffsetScreenHeightTopMargin);

		//cover the right margin
		g.fillRect(0+iOffsetScreenWidthLeftMargin+iStageWidth,0,0+iOffsetScreenWidthLeftMargin+iStageWidth+iOffsetScreenWidthRightMargin,iStageHeight+iOffsetScreenHeightTopMargin);

		//System.out.println(">>>");

		//added by Mike, 20240904
		//TOP margins
		g.fillRect(0+iOffsetScreenWidthLeftMargin,0,0+iOffsetScreenWidthLeftMargin+iStageWidth,0+iOffsetScreenHeightTopMargin);

		//BOTTOM margins
		g.fillRect(0+iOffsetScreenWidthLeftMargin,0+iOffsetScreenHeightTopMargin+iStageHeight,0+iOffsetScreenWidthLeftMargin+iStageWidth,0+iOffsetScreenHeightTopMargin+iStageHeight+iOffsetScreenHeightTopMargin);

		//removed by Mike, 20240711; from 20240625
		//put after the last object to be drawn
		//g2d.dispose();
	}

	//added by Mike, 20240901
	//public void drawMiniMapBuggyWithEnemyAircraft(Graphics g) {
	public void drawMiniMap(Graphics g) {
		//added by Mike, 20240903; from 20240901
		//13x65; height x width; up to row 13; column BM
		int iMiniMapWidth=iTileWidth*7; //*5
		int iMiniMapHeight=iTileHeight*2;
		int iMiniMapX=0+iOffsetScreenWidthLeftMargin+(iStageWidth-iMiniMapWidth)/2;//iTileWidth*4;
		int iMiniMapY=0+iOffsetScreenHeightTopMargin+iStageHeight-iTileHeight*2;

		int iMiniMapTileWidth=iMiniMapWidth/MAX_TILE_MAP_WIDTH;
		//edited by Mike, 20240905
		//int iMiniMapTileHeight=iMiniMapHeight/MAX_TILE_MAP_HEIGHT;
		int iMiniMapTileHeight=(iMiniMapHeight-iMiniMapTileWidth*2)/MAX_TILE_MAP_HEIGHT;

		int[][] myBGTileMap=myBackgroundCanvas.getTileMap();

		//int iMiniMapOffsetScreenWidthLeftMargin=(iMiniMapWidth-iMiniMapHeight)/2;
		int iMiniMapOffsetScreenWidthLeftMargin=(iMiniMapWidth-iMiniMapTileWidth*MAX_TILE_MAP_WIDTH)/2;

		//update here after
		iMiniMapHeight=iMiniMapTileHeight*MAX_TILE_MAP_HEIGHT;
		iMiniMapWidth=iMiniMapTileWidth*MAX_TILE_MAP_WIDTH;

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

		//#1d2022; darker
		//g.setColor(new Color(19,21,22,22));
		//g.fillRect(iMiniMapX,iMiniMapY,iMiniMapWidth,iMiniMapHeight);

		g.setColor(Color.decode("#33c699")); //lubuntu, BreezeModified theme; dark terminal

		for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
			g.drawLine(iMiniMapOffsetScreenWidthLeftMargin+iMiniMapX,iMiniMapY+iMiniMapTileHeight*i,iMiniMapOffsetScreenWidthLeftMargin+iMiniMapX+iMiniMapWidth,iMiniMapY+iMiniMapTileHeight*i);			
		}

		//draw vertical line
		//include the last line
		for (int j=0; j<=MAX_TILE_MAP_WIDTH; j++) {
			g.drawLine(iMiniMapOffsetScreenWidthLeftMargin+iMiniMapX+iMiniMapTileWidth*j,iMiniMapY,iMiniMapOffsetScreenWidthLeftMargin+iMiniMapX+iMiniMapTileWidth*j,iMiniMapY+iMiniMapHeight);
		}

		g.drawRect(iMiniMapOffsetScreenWidthLeftMargin+iMiniMapX,iMiniMapY,iMiniMapWidth,iMiniMapHeight);
		
		double dMiniMapHeroX=((iViewPortX-iOffsetScreenWidthLeftMargin-iMiniMapTileWidth*1.5)/(MAX_TILE_MAP_WIDTH*iTileWidth)*iMiniMapWidth);

		//System.out.println("iViewPortX: "+iViewPortX);

		//use myRobotShip.getY(), instead of myViewPortY,
		//due to can move more vertically;

		double dMiniMapHeroY=((myRobotShip.getY()*1.0)/(MAX_TILE_MAP_HEIGHT*iTileHeight)*iMiniMapHeight)-iMiniMapTileHeight;
		
		//added by Mike, 20240905
		if (bIsMaxedMonitorHeight) {
			//goes to the negative -248			
			double dAdditionalOffset=((iViewPortWidth/2*1.0)/(MAX_TILE_MAP_WIDTH*iTileWidth)*iMiniMapWidth);

			dMiniMapHeroX+=dAdditionalOffset;//-iMiniMapTileWidth;
			dMiniMapHeroY+=iMiniMapTileHeight;
		}

		if (iFrameCountDelay<iFrameCountDelayMax/6) {
			iFrameCountDelay++;
		}
		else {
			iFrameCount=(iFrameCount+1)%iFrameCountMax;
			iFrameCountDelay=0;
		}

		//System.out.println(">>>>>>>>>>>>>>>>>>>>>iFrameCount: "+iFrameCount);

	    for (int i=0; i<MAX_TILE_MAP_HEIGHT; i++) {
		  for (int k=0; k<MAX_TILE_MAP_WIDTH; k++) {

			if (myBGTileMap[i][k]==TILE_BASE) {
				//#E2536F")); //pink; //#FF7236")); //orange
				g.setColor(Color.decode("#A6340C"));

				double dTileMapY=((i*iTileHeight*1.0)/(MAX_TILE_MAP_HEIGHT*iTileHeight)*iMiniMapHeight);

				double dTileMapX=((k*iTileWidth*1.0)/(MAX_TILE_MAP_WIDTH*iTileWidth)*iMiniMapWidth);//+iMiniMapTileWidth/2;
/*
				System.out.println(">>>>>>>>>>>>>>dTileMapX: "+dTileMapX);
				System.out.println(">>>>>>>>>>>>>>dTileMapY: "+dTileMapY);
*/
				//blinking
				if (iFrameCount==0) {
					g.fillRect(iMiniMapOffsetScreenWidthLeftMargin+iMiniMapX+(int)dTileMapX-iMiniMapTileWidth/2,iMiniMapY+(int)dTileMapY-iMiniMapTileHeight/2,iMiniMapTileWidth+iMiniMapTileWidth,iMiniMapTileHeight+iMiniMapTileHeight);
				}
				else {
					g.fillRect(iMiniMapOffsetScreenWidthLeftMargin+iMiniMapX+(int)dTileMapX,iMiniMapY+(int)dTileMapY,iMiniMapTileWidth,iMiniMapTileHeight);
				}
			}
		  }
	    }
	
		//added by Mike, 20240902
		for (int i=0; i<MAX_ENEMY_AIRCRAFT_COUNT; i++) {
			
			if (!myEnemyAircraftContainer[i].isActive()) {
				continue;
			}

			//#E2536F")); //pink; //#FF7236")); //orange
			g.setColor(Color.decode("#FF7236")); //A6340C
						
			double dTileMapY=((myEnemyAircraftContainer[i].getY()*1.0-iOffsetScreenHeightTopMargin)/(MAX_TILE_MAP_HEIGHT*iTileHeight)*iMiniMapHeight);
			
			double dTileMapX=((myEnemyAircraftContainer[i].getX()*1.0-iOffsetScreenWidthLeftMargin)/(MAX_TILE_MAP_WIDTH*iTileWidth)*iMiniMapWidth);

			double dMiniMapOffsetLeftMargin=((iOffsetScreenWidthLeftMargin)/(iOffsetScreenWidthLeftMargin+MAX_TILE_MAP_WIDTH*iTileWidth)*iMiniMapWidth);
			
			g.fillRect(iMiniMapOffsetScreenWidthLeftMargin+iMiniMapX+(int)dTileMapX,iMiniMapY+(int)dTileMapY,iMiniMapTileWidth,iMiniMapTileHeight);
			
			//note isosceles triangle too large;
/*			
			//added by Mike, 20240928
			//https://stackoverflow.com/questions/675878/how-to-fill-color-on-triangle/675888#675888; last accessed: 20240928
			
			int[] x = new int[3];
			int[] y = new int[3];
		
			//triangle
			x[0]=iMiniMapOffsetScreenWidthLeftMargin+iMiniMapX+(int)dTileMapX; 
			x[1]=iMiniMapOffsetScreenWidthLeftMargin+iMiniMapX+(int)dTileMapX+(iMiniMapTileWidth*3)/1; 
			x[2]=iMiniMapOffsetScreenWidthLeftMargin+iMiniMapX+(int)dTileMapX;
			
			y[0]=iMiniMapY+(int)(dTileMapY+iMiniMapTileHeight/2)-(iMiniMapTileHeight)/1; 
			y[1]=iMiniMapY+(int)(dTileMapY+iMiniMapTileHeight/2); 
			y[2]=iMiniMapY+(int)(dTileMapY+iMiniMapTileHeight/2)+(iMiniMapTileHeight)/1;
			int n = 3;

			Polygon p = new Polygon(x, y, n);
			g.setColor(Color.decode("#ff0000")); 
			g.fillPolygon(p);
*/			
		}
		
		for (int i=0; i<MAX_ENEMY_AIRCRAFT_BOSS_COUNT; i++) {
			
			if (!myEnemyAircraftBossContainer[i].isActive()) {
				continue;
			}

			//#E2536F")); //pink; //#FF7236")); //orange
			g.setColor(Color.decode("#FF7236")); //A6340C
						
			double dTileMapY=((myEnemyAircraftBossContainer[i].getY()*1.0-iOffsetScreenHeightTopMargin)/(MAX_TILE_MAP_HEIGHT*iTileHeight)*iMiniMapHeight);
			
			double dTileMapX=((myEnemyAircraftBossContainer[i].getX()*1.0-iOffsetScreenWidthLeftMargin)/(MAX_TILE_MAP_WIDTH*iTileWidth)*iMiniMapWidth);

			double dMiniMapOffsetLeftMargin=((iOffsetScreenWidthLeftMargin)/(iOffsetScreenWidthLeftMargin+MAX_TILE_MAP_WIDTH*iTileWidth)*iMiniMapWidth);
			
			g.fillRect(iMiniMapOffsetScreenWidthLeftMargin+iMiniMapX+(int)dTileMapX,iMiniMapY+(int)dTileMapY,iMiniMapTileWidth,iMiniMapTileHeight);
		}

        g.setColor(Color.decode("#59A6DC")); //blue; hero
        g.fillRect(iMiniMapOffsetScreenWidthLeftMargin+iMiniMapX+(int)dMiniMapHeroX,iMiniMapY+(int)dMiniMapHeroY,iMiniMapTileWidth,iMiniMapTileHeight);

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
		
		for (int iEnemyAircraftBossCount=0; iEnemyAircraftBossCount<MAX_ENEMY_AIRCRAFT_BOSS_COUNT; iEnemyAircraftBossCount++) {
			if (myEnemyAircraftBossContainer[iEnemyAircraftBossCount].isActive()) {
				myEnemyAircraftBossContainer[iEnemyAircraftBossCount].draw(g);
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
		
		//added by Mike, 20241018
		for (int iEnemyPlasmaCount=0; iEnemyPlasmaCount<MAX_ENEMY_PLASMA_COUNT; iEnemyPlasmaCount++) {			
			if (myEnemyPlasmaContainer[iEnemyPlasmaCount].isActive()) {
				myEnemyPlasmaContainer[iEnemyPlasmaCount].draw(g);
				
				System.out.println("HALLO");
				System.out.println(">>>>>>>>>>>>>>myEnemyPlasmaContainer[iEnemyPlasmaCount].getX(): "+myEnemyPlasmaContainer[iEnemyPlasmaCount].getX());
				
				System.out.println(">>>>>>>>>>>>>>myEnemyPlasmaContainer[iEnemyPlasmaCount].getY(): "+myEnemyPlasmaContainer[iEnemyPlasmaCount].getY());

			}
		}

		myRobotShip.draw(g);

		drawMiniMap(g);

		drawMargins(g);

		//added by Mike, 20240907
		myUsbongText.draw(g);
		
		//added by Mike, 20240908
		//myUsbongFont.draw(g); 
	}
}
