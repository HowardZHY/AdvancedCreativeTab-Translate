package fr.atesab.act.command;

import com.mojang.brigadier.Command;

import fr.atesab.act.ACTMod;
import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import net.minecraft.command.CommandSource;

public class ModdedCommandEdit extends ModdedCommand {

	public ModdedCommandEdit() {
		super("edit", "cmd.act.edit", CommandClickOption.doCommand);
		addAlias("e");
	}

	@Override
	protected Command<CommandSource> onNoArgument() {
		return c -> {
			ACTMod.openGiver();
			return 0;
		};
	}

}
