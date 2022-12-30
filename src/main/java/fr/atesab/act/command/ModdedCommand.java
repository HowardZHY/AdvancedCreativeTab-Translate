package fr.atesab.act.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.ClientChatPreview;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.*;

public class ModdedCommand {

    public static final CommandBuildContext COMMAND_BUILD_CONTEXT = new CommandBuildContext(RegistryAccess.BUILTIN.get());
    public static final int MAX_EXAMPLE = 6;

    public static MutableComponent createPrefix(String text, ChatFormatting color, ChatFormatting border) {
        return createText("[", border).append(createText(text, color)).append(createText("] ", border));
    }

    public static MutableComponent createText(String text, ChatFormatting color) {
        return Component.literal(Objects.requireNonNull(text)).withStyle(color);
    }

    public static MutableComponent createTranslatedPrefix(String text, ChatFormatting color, ChatFormatting border,
                                                          Object... args) {
        return createText("[", border).append(createTranslatedText(text, color, args)).append(createText("] ", border));
    }

    public static MutableComponent createTranslatedText(String lang, ChatFormatting color, Object... args) {
        return Component.translatable(Objects.requireNonNull(lang), args).withStyle(color);
    }

    /**
     * send a signed chat or command
     *
     * @param message the message
     */
    public static void sendSigned(String message) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        ClientChatPreview chatPreview = new ClientChatPreview(mc);
        Component component = Util.mapNullable(chatPreview.pull(message), ClientChatPreview.Preview::response);
        if (message.startsWith("/")) {
            player.commandSigned(message.substring(1), component);
        } else {
            player.chatSigned(message, component);
        }
    }

    private final Map<String, ModdedCommand> NAME_TO_COMMAND = new TreeMap<>();
    private final List<ModdedCommand> SUB_COMMANDS = new ArrayList<>();
    private final List<String> subCommandTitleExample = new ArrayList<>(MAX_EXAMPLE);
    private ModdedCommand parent;
    public ModdedCommand defaultCommand;
    private final String name;
    private final String description;
    private final CommandClickOption clickOption;

    private final boolean displayInHelp;
    private final List<String> aliases;
    protected LiteralCommandNode<CommandSourceStack> node;
    private CommandDispatcher<CommandSourceStack> dispatcher;
    private boolean registered = false;

    public ModdedCommand(String name) {
        this(name, "", CommandClickOption.doCommand);
    }

    public ModdedCommand(String name, String description, CommandClickOption clickOption) {
        this(name, description, clickOption, true);
    }

    public ModdedCommand(String name, String description, CommandClickOption clickOption, boolean displayInHelp) {
        this.description = description;
        this.clickOption = clickOption;
        this.displayInHelp = displayInHelp;
        this.name = name.toLowerCase();
        aliases = new ArrayList<>(1);
        addAlias(getName());
    }

    /**
     * add an alias to this command
     *
     * @param alias the alias
     * @return the current command
     */
    public ModdedCommand addAlias(String alias) {
        this.aliases.add(alias.toLowerCase());
        return this;
    }

    /**
     * @return a unmodifiable list of the aliases
     */
    public List<String> getAliases() {
        return Collections.unmodifiableList(aliases);
    }

    /**
     * @return the option in a {@link ModdedCommandHelp} command
     */
    public CommandClickOption getClickOption() {
        return clickOption;
    }

    /**
     * @return the translated description of this command
     */
    public String getDescription() {
        return I18n.get(getDescriptionTranslationKey());
    }

    /**
     * @return the translation key of the description
     */
    public String getDescriptionTranslationKey() {
        return description;
    }

    /**
     * @return the command on which this {@link ModdedCommand} has been registered
     */
    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return dispatcher;
    }

    /**
     * @return the name with the parent(s) name(s)
     */
    public String getGlobalName() {
        return parent != null ? parent.getGlobalName() + " " + getName() : getName();
    }

    /**
     * @return the command name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the node
     */
    public LiteralCommandNode<CommandSourceStack> getNode() {
        return node;
    }

    /**
     * @return the parent (if registered) of this command
     */
    public ModdedCommand getParent() {
        return parent;
    }

    /**
     * @return an unmodifiable {@link Collection} of the sub commands
     */
    public List<ModdedCommand> getSubCommands() {
        return Collections.unmodifiableList(SUB_COMMANDS);
    }

    /**
     * @return an unmodifiable {@link Map} of the sub commands
     */
    public Map<String, ModdedCommand> getSubCommandsMap() {
        return Collections.unmodifiableMap(NAME_TO_COMMAND);
    }

    /**
     * @return if this command is display in a {@link ModdedCommandHelp} call
     */
    public boolean isDisplayInHelp() {
        return displayInHelp;
    }

    /**
     * Called for registering argument command, should not be used for no argument
     * command, see {@link #onNoArgument()} for that
     *
     * @param command the argument builder
     * @return the same command argument builder modified
     */
    protected LiteralArgumentBuilder<CommandSourceStack> onArgument(
            LiteralArgumentBuilder<CommandSourceStack> command) {
        return command;
    }

    /**
     * Called for registering non argument command, should not be used for argument
     * command, see {@link #onArgument(LiteralArgumentBuilder)} for that
     *
     * @return the command to execute
     */
    protected Command<CommandSourceStack> onNoArgument() {
        return defaultCommand != null ? defaultCommand.onNoArgument() : c -> {
            c.getSource().sendFailure(Component.translatable("cmd.act.error.noargument")
                    .withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
            return 1;
        };
    }

    private void register() {
        if (registered)
            throw new IllegalStateException("A command can't be registered more than once");
        registered = true;
    }

    /**
     * register the command to a {@link CommandDispatcher}
     *
     * @param dispatcher the dispatcher
     */
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        register();
        this.dispatcher = dispatcher;
        register(dispatcher.getRoot());
    }

    private void register(CommandNode<CommandSourceStack> node) {
        LiteralArgumentBuilder<CommandSourceStack> bld;
        Command<CommandSourceStack> noArgument;
        for (String alias : aliases) {
            noArgument = onNoArgument();
            bld = onArgument(Commands.literal(alias));
            if (noArgument != null) {
                bld.executes(noArgument);
            }
            this.node = bld.build();
            NAME_TO_COMMAND.forEach((salias, cmd) -> {
                cmd.dispatcher = dispatcher;
                cmd.register(this.node);
            });
            node.addChild(this.node);
        }
    }

    /**
     * register a {@link ModdedCommand} and made it default
     *
     * @param subCommand the {@link ModdedCommand}
     * @return the parent command
     */
    public ModdedCommand registerDefaultSubCommand(ModdedCommand subCommand) {
        subCommand.register();
        for (String alias : subCommand.getAliases()) {
            NAME_TO_COMMAND.put(alias, subCommand);
        }

        if (subCommandTitleExample.size() < MAX_EXAMPLE) {
            subCommandTitleExample.add(0, subCommand.getName());
        } else {
            subCommandTitleExample.remove(MAX_EXAMPLE - 1);
            subCommandTitleExample.add(0, subCommand.getName());
        }
        SUB_COMMANDS.add(subCommand);
        defaultCommand = subCommand;
        return this;
    }

    /**
     * register a {@link ModdedCommand}
     *
     * @param subCommand the {@link ModdedCommand}
     * @return the parent command
     */
    public ModdedCommand registerSubCommand(ModdedCommand subCommand) {
        subCommand.register();
        subCommand.parent = this;
        for (String alias : subCommand.getAliases()) {
            NAME_TO_COMMAND.put(alias, subCommand);
        }
        if (subCommandTitleExample.size() < MAX_EXAMPLE) {
            subCommandTitleExample.add(subCommand.getName());
        }
        SUB_COMMANDS.add(subCommand);
        return this;
    }
}
