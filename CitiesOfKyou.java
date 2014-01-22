import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import java.lang.Math;
import java.util.Random;
import java.util.ArrayList;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.SwingUtilities;

//Class to start everything up
public class Noto {
	//Window dimensions
	private static final int WIDTH = 800;
	private static final int HEIGHT = 640;
	
	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	
	public static void createAndShowGUI() {
		JFrame frame = new NotoFrame(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
	}
}



//This class represents the game window (menubar, frame)
class NotoFrame extends JFrame {
	//Variables for the dimensions of the frame
	private final int WIDTH;
	private final int HEIGHT;
	
	//Load file chooser for loading/saving games
	private JFileChooser chooser = new JFileChooser();
	
	//Constructor
	public NotoFrame(int width, int height) {
		setTitle("Noto - Note-Taking App");
		WIDTH = width;
		HEIGHT = height;
		setSize(width, height);
		
		//Add file menu to game screen
		addMenu();
	}
	
	private void addMenu() {
		// Create menu object
		JMenu menu = new JMenu("File");
		
		// Create a new item to put in menu
		// Menu item to save plain-text file
		JMenuItem item = new JMenuItem("Save");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Function to save out plain-text file here
			}
		});
		menu.add(item);
		
		// Menu item to load plain-text file
		item = new JMenuItem("Load");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Function to load plain-text file here
			}
		});
		menu.add(item);
		
		// Menu item to exit/quit the application
		item = new JMenuItem("Exit");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		menu.add(item);
		
		//Attach menu to the menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);

		// Create second build-menu
		menu = new JMenu("Build");
		
		// Create a new item to put in menu
		// Menu item to save plain-text file
		JMenuItem item = new JMenuItem("Build Single");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Function to save out plain-text file here
			}
		});
		menu.add(item);
		
		// Menu item to load plain-text file
		item = new JMenuItem("Build All");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Function to load plain-text file here
			}
		});
		menu.add(item);

		menuBar.add(menu);

		setJMenuBar(menuBar);
	}
	
	//Method used to load an older game
	public void load() {
		
	}
	
	//Method used to save out current game
	public void save() {
		
	}
}



//This class handles all the game functionality
//it houses major aspects of the game, so it needs
//to extend JPanel to display things
/*class Game extends JPanel {
	//Different characteristics of the game
	private String playerName;
	private Player player;
	private City city;
	private Menu menu;
	
	//Determine if in pause/start menu
	private boolean paused = false;
	//Determine if inside a store
	private boolean inside = false;
	//Which building is player in
	//And where did they enter from
	private int buildingNum, oldX, oldY;
	//Determine if inside the corporation building
	private boolean insideCorp = false;

	//Timer to redraw/animate graphics
	private Timer timer;

	//Graphical assests for the game
	private Graphics2D g2d;
	
	//How big the viewport is
	private final int WIDTH = 800;
	private final int HEIGHT = 640;
	
	public Game(String name) {
		//Instantiate parts of the game
		city = new City();		
		playerName = name;
		player = new Player(name);
		menu = new Menu(player.items);
		
		//Set panel characteristics
		setFocusable(true);
		setVisible(true);
		setBackground(Color.BLACK);

		//Add timer to add in the animation
		//Redraws the image every half-second
		timer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				repaint();
			}
		});

		//Add a listner to manage the keyboard controls
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				int key = event.getKeyCode();

				//Key events for game play
				if(!paused) {
					//Get player's x and y in the city grid
					//Refer to CITY class
					int pX = player.getX()/20;
					int pY = player.getY()/20;
					
					//Moving the player controls
					//Using a WASD movement layout, check for collisions with
					//map borders, buildings, and other characters
					
					//Move up with 'W' key
					if(key == KeyEvent.VK_W) {
						//Check for upper boundary
						if(pY != 0) {
							//Check for collision in outside world
							if(!inside && !insideCorp) {
								//Next to something
								if(city.map[pX][pY-1]) {
									//Check to see if next to store door
									if(city.map[pX-1][pY-1] && city.map[pX-2][pY-1] && !(city.map[pX-3][pY-1]) && city.map[pX+1][pY-1] && city.map[pX+2][pY-1] && !(city.map[pX+3][pY-1])
										|| city.map[pX-1][pY-1] && !(city.map[pX-2][pY-1]) && city.map[pX+1][pY-1] && city.map[pX+2][pY-1] && city.map[pX+3][pY-1] && !(city.map[pX+4][pY-1])) {
										//Go inside building
										inside = true;
										//Stop citizens from moving
										city.npcTimer.stop();
										//Remember where the player entered at
										oldX = player.x; player.x = 240;
										oldY = player.y; player.y = 500;
										//Determine which building was entered
										//Made sure to place every building at a different Y location
										for(int i=0; i<city.buildings.length;i++) {
											if(city.buildings[i].getY() == oldY - 100) { buildingNum = i; }
										}
									}
									//Not next to door
									else { return; }
								}
							}
							//Check inside store
							else if(inside) {
								if(city.buildings[buildingNum].map[pX][pY-1]) { return; };
							}
							//Check inside corporation
							else if(insideCorp) {
								if(city.corporation.map[pX][pY-1]) { return; }
							}
							player.moveUp();
						}
					}
					
					//Move left with 'A' key
					else if(key == KeyEvent.VK_A) {
						//Check for left boundary
						if(pX != 0) {
							if(!inside && !insideCorp) {
								if(city.map[pX-1][pY]) { return; }
							}
							//Check inside store
							else if(inside) {
								if(city.buildings[buildingNum].map[pX-1][pY]) { return; }
							}
						}
						//Check inside corporation
						else if(insideCorp) {
							//Check if leaving the corporation
							if(pX == 0 && pY == 12) { 
								insideCorp = false;
								//Start citizens moving again
								city.npcTimer.restart();
								//Remember where the player entered at
								player.x = oldX;
								player.y = oldY;
								return;
							}
							//Check other objects
							else if(pX != 0) {
								if(city.corporation.map[pX-1][pY]) { return; }
							}
						}
						if(pX != 0) { player.moveLeft(); }
					}
					
					//Move down with 'S' key
					else if(key == KeyEvent.VK_S) {
						if(!inside && !insideCorp) {
							//Check bottom boundary
							if(pY == 53) { return; }
							//Check other objects
							if(city.map[pX][pY+1]) { return; }
						}
						//Check for inside store
						else if(inside) {
							//Check to see if exiting the building
							if(pY == 24 && pX == 12) {
								player.y = oldY;
								player.x = oldX;
								inside = false;
								city.npcTimer.restart();
								return;
							} 
							//Check bottom boundary
							else if(pY == 24) { return; }
							//Check other objects
							else if(city.buildings[buildingNum].map[pX][pY+1]) { return; }
						}
						//Check for inside corporation
						else if(insideCorp) {
							//Check bottom boudnary
							if(pY == 24) { return; }
							//Check other objects
							else if(city.corporation.map[pX][pY+1]) { return; }
						}
						player.moveDown();
					}
					
					//Move right with 'D' key
					else if(key == KeyEvent.VK_D) {
						if(!inside && !insideCorp) {
							//Check right boundary
							if(pX == 94) { return; }
							//Check if entering into corporation
							else if(city.map[pX+1][pY] && city.map[pX+2][pY] && city.map[pX+1][pY+1] &&  city.map[pX+1][pY+2] && !(city.map[pX+1][pY+3])
									|| city.map[pX+1][pY] && city.map[pX+2][pY] && city.map[pX+1][pY+1] &&  city.map[pX+1][pY+2] && city.map[pX+1][pY+3]) {
								insideCorp = true;
								//Stop citizens from moving
								city.npcTimer.stop();
								//Remember where the player entered at
								oldX = player.x; player.x = 0;
								oldY = player.y; player.y = 240;
								return;
							}
							//Check other objects
							else if(city.map[pX+1][pY]) { return; }
						}
						//Check in store
						else if(inside) {
							//Check right boundary
							if(pX == 24) { return; }
							//Check other objects
							else if(city.buildings[buildingNum].map[pX+1][pY]) { return; }
						}
						//Check in corporation
						else if(insideCorp) {
							//Check right boundary
							if(pX == 24) { return; }
							//Check other objects
							else if(city.corporation.map[pX+1][pY]) { return; }
						}
						player.moveRight();
					}

					//Enter into player menu/pause screen
					else if(key == KeyEvent.VK_M) {
						timer.stop();
						city.npcTimer.stop();
						paused = true;
					}

					//Interact with people/objects by using 'SPACE'
					else if(key == KeyEvent.VK_SPACE) {
						//Check for interactions out in the city
						if(!inside && !insideCorp) {
							//Check to see if near something
							int locX, locY;

							try {
								if(city.map[pX][pY+1]) {
									locX = pX * 20;
									locY = (pY+1) * 20;
								} else if(city.map[pX][pY-1]) {
									locX = pX * 20;
									locY = (pY-1) * 20;
								} else if(city.map[pX+1][pY]) {
									locX = (pX+1) * 20;
									locY = pY * 20;
								} else if(city.map[pX-1][pY]) {
									locX = (pX-1) * 20;
									locY = pY * 20;
								}

								//Not next to anything
								else { return; }
							} catch (ArrayIndexOutOfBoundsException e) { return; }
							
							//Determine if person
							for(int i=0; i<city.citizens.length; i++) {
								if(city.citizens[i].getX() == locX && city.citizens[i].getY() == locY) {
									//Match found
									//If the person is an NPC open a dialog box
									if(city.citizens[i] instanceof NPC) {
										NPC npc = (NPC)city.citizens[i];
										city.npcTimer.stop();
										npc.talk();
										city.npcTimer.restart();
									}
								}
							}
						}
						//Next to store owner behind counter
						else if(inside) {
							//If standing next counter where shopkeeper is
							if(pX == 12 && pY == 15) {
								//Select the shopkeeper and talk start talking
								Shopkeeper shopkeep = (Shopkeeper)city.buildings[buildingNum].getOwner();
								int result = shopkeep.talk();

								switch(result) {
									//Option to buy something
									case JOptionPane.YES_OPTION:	if(shopkeep.buy(player)) {
																		//Decrease the player's preferential treatment
																		//with every other shopkeeper
																		for(int j=0; j<4; j++) {
																			if(j != buildingNum) {
																				shopkeep = (Shopkeeper)city.buildings[j].getOwner();
																				shopkeep.preferenceDown();
																			}
																		}
																	}
																	break;
									//Option to sell something
									case JOptionPane.NO_OPTION: 	if(shopkeep.sell(player)) {
																		//Decrease the player's preferential treatment
																		//with every other shopkeeper
																		for(int j=0; j<4; j++) {
																			if(j != buildingNum) {
																				shopkeep = (Shopkeeper)city.buildings[j].getOwner();
																				shopkeep.preferenceDown();
																			}
																		}
																	}
																	break;
								}
							}
							return;
						}
						//Check for next to treasure chest in corporation
						//Player must be below it
						else if(insideCorp) {
							if(pX == city.corporation.chest.getX()/20 && (pY-1) == city.corporation.chest.getY()/20) {
								Item item = city.corporation.chest.takeItem();
								player.items.add(item);
								JOptionPane.showMessageDialog(null, "Congrats! You got " + item.description);
							}
						}
					}
				}

				//Game is paused/player is in menu
				else {
					//Key events for menu
					//Exit from menu
					if(key == KeyEvent.VK_M) {
						paused = false;
						timer.restart();
						//If outside, start the characters moving again
						if(!inside && !insideCorp) {
							city.npcTimer.restart();
						}
					}

					//Controls for navigating the menu
					else if(key == KeyEvent.VK_W) {
						if(menu.selection > 0) { menu.selection--; }
					}
					else if(key == KeyEvent.VK_S) {
						if(menu.selection < player.items.size() - 1) { menu.selection++; }
					}
				}
				repaint();
			}
		});

		//Start animation timer
		timer.start();
	}
	
	public void paint(Graphics g) {
		//Render graphics for gameplay
		g2d = (Graphics2D) g;

		//Paint background black to define edges of playable area
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, WIDTH, HEIGHT);

		//Render graphics for the city
		if(!paused && !inside && !insideCorp) {
			g2d.drawImage(city.getImage(),  WIDTH/2 - player.getX(), HEIGHT/2 - player.getY(), null);
			for(int i=0; i<city.buildings.length; i++) {
				g2d.drawImage(city.buildings[i].getImage(), (WIDTH/2 - player.getX()) + city.buildings[i].getX(), (HEIGHT/2 - player.getY()) + city.buildings[i].getY(), null);
			}
			for(int i=0; i<city.citizens.length; i++) {
				g2d.drawImage(city.citizens[i].getImage(), (WIDTH/2 - player.getX()) + city.citizens[i].getX(), (HEIGHT/2 - player.getY()) + city.citizens[i].getY(), null);
			}
			g2d.drawImage(player.getImage(), WIDTH/2, HEIGHT/2, null);
		}

		//Render graphics for inside one of the stores
		else if(!paused && inside) {
			g2d.drawImage(city.buildings[buildingNum].getImageInside(), WIDTH/2 - player.getX(), HEIGHT/2 - player.getY(), null);
			g2d.drawImage(player.getImage(), WIDTH/2, HEIGHT/2, null);
			Shopkeeper owner = city.buildings[buildingNum].getOwner();
			g2d.drawImage(owner.getImage(), (WIDTH/2 - player.getX()) + owner.getX(), (HEIGHT/2 - player.getY()) + owner.getY(), null);
			
			//Check to see if player leveled up or not
			if(player.levelUp()) {
				JOptionPane.showMessageDialog(null, "Congratulations! You advanced to level: " + player.level);
			}
		}

		//Render graphics for inside corporation
		else if(!paused && insideCorp) {
			g2d.drawImage(city.corporation.getImage(), WIDTH/2 - player.getX(), HEIGHT/2 - player.getY(), null);
			g2d.drawImage(player.getImage(), WIDTH/2, HEIGHT/2, null);
			g2d.drawImage(city.corporation.chest.getImage(), (WIDTH/2 - player.getX()) + city.corporation.chest.getX(), (HEIGHT/2 - player.getY()) + city.corporation.chest.getY(), null);
			
			boolean detected = false;
			int gX, gY, los;
			int pX = player.getX()/20;
			int pY = player.getY()/20;
			
			for(int i=0; i<city.corporation.guards.length; i++) {
				gX = city.corporation.guards[i].getX();
				gY = city.corporation.guards[i].getY();
				g2d.drawImage(city.corporation.guards[i].getImage(), (WIDTH/2 - player.getX()) + gX, (HEIGHT/2 - player.getY()) + gY, null);
				//Check to see if player is detected
				gX = gX/20; gY = gY/20;
				los = city.corporation.guards[i].getLOS();
				switch (city.corporation.guards[i].getDir()) {
					//Guard is facing up
					case 0:		if(pY < gY && pY > gY - los && pX == gX) { detected = true; }
								break;
					//Guard is facing left
					case 1:		if(pX < gX && pX > gX - los && pY == gY) { detected = true; }
								break;
					//Guard is facing down
					case 2:		if(pY > gY && pY < gY + los && pX == gX) { detected = true; }
								break;
					//Guard is facing right			
					case 3:		if(pX > gX && pX < gX + los && pY == gY) { detected = true; }
								break;
								
				}
			}
			
			if(detected) {
				JOptionPane.showMessageDialog(null, "You've been caught!");
				player.credits -= 50 * player.level;
				insideCorp = false;
				//Start citizens moving again
				city.npcTimer.restart();
				//Remember where the player entered at
				player.x = oldX;
				player.y = oldY;
			}
		}

		//Render graphics for pause screen/menu
		else {
			//Draw menu background
			g2d.drawImage(menu.getImage(), 0, 0, null);

			//Display default player information (level, credits, next level credits)
			g2d.setColor(Color.WHITE);
			g2d.drawString("Level: " + player.level, 16, 24);
			g2d.drawString("Credits: " + player.credits, 16, 48);
			g2d.drawString("Next Level: " + player.nextLevel, 16, 72);

			//Display items in player's inventory and their value
			for(int i=0; i<menu.items.size(); i++) {
				if(i != menu.selection) {
					g2d.setColor(Color.WHITE);
				} else {
					//If the item is selected turn red
					g2d.setColor(Color.RED);
				}
				g2d.drawString(menu.items.get(i).description + "   " + menu.items.get(i).value, 16, (i+4)*24);
			}
		}
	}
}*/