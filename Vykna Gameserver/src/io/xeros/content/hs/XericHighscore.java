package io.xeros.content.hs;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;


/**
 * Created by C.T
 *
 */
public class XericHighscore implements Highscore {

    private String type = "";
    private ArrayList<Player> playerList = new ArrayList<>(10);

    @Override
    public void process() {
        playerList = (ArrayList<Player>) Arrays.asList(PlayerHandler.players).stream().filter(p -> p != null).collect(Collectors.toList());

        Collections.sort(playerList, new Comparator<Player>() {
            @Override
            public int compare(Player player1, Player player2) {
                Player client1 = (Player) player1;
                Player client2 = (Player) player2;

                if (client1.raidCount == client2.raidCount) {
                    return 0;
                } else if (client2.raidCount > client1.raidCount) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void generateList(Player client) {
        resetList(client);
        if (playerList.size() > 10) {
            for (int i = 0; i < 10; i++) {
        	Player rankedClient = (Player) playerList.get(i);
        	client.getPA().sendFrame126("RaidsDone", 46509);
        	client.getPA().sendFrame126("ChestOpen", 46510);
        	client.getPA().sendFrame126("RareKeys", 46511);
        	client.getPA().sendFrame126("CommonKeys", 46512);
        	client.getPA().sendFrame126((i + 1) + "): @gre@" + Misc.optimizeText(rankedClient.getLoginName()), 46513 + i);
        	client.getPA().sendFrame126(String.valueOf(rankedClient.raidCount), 46523 + i);
        	client.getPA().sendFrame126(String.valueOf(rankedClient.chestsOpen), 46533 + i);
        	client.getPA().sendFrame126(String.valueOf(rankedClient.rarekeysCollected), 46543 + i);
        	client.getPA().sendFrame126(String.valueOf(rankedClient.keysCollected), 46553 + i);
            }
        } else {
        	 for (int i = 0; i < playerList.size(); i++) {
                 Player rankedClient = (Player) playerList.get(i);
                 client.getPA().sendFrame126("RaidDone", 46509);
                 client.getPA().sendFrame126("ChestOpen", 46510);
                 client.getPA().sendFrame126("RareKeys", 46511);
                 client.getPA().sendFrame126("CommonKeys", 46512);
                 client.getPA().sendFrame126((i + 1) + "): @gre@" + Misc.optimizeText(rankedClient.getLoginName()), 46513 + i);
                 client.getPA().sendFrame126(String.valueOf(rankedClient.raidCount), 46523 + i);
                 client.getPA().sendFrame126(String.valueOf(rankedClient.chestsOpen), 46533 + i);
                 client.getPA().sendFrame126(String.valueOf(rankedClient.rarekeysCollected), 46543 + i);
                 client.getPA().sendFrame126(String.valueOf(rankedClient.keysCollected), 46553 + i);
        	 }
        }
        client.getPA().showInterface(46500);
        client.flushOutStream();
        playerList.clear();
    }

    @Override
    public void resetList(Player client) {
        client.getPA().sendFrame126("Live Xeric Hiscores" + getType(), 46502);
        for (int i = 46513; i < 46563; i++) {
            client.getPA().sendFrame126("", i);
            client.flushOutStream();
        }
    }
}
