package io.xeros.content.bossfactory.drop;

public class BossDropContext {
    private static final ThreadLocal<DropReceiver> RECEIVER = new ThreadLocal<>();

    public static void runWithReceiver(DropReceiver receiver, Runnable runnable) {
        DropReceiver previous = RECEIVER.get();
        RECEIVER.set(receiver);
        try {
            runnable.run();
        } finally {
            if (previous != null) {
                RECEIVER.set(previous);
            } else {
                RECEIVER.remove();
            }
        }
    }

    public static DropReceiver getReceiver() {
        return RECEIVER.get();
    }
}
