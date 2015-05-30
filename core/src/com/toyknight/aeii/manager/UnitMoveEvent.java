package com.toyknight.aeii.manager;

import com.toyknight.aeii.AnimationDispatcher;
import com.toyknight.aeii.animator.UnitMoveAnimator;
import com.toyknight.aeii.entity.GameCore;
import com.toyknight.aeii.entity.Point;
import com.toyknight.aeii.entity.Unit;
import com.toyknight.aeii.entity.player.LocalPlayer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by toyknight on 4/21/2015.
 */
public class UnitMoveEvent implements GameEvent, Serializable {

    private static final long serialVersionUID = 04232015L;

    private final int unit_x;
    private final int unit_y;
    private final int dest_x;
    private final int dest_y;
    private final int mp_left;
    private final ArrayList<Point> move_path;

    public UnitMoveEvent(int unit_x, int unit_y, int dest_x, int dest_y, int mp_left, ArrayList<Point> move_path) {
        this.unit_x = unit_x;
        this.unit_y = unit_y;
        this.dest_x = dest_x;
        this.dest_y = dest_y;
        this.mp_left = mp_left;
        this.move_path = move_path;
    }

    @Override
    public Point getFocus() {
        if (move_path == null) {
            return new Point(unit_x, unit_y);
        } else {
            Point dest = move_path.get(move_path.size() - 1);
            return new Point(dest.x, dest.y);
        }
    }

    @Override
    public boolean canExecute(GameCore game) {
        if (move_path == null) {
            return true;
        } else {
            Unit target = game.getMap().getUnit(unit_x, unit_y);
            return target != null && game.canUnitMove(target, dest_x, dest_y);
        }
    }

    @Override
    public void execute(GameManager manager) {
        if (move_path == null) {
            //unable to move, cancel move phase
            if (manager.getState() == GameManager.STATE_MOVE) {
                manager.cancelMovePhase();
            }
        } else {
            Unit unit = manager.getGame().getMap().getUnit(unit_x, unit_y);
            manager.getGame().moveUnit(unit_x, unit_y, dest_x, dest_y);
            unit.setCurrentMovementPoint(mp_left);
            manager.submitAnimation(new UnitMoveAnimator(unit, move_path));
            switch (manager.getState()) {
                case GameManager.STATE_MOVE:
                    manager.setState(GameManager.STATE_ACTION);
                    break;
                case GameManager.STATE_REMOVE:
                    manager.setState(GameManager.STATE_SELECT);
                    unit.setStandby(true);
                    break;
            }
        }
    }

}

