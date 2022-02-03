package to.pabli.twitchchat;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import to.pabli.twitchchat.commands.TwitchBaseCommand;
import to.pabli.twitchchat.config.ModConfig;
import to.pabli.twitchchat.twitch_integration.CalculateMinecraftColor;
import to.pabli.twitchchat.twitch_integration.Toasts;

public class TwitchChatMod implements ModInitializer {
    public static OAuth2Credential credential;
    public static TwitchClient client;

    @Override
    public void onInitialize() {
        ModConfig.getConfig().load();
        //I'm gonna move these two at some point to up where the bot is currently done but I can't be bothered rn

        // Register commands
        CommandDispatcher<FabricClientCommandSource> dispatcher = ClientCommandManager.DISPATCHER;
        new TwitchBaseCommand().registerCommands(dispatcher);
    }

    public static void addTwitchMessage(String time, String username, String message, Formatting textColor, boolean isMeMessage) {
        MutableText timestampText = new LiteralText(time);
        MutableText usernameText = new LiteralText(username).formatted(textColor);
        MutableText messageBodyText;

        if (!isMeMessage) {
            messageBodyText = new LiteralText(": " + message);
        } else {
            // '/me' messages have the same color as the username in the Twitch website.
            // And thus I set the color of the message to be the same as the username.
            // They also don't have a colon after the username.
            messageBodyText = new LiteralText(" " + message).formatted(textColor);

            // In Minecraft, a '/me' message is marked with a star before the name, like so:
            //
            // <Player> This is a normal message
            // * Player this is a '/me' message
            //
            // The star is always white (that's why I don't format it).
            usernameText = new LiteralText("* ").append(usernameText);
        }

        if (ModConfig.getConfig().isBroadcastEnabled()) {
            try {
                String plainTextMessage = ModConfig.getConfig().getBroadcastPrefix() + username + ": " + message;
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendChatMessage(plainTextMessage);
                }
            } catch (NullPointerException e) {
                System.err.println("TWITCH BOT FAILED TO BROADCAST MESSAGE: " + e.getMessage());
            }
        } else {
            if(usernameText != null && messageBodyText != null) {
                MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT,
                        timestampText
                                .append(usernameText)
                                .append(messageBodyText), UUID.randomUUID());
            }
        }
    }

    public static void addNotification(MutableText message) {
        MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, message.formatted(Formatting.DARK_GRAY), UUID.randomUUID());
    }

    public static String formatTMISentTimestamp(String tmiSentTS) {
        return formatTMISentTimestamp(Long.parseLong(tmiSentTS));
    }

    public static String formatTMISentTimestamp(long tmiSentTS) {
        Date date = new Date(tmiSentTS);
        return formatDateTwitch(date);
    }

    public static String formatDateTwitch(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat(ModConfig.getConfig().getDateFormat());
        return sf.format(date);
    }

    public static void startTwitchChat(){
        credential = new OAuth2Credential("twitch", ModConfig.getConfig().getOauthKey());
        client = TwitchClientBuilder.builder().withEnableChat(true).withChatAccount(TwitchChatMod.credential).withEnableHelix(true).build();
        client.getEventManager().onEvent(IRCMessageEvent.class, event -> {
            System.out.println(event.getTags().get("color") != null ? getUserColor(event) : "ajsdhalfnull");
            System.out.println(Arrays.toString(event.getTags().entrySet().toArray()));
            if(event.getUserName() != null && event.getMessage().isPresent() && event.isValid())
                addTwitchMessage(formatDateTwitch(new Date()), event.getUserName(), event.getMessage().orElse(""), CalculateMinecraftColor.findNearestMinecraftColor(getUserColor(event)), false);
        });
        client.getEventManager().onEvent(SubscriptionEvent.class, subscriptionEvent -> {
//            MinecraftClient.getInstance().inGameHud
            MinecraftClient.getInstance().getToastManager().add(new Toasts.SubscriptionToast(subscriptionEvent));
        });
    }
    private static Color lastValueNotNull;
    private static Color lastColor;
    public static Color getUserColor(IRCMessageEvent evt){
        String tag = getColorTag(evt);
        if(tag!= null) {
            int color = Integer.decode(tag);
            System.out.println("Integer: " + color);
            lastValueNotNull = Color.getColor(tag);
            return Color.getColor(tag);
        }else {
            return Color.getColor(CalculateMinecraftColor.getDefaultUserColor(evt.getUserName()).getColorValue().toString());
        }
    }

    public static String getColorTag(IRCMessageEvent evt) {

        for (Object el :
                evt.getTags().entrySet().toArray()) {
            if (el instanceof String str) {
                if (str.startsWith("color=")) {

                    char[] token1 = str.toCharArray();
                    char[] token2 = new char[str.length() - 7];
                    for (int i = 0; i < token1.length - 7; i++) {
                        token2[i] = token1[i + 7];
                    }
                    return String.valueOf(token2);
                }

            }
        }
        return null;
    }
}

