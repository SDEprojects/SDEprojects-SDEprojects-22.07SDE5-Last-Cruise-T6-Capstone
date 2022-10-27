package com.lastcruise.model;

import com.lastcruise.controller.KeyHandler;
import com.lastcruise.model.entity.Player;
import com.lastcruise.model.tile.TileManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

  // SCREEN SETTINGS
  private final int originalTileSize = 16;
  private final int scale = 3;

  private final int tileSize = originalTileSize * scale;
  private final int maxScreenCol = 16;
  private final int maxScreenRow = 12;
  private final int screenWidth = tileSize * maxScreenCol;
  private final int screenHeight = tileSize * maxScreenRow;
  // Frames per second
  private int FPS = 60;

  private KeyHandler keyHandler = new KeyHandler();
  private Thread gameThread;
  private Player player = new Player();
  private TileManager tileManager = new TileManager( maxScreenCol, maxScreenRow);
  private Collision collision = new Collision(tileSize, tileManager.getMapTileIndex(), tileManager.getTile());
  public GamePanel() {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setBackground(Color.black);
    this.setDoubleBuffered(true);
    this.addKeyListener(keyHandler);
    this.setFocusable(true);
  }

  public void startGameThread() {
    gameThread = new Thread(this);
    gameThread.start();
  }

  @Override
  public void run() {
    double drawInterval = 1000000000/FPS;
    double delta = 0;
    long lastTime = System.nanoTime();
    long currentTime;

    while (gameThread != null) {
      currentTime = System.nanoTime();
      delta += (currentTime - lastTime) / drawInterval;
      lastTime = currentTime;
      if (delta >= 1) {
        update();
        repaint();
        delta--;
      }
    }
  }

  public void update() {
    if (keyHandler.isUpPressed() || keyHandler.isDownPressed() || keyHandler.isLeftPressed() || keyHandler.isRightPressed()){
      player.updateDirection(keyHandler.isUpPressed(), keyHandler.isDownPressed(), keyHandler.isLeftPressed(), keyHandler.isRightPressed());

      int topRow = (player.getY() + player.getSolidArea().y - player.getSpeed()) / tileSize;
      int bottomRow = (player.getY() + player.getSolidArea().y + player.getSolidArea().height + player.getSpeed()) / tileSize;
      int leftCol = (player.getX() + player.getSolidArea().x - player.getSpeed()) / tileSize;
      int rightCol = (player.getX() + player.getSolidArea().x + player.getSolidArea().width + player.getSpeed()) / tileSize;

      player.setCollisionOn(collision.checkTile(topRow, bottomRow, leftCol, rightCol, player.getDirection()));
      // IF COLLISION IS FALSE THE PLAYER CAN MOVE
      player.updatePosition();
    }
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    tileManager.draw(g2, tileSize);
    player.draw(g2, tileSize);
    g2.dispose();
  }
}
