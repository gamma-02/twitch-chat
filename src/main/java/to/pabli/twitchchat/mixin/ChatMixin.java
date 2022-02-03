package to.pabli.twitchchat.mixin;

import java.util.*;

import com.github.twitch4j.tmi.domain.Chatters;
import net.fabricmc.fabric.impl.client.indigo.IndigoMixinConfigPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import to.pabli.twitchchat.TwitchChatMod;
import to.pabli.twitchchat.config.ModConfig;
import to.pabli.twitchchat.twitch_integration.CalculateMinecraftColor;
import to.pabli.twitchchat.twitch_integration.FormattingColors;

@Mixin(Screen.class)
public class ChatMixin {
	@Inject(at = @At("HEAD"), method = "sendMessage(Ljava/lang/String;Z)V", cancellable = true)
	private void sendMessage(String text, boolean showInHistory, CallbackInfo info) {
        String token = ModConfig.getConfig().getOauthKey();
        System.out.println("token: " + token);
        char[] token1 = token.toCharArray();
        char[] token2 = new char[token.length()-6];
        for (int i = 0; i < token1.length-6; i++) {
            token2[i] = token1[i+6];
        }
        String token3 = String.valueOf(token2);
        System.out.println(token);
        System.out.println("token: " + Arrays.toString(token1));

        System.out.println("token: "+ Arrays.toString(token2));

        System.out.println(token3);
      ModConfig config = ModConfig.getConfig();


      String prefix = config.getPrefix();

      // Allow users to write /twitch commands (such as disabling and enabling the mod) when their prefix is "".
      if (prefix.equals("") && text.startsWith("/twitch")) {
        return; // Don't cancel the message, return execution to the real method
      }

      // If the message is a twitch message
      if (text.startsWith(prefix)) {
//        if (TwitchChatMod.bot != null && TwitchChatMod.bot.isConnected()) {
//          String textWithoutPrefix = text.substring(text.indexOf(prefix) + prefix.length());
//          TwitchChatMod.bot.sendMessage(textWithoutPrefix); // Send the message to the Twitch IRC Chat
//
//          Date currentTime = new Date();
//          String formattedTime = TwitchChatMod.formatDateTwitch(currentTime);
//
//          String username = TwitchChatMod.bot.getUsername();
//          Formatting userColor;
//          if (TwitchChatMod.bot.isFormattingColorCached(username)) {
//            userColor = TwitchChatMod.bot.getFormattingColor(username);
//          } else {
//            userColor = CalculateMinecraftColor.getDefaultUserColor(username);
//            TwitchChatMod.bot.putFormattingColor(username, userColor);
//          }
//
//          boolean isMeMessage = textWithoutPrefix.startsWith("/me");
//
//          // Add the message to the Minecraft Chat
//          TwitchChatMod.addTwitchMessage(formattedTime, username, isMeMessage ? textWithoutPrefix.substring(4) : textWithoutPrefix, userColor, isMeMessage);
//          MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(text);
//          info.cancel();
//        } else {
//          TwitchChatMod.addNotification(new TranslatableText("text.twitchchat.chat.integration_disabled"));
//        }
          if(TwitchChatMod.client != null){
              if(TwitchChatMod.client.getChat().isChannelJoined(ModConfig.getConfig().getChannel())){
                  String textWithoutPrefix = text.substring(text.indexOf(prefix) + prefix.length());
                  TwitchChatMod.client.getChat().sendMessage(ModConfig.getConfig().getChannel(), textWithoutPrefix);
                  Date currentTime = new Date();
                  String formattedTime = TwitchChatMod.formatDateTwitch(currentTime);


//                  List<String> username = new ArrayList<>();
//                  try {
//                      TwitchChatMod.client.getHelix().getUsers(ModConfig.getConfig().getUsername(), null, Collections.singletonList(token3)).execute().getUsers().forEach((user) -> {
//                          username.add(user.getDisplayName());
//                      });
//                  }catch(Exception e){
//                      System.out.println("caught invalid oauth token!");
//                      e.printStackTrace();
//                      return;
//                  } Yeah.... um..... hm.......... im stoopid... diddn't realize that this new API diddn't need an oauth key for much lmfao

//                  Formatting userColor;
//                  if (FormattingColors.isFormattingColorCached(username.get(0))) {
//                    userColor = FormattingColors.getFormattingColor(username.get(0));
//                  } else {
//                    userColor = CalculateMinecraftColor.getDefaultUserColor(username.get(0));//can't get the user color from the API, unfortunatly
//                    FormattingColors.putFormattingColor(username.get(0), userColor);
//                  }
                  TwitchChatMod.client.getChat().sendMessage(ModConfig.getConfig().getChannel(), ModConfig.getConfig().getUsername());

                  boolean isMeMessage = textWithoutPrefix.startsWith("/me");

                  // Add the message to the Minecraft Chat
//                  TwitchChatMod.addTwitchMessage(formattedTime, username.get(0), isMeMessage ? textWithoutPrefix.substring(4) : textWithoutPrefix, userColor, isMeMessage);
//                  MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(text);
                  info.cancel();
              }

          }
      }
	}
}
