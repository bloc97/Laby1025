/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labyrinthe;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import static java.lang.Thread.sleep;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import static labyrinthe.Helper.canMove;
import static labyrinthe.Helper.createArrow;
import static labyrinthe.Helper.directionToChar;
import static labyrinthe.Helper.isVisited;

/**
 *
 * @author Bowen Peng, Zhenglong, Devoir 2
 */
public class AIBreadthFirst implements AI { //Breadth-First AI Implementation
    private boolean[][] visited;
    private boolean[][] examined;
    private LinkedList<Integer> directionPath;
    private int x, y, dir;
    private int winX, winY;
    
    private boolean foundPath = false;
    
    private boolean useHeuristic;
    
    public AIBreadthFirst() {
        useHeuristic = false;
    }
    
    public AIBreadthFirst(boolean h) {
        useHeuristic = h;
    }
    
    private class DirectionPoint {
        public Point point;
        public LinkedList<Integer> directions;

        public DirectionPoint(Point point, LinkedList<Integer> directions) {
            this.point = point;
            this.directions = directions;
        }
        
    }
    
    public void getPath(int x, int y, int w, int h, ListeMuret murs, Muret sortie, JComponent affichage, int animWait) {
        
        visited = new boolean[w][h];
        examined = new boolean[w][h];
        LinkedList<DirectionPoint> queue = new LinkedList<>();
        directionPath = new LinkedList<>();
        
        winX = (sortie.isHorz) ? sortie.x : sortie.x-1;
        winY = (sortie.isHorz) ? sortie.y-1 : sortie.y;
        
        Point startPoint = new Point(x, y);
        LinkedList<Integer> startPath = new LinkedList<>();
        
        queue.addFirst(new DirectionPoint(startPoint, startPath));
        
        while (queue.size() > 0) {
            DirectionPoint currentDP = queue.getLast();
            double minScore = Double.MAX_VALUE;
            
            if (useHeuristic) {
                
                for (DirectionPoint dp : queue) {
                    double thisScore = Helper.distanceSqScore(dp.point.x, dp.point.y, sortie);
                    if (thisScore < minScore) {
                        currentDP = dp;
                        minScore = thisScore;
                    }
                }
            }
            
            queue.remove(currentDP);
            Point currentPoint = currentDP.point;
            LinkedList<Integer> currentPath = currentDP.directions;
            x = currentPoint.x;
            y = currentPoint.y;
            
            try {
                affichage.repaint();
                sleep(animWait);
            } catch (InterruptedException ex) {
                Logger.getLogger(RandomGenerators.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (x == winX && y == winY) {
                directionPath = currentPath;
                foundPath = true;
                return;
            } else {
                for (int i=0; i<4; i++) {
                    if (!isVisited(x, y, i, visited) && canMove(i, x, y, w, h, murs)) {
                        Point newPoint = Helper.getPointFromDirection(x, y, i);
                        visited[newPoint.x][newPoint.y] = true;
                        LinkedList<Integer> newPath = (LinkedList<Integer>)currentPath.clone();
                        newPath.add(i);
                        queue.addFirst(new DirectionPoint(newPoint, newPath));
                    }
                }
                visited[x][y] = true;
                examined[x][y] = true;
                
            }
            
            
            
        }
        
    } //Trouver le path (Avec animation)
    
    @Override
    public char getNextDirection(int x, int y, int w, int h, ListeMuret murs, Muret sortie, JComponent affichage, int animWait) {
        this.x = x;
        this.y = y;
        if (directionPath == null) {
            getPath(x, y, w, h, murs, sortie, affichage, animWait);
        }
        this.dir = directionPath.pop();
        affichage.repaint();
        return directionToChar(dir);
        
    }

    @Override
    public void paint(Graphics2D g2, double sqSize) {
        if (visited == null || g2 == null) {
            return;
        }
        g2.setColor(new Color(20, 90, 120));
        g2.setStroke(new BasicStroke(2));
        
        for (int x=0; x<visited.length; x++) {
            for (int y=0; y<visited[0].length; y++) {
                if(visited[x][y]) {
                    g2.fillRect((int)((x+1)*sqSize), (int)((y+1)*sqSize), (int)sqSize, (int)sqSize);
                    //g2.draw(createArrow(new Point((int)((x+1+0.5f)*sqSize), (int)((y+1+0.8f)*sqSize)), new Point((int)((x+1+0.5f)*sqSize), (int)((y+1+0.2f)*sqSize))));
                }
            }
        }
        if (directionPath == null) {
            return;
        }
        if (directionPath.size() <= 0) {
            return;
        }
        g2.setColor(Color.CYAN);
        int ix = x;
        int iy = y;
        if (foundPath) {
            switch(dir) {
                    case 0:
                        iy--;
                        break;
                    case 1:
                        ix++;
                        break;
                    case 2:
                        iy++;
                        break;
                    case 3:
                        ix--;
                        break;
            }
        }
        
        
        for (int d : directionPath) {
            switch(d) {
                    case 0:
                        g2.draw(createArrow(new Point((int)((ix+1+0.5f)*sqSize), (int)((iy+1+0.8f)*sqSize)), new Point((int)((ix+1+0.5f)*sqSize), (int)((iy+1+0.2f)*sqSize))));
                        iy--;
                        break;
                    case 1:
                        g2.draw(createArrow(new Point((int)((ix+1+0.2f)*sqSize), (int)((iy+1+0.5f)*sqSize)), new Point((int)((ix+1+0.8f)*sqSize), (int)((iy+1+0.5f)*sqSize))));
                        ix++;
                        break;
                    case 2:
                        g2.draw(createArrow(new Point((int)((ix+1+0.5f)*sqSize), (int)((iy+1+0.2f)*sqSize)), new Point((int)((ix+1+0.5f)*sqSize), (int)((iy+1+0.8f)*sqSize))));
                        iy++;
                        break;
                    case 3:
                        g2.draw(createArrow(new Point((int)((ix+1+0.8f)*sqSize), (int)((iy+1+0.5f)*sqSize)), new Point((int)((ix+1+0.2f)*sqSize), (int)((iy+1+0.5f)*sqSize))));
                        ix--;
                        break;
            }
        }
    }

}
