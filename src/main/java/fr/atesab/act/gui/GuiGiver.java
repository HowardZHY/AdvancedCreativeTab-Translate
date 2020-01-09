package fr.atesab.act.gui;

import java.awt.Color;
import java.util.function.Consumer;

import fr.atesab.act.ACTMod;
import fr.atesab.act.gui.modifier.GuiItemStackModifier;
import fr.atesab.act.gui.modifier.GuiModifier;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class GuiGiver extends GuiModifier<String> {
	private Button giveButton, saveButton, doneButton;
	private TextFieldWidget code;
	private String preText;
	private ItemStack currentItemStack;
	private Consumer<String> setter;
	private boolean deleteButton;

	public GuiGiver(Screen parent) {
		super(parent, "gui.act.give", s -> {
		});
		if (mc.player != null) {
			ItemStack mainHand = mc.player.getHeldItem(Hand.MAIN_HAND);
			this.currentItemStack = mainHand != null ? mainHand : mc.player.getHeldItem(Hand.OFF_HAND);
			this.preText = ItemUtils.getGiveCode(this.currentItemStack);
		}
	}

	public GuiGiver(Screen parent, ItemStack itemStack) {
		this(parent, itemStack, null, false);
	}

	public GuiGiver(Screen parent, ItemStack itemStack, Consumer<String> setter, boolean deleteButton) {
		super(parent, "gui.act.give", s -> {
		});
		this.preText = itemStack != null ? ItemUtils.getGiveCode(itemStack) : "";
		this.currentItemStack = itemStack;
		this.setter = setter;
		this.deleteButton = deleteButton;
	}

	public GuiGiver(Screen parent, String preText) {
		this(parent, preText, s -> {
		}, false);
	}

	public GuiGiver(Screen parent, String preText, Consumer<String> setter, boolean deleteButton) {
		this(parent, preText != null && !preText.isEmpty() ? ItemUtils.getFromGiveCode(preText) : null);
		this.preText = preText;
	}

	@Override
	public boolean isPauseScreen() {
		return false || (parent != null && parent.isPauseScreen());
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		code.render(mouseX, mouseY, partialTicks);
		GuiUtils.drawCenterString(font, I18n.format("gui.act.give"), width / 2, code.y - 21, Color.ORANGE.getRGB(), 20);
		super.render(mouseX, mouseY, partialTicks);
		if (currentItemStack != null) {
			GuiUtils.drawItemStack(itemRenderer, this, currentItemStack, code.x + code.getWidth() + 5, code.y - 2);
			if (GuiUtils.isHover(code.x + code.getWidth() + 5, code.y, 20, 20, mouseX, mouseY))
				renderTooltip(currentItemStack, mouseX, mouseY);
		}
	}

	@Override
	public void init() {

		code = new TextFieldWidget(font, width / 2 - 178, height / 2 + 2, 356, 16, "");
		code.setMaxStringLength(Integer.MAX_VALUE);
		if (preText != null)
			code.setText(preText.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&"));
		boolean flag2 = deleteButton; // deleteCancel
		int s1 = flag2 ? 120 : 180;
		int s2 = 120;
		addButton(giveButton = new Button(width / 2 - 180, height / 2 + 21, s1, 20, I18n.format("gui.act.give.give"),
				b -> ItemUtils.give(currentItemStack)));
		addButton(new Button(width / 2 + s1 - 178, height / 2 + 21, s1 - 2, 20, I18n.format("gui.act.give.copy"),
				b -> GuiUtils.addToClipboard(code.getText())));
		addButton(new Button(width / 2 - 180 + (flag2 ? 2 * s1 + 1 : 0), height / 2 + 21 + (flag2 ? 0 : 21),
				(flag2 ? s1 - 1 : s2), 20, I18n.format("gui.act.give.editor"), b -> {
					getMinecraft().displayGuiScreen(
							new GuiItemStackModifier(this, currentItemStack, itemStack -> setCurrent(itemStack)));
				}));
		addButton(doneButton = new Button(width / 2 - 179 + 2 * s2, height / 2 + 42, s2 - 1, 20,
				I18n.format("gui.done"), b -> {
					if (setter != null && currentItemStack != null)
						setter.accept(code.getText());
					getMinecraft().displayGuiScreen(parent);
				}));
		if (setter != null)
			addButton(new Button(width / 2 - 58, height / 2 + 42, s2 - 2, 20, I18n.format("gui.act.cancel"),
					b -> getMinecraft().displayGuiScreen(parent)));
		else
			addButton(saveButton = new Button(width / 2 - 58, height / 2 + 42, s2 - 2, 20, I18n.format("gui.act.save"),
					b -> {
						if (parent instanceof GuiMenu)
							((GuiMenu) parent).get();
						ACTMod.saveItem(code.getText());
						getMinecraft().displayGuiScreen(new GuiMenu(parent));
					}));
		if (deleteButton)
			addButton(new Button(width / 2 - 180, height / 2 + 42, s2, 20, I18n.format("gui.act.delete"), b -> {
				setter.accept(null);
				getMinecraft().displayGuiScreen(parent);
			}));
		super.init();
	}

	@Override
	public boolean charTyped(char key, int modifiers) {
		return code.charTyped(key, modifiers);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		return code.keyPressed(key, scanCode, modifiers) || super.keyPressed(key, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return code.mouseClicked(mouseX, mouseY, mouseButton) || super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	private void setCurrent(ItemStack currentItemStack) {
		preText = ItemUtils.getGiveCode(this.currentItemStack = currentItemStack);
	}

	public void setCurrentItemStack(ItemStack currentItemStack) {
		this.currentItemStack = currentItemStack;
		this.preText = ItemUtils.getGiveCode(currentItemStack);
	}

	public void setPreText(String preText) {
		this.preText = preText;
		this.currentItemStack = preText != null && !preText.isEmpty() ? ItemUtils.getFromGiveCode(preText) : null;
	}

	@Override
	public void tick() {
		code.tick();
		this.currentItemStack = ItemUtils
				.getFromGiveCode(code.getText().replaceAll("&", String.valueOf(ChatUtils.MODIFIER)));
		this.giveButton.active = this.currentItemStack != null && getMinecraft().player != null
				&& getMinecraft().player.isCreative();
		this.doneButton.active = (setter != null && this.currentItemStack != null) || setter == null;
		if (saveButton != null)
			saveButton.active = this.currentItemStack != null;
		super.tick();
	}
}
