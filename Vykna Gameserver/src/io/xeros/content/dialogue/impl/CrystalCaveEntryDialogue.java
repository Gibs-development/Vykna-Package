package io.xeros.content.dialogue.impl;

import io.xeros.content.collection_log.CollectionLog;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.entity.player.Player;

public class CrystalCaveEntryDialogue extends DialogueBuilder {


    public CrystalCaveEntryDialogue(Player player) {
        super(player);
        setNpcId(8761);
                    npc("Which cave would you like to go to?")
                    .option(new DialogueOption("Regular", p -> enterCave(player, 0, false)),
                            new DialogueOption("No-Cannon", p -> enterCave(player, 4, false)),
                            new DialogueOption("Premium (Cost: 1000 Crystal shards)", p -> enterCave(player, 0, true)));

    }



    private void enterCave(Player c, int floor, boolean fee) {
        c.getPA().closeAllWindows();
        if ((!c.getSlayer().getTask().isPresent() || !c.getSlayer().getTask().get().getPrimaryName().contains("crystalline")) && !c.getItems().playerHasItem(23951)) {
            c.sendMessage("@red@You must have a crystalline task to go in this cave.");
            return;
        }
        if (fee) {
            if (!c.getItems().playerHasItem(23877, 1000)) {
                c.sendMessage("@red@You need 1000 crystal shards.");
                return;
            }
            c.getItems().deleteItem2(23877, 1000);
            floor = 8;
        }
        c.sendMessage("@red@Use command @bla@::crystal @red@for the crystal caves guide.");
        c.objectDistance = 5;
        c.getPA().movePlayer(3225, 12445, floor);

        c.getPA().showInterface(43900);//to show teleport to each mob
    }


    public static boolean handleTeleportButtons(Player player, int buttonId) {
        switch(buttonId) {
            case 171127://Crystalline bat
                player.getPA().startTeleport(3229, 12459, 0, "MODERN", false);//
                player.getPA().closeAllWindows();
                return true;

            case 171128://Crystalline bear
                player.getPA().startTeleport(3223, 12421, 0, "MODERN", false);//
                player.getPA().closeAllWindows();
                return true;

            case 171129://Crystalline dark beast
                player.getPA().startTeleport(3222, 12393, 0, "MODERN", false);//
                player.getPA().closeAllWindows();
                return true;

            case 171130://Crystalline dragon
                player.getPA().startTeleport(3183, 12468, 0, "MODERN", false);//
                player.getPA().closeAllWindows();
                return true;

            case 171131://Crystalline rat
                player.getPA().startTeleport(3241, 12438, 0, "MODERN", false);//
                player.getPA().closeAllWindows();
                return true;

            case 171132://Crystalline scorpion
                player.getPA().startTeleport(3256, 12376, 0, "MODERN", false);//
                player.getPA().closeAllWindows();
                return true;

            case 171133://Crystalline spider
                player.getPA().startTeleport(3251, 12466, 0, "MODERN", false);//
                player.getPA().closeAllWindows();
                return true;

            case 171134://Crystalline unicorn
                player.getPA().startTeleport(3250, 12419, 0, "MODERN", false);//
                player.getPA().closeAllWindows();
                return true;

            case 171135://Crystalline wolf
                player.getPA().startTeleport(3212, 12368, 0, "MODERN", false);//
                player.getPA().closeAllWindows();
                return true;

            case 171136://Crystalline hunllef
                player.getPA().startTeleport(3165, 12428, 0, "MODERN", false);//
                player.getPA().closeAllWindows();
                return true;
        }
        return false;
    }

}
