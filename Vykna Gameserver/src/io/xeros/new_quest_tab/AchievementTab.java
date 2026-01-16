package io.xeros.new_quest_tab;

import io.xeros.content.questing.LearningTheRopes.LearningTheRopesQuest;
import io.xeros.content.questing.MonkeyMadness.MonkeyMadnessQuest;
import io.xeros.content.questing.Quest;
import io.xeros.content.questing.hftd.HftdQuest;
import io.xeros.model.entity.player.Player;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * The achievement tab inside the quest tab.
 *
 * @author C.T for runerogue
 *
 */

public class AchievementTab {

    public static List<Integer> getLines() {
        return IntStream.range(46_924, 46_924 + 400).boxed().collect(Collectors.toList());//51901
    }

    public static void loadInformation(Player player) {
        List<Integer> lines = getLines();
        int index = 0;

        player.getPA().sendFrame126("<col=CC8400>Provinces completed: </col>@gre@"+player.amountOfDiariesComplete()+"/12", 47_523);

        player.getPA().sendFrame126(player.getDiaryManager().getVarrockDiary().hasDoneAll() ? "@gre@Varrock" : "<col=CC8400>Varrock", lines.get(index++));

        player.getPA().sendFrame126(player.getDiaryManager().getArdougneDiary().hasDoneAll() ? "@gre@Ardougne" : "<col=CC8400>Ardougne", lines.get(index++));;

        player.getPA().sendFrame126(player.getDiaryManager().getDesertDiary().hasDoneAll() ? "@gre@Desert" : "<col=CC8400>Desert", lines.get(index++));

        player.getPA().sendFrame126(player.getDiaryManager().getFaladorDiary().hasDoneAll() ? "@gre@Falador" : "<col=CC8400>Falador", lines.get(index++));

        player.getPA().sendFrame126(player.getDiaryManager().getFremennikDiary().hasDoneAll() ? "@gre@Fremennik" : "<col=CC8400>Fremennik", lines.get(index++));

        player.getPA().sendFrame126(player.getDiaryManager().getKandarinDiary().hasDoneAll() ? "@gre@Kandarin" : "<col=CC8400>Kandarin", lines.get(index++));

        player.getPA().sendFrame126(player.getDiaryManager().getKaramjaDiary().hasDoneAll() ? "@gre@Karamja" : "<col=CC8400>Karamja", lines.get(index++));

        player.getPA().sendFrame126(player.getDiaryManager().getLumbridgeDraynorDiary().hasDoneAll() ? "@gre@Lumbridge & Draynor" : "<col=CC8400>Lumbridge & Draynor", lines.get(index++));

        player.getPA().sendFrame126(player.getDiaryManager().getMorytaniaDiary().hasDoneAll() ? "@gre@Morytania" : "<col=CC8400>Morytania", lines.get(index++));

        player.getPA().sendFrame126(player.getDiaryManager().getWesternDiary().hasDoneAll() ? "@gre@Western" : "<col=CC8400>Western", lines.get(index++));

        player.getPA().sendFrame126(player.getDiaryManager().getWildernessDiary().hasDoneAll() ? "@gre@Wilderness" : "<col=CC8400>Wilderness", lines.get(index++));

        player.getPA().sendFrame126("", lines.get(index++));

        player.getPA().sendFrame126("@or1@<img=24> Quests:", lines.get(index++));

        player.getPA().sendFrame126(""+getQuestLineColor(new LearningTheRopesQuest(player))+"Learning the ropes", lines.get(index++));

        player.getPA().sendFrame126(""+getQuestLineColor(new HftdQuest(player))+"Horror from the deep", lines.get(index++));

        player.getPA().sendFrame126(""+getQuestLineColor(new MonkeyMadnessQuest(player))+"Monkey madness", lines.get(index++));


       // dataList.add(new PlayerAssistant.TextData(player.getDiaryManager().getKourendKebosDiary().hasDoneAll() ? "@gre@Kourend" : "<col=CC8400>Kourend & Kebos", index)); //to be released

        while (index < lines.size()) {
            player.getPA().sendString("", lines.get(index++));
        }
    }

    private static String getQuestLineColor(Quest quest) {
        return quest.getStage() == 0 ? "@red@" : quest.getStage() == quest.getCompletionStage() ? "@gre@" : "@yel@";
    }

}
