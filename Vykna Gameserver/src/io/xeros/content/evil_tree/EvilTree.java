package io.xeros.content.evil_tree;



import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.shooting_star.ShootingStar;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 
 * @author C.T for koranes
 *
 */

public class EvilTree {


public static boolean spawned = false;

private static EvilTreeLocation[] locations = {new EvilTreeLocation(3091, 3488, "Edgeville bank", "next to"),};

private static EvilTreeLocation currentLocation;

private static EvilTreeLocation currentHint;
		
private static String name;

private static CycleEvent cycleEvent;

private static CycleEventContainer cycleEventContainer;

public static int lastX = -1;
public static int lastY = -1;



public static void spawnEvilTreeGlobal() {


	cycleEvent = new CycleEvent() {
		@Override
		public void execute(CycleEventContainer container) {
			if(spawned == true) {
				return;
			}
			container.setTick(Misc.toCycles(128, TimeUnit.MINUTES));
			if(getCurrentLocation() != null) {
				destroyLocation();
				Configuration.TREE_EVENT_TIMER = EvilTree.generateTime();
				return;
			}
			container.setTick(generateTime());
		    spawnEvilTree();
			Configuration.TREE_EVENT_TIMER = EvilTree.generateTime();
		}
	};
	setCycleEventContainer(CycleEventHandler.getSingleton().addEvent(EvilTree.class, cycleEvent, generateTime()));
}


public static int generateTime(){
		return Misc.toCycles(128, TimeUnit.MINUTES);
	}

public static void destroyLocation() {
	if(getCurrentLocation() == null)
		return;
	List<EvilTreeLocation> locationsList = Arrays.asList(locations);
	EvilTreeLocation randomLocation = Misc.randomTypeOfList(locationsList);
	currentLocation = randomLocation;
	lastX = randomLocation.getX();
	lastY = randomLocation.getY();
	EvilTree.setCurrentLocation(null);
	Server.getGlobalObjects().add(new GlobalObject(-1, randomLocation.getX(), randomLocation.getY(), 0, 2, 10, -1, -1));
	spawned = false;

}

public static void spawnEvilTree() {

	List<EvilTreeLocation> locationsList = Arrays.asList(locations);
	EvilTreeLocation randomLocation = Misc.randomTypeOfList(locationsList);
	currentLocation = randomLocation;
	lastX = randomLocation.getX();
	lastY = randomLocation.getY();
	Server.getGlobalObjects().add(new GlobalObject(34918, randomLocation.getX(), randomLocation.getY(), 0, 2, 10, -1, -1));
	spawned = true;
	Server.amountCut = 0;
	new Broadcast("@blu@[ Crystal Tree ]: <col=E9E919><shad=0>The crystal tree has sprouted " + randomLocation.getHint() +" " + randomLocation.getLocationName() + "!").copyMessageToChatbox().submit();
	Discord.writeEventMessage("The crystal tree has sprouted " + randomLocation.getHint() +" " + randomLocation.getLocationName() + "!");
    }



	public static void despawnTree() {
		EvilTree.spawned = false;
		Server.getGlobalObjects().add(new GlobalObject(-1, currentLocation.getX(), currentLocation.getY(), 0, 2, 10, -1, -1));
		EvilTree.setCurrentLocation(null);
		if(EvilTree.spawned = false) {
			PlayerHandler.executeGlobalMessage(" <shad=1><col=FF9933> [ Crystal Tree ]: </col>The crystal tree has been chopped down.");
		}
	}

public static CycleEventContainer getCycleEventContainer() {
		return cycleEventContainer;
	}

public static void setCycleEventContainer(CycleEventContainer cycleEventContainer) {
	EvilTree.cycleEventContainer = cycleEventContainer;
}


public static EvilTreeLocation getCurrentLocation() {
	return currentLocation;
}

public static void setCurrentLocation(EvilTreeLocation currentLocation) {
	EvilTree.currentLocation = currentLocation;
}

public static String getName() {
	return name;
}

}

