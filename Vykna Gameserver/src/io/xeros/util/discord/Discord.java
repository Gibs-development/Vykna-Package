package io.xeros.util.discord;


import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.ServerState;
import io.xeros.util.Misc;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Discord {

    private static final Logger logger = LoggerFactory.getLogger(Discord.class);
    private static final Map<String, TextChannel> channels = new ConcurrentHashMap<>();
    private static JDA jda = null;

    /**
     * Write to a channel that contains misc. types of information about player activity.
     */
    public static void writeServerSyncMessage(String message, Object...args) {
        sendChannelMessage("server-sync", message, args);//server
    }

    public static void writeDeathMessage(String message, Object...args) {
        writeServerSyncMessage(message, args);
        sendChannelMessage("runerogue-bot", message, args);//server
    }

    public static void writeDropsSyncMessage(String message, Object...args) {
        writeServerSyncMessage(message, args);
        sendChannelMessage("runerogue-bot", message, args);//server
        sendChannelMessage("drop-sync", message, args);//server
    }

    public static void writeWellMessage(String message, Object...args) {
        writeServerSyncMessage(message, args);
        sendChannelMessage("well-sync", message, args);
        sendChannelMessage("runerogue-bot", message, args);//server
    }


    public static void writeBugMessage(String message, Object...args) {
        sendChannelMessage("bug-sync", message, args);
    }

    public static void writePickupMessage(String message, Object...args) {
        writeServerSyncMessage(message, args);
        sendChannelMessage("pickup-sync", message, args);

    }

    public static void writeSuggestionMessage(String message, Object...args) {
        sendChannelMessage("suggestion-sync", message, args);
    }

    public static void writeEventMessage(String message, Object...args) {
        writeServerSyncMessage(message, args);
        sendChannelMessage("event-sync", message, args);
        sendChannelMessage("runerogue-bot", message, args);//server
    }

    public static void writeFoeMessage(String message, Object...args) {
        writeServerSyncMessage(message, args);
        sendChannelMessage("foe-sync", message, args);
        sendChannelMessage("runerogue-bot", message, args);//server
    }

    public static void writeReferralMessage(String message, Object...args) {
        writeServerSyncMessage(message, args);
        sendChannelMessage("referral-sync", message, args);
    }

    public static void writeCheatEngineMessage(String message, Object...args) {
        writeServerSyncMessage(message, args);
        sendChannelMessage("cheat-engine-sync", message, args);
    }

    /**
     * Write to a channel that should not be ignored by staff.
     */
    public static void writeAddressSwapMessage(String message, Object...args) {
        writeServerSyncMessage(message, args);
        sendChannelMessage("address-swap-sync", message, args);
    }

    private static void sendChannelMessage(String channelName, String message, Object...args) {
        if (Configuration.DISABLE_DISCORD_MESSAGING || !enabled())
            return;
        Server.getIoExecutorService().submit(() -> {
            try {
                TextChannel channel = getChannel(channelName);
                if (channel == null)
                    return;
                channel.sendMessage(Misc.replaceBracketsWithArguments(message, args)).queue();
           } catch (Exception e) {
                if (e.getCause() != null && e.getCause().getMessage() != null) {
                    String errorMessage = e.getCause().getMessage();
                    if (errorMessage.contains("java.net.SocketTimeoutException")) {
                        logger.error("Socket timed out: {}", channelName);
                        return;
                    }
                }

                logger.error("Error writing to channel: {}", channelName, e);
            }
        });
    }

    private static TextChannel getChannel(String name) throws LoginException, InterruptedException {
        if (!enabled())
           return null;
        if (channels.containsKey(name))
            return channels.get(name);

        List<TextChannel> foundChannels = getJDA().getTextChannelsByName(name, true);
        if (foundChannels.isEmpty()) {
            logger.error("No discord channel found with name: " + name);
            return null;
        }
        TextChannel channel = foundChannels.get(0);
        channels.put(name, channel);
        return channels.get(name);
    }

    private static JDA getJDA() throws LoginException, InterruptedException {
        if (jda == null) {
            jda = JDABuilder.createDefault("OTY5Mzk1ODg3Mzk2NzU3NTY0.YmsyRg.thKsra9X95a91EwWIcwOdbLB_Ws").build();//ODUxMjA4NzAzNTM3MDUzNzE2.YL074Q.bSctppX2eUF_Ke94FNvkkolCs74
            jda.awaitReady();
        }

        return jda;
    }

    private static boolean enabled() {
        return Server.isPublic() && Server.getConfiguration().getServerState() != ServerState.TEST_PUBLIC;
    }

}
