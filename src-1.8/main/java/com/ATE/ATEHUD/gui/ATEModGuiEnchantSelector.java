package com.ATE.ATEHUD.gui;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.ATE.ATEHUD.ModMain;
import com.ATE.ATEHUD.superclass.Colors;
import com.ATE.ATEHUD.superclass.EnchantmentInfo;
import com.ATE.ATEHUD.superclass.Enchantments;
import com.ATE.ATEHUD.utils.GuiUtils;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;

public class ATEModGuiEnchantSelector extends GuiScreen{
	private ATEModGuiItemFactory Last;
	private GuiButton bdone,bcancel,ball100,ball0,bmax;
	private int preButton=10;
	private Enchantments[] list=new Enchantments[]{};
	private GuiTextField[] tfs=new GuiTextField[]{};
	public void actionPerformed(GuiButton button){
		if(button==bdone){
			EnchantmentInfo[] ench=(EnchantmentInfo[]) ModMain.copyOf(new EnchantmentInfo[]{},list.length,EnchantmentInfo[].class);
			for (int i = 0; i < ench.length; i++) {
				ench[i]=null;
			}int j=0;
			for (int i = 0; i < list.length; i++) {
				int level=1;
				boolean hasLevel=false;
				if(!tfs[i].getText().isEmpty())
					try {
						level=Integer.valueOf(tfs[i].getText());
						hasLevel=true;
					} catch (Exception e){}
				if(hasLevel){
					ench[j]=new EnchantmentInfo(list[i], level);
					j++;
				}
			}
			Last.enchantments=(EnchantmentInfo[]) ModMain.copyOf(ench, j, EnchantmentInfo[].class);
			mc.displayGuiScreen(Last);
		}
		if(button==bmax){
			for (int i = 0; i < tfs.length; i++) {
				tfs[i].setText(String.valueOf(list[i].getEnchantment().getMaxLevel()));
			}
		}
		if(button==bcancel)mc.displayGuiScreen(Last);
		if(button==ball100){
			for (int i = 0; i < tfs.length; i++) {
				tfs[i].setText("100");
			}
		}
		if(button==ball0){
			for (int i = 0; i < tfs.length; i++) {
				tfs[i].setText("");
			}
		}
	}
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		for (int i = 0; i < tfs.length; i++) {
			//Gray=not use / White=use / Red=Not a Integer
			int isInteger=Colors.GRAY;
			if(!tfs[i].getText().isEmpty())
			try {
				Integer.valueOf(tfs[i].getText());
				isInteger=Colors.azure1;
			} catch (Exception e){isInteger=Colors.RED;}
			GuiUtils.drawRightString(fontRendererObj, I18n.format(list[i].getEnchantment().getName())+" : ", tfs[i].xPosition, tfs[i].yPosition, tfs[i].height, isInteger);
			tfs[i].drawTextBox();
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	public void updateScreen()
	{
		for (int i = 0; i < tfs.length; i++) {
			tfs[i].updateCursorCounter();
		}
		super.updateScreen();
	}
	protected void keyTyped(char par1, int par2) throws IOException{
		for (int i = 0; i < tfs.length; i++) {
			tfs[i].textboxKeyTyped(par1, par2);
		}
		super.keyTyped(par1, par2);
	}
	protected void mouseClicked(int x, int y, int btn) throws IOException {

		for (int i = 0; i < tfs.length; i++) {
			tfs[i].mouseClicked(x, y, btn);
		}
		super.mouseClicked(x, y, btn);
	}
	public void initGui() {
		list=Enchantments.enchantments;
		tfs=(GuiTextField[]) ModMain.copyOf(new GuiTextField[]{}, list.length, GuiTextField[].class);
		int ix=0,iy=0;
		for (int i = 0; i < list.length; i++) {
			GuiTextField tf=new GuiTextField(i, fontRendererObj, width/2-100+ix*200, 50+iy*21, 40, 20);
			if(tf!=null)tfs[i]=tf;
			if(ix==1){
				ix=0;
				iy++;
			}else{ix++;}
		}
		//fill enchantment level
		for (int i = 0; i < list.length; i++) {
			for (int j = 0; j < Last.enchantments.length; j++) {
				if(list[i]==Last.enchantments[j].Enchantment){
					tfs[i].setText(String.valueOf(Last.enchantments[j].Level));
				}
			}
		}
		bmax=new GuiButton(1+list.length,width/2-150,26,199,20,I18n.format("gui.act.itemfactory.max"));
		
		ball100=new GuiButton(3+list.length,width/2-50,5,99,20,I18n.format("gui.act.itemfactory.set100"));
		ball0=new GuiButton(4+list.length,width/2-150,5,99,20,I18n.format("gui.act.itemfactory.set0"));
		
		bcancel=new GuiButton(5+list.length,width/2+50,5,99,20,I18n.format("gui.act.cancel"));
		bdone =new GuiButton(6+list.length,width/2+50,26,99,20,I18n.format("gui.done"));
		buttonList.add(bdone);
		buttonList.add(bcancel);
		buttonList.add(ball100);
		buttonList.add(ball0);
		buttonList.add(bmax);
		super.initGui();
	}
	public ATEModGuiEnchantSelector(ATEModGuiItemFactory last){
		Last=last;
	}
}