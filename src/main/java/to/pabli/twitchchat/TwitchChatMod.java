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
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
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
        // Register commands
        CommandDispatcher<FabricClientCommandSource> dispatcher = ClientCommandManager.DISPATCHER;
        new TwitchBaseCommand().registerCommands(dispatcher);
    }

    public static void addTwitchMessage(String time, String username, String message, Color textColor, boolean isMeMessage) {
        MutableText timestampText = new LiteralText(time);
        MutableText usernameText = new LiteralText(username).fillStyle(Style.EMPTY.withColor(TextColor.fromRgb(textColor.getRGB())));
        MutableText messageBodyText;

        if (!isMeMessage) {
            messageBodyText = new LiteralText(": " + message);
        } else {
            // '/me' messages have the same color as the username in the Twitch website.
            // And thus I set the color of the message to be the same as the username.
            // They also don't have a colon after the username.
            messageBodyText = new LiteralText(" " + message).fillStyle(Style.EMPTY.withColor(TextColor.fromRgb(textColor.getRGB())));

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
            System.out.println(Arrays.toString(event.getTags().entrySet().toArray()));
            if(event.getUserName() != null && event.getMessage().isPresent() && event.isValid())
                addTwitchMessage(formatDateTwitch(new Date()), event.getUserName(), event.getMessage().orElse(""), getUserColor(event), false);
        });
        client.getEventManager().onEvent(SubscriptionEvent.class, subscriptionEvent -> {
//            MinecraftClient.getInstance().inGameHud
            MinecraftClient.getInstance().getToastManager().add(new Toasts.SubscriptionToast(subscriptionEvent));
        });
    }
    private static Color lastValueNotNull;
    private static Color lastColor;
    public static Color getUserColor(IRCMessageEvent evt){
        if(CalculateMinecraftColor.cachedNames.containsKey(evt.getUserName())){
            return CalculateMinecraftColor.cachedNames.get(evt.getUserName());
        }
        Color tag = getColorTag(evt.getRawTags().get("color").toString());
        CalculateMinecraftColor.cachedNames.put(evt.getUserName(), tag);
        return tag;

    }

    public static Color getColorTag(String str) {
        char[] returnVal = new char[str.length()-1];
        for (int i = 1; i < str.length(); i++) {
            returnVal[i-1] = str.charAt(i);
        }
        System.out.println(returnVal);
        char[] r1 = {returnVal[0], returnVal[1]};
        int r = Integer.parseInt(String.valueOf(r1));
        char[] g1 = {returnVal[2], returnVal[3]};
        int g = Integer.parseInt(String.valueOf(g1));
        char[] b1 = {returnVal[4], returnVal[5]};
        int b = Integer.parseInt(String.valueOf(b1));

        return new Color(r, g, b);




    }
}

