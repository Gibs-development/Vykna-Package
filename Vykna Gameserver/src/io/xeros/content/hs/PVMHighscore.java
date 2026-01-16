package io.xeros.content.hs;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by C.T for koranes
 *
 */
public class PVMHighscore implements Highscore {

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

                if (client1.getNpcDeathTracker().getTotal() == client2.getNpcDeathTracker().getTotal()) {
                    return 0;
                } else if (client2.getNpcDeathTracker().getTotal() > client1.getNpcDeathTracker().getTotal()) {
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
        	client.getPA().sendFrame126("PvM Kills:", 46509);
        	client.getPA().sendFrame126("CoX Done:", 46510);
        	client.getPA().sendFrame126("Boss points:", 46511);
        	client.getPA().sendFrame126("Cmb level:", 46512);
        	client.getPA().sendFrame126((i + 1) + "): @gre@" + Misc.optimizeText(rankedClient.getLoginName()), 46513 + i);
        	client.getPA().sendFrame126(String.valueOf(rankedClient.getNpcDeathTracker().getTotal()), 46523 + i);
        	client.getPA().sendFrame126(String.valueOf(rankedClient.raidCount), 46533 + i);
        	client.getPA().sendFrame126(String.valueOf(rankedClient.bossPoints), 46543 + i);
        	client.getPA().sendFrame126(String.valueOf(rankedClient.combatLevel), 46553 + i);
            }
        } else {
        	 for (int i = 0; i < playerList.size(); i++) {
                 Player rankedClient = (Player) playerList.get(i);
                 client.getPA().sendFrame126("PvM Kills:", 46509);
                 client.getPA().sendFrame126("CoX Done:", 46510);
                 client.getPA().sendFrame126("Boss points:", 46511);
                 client.getPA().sendFrame126("Cmb level:", 46512);
                 client.getPA().sendFrame126((i + 1) + "): @gre@" + Misc.optimizeText(rankedClient.getLoginName()), 46513 + i);
                 client.getPA().sendFrame126(String.valueOf(rankedClient.getNpcDeathTracker().getTotal()), 46523 + i);
                 client.getPA().sendFrame126(String.valueOf(rankedClient.raidCount), 46533 + i);
                 client.getPA().sendFrame126(String.valueOf(rankedClient.bossPoints), 46543 + i);
                 client.getPA().sendFrame126(String.valueOf(rankedClient.combatLevel), 46553 + i);
        	 }
        }
        client.getPA().showInterface(46500);
        client.flushOutStream();
        playerList.clear();
    }

    @Override
    public void resetList(Player client) {
        client.getPA().sendFrame126("Live Pvm Highscores" + getType(), 46502);
        for (int i = 46513; i < 46563; i++) {
            client.getPA().sendFrame126("", i);
            client.flushOutStream();
        }
    }
}
