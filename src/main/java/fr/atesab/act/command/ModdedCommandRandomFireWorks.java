package fr.atesab.act.command;

import com.mojang.brigadier.Command;
import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.commands.CommandSourceStack;

public class ModdedCommandRandomFireWorks extends ModdedCommand {
    public ModdedCommandRandomFireWorks() {
        super("randomfireworks", "cmd.act.rfw", CommandClickOption.doCommand);
        addAlias("rfw");
    }

    @Override
    protected Command<CommandSourceStack> onNoArgument() {
        return c -> {
            ItemUtils.give(ItemUtils.getRandomFireworks());
            return 1;
        };
    }

}
