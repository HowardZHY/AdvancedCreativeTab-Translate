package com.ATE.ATEHUD.gui;

import java.io.IOException;

import com.ATE.ATEHUD.ModMain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class ATEModGuiLoreSelector extends GuiScreen {
	public static final int maxLore=5;
	public ATEModGuiItemFactory Last;
	public String[] lores=new String[]{};
	public GuiTextField[] loresTF=new GuiTextField[]{};
	public GuiButton done,cancel;
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
			throws IOException {
		for (int i = 0; i < loresTF.length; i++) {
			loresTF[i].mouseClicked(mouseX, mouseY, mouseButton);
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for (int i = 0; i < loresTF.length; i++) {
			loresTF[i].textboxKeyTyped(typedChar, keyCode);
		}
		super.keyTyped(typedChar, keyCode);
	}
	public void updateScreen() {
		for (int i = 0; i < loresTF.length; i++) {
			loresTF[i].updateCursorCounter();
		}
		super.updateScreen();
	}
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		for (int i = 0; i < loresTF.length; i++) {
			loresTF[i].drawTextBox();
			lores[i]=loresTF[i].getText();
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button==done) {
			Last.lores=this.lores;
			Minecraft.getMinecraft().displayGuiScreen(Last);
		}
		if(button==cancel)Minecraft.getMinecraft().displayGuiScreen(Last);
		super.actionPerformed(button);
	}
	public void initGui() {
		loresTF=(GuiTextField[]) ModMain.copyOf(new GuiTextField[]{}, maxLore, GuiTextField[].class);
		lores=Last.lores;
		lores=(String[])ModMain.copyOf(lores, maxLore, String[].class);
		for (int i = 0; i < lores.length; i++) {
			if(lores[i]==null)lores[i]="";
		}
		for (int i = 0; i < loresTF.length; i++) {
			loresTF[i]=new GuiTextField(i+2, fontRendererObj, width/2-148, 30+i*20, 296, 16);
			loresTF[i].setText(lores[i]);
		}
		done=new GuiButton(0, width/2-100,5,99,20, I18n.format("gui.done"));
		cancel=new GuiButton(1, width/2,5,100,20, I18n.format("gui.act.cancel"));
		buttonList.add(done);
		buttonList.add(cancel);
		super.initGui();
	}
	
	public ATEModGuiLoreSelector(ATEModGuiItemFactory last){
		Last=last;
	}
}