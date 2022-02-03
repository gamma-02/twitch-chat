package to.pabli.twitchchat.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import to.pabli.twitchchat.TwitchChatMod;
import to.pabli.twitchchat.config.ModConfig;

public class TwitchDisableCommand implements SubCommand {
  public ArgumentBuilder<FabricClientCommandSource, ?> getArgumentBuilder() {
    return ClientCommandManager.literal("disable")
        // The command to be executed if the command "twitch" is entered with the argument "disable"
        // It shuts down the irc bot.
        .executes(ctx -> {
          if (TwitchChatMod.client == null || !TwitchChatMod.client.getChat().isChannelJoined(ModConfig.getConfig().getChannel())) {
            ctx.getSource().sendFeedback(new TranslatableText("text.twitchchat.command.disable.already_disabled"));
            return 0;
          }

          TwitchChatMod.client.getChat().disconnect();
          ctx.getSource().sendFeedback(new TranslatableText("text.twitchchat.command.disable.disabled").formatted(
              Formatting.DARK_GRAY));

          // Return a result. -1 is failure, 0 is a pass and 1 is success.
          return 1;
        });
  }
}
