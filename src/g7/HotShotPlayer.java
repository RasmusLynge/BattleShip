package g7;

import battleship.interfaces.BattleshipsPlayer;
import battleship.interfaces.Fleet;
import battleship.interfaces.Position;
import battleship.interfaces.Board;
import battleship.interfaces.Ship;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import java.util.ArrayList;
import java.util.Random;
import javafx.beans.binding.Bindings;

public class HotShotPlayer implements BattleshipsPlayer {

    private final static Random rnd = new Random();
    private int sizeX;
    private int sizeY;
    private Board myBoard;
    ArrayList<Position> shotsFired = new ArrayList();
    ArrayList<Position> shotsFiredNeighbours = new ArrayList();
    private boolean hit;
    private boolean searchAndDestory;
    private int before;
    private Position lastHit;
    private int after;
    private int hitCounter = 0;
    
    private boolean seek = false;

    public HotShotPlayer() {
        
    }

    /**
     * The method called when its time for the AI to place ships on the board
     * (at the beginning of each round).
     *
     * The Ship object to be placed MUST be taken from the Fleet given (do not
     * create your own Ship objects!).
     *
     * A ship is placed by calling the board.placeShip(..., Ship ship, ...) for
     * each ship in the fleet (see board interface for details on placeShip()).
     *
     * A player is not required to place all the ships. Ships placed outside the
     * board or on top of each other are wrecked.
     *
     * @param fleet Fleet all the ships that a player should place.
     * @param board Board the board were the ships must be placed.
     */
    @Override
    @SuppressWarnings("empty-statement")
    public void placeShips(Fleet fleet, Board board) {
        after = fleet.getNumberOfShips();
        myBoard = board;
        sizeX = board.sizeX();
        sizeY = board.sizeY();
        boolean[][] myShips = new boolean[sizeX][sizeY];

        for (int i = 0; i < fleet.getNumberOfShips(); ++i) {
            Ship s = fleet.getShip(i);
            Position pos;
            boolean vertical;

            do {
                vertical = rnd.nextBoolean();
                if (vertical) {
                    int x = rnd.nextInt(sizeX);
                    int y = rnd.nextInt(sizeY - (s.size() - 1));
                    pos = new Position(x, y);
                } else {
                    int x = rnd.nextInt(sizeX - (s.size() - 1));
                    int y = rnd.nextInt(sizeY);
                    pos = new Position(x, y);
                }
            } while (collision(pos, s, vertical, myShips));

            for (int j = 0; j < s.size(); j++) {
                {
                    if (vertical) {
                        myShips[pos.x][pos.y + j] = true;
                    } else {
                        myShips[pos.x + j][pos.y] = true;
                    }
                }
            }
            board.placeShip(pos, s, vertical);
        }
    }

    private boolean collision(Position pos, Ship s, boolean vertical, boolean[][] t) {
        for (int j = 0; j < s.size(); j++) {
            {
                if (vertical) {
                    if (t[pos.x][pos.y + j]) {
                        return true;
                    }
                } else {
                    if (t[pos.x + j][pos.y]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Called every time the enemy has fired a shot.
     *
     * The purpose of this method is to allow the AI to react to the enemy's
     * incoming fire and place his/her ships differently next round.
     *
     * @param pos Position of the enemy's shot
     */
    @Override
    public void incoming(Position pos) {
        //Do nothing
    }

    /**
     * Called by the Game application to get the Position of your shot.
     * hitFeedBack(...) is called right after this method.
     *
     * @param enemyShips Fleet the enemy's ships. Compare this to the Fleet
     * supplied in the hitFeedBack(...) method to see if you have sunk any
     * ships.
     *
     * @return Position of you next shot.
     */
    @Override
    public Position getFireCoordinates(Fleet enemyShips) {

        Position p;
        
        before = after;
        after = enemyShips.getNumberOfShips();
        
        System.out.println(before);
        System.out.println(after);
        
        if(before != after){
            seek = false;
        }
        
      
        System.out.println("seek:" + seek);
        if (!shotsFiredNeighbours.isEmpty() && seek ) {

//            System.out.println("hitcounter" + hitCounter + " StartTotal " + startTotalShipLength + " enemyShips " + enmeyShipsLengthTotal);
            System.out.println("while loop vi er i search and Destory mode");
            System.out.println("TARGET");

            p = target();
        } else {
//            System.out.println("test length = " + startTotalShipLength);
            hitCounter = 0;
            shotsFiredNeighbours.clear();
            p = hunt();
        }
        shotsFired.add(p);
        return p;
    }

    public Position hunt() {
        Position p;
        do {
            p = getARandomShot();
        } while (shotsFired.contains(p));

        System.out.println("hunt = " + shotsFired.toString());
        return p;
    }

    public Position target() {
        Position p = shotsFiredNeighbours.remove(0);
        System.out.println("target: " + p);
        return p;
    }

    private Position getARandomShot() {
        int x = rnd.nextInt(sizeX);
        int y = rnd.nextInt(sizeY);
        return new Position(x, y);
    }

    /**
     * Called right after getFireCoordinates(...) to let your AI know if you hit
     * something or not.
     *
     * Compare the number of ships in the enemyShips with that given in
     * getFireCoordinates in order to see if you sunk a ship.
     *
     * @param hit boolean is true if your last shot hit a ship. False otherwise.
     * @param enemyShips Fleet the enemy's ships.
     */
    @Override
    public void hitFeedBack(boolean hit, Fleet enemyShips) {
        this.hit = hit;

        
        if (hit) {
            seek = true;
            hitCounter++;
            System.out.println("kig efter neighbours");
            lastHit = shotsFired.get(shotsFired.size() - 1);
            Position east = new Position(lastHit.x + 1, lastHit.y);
            Position west = new Position(lastHit.x - 1, lastHit.y);
            Position north = new Position(lastHit.x, lastHit.y + 1);
            Position south = new Position(lastHit.x, lastHit.y - 1);
            
            if (isNextHitShotValid(east)) {
                System.out.println("B1");
                shotsFiredNeighbours.add(0, east);
            }
            if (isNextHitShotValid(west)) {
                System.out.println("B2");
                shotsFiredNeighbours.add(0, west);
            }
            if (isNextHitShotValid(north)) {
                System.out.println("B3");
                shotsFiredNeighbours.add(0, north);
            }
            if (isNextHitShotValid(south)) {
                System.out.println("B4");
                shotsFiredNeighbours.add(0, south);
            }
            System.out.println("shots fired at neighbours" + shotsFiredNeighbours.toString());
        }
    }

    public boolean isNextHitShotValid(Position p) {
        return (p.x >= 0 && p.x < sizeX && p.y >= 0 && p.y < sizeY
                && !shotsFired.contains(p) && !shotsFiredNeighbours.contains(p));
    }

    /**
     * Called in the beginning of each match to inform about the number of
     * rounds being played.
     *
     * @param rounds int the number of rounds i a match
     */
    @Override
    public void startMatch(int rounds, Fleet ships, int sizeX, int sizeY) {

    }

    /**
     * Called at the beginning of each round.
     *
     * @param round int the current round number.
     */
    @Override
    public void startRound(int round) {
        
    }

    /**
     * Called at the end of each round to let you know if you won or lost.
     * Compare your points with the enemy's to see who won.
     *
     * @param round int current round number.
     * @param points your points this round: 100 - number of shot used to sink
     * all of the enemy's ships.
     *
     * @param enemyPoints int enemy's points this round.
     */
    @Override
    public void endRound(int round, int points, int enemyPoints) {
        shotsFiredNeighbours.clear();
        shotsFired.clear();
    }

    /**
     * Called at the end of a match (that usually last 1000 rounds) to let you
     * know how many losses, victories and draws you scored.
     *
     * @param won int the number of victories in this match.
     * @param lost int the number of losses in this match.
     * @param draw int the number of draws in this match.
     */
    @Override
    public void endMatch(int won, int lost, int draw) {
        
    }
}
