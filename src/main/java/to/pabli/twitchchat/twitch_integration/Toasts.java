package to.pabli.twitchchat.twitch_integration;

import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Toasts {
    public static class SubscriptionToast implements Toast{
        private boolean soundPlayed = false;
        private SubscriptionEvent linkedEvent;

        public SubscriptionToast(SubscriptionEvent event){
            linkedEvent = event;
        }

        @Override
        public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            manager.drawTexture(matrices, 0, 0, 0, 0, this.getWidth(), this.getHeight());

            List<OrderedText> list = manager.getClient().textRenderer.wrapLines(new TranslatableText("text.twitchchat.integration.subscription"), 125);
            int i = 16746751;
            if (list.size() == 1) {
                manager.getClient().textRenderer.draw(matrices, new LiteralText(this.linkedEvent.getGiftedBy().getName()).formatted(Formatting.GOLD), 30.0F, 7.0F, i | -16777216);
                manager.getClient().textRenderer.draw(matrices, (OrderedText)list.get(0), 30.0F, 18.0F, -1);
            } else {

                float f = 300.0F;
                int k;
                if (startTime < 1500L) {
                    k = MathHelper.floor(MathHelper.clamp((float)(1500L - startTime) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
                    manager.getClient().textRenderer.draw(matrices, new LiteralText(this.linkedEvent.getGiftedBy().getName()).formatted(Formatting.GOLD), 30.0F, 11.0F, i | k);
                } else {
                    k = MathHelper.floor(MathHelper.clamp((float)(startTime - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                    int var10000 = this.getHeight() / 2;
                    int var10001 = list.size();
                    Objects.requireNonNull(manager.getClient().textRenderer);
                    int l = var10000 - var10001 * 9 / 2;

                    for(Iterator var12 = list.iterator(); var12.hasNext(); l += 9) {
                        OrderedText orderedText = (OrderedText)var12.next();
                        manager.getClient().textRenderer.draw(matrices, orderedText, 30.0F, (float)l, 16777215 | k);
                        Objects.requireNonNull(manager.getClient().textRenderer);
                    }
                }


                if ( !this.soundPlayed && startTime > 0L) {
                    this.soundPlayed = true;

                    manager.getClient().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));

                }

                manager.getClient().getItemRenderer().renderInGui(Items.NETHERITE_BLOCK.getDefaultStack(), 8, 8);
                return startTime >= 5000L ? Visibility.HIDE : Visibility.SHOW;
            }
            return Visibility.HIDE;
        }
    }
}
