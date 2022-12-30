package fr.atesab.act.gui.modifier;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.atesab.act.gui.components.ACTButton;
import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.ItemUtils.PotionInformation;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiPotionModifier extends GuiListModifier<PotionInformation> {
    private static class CustomPotionListElement extends ListElement {
        private final EditBox duration;
        private final EditBox amplifier;
        private MobEffect potion;
        private int durationTime, amplifierValue;
        private boolean ambient;
        private final boolean showIcon;
        private boolean showParticles;
        private boolean errDur = false, errAmp = false;
        private final Button type;

        @SuppressWarnings("deprecation")
        public CustomPotionListElement(GuiPotionModifier parent, MobEffectInstance potionEffect) {
            super(400, 50);
            int l = 5 + Math.max(font.width(I18n.get("gui.act.modifier.meta.potion.duration") + " : "),
                    font.width(I18n.get("gui.act.modifier.meta.potion.amplifier") + " : "));
            potion = potionEffect.getEffect();
            durationTime = potionEffect.getDuration();
            amplifierValue = potionEffect.getAmplifier();
            ambient = potionEffect.isAmbient();
            showIcon = potionEffect.showIcon();
            showParticles = potionEffect.isVisible();

            duration = new EditBox(font, l, 1, 150 - l, 18, Component.literal(""));
            duration.setValue(String.valueOf(durationTime));
            amplifier = new EditBox(font, l, 22, 150 - l, 18, Component.literal(""));
            amplifier.setValue(String.valueOf(amplifierValue));
            buttonList.add(type = new ACTButton(153, 0, 200, 20,
                    Component.translatable("gui.act.modifier.meta.potion.type"), b -> {
                List<Tuple<String, MobEffect>> pots = new ArrayList<>();
                // Potion.REGISTRY
                Registry.MOB_EFFECT
                        .forEach(pot -> pots.add(new Tuple<>(I18n.get(pot.getDescriptionId()), pot)));
                mc.setScreen(new GuiButtonListSelector<>(parent,
                        Component.translatable("gui.act.modifier.meta.potion.type"), pots, pot -> {
                    potion = pot;
                    setButtonText();
                    return null;
                }));
            }));
            buttonList.add(new GuiBooleanButton(153, 21, 100, 20,
                    Component.translatable("gui.act.modifier.meta.potion.ambient"), b -> ambient = b,
                    () -> ambient));
            buttonList.add(new GuiBooleanButton(255, 21, 99, 20,
                    Component.translatable("gui.act.modifier.meta.potion.showParticles"), b -> showParticles = b,
                    () -> showParticles));
            buttonList.add(new RemoveElementButton(parent, 355, 0, 20, 20, this));
            buttonList.add(new AddElementButton(parent, 377, 0, 20, 20, this, parent.supplier));
            buttonList.add(new AddElementButton(parent, 355, 21, 43, 20, Component.translatable("gui.act.give.copy"),
                    this, () -> new CustomPotionListElement(parent, getEffect())));
            setButtonText();
        }

        @Override
        public void draw(PoseStack stack, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
            GuiUtils.drawRelative(stack, amplifier, offsetX, offsetY, mouseX, mouseY, partialTicks);
            GuiUtils.drawRelative(stack, duration, offsetX, offsetY, mouseX, mouseY, partialTicks);
            GuiUtils.drawRightString(font, I18n.get("gui.act.modifier.meta.potion.duration") + " : ", duration,
                    (errDur ? Color.RED : Color.WHITE).getRGB(), offsetX, offsetY);
            GuiUtils.drawRightString(font, I18n.get("gui.act.modifier.meta.potion.amplifier") + " : ", amplifier,
                    (errAmp ? Color.RED : Color.WHITE).getRGB(), offsetX, offsetY);
            super.draw(stack, offsetX, offsetY, mouseX, mouseY, partialTicks);
        }

        public MobEffectInstance getEffect() {
            return new MobEffectInstance(potion, durationTime, amplifierValue, ambient, showParticles, showIcon);
        }

        @Override
        public void init() {
            amplifier.setFocus(false);
            duration.setFocus(false);
            super.init();
        }

        @Override
        public boolean isFocused() {
            return amplifier.isFocused() || duration.isFocused();
        }

        @Override
        public boolean charTyped(char key, int modifiers) {
            return amplifier.charTyped(key, modifiers) || duration.charTyped(key, modifiers)
                    || super.charTyped(key, modifiers);
        }

        @Override
        public boolean keyPressed(int key, int scanCode, int modifiers) {
            return amplifier.keyPressed(key, scanCode, modifiers) || duration.keyPressed(key, scanCode, modifiers)
                    || super.keyPressed(key, scanCode, modifiers);
        }

        @Override
        public boolean match(String search) {
            return (potion == null ? "" : I18n.get(potion.getDescriptionId()).toLowerCase())
                    .contains(search.toLowerCase());
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            amplifier.mouseClicked(mouseX, mouseY, mouseButton);
            duration.mouseClicked(mouseX, mouseY, mouseButton);
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        private void setButtonText() {
            type.setMessage(Component.translatable("gui.act.modifier.meta.potion.type").append(" (").append(
                            potion == null ? Component.literal("null") : Component.translatable(potion.getDescriptionId()))
                    .append(")"));
        }

        @Override
        public void update() {
            amplifier.tick();
            duration.tick();
            try {
                durationTime = Integer.parseInt(duration.getValue());
                errDur = false;
            } catch (Exception e) {
                errDur = true;
            }
            try {
                int i = Integer.parseInt(amplifier.getValue());
                if (!(errAmp = i < -128 || i > 127))
                    amplifierValue = i;
            } catch (Exception e) {
                errAmp = true;
            }
            super.update();
        }

    }

    private static class MainPotionListElement extends ListElement {
        private static String getPotionName(Potion pot) {
            String name = pot.getName("");
            String registry = ItemUtils.getRegistry(ForgeRegistries.POTIONS, pot).toString();
            return name + (registry.contains("long_")
                    ? " (" + I18n.get("gui.act.modifier.meta.potion.long") + ")"
                    : registry.contains("strong_") ? " II" : "");
        }

        private final GuiPotionModifier parent;

        private final Button type;

        @SuppressWarnings("deprecation")
        public MainPotionListElement(GuiPotionModifier parent) {
            super(400, 29);
            this.parent = parent;
            buttonList.add(new ACTButton(0, 0, 200, 20, Component.translatable("gui.act.modifier.meta.setColor"),
                    b -> mc.setScreen(
                            new GuiColorModifier(parent, i -> parent.customColor = i, parent.customColor, true))));
            buttonList.add(type = new ACTButton(201, 0, 199, 20, Component.literal(""), b -> {
                List<Tuple<String, Potion>> pots = new ArrayList<>();
                // PotionType.REGISTRY
                Registry.POTION.forEach(type -> pots.add(new Tuple<>(getPotionName(type), type)));
                mc.setScreen(new GuiButtonListSelector<>(parent,
                        Component.translatable("gui.act.modifier.meta.potion.type"), pots, pot -> {
                    parent.main = pot;
                    defineButton();
                    return null;
                }));
            }));
            defineButton();
        }

        private void defineButton() {
            type.setMessage(Component.translatable("gui.act.modifier.meta.potion.type").append(" (")
                    .append(getPotionName(parent.main)).append(")"));
        }

    }

    private OptionalInt customColor;

    private Potion main;

    private final Supplier<ListElement> supplier = () -> new CustomPotionListElement(this,
            new MobEffectInstance(MobEffects.MOVEMENT_SPEED));

    @SuppressWarnings("unchecked")
    public GuiPotionModifier(Screen parent, Consumer<PotionInformation> setter, PotionInformation info) {
        super(parent, Component.translatable("gui.act.modifier.meta.potion"), new ArrayList<>(), setter,
                new Tuple[0]);
        this.customColor = info.getCustomColor();
        this.main = info.getMain();
        addListElement(new MainPotionListElement(this));
        info.getCustomEffects().forEach(t -> addListElement(new CustomPotionListElement(this, t)));
        addListElement(new AddElementList(this, supplier));
    }

    @Override
    protected PotionInformation get() {
        List<MobEffectInstance> customEffects = new ArrayList<>();
        getElements().stream().filter(le -> le instanceof CustomPotionListElement)
                .map(le -> (CustomPotionListElement) le).forEach(cpl -> customEffects.add(cpl.getEffect()));
        return new PotionInformation(customColor, main, customEffects);
    }

}
