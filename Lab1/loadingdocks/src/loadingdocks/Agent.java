package loadingdocks;

import java.awt.Color;
import java.awt.Point;

import loadingdocks.Block.Shape;

/**
 * Agent behavior
 *
 * @author Rui Henriques
 */

enum AgentState {
    MOVE_TO_RAMP,
    MOVE_TO_SHELF,
    STANDBY
}

public class Agent extends Entity {

    public int direction = 90;
    public Box cargo;
    public AgentState state = AgentState.MOVE_TO_RAMP;

    public Agent(Point point, Color color) {
        super(point, color);
    }


    /**********************
     **** A: decision *****
     **********************/

    public void agentDecision() {
        if (this.state == AgentState.MOVE_TO_RAMP) {
            if (isRamp()) {
            	if(isBox()){
					pickupBox();
					this.state = AgentState.MOVE_TO_SHELF;
				}

            } else {
				if (!isWall() && isFreeCell()) { // If we can move forward, go forward
					moveAhead();
				}
            }
        }else if(this.state == AgentState.MOVE_TO_SHELF){
            if(isWall() || isRamp()){
                rotateRight();
            }else if(isShelf()){ // Valid shelf in front of us, drop box
                dropBox();
                state = AgentState.STANDBY;
            }else if(!isFreeCell()){ // Not a (valid) shelf in front of us, rotate randomly
                this.random.ints(0, 1);
                if (this.random.nextInt() == 0) rotateLeft();
                else rotateRight();
            }else{ // If all else fails, just move forward
                moveAhead();
            }
        }
    }

    /********************/
    /**** B: sensors ****/
    /********************/

    /* Check if the cell ahead is floor (which means not a wall, not a shelf nor a ramp) and there are any robot there */
    protected boolean isFreeCell() {
        Point ahead = aheadPosition();
        return Board.getBlock(ahead).shape.equals(Shape.free);
    }

    /* Check if the cell ahead is a wall */
    protected boolean isWall() {
        Point ahead = aheadPosition();
        return ahead.x < 0 || ahead.y < 0 || ahead.x >= Board.nX || ahead.y >= Board.nY;
    }

    /* Check if the cell ahead is ramp*/
    protected boolean isRamp() {
        Point ahead = aheadPosition();
        return Board.getBlock(ahead).shape.equals(Shape.ramp);
    }

	/* Check if there are boxes in ramp*/
	protected boolean isBox() {
		Point ahead = aheadPosition();
		Entity entity = Board.getEntity(ahead);
		return entity!=null && entity instanceof Box;
	}

    /* Check if the cell ahead is shelf of same colour as box*/
    protected boolean isShelf() {
        if (cargo == null) {
            return false;
        }

        Point ahead = aheadPosition();
        return Board.getBlock(ahead).shape.equals(Shape.shelf) && Board.getBlock(ahead).color == cargo.color;
    }

    /**********************/
    /**** C: actuators ****/
    /**********************/
    /* Move agent forward */
    public void moveAhead() {
        Point ahead = aheadPosition();
        Board.updateEntityPosition(point, ahead);
        point = ahead;

        if(cargo != null){
            cargo.move(point);
        }
    }

    /* Rotate right */
    private void rotateRight() {
        direction = (direction + 90) % 360;
    }

    /* Rotate left */
    private void rotateLeft() {
        direction -= 90;
        if (direction < 0) {
            direction = 270;
        }
    }

    public void pickupBox() {
		Point ahead = aheadPosition();
		cargo = (Box) Board.getEntity(ahead);
		cargo.pickup(point);
    }

    public void dropBox() {
        if(isShelf() || (!isWall() && isFreeCell())){
            Point ahead = aheadPosition();
            cargo.drop(ahead);
            cargo = null;
        }
    }

    /**********************/
    /**** D: auxiliary ****/
    /**********************/

    /* Position ahead */
    private Point aheadPosition() {
        Point newpoint = new Point(point.x, point.y);
        switch (direction) {
            case 0:
                newpoint.y++;
                break;
            case 90:
                newpoint.x++;
                break;
            case 180:
                newpoint.y--;
                break;
            default:
                newpoint.x--;
        }
        return newpoint;
    }
}
