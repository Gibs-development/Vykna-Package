package io.xeros.content.vykna_progression;

import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.WeakHashMap;

public final class VyknaProgressionToast {
    private static final int TOAST_DURATION_CYCLES = 7; // ~4 seconds on 600ms cycles.
    private static final int TOAST_EVENT_ID = 932_001;
    private static final Map<Player, ToastQueue> QUEUES = new WeakHashMap<>();

    private VyknaProgressionToast() {
    }

    public static void showCompleteToast(Player player, ProgressionEntry entry) {
        if (player == null || entry == null) {
            return;
        }
        ToastQueue queue = QUEUES.computeIfAbsent(player, key -> new ToastQueue());
        queue.entries.addLast(entry);
        if (!queue.showing) {
            showNext(player, queue);
        }
    }

    private static void showNext(Player player, ToastQueue queue) {
        ProgressionEntry entry = queue.entries.pollFirst();
        if (entry == null) {
            queue.showing = false;
            return;
        }
        queue.showing = true;
        queue.previousWalkable = player.getPA().getCurrentWalkableInterface();

        player.getPA().sendString("COMPLETED", VyknaProgressionInterfaces.TOAST_TEXT_TITLE);
        player.getPA().sendString(entry.getName(), VyknaProgressionInterfaces.TOAST_TEXT_NAME);

        String extraLine = entry.getPoints() > 0
                ? "+" + entry.getPoints() + " points"
                : entry.getDescription();
        player.getPA().sendString(extraLine, VyknaProgressionInterfaces.TOAST_TEXT_EXTRA);
        player.getPA().walkableInterface(VyknaProgressionInterfaces.TOAST_INTERFACE_ID);

        CycleEventHandler.getSingleton().stopEvents(player, TOAST_EVENT_ID);
        CycleEventHandler.getSingleton().addEvent(TOAST_EVENT_ID, player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                hideToast(player, queue);
                container.stop();
            }
        }, TOAST_DURATION_CYCLES);
    }

    private static void hideToast(Player player, ToastQueue queue) {
        if (player.getPA().getCurrentWalkableInterface() == VyknaProgressionInterfaces.TOAST_INTERFACE_ID) {
            player.getPA().walkableInterface(queue.previousWalkable);
        }
        queue.showing = false;
        if (!queue.entries.isEmpty()) {
            showNext(player, queue);
        }
    }

    private static final class ToastQueue {
        private final Deque<ProgressionEntry> entries = new ArrayDeque<>();
        private boolean showing;
        private int previousWalkable = -1;
    }
}
