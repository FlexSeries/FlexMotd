/**
 * FlexMotd - Licensed under the MIT License (MIT)
 *
 * Copyright (c) Stealth2800 <http://stealthyone.com/>
 * Copyright (c) contributors <https://github.com/FlexSeries>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.st28.flexseries.flexmotd.commands;

import me.st28.flexseries.flexlib.command.*;
import me.st28.flexseries.flexlib.command.CommandInterruptedException.InterruptReason;
import me.st28.flexseries.flexlib.message.MessageManager;
import me.st28.flexseries.flexlib.message.ReplacementMap;
import me.st28.flexseries.flexlib.message.list.ListBuilder;
import me.st28.flexseries.flexlib.message.reference.MessageReference;
import me.st28.flexseries.flexlib.message.reference.PlainMessageReference;
import me.st28.flexseries.flexlib.message.variable.MessageVariable;
import me.st28.flexseries.flexlib.plugin.FlexPlugin;
import me.st28.flexseries.flexlib.utils.StringConverter;
import me.st28.flexseries.flexlib.utils.StringUtils;
import me.st28.flexseries.flexmotd.FlexMotd;
import me.st28.flexseries.flexmotd.backend.MotdManager;
import me.st28.flexseries.flexmotd.commands.arguments.MotdArgument;
import me.st28.flexseries.flexmotd.permission.PermissionNodes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CmdMotd extends FlexCommand<FlexMotd> {

    public CmdMotd(FlexMotd plugin) {
        super(plugin, new CommandDescriptor("motd").permission(PermissionNodes.MOTD));

        registerSubcommand(new SCmdList(this));
        registerSubcommand(new SCmdSet(this));
    }

    @Override
    public void handleExecute(CommandContext context) {
        MotdManager motdManager = FlexPlugin.getGlobalModule(MotdManager.class);
        String motd = motdManager.getCurrentMotd();

        if (motd == null) {
            throw new CommandInterruptedException(InterruptReason.COMMAND_END, MessageManager.getMessage(FlexMotd.class, "errors.motd_not_set"));
        }

        final CommandSender sender = context.getSender();

        ListBuilder builder = new ListBuilder("subtitle", "MOTD", motdManager.getSelected(), context.getLabel());

        builder.addMessage(new PlainMessageReference(MessageVariable.handleReplacements((Player) sender, motd)));

        builder.sendTo(sender, 1);
    }

    // ------------------------------------------------------------------------------------------ //

    static class SCmdList extends Subcommand<FlexMotd> {

        public SCmdList(AbstractCommand<FlexMotd> parent) {
            super(parent, new CommandDescriptor("list").description("List loaded MOTDs").permission(PermissionNodes.MOTD_LIST));
        }

        @Override
        public void handleExecute(CommandContext context) {
            final CommandSender sender = context.getSender();

            final MotdManager motdManager = FlexPlugin.getGlobalModule(MotdManager.class);

            ListBuilder builder = new ListBuilder("title", "MOTD", null, context.getLabel());

            builder.addMessage(StringUtils.collectionToString(motdManager.getMotds().keySet(), ChatColor.DARK_GRAY + ", ", new StringConverter<String>() {
                @Override
                public String toString(String object) {
                    return (motdManager.getCurrentMotd().equals(object) ? ChatColor.GREEN : ChatColor.RED) + object;
                }
            }, "" + ChatColor.RED + ChatColor.ITALIC + "Nothing here"));

            builder.sendTo(sender, 1);
        }
    }

    // ------------------------------------------------------------------------------------------ //

    static class SCmdSet extends Subcommand<FlexMotd> {

        public SCmdSet(AbstractCommand<FlexMotd> parent) {
            super(parent, new CommandDescriptor("set").description("Set the current MOTD").permission(PermissionNodes.MOTD_SET));

            addArgument(new MotdArgument("name", true));
        }

        @Override
        public void handleExecute(CommandContext context) {
            final MotdManager motdManager = FlexPlugin.getGlobalModule(MotdManager.class);

            // Set message
            MessageReference message;

            String name = getRelativeArgs(context).get(0);
            try {
                if (motdManager.setMotd(name)) {
                    message = MessageManager.getMessage(FlexMotd.class, "notices.motd_set", new ReplacementMap("{NAME}", name).getMap());
                } else {
                    message = MessageManager.getMessage(FlexMotd.class, "errors.motd_already_set", new ReplacementMap("{NAME}", name).getMap());
                }
            } catch (IllegalArgumentException ex) {
                message = MessageManager.getMessage(FlexMotd.class, "errors.motd_not_found", new ReplacementMap("{NAME}", name).getMap());
            }

            throw new CommandInterruptedException(InterruptReason.COMMAND_END, message);
        }

    }

}