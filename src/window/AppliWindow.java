package window;


import exceptions.MapFileException;
import exceptions.RealPlayerException;
import game.Base;
import game.Game;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.vecmath.Vector2f;

import org.jdom2.JDOMException;

@SuppressWarnings("serial")
public class AppliWindow extends JFrame {

	private int width;
	private int height;
	private Panel content;
	private JLabel image;

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Panel getContent() {
		return content;
	}

	public void setContent(Panel content) {
		this.content = content;
	}
	
	public int getTilesSize() {
		return 50;
	}

	public int getNumOfTile() {
		return ((this.width/getTilesSize()) * (this.height/getTilesSize()));
	}

	// How many tiles on the width : for 800px it's 16, because 16x50=800
	public int getNumOfTileWidth() {
		return (this.width/getTilesSize());
	}

	public int getNumOfTileHeight() {
		return (this.height/getTilesSize());
	}

	public JLabel getImage() {
		return image;
	}

	public void setImage(JLabel image) {
		this.image = image;
	}

	public AppliWindow(String title, int width, int height, boolean resize, String pathImage) throws MapFileException, JDOMException, IOException{
		super();
		this.width = width;
		this.height = height;
		
		buildWindow(title, resize); // On initialise notre fenetre (We initiate the window)
		
		// Add a background, providing the path
		buildBackground(pathImage);

	}

	/**
	 * Builds the window of the application
	 * @param width		The width of the window
	 * @param height	The height of the window
	 * @param resize	indicates if the window is resizable or not
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws MapFileException 
	 */ 
	private void buildWindow(String title, boolean resize) throws MapFileException, JDOMException, IOException {
		setTitle(title);
		setSize(this.width, this.height);
		setLocationRelativeTo(null); // null => The window is centered on the screen
		setResizable(resize); // Resizable window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // The application musts close when we click on the cross
		setContentPane(buildContentPane(width, height));
		
	}

	/**
	 * Builds the elements of the window corresponding to the bases
	 */
	private void buildBases() {

		Game game = Game.getInstance();
		
		for(final Base base: game.getBaseManager().getBases()) {
			base.setBorder(BorderFactory.createLineBorder(Color.black));
			base.setContentAreaFilled(false);
			base.setBounds((int)base.getPosition().x, (int)base.getPosition().y, getTilesSize(), getTilesSize());
			base.setOpaque(false);
			try
			{
				base.setIcon(new ImageIcon(ImageIO.read(new File("design/cercle2.png"))));
			}
			catch (IOException e1)
			{
				System.err.println("Error ! Loading base image");
			}
			base.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// all actions are managed by the base
					try {
						base.clicked();
					} catch (RealPlayerException e1) {
						System.err.println("Error with RealPlayer : can't manage the click.");
					} 
				}
			});
			
			/*
			// Am I doing it right ?? (I guess not)
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = (int) base.getPosition().x;
			c.gridy = (int) base.getPosition().y;
			*/
			content.add(base);
			
		}
	}
	
	/**
	 * Add JPanel Tiles where there is nothing else.
	 * In a bulk, it adds square, the same as Base, but with nothing to do on it.
	 * Just as a map, a game map.
	 * 
	 * Still need a more random disposition of Tiles.
	 * @deprecated The map is no more built with Tiles
	 */
	@Deprecated
	public void buildTiles() {
	
		Game game = Game.getInstance();
		
		// How many tiles
		int tilesToBuild = this.getNumOfTile() - game.getBaseManager().getBases().size();
		
		// Tab of the Base coords where to not draw tiles (2D integers)
		int tab[][] = new int[game.getBaseManager().getBases().size()][game.getBaseManager().getBases().size()];
		
		// Initialize the tab with the good coordinates
		for(int k = 0; k<tab.length; ++k) {
			tab[k][0] = (int) game.getBaseManager().getBases().get(k).getPosition().x;
			tab[k][1] = (int) game.getBaseManager().getBases().get(k).getPosition().y;
		}
		
		// ArrayList of Vector2f where to build.
		ArrayList<Vector2f> tabOfWhereToBuild = new ArrayList<Vector2f>(tilesToBuild);
		
		// Creating all the ArrayList of Vector2f for Tiles, even the bad one
		for(int i = 0; i<getNumOfTileWidth(); ++i) {
			for(int j = 0; j<getNumOfTileHeight(); ++j) {	
				tabOfWhereToBuild.add(new Vector2f(i, j));
			}	
		}
		
		// DEBUG : tous les vecteurs dans l'ordre.
		for(int i = 0; i<this.getNumOfTile(); ++i) {
			System.out.println(tabOfWhereToBuild.get(i));
		}
		
		// Remove the bad one and add what we wanted. That's the better way I find after 4h of trying it, deal with it =D
		for(int k=0; k<tab.length; ++k) {
			tabOfWhereToBuild.remove( ( tab[k][1] +  tab[k][0]*(getNumOfTileHeight()) ) -k );
			tabOfWhereToBuild.add(new Vector2f(tab[k][0], tab[k][1]));
			System.out.println("Current index of the Tiles : "+ ( tab[k][1] +  tab[k][0]*(getNumOfTileHeight()) -k ) );
			System.out.println("Tiles'coordinates : ("+tab[k][0]+"; "+tab[k][1]+") ");
		}	
		
		for(int k = 0; k < tilesToBuild; ++k) {
			JButton pan = new JButton();
			pan.setBorder(BorderFactory.createLineBorder(Color.black));
			pan.setContentAreaFilled(false);
			pan.setBounds(0, 0, 1, 1);
			try
			{	
				//Stupid random =D
				if(k==5 || k==6 || k==7 || k==8 || k==9 )
					pan.setIcon(new ImageIcon(ImageIO.read(new File("design/rock2-tile.png"))));
				else if(k==18 || k==19 || k==20 || k==30)
					pan.setIcon(new ImageIcon(ImageIO.read(new File("design/rock2-tile.png"))));
				else if(k==180 || k==181 || k==182 || k==183 || k==184 )
					pan.setIcon(new ImageIcon(ImageIO.read(new File("design/rock2-tile.png"))));
				else if(k==170 || k==171 || k==172 || k==160 )
					pan.setIcon(new ImageIcon(ImageIO.read(new File("design/rock2-tile.png"))));
				else if(k==46 || k==58 || k==70 || k==82 || k==94 || k==106 )
					pan.setIcon(new ImageIcon(ImageIO.read(new File("design/rock2-tile.png"))));
				else if(k==59 || k==71 || k==72 || k==83 || k==84 || k==85 || k==95 )
					pan.setIcon(new ImageIcon(ImageIO.read(new File("design/rock2-tile.png"))));
				else if(k==69 || k==83 || k==93 || k==105 || k==117 || k==129 )
					pan.setIcon(new ImageIcon(ImageIO.read(new File("design/rock2-tile.png"))));
				else if(k==79 ||k==80 ||k==81 ||k==92||k==104 ||k==116|| k==105 ||k==91 ||k==90)
					pan.setIcon(new ImageIcon(ImageIO.read(new File("design/rock2-tile.png"))));
				
				
				else if(k==39 || k==51 || k == 120|| k == 125)
					pan.setIcon(new ImageIcon(ImageIO.read(new File("design/pit1-tile.png"))));
				
				else if(k==45 || k==57 || k==69 || k==81 || k==93 || k==105 )
					pan.setIcon(new ImageIcon(ImageIO.read(new File("design/gravel-tile.png"))));
				else if(k==20 || k==21 || k==17|| k==16 || k==4|| k==169 || k==179 || k==173 || k==155 || k==156 || k==161)
					pan.setIcon(new ImageIcon(ImageIO.read(new File("design/gravel-tile.png"))));
				
				else
					pan.setIcon(new ImageIcon(ImageIO.read(new File("design/rock1-tile.png"))));
			}
			catch (IOException e1)
			{
				System.err.println("Failed to load Tiles image!");
			}
			/*
			c.gridx = (int)tabOfWhereToBuild.get(k).getX();
			c.gridy = (int)tabOfWhereToBuild.get(k).getY();
			*/
		}
		
	}

	
	/**
	 * Build the game : creates the informations from a XML file and build all corresponding elements in the window.
	 * @param fileName	The name of the XML file needed to create the game
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws MapFileException 
	 */
	private void buildGame(String xmlFileName, String mapFileName) throws MapFileException, JDOMException, IOException {
		Game game = Game.getInstance();
		game.initGame(xmlFileName, mapFileName);
		buildBases();
//		buildAgents();
//		buildTowers();
//		//...
		
		game.start();


	}

	/**
	 * Builds the window ????????????????????????????????
	 * @param width		The width of the window ?????????
	 * @param height	The height of the window ????????
	 * @return			The panel ???????????????????????
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws MapFileException 
	 */
	private Panel buildContentPane(int width, int height) throws MapFileException, JDOMException, IOException {

		this.content = new Panel();

		content.setLayout(null);
		content.setBackground(Color.GRAY);
		
		buildGame("game.xml", "testMap");

		return content;
	}

	/**
	 * Add a background image, which will fit all the window.
	 * @param pathImage		A String for the image path
	 */
	private void buildBackground(String pathImage) {
		// This try/catch is used for trying to set a background !
		try {
			this.image =  new JLabel(new ImageIcon(ImageIO.read(new File(pathImage))), JLabel.CENTER);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.getImage().setBounds(0, 0, getWidth(), getHeight());
		this.content.add(this.image);
	}

}
