package io.xeros.content.combat.core;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.xeros.Configuration;
import io.xeros.Server;

import io.xeros.model.CombatType;
import io.xeros.model.Items;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.*;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import io.xeros.model.multiplayersession.duel.DuelSessionRules;
import org.apache.commons.lang3.StringUtils;

public class AttackPlayerCheck {
    
    private static void sendCheckMessage(Player c, boolean sendMessages, String message) {
        if (sendMessages) {
            c.sendMessage(message);
        }
    }

    public static boolean check(Player c, Entity targetEntity, boolean sendMessages) {
        Player o = targetEntity.asPlayer();
        if (o == null || c.getIndex() == o.getIndex() || c.equals(o)) {
            return false;
        }

        if (o.getBankPin().requiresUnlock()) {
            sendCheckMessage(c, sendMessages, "You cannot do this while a player is in lock-down.");
            return false;
        }

        if (o.isInvisible()) {
            sendCheckMessage(c, sendMessages, "You cannot attack another player whilst they are invisible.");
            return false;
        }
        if (o.isNpc) {
            sendCheckMessage(c, sendMessages, "You cannot attack another player in this form.");
            return false;
        }
        if (c.hasOverloadBoost) {
            if (Boundary.isIn(c, Boundary.DUEL_ARENA) || Boundary.isIn(c, Boundary.WILDERNESS)) {
                c.getPotions().resetOverload();
                c.getPA().sendGameTimer(ClientGameTimer.OVERLOAD, TimeUnit.MINUTES, 0);
            }
        }
        if (Boundary.isIn(c, Boundary.DUEL_ARENA)) {
            if (Boundary.isIn(o, Boundary.DUEL_ARENA)) {
                DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
                if (Objects.isNull(session)) {
                    sendCheckMessage(c, sendMessages, "You cannot attack this player.");
                    return false;
                }
                if (!session.getPlayers().containsAll(Arrays.asList(o, c))) {
                    sendCheckMessage(c, sendMessages, "This player is not your opponent.");
                    return false;
                }
                if (!Boundary.isInSameBoundary(c, session.getOther(c), Boundary.DUEL_ARENA)) {
                    sendCheckMessage(c, sendMessages, "You cannot attack a player if you're not in the same arena.");
                    c.getPA().movePlayer(session.getArenaBoundary().getMinimumX(), session.getArenaBoundary().getMinimumX(), 0);
                    return false;
                }
                if (!session.isAttackingOperationable()) {
                    sendCheckMessage(c, sendMessages, "You cannot attack your opponent yet.");
                    return false;
                }

                if (c.attacking.getCombatType() == CombatType.MAGE && session.getRules().contains(DuelSessionRules.Rule.NO_MAGE)) {
                    sendCheckMessage(c, sendMessages, "You cannot use mage in this duel.");
                    return false;
                } else if (c.attacking.getCombatType() == CombatType.MELEE && session.getRules().contains(DuelSessionRules.Rule.NO_MELEE)) {
                    sendCheckMessage(c, sendMessages, "You cannot use melee in this duel.");
                    return false;
                } else if (c.attacking.getCombatType() == CombatType.RANGE && session.getRules().contains(DuelSessionRules.Rule.NO_RANGE)) {
                    sendCheckMessage(c, sendMessages, "You cannot use ranged in this duel.");
                    return false;
                }
                return true;
            } else {
                sendCheckMessage(c, sendMessages, "You cannot attack a player outside of the duel arena.");
                return false;
            }
        }


        if (c.inPits) {
            if (o.inPits) {
                return true;
            }
            return false;
        }

        if (!o.getPosition().inWild() && !c.getItems().isWearingItem(Items.SNOWBALL, 3)) {
            sendCheckMessage(c, sendMessages, "That player is not in the wilderness.");
            return false;
        }

        if (Boundary.isIn(c, Boundary.LMS_ARENA)) {
            return true;
        }

        if (c.getItems().isWearingItem(22323)
            || c.getItems().isWearingItem(25731)
            || c.getItems().isWearingItem(22288)
            || c.getItems().isWearingItem(11905)
            || c.getItems().isWearingItem(12899)
            || c.getItems().isWearingItem(22292)) {
            sendCheckMessage(c, sendMessages, "@red@You cant attack an player with that weapon.");
            return false;
        }


        if (!c.getPosition().inWild() && !c.getItems().isWearingItem(Items.SNOWBALL, 3)) {
            sendCheckMessage(c, sendMessages, "You are not in the wilderness.");
            return false;
        }

        if (Configuration.COMBAT_LEVEL_DIFFERENCE && !c.getItems().isWearingItem(10501, 3)) {
            if (c.getPosition().inWild()) {
                int combatDif1 = c.getCombatLevelDifference(o);
                if ((combatDif1 > c.wildLevel || combatDif1 > o.wildLevel)) {
                    sendCheckMessage(c, sendMessages, "Your combat level difference is too great to attack that player here.");
                    return false;
                }
            } else {
                int myCB = c.combatLevel;
                int pCB = o.combatLevel;
                if ((myCB > pCB + 12) || (myCB < pCB - 12)) {
                    sendCheckMessage(c, sendMessages, "You can only fight players in your combat range!");
                    return false;
                }
            }
        }

        if (Configuration.SINGLE_AND_MULTI_ZONES) {
            if (!o.getPosition().inMulti()) { // single combat zones
                if ((o.underAttackByPlayer != c.getIndex() && o.underAttackByPlayer != 0 || o.underAttackByNpc > 0)) {
                        sendCheckMessage(c, sendMessages, "That player is already in combat.");
                        return false;
                }
                if (o.getIndex() != c.underAttackByPlayer && c.underAttackByPlayer != 0 || c.underAttackByNpc > 0) {
                    sendCheckMessage(c, sendMessages, "You are already in combat.");
                    return false;
                }
            }
        }

        if (c.tournamentTarget >= 0) {//For the tournaments
            if (o.tournamentTarget != c.getIndex() || c.tournamentTarget != o.getIndex()) {
               if (sendMessages) {
                    sendCheckMessage(c, true, "@red@This is not your challenger!");
                }
                return false;
            }

            if (c.getDuelCount() > 0) {
                if (sendMessages) {
                    sendCheckMessage(c, true, "@red@The tournament hasn't started yet!");
                }
                return false;
            }
        }



        if (c.connectedFrom.equals(o.connectedFrom) && !c.getRights().isOrInherits(Right.MODERATOR)
                && !Server.isDebug()) {
            sendCheckMessage(c, sendMessages, "You cannot attack same ip address.");
            return false;
        }


        if (o.lastDefend != null && o.getPosition().inMulti()) {
            Player def = (Player) o.lastDefend.get();
            if (def != null && c != def && c.getIpAddress().equalsIgnoreCase(def.getIpAddress()) && (System.currentTimeMillis() - o.lastDefendTime) < 10_000) {
                c.sendMessage("You can only attack "+ o.getDisplayName()+" on one account.");
                return false;
            }
        }
        return true;
    }

}
