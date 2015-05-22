package com.toyknight.aeii.event;

import com.toyknight.aeii.AnimationDispatcher;
import com.toyknight.aeii.animator.SummonAnimator;
import com.toyknight.aeii.animator.UnitLevelUpAnimator;
import com.toyknight.aeii.entity.GameCore;
import com.toyknight.aeii.entity.Unit;
import com.toyknight.aeii.utils.UnitFactory;

import java.io.Serializable;

/**
 * Created by toyknight on 5/21/2015.
 */
public class SummonEvent implements GameEvent, Serializable {

    private static final long serialVersionUID = 05212015L;

    private final int summoner_x;
    private final int summoner_y;
    private final int target_x;
    private final int target_y;
    private final int experience;

    public SummonEvent(int summoner_x, int summoner_y, int target_x, int target_y, int experience) {
        this.summoner_x = summoner_x;
        this.summoner_y = summoner_y;
        this.target_x = target_x;
        this.target_y = target_y;
        this.experience = experience;
    }

    @Override
    public boolean canExecute(GameCore game) {
        return game.getMap().isTomb(target_x, target_y);
    }

    @Override
    public void execute(GameCore game, AnimationDispatcher animation_dispatcher) {
        Unit summoner = game.getMap().getUnit(summoner_x, summoner_y);
        game.getMap().removeTomb(target_x, target_y);
        game.createUnit(UnitFactory.getSkeletonIndex(), summoner.getTeam(), "default", target_x, target_y);
        game.standbyUnit(target_x, target_y);
        animation_dispatcher.submitAnimation(new SummonAnimator(summoner, target_x, target_y));
        boolean level_up = summoner.gainExperience(experience);
        if (level_up) {
            animation_dispatcher.submitAnimation(new UnitLevelUpAnimator(summoner));
        }
    }
}
