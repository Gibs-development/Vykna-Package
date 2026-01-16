package io.xeros.content.pkertab;


import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;

/**
 *
 * Top list Execution
 *
 *
 * @author C.T for runerogue
 */

public class ToplistExecution {

    public static void execute() {
        Object object = new Object();
        CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                System.out.println("Executed top list update...");
                Toplist.updateToplist();
            }

            @Override
            public void onStopped() {
            }
        }, 60);
    }
}
