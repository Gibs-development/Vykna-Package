package io.xeros.content.dungeons;

import io.xeros.content.combat.Hitmark;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

public class KaruulmDungeon {


    public static boolean clickObject(Player c, int objectType, int obX, int obY) {
        switch (objectType) {

            case 34655://pipe
                if (c.playerLevel[c.playerAgility] < 88) {
                    c.sendMessage("You need an agility level of 88, to use this shortcut.");
                    break;
                }
                if (c.getX() == 1316 && c.getY() == 10213 || c.getX() == 1315 && c.getY() == 10214) {
                    AgilityHandler.delayEmote(c, "CRAWL", 1346, 10232, 0, 4);
                } else if (c.getX() == 1346 && c.getY() == 10232 || c.getX() == 1345 && c.getY() == 10231) {
                    AgilityHandler.delayEmote(c, "CRAWL", 1316, 10213, 0, 4);
                }
                break;

            case 34548://Karulm rocks
                if (c.getX() == 1352 && c.getY() == 10250 || c.getX() == 1351 && c.getY() == 10250) {
                    AgilityHandler.delayEmote(c, "JUMP", 1351, 10252, 0, 2);
                } else if (c.getX() == 1351 && c.getY() == 10252 || c.getX() == 1352 && c.getY() == 10252) {
                    AgilityHandler.delayEmote(c, "JUMP", 1352, 10250, 0, 2);
                }
                break;

            case 34544: //Karuulm Rocks/Stone Bars (Intro)
                if (!c.getItems().isWearingItem(23037) || !c.getItems().isWearingItem(22951) || !c.getItems().isWearingItem(21643)) {//Boots of brimstone
                    if (c.getX() == 1312 && c.getY() == 10216) {
                        AgilityHandler.delayEmote(c, "JUMP", 1312, 10214, 0, 2);
                        break;
                    }
                    if (c.getX() == 1311 && c.getY() == 10216) {
                        AgilityHandler.delayEmote(c, "JUMP", 1311, 10214, 0, 2);
                        break;
                    }
                    if (c.getX() == 1301 && c.getY() == 10205) {
                        AgilityHandler.delayEmote(c, "JUMP", 1303, 10205, 0, 2);
                        break;
                    }
                    if (c.getX() == 1301 && c.getY() == 10206) {
                        AgilityHandler.delayEmote(c, "JUMP", 1303, 10206, 0, 2);
                        break;
                    }
                    if (c.getX() == 1322 && c.getY() == 10205) {
                        AgilityHandler.delayEmote(c, "JUMP", 1320, 10205, 0, 2);
                        break;
                    }
                    if (c.getX() == 1322 && c.getY() == 10206) {
                        AgilityHandler.delayEmote(c, "JUMP", 1320, 10206, 0, 2);
                        break;
                    }
                    c.getDH().sendDialogues(44001, -1);
                }
                break;
            case 34530://Karulm first and second floor
                if (c.getX() == 1329 && c.getY() == 10206) {
                    c.getPA().movePlayer(1334, 10205, 1);
                } else if (c.getX() == 1313 && c.getY() == 10189) {
                    c.getPA().movePlayer(1318, 10189, 2);
                }
                break;
            case 34531:// Top floor exit to ground
                c.getPA().movePlayer(1329, 10206, 0);
                break;

        }
        return false;
    }


    public static void createDamage(Player c) {
        if (Boundary.isIn(c, Boundary.KARUULM_ROOMS)) {
            CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.OVERLOAD_HITMARK_ID);
            CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.OVERLOAD_HITMARK_ID, c, new CycleEvent() {
                int time = 200;

                @Override
                public void execute(CycleEventContainer b) {

                    if (c.getItems().isWearingItem(23037) || c.getItems().isWearingItem(22951) || c.getItems().isWearingItem(21643)) {//Boots
                        b.stop();
                        return;
                    }

                    if (Boundary.isIn(c, Boundary.KARUULM1)) {//Square area ground floor
                        b.stop();
                        return;
                    }

                    if (!Boundary.isIn(c, Boundary.KARUULM_ROOMS)) {//Full rooms if not within area then stop
                        b.stop();
                        return;
                    }

                    if (c == null) {
                        b.stop();
                        return;
                    }
                    if (time <= 0) {
                        b.stop();
                        return;
                    }
                    if (time > 0) {
                        if (c.getHealth().getCurrentHealth() <= 0) {
                            b.stop();
                            return;
                        }
                        time--;
                        c.startAnimation(3170);
                        c.appendDamage(4, Hitmark.HIT);
                        c.sendMessage("@red@You are dealt damage as you have no protective boots.");
                    }
                }

                @Override
                public void stop() {

                }
            }, 1);
        }
    }
}
