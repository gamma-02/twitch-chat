package to.pabli.twitchchat.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import to.pabli.twitchchat.TwitchChatMod;
import to.pabli.twitchchat.config.ModConfig;

public class TwitchEnableCommand implements SubCommand {
    public ArgumentBuilder<FabricClientCommandSource, ?> getArgumentBuilder() {
        return ClientCommandManager.literal("enable")
                // The command to be executed if the command "twitch" is entered with the argument "enable"
                // It starts up the irc bot.
                .executes(ctx -> {
                    ModConfig config = ModConfig.getConfig();

                    if (TwitchChatMod.client != null && TwitchChatMod.client.getChat().isChannelJoined(config.getChannel())) {
                        ctx.getSource().sendFeedback(new TranslatableText("text.twitchchat.command.enable.already_enabled"));
                        return 1;
                    }else{
                        TwitchChatMod.startTwitchChat();
                    }

                    if (config.getUsername().equals("") || config.getOauthKey().equals("")) {
                        ctx.getSource().sendFeedback(new TranslatableText("text.twitchchat.command.enable.set_config"));
                        return -1;
                    }

                    if (config.getChannel().equals("")) {
                        ctx.getSource().sendFeedback(new TranslatableText("text.twitchchat.command.enable.select_channel"));
                    }

                    ctx.getSource().sendFeedback(new TranslatableText("text.twitchchat.command.enable.connecting").formatted(Formatting.DARK_GRAY));

                    // Return a result. -1 is failure, 0 is a pass and 1 is success.
                    return 1;
                });
    }
}
