package io.xeros.content.questsystem.sample;

import java.util.List;
import java.util.Map;

import io.xeros.content.questsystem.model.QuestDefinition;
import io.xeros.content.questsystem.model.QuestId;
import io.xeros.content.questsystem.model.QuestReward;
import io.xeros.content.questsystem.model.QuestStep;
import io.xeros.content.questsystem.step.BossInstanceStep;
import io.xeros.content.questsystem.step.AreaRequirement;
import io.xeros.content.questsystem.step.BringItemsStep;
import io.xeros.content.questsystem.step.GoToAreaStep;
import io.xeros.content.questsystem.step.TalkToNpcStep;
import io.xeros.content.questsystem.step.UseItemOnObjectStep;
import io.xeros.model.Npcs;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Position;

public class CooksAssistantQuestDefinition implements QuestDefinition {
    private static final QuestId ID = new QuestId("cooks_assistant");

    private final List<QuestStep> steps = List.of(
            new TalkToNpcStep(
                    "talk-cook",
                    4626,
                    List.of("Talk to the cook in Lumbridge Castle.")
            ),
            new BringItemsStep(
                    "bring-items",
                    Map.of(
                            1944, 1,
                            1933, 1,
                            1927, 1
                    ),
                    List.of("Bring the cook an egg, a pot of flour, and a bucket of milk.")
            ),
            new GoToAreaStep(
                    "go-kitchen",
                    new AreaRequirement(3206, 3211, 3210, 3214, 0),
                    List.of("Head back into the castle kitchen.")
            ),
            new UseItemOnObjectStep(
                    "use-range",
                    1929,
                    12269,
                    List.of("Use the cake mixture on the range.")
            ),
            new BossInstanceStep(
                    "boss-instance",
                    ID.value(),
                    Npcs.SKELETON,
                    6,
                    new Boundary(3200, 3208, 3209, 3216),
                    new Position(3204, 3212, 0),
                    new Position(3207, 3213, 0),
                    new Position(3206, 3212, 0),
                    120_000L,
                    List.of("Enter the instance and defeat the summoned skeleton.")
            )
    );

    @Override
    public QuestId id() {
        return ID;
    }

    @Override
    public String name() {
        return "Cook's Assistant";
    }

    @Override
    public String description() {
        return "Help the cook prepare a special dish by gathering ingredients and using the kitchen range.";
    }

    @Override
    public List<QuestStep> steps() {
        return steps;
    }

    @Override
    public List<QuestReward> rewards() {
        return List.of();
    }
}
