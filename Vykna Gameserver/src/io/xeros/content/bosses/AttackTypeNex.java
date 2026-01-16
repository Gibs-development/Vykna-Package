package io.xeros.content.bosses;

/**
 * @author C.T for vorkath script
 */

//Projectile and gfx ids for nex and minions

//2002 - red 2 arrows - 2003 end gfx for 2002
//2004 - ice attack - 2005 ice barrage

//2006 - ice spikes
//2007 - zaras attack - 2008 zaros attack gfx

//2010 - BLUE ARROWS - 2011 BLUE ARROWS GFX / MINIONS SHOOT THIS

//2012 - white bomb - 2013 WHITE BOMB GFX

// 42944 Prison attack object

public enum AttackTypeNex {


    MAGIC(9181, 2002, 2003),
    RANGED(9182, 2004, 1473),
    ONE_HIT(9185, 1491, 157),//could be 9183
    PRAYER_SNIPE(9185, 1471, 1473),
    POISON(9185, 1470, 1472),
    DRAGON_FIRE(9180, 2009, 157),

    SPECIAL_1(9185, 2006, 1473), //acid  // ICE SPIKES
    SPECIAL_2(9186, 2007, 2008); //jihad  // ZAROS ATTACK

    private int animationId;

    private int projectileId;

    private int endGfx;

    AttackTypeNex(int animationId, int projectileId, int endGfx) {
        this.animationId = animationId;
        this.projectileId = projectileId;
        this.endGfx = endGfx;
    }


    public int getAnimationId() {
        return animationId;
    }

    public int getProjectileId() {
        return projectileId;
    }

    public int getEndGfx() {
        return endGfx;
    }
}