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
import me.st28.flexseries.flexlib.plugin.FlexPlugin;
import me.st28.flexseries.flexlib.utils.StringConverter;
import me.st28.flexseries.flexlib.utils.StringUtils;
import me.st28.flexseries.flexmotd.FlexMotd;
import me.st28.flexseries.flexmotd.backend.PingManager;
import me.st28.flexseries.flexmotd.commands.arguments.PingGroupArgument;
import me.st28.flexseries.flexmotd.commands.arguments.PingImageArgument;
import me.st28.flexseries.flexmotd.commands.arguments.PingMessageArgument;
import me.st28.flexseries.flexmotd.permission.PermissionNodes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class CmdPing extends DummyCommand<FlexMotd> {

    public CmdPing(FlexMotd plugin) {
        super(plugin, new CommandDescriptor("flexping").dummy(true));

        registerSubcommand(new SCmdGroup(this));
        registerSubcommand(new SCmdImage(this));
        registerSubcommand(new SCmdMessage(this));
    }

    // ------------------------------------------------------------------------------------------ //

    static class SCmdGroup extends Subcommand<FlexMotd> {

        public SCmdGroup(AbstractCommand<FlexMotd> parent) {
            super(parent, new CommandDescriptor("group").permission(PermissionNodes.PING_GROUP_LIST).description("Select or list ping information groups"));

            addArgument(new PingGroupArgument("name", false));
        }

        @Override
        public void handleExecute(CommandContext context) {
            final CommandSender sender = context.getSender();
            final List<String> args = getRelativeArgs(context);

            final PingManager pingManager = FlexPlugin.getGlobalModule(PingManager.class);

            if (args.isEmpty()) {
                // List

                ListBuilder builder = new ListBuilder("subtitle", "Ping Info", "Group", context.getLabel());

                builder.addMessage(StringUtils.collectionToString(pingManager.getGroups().keySet(), ChatColor.DARK_GRAY + ", ", new StringConverter<String>() {
                    @Override
                    public String toString(String string) {
                        return ChatColor.GOLD + string;
                    }
                }, "" + ChatColor.RED + ChatColor.ITALIC + "Nothing here"));

                builder.sendTo(sender, 1);
                return;
            }

            CommandUtils.performPermissionCheck(context, PermissionNodes.PING_GROUP_SET);

            // Set group
            MessageReference message;

            String name = args.get(0);
            try {
                pingManager.setGroup(name);
                message = MessageManager.getMessage(FlexMotd.class, "notices.ping_group_set", new ReplacementMap("{NAME}", name).getMap());
            } catch (IllegalArgumentException ex) {
                message = MessageManager.getMessage(FlexMotd.class, "errors.ping_group_not_found", new ReplacementMap("{NAME}", name).getMap());
            }

            throw new CommandInterruptedException(InterruptReason.COMMAND_END, message);
        }

    }

    // ------------------------------------------------------------------------------------------ //

    static class SCmdImage extends Subcommand<FlexMotd> {

        public SCmdImage(AbstractCommand<FlexMotd> parent) {
            super(parent, new CommandDescriptor("image").permission(PermissionNodes.PING_IMAGE_LIST).description("Select or list ping images"));

            addArgument(new PingImageArgument("name", false));
        }

        @Override
        public void handleExecute(CommandContext context) {
            final CommandSender sender = context.getSender();
            final List<String> args = getRelativeArgs(context);

            final PingManager pingManager = FlexPlugin.getGlobalModule(PingManager.class);

            if (args.isEmpty()) {
                // List

                ListBuilder builder = new ListBuilder("subtitle", "Ping Info", "Image", context.getLabel());

                builder.addMessage(StringUtils.collectionToString(pingManager.getImages().keySet(), ChatColor.DARK_GRAY + ", ", new StringConverter<String>() {
                    @Override
                    public String toString(String string) {
                        return (pingManager.getSelectedImage().equals(string) ? ChatColor.GREEN : ChatColor.RED) + string;
                    }
                }, "" + ChatColor.RED + ChatColor.ITALIC + "Nothing here"));

                builder.sendTo(sender, 1);
                return;
            }

            CommandUtils.performPermissionCheck(context, PermissionNodes.PING_IMAGE_SET);

            // Set image
            MessageReference message;

            String name = args.get(0);
            try {
                if (pingManager.setImage(name)) {
                    message = MessageManager.getMessage(FlexMotd.class, "notices.ping_image_set", new ReplacementMap("{NAME}", name).getMap());
                } else {
                    message = MessageManager.getMessage(FlexMotd.class, "errors.ping_image_already_set", new ReplacementMap("{NAME}", name).getMap());
                }
            } catch (IllegalArgumentException ex) {
                message = MessageManager.getMessage(FlexMotd.class, "errors.ping_image_not_found", new ReplacementMap("{NAME}", name).getMap());
            }

            throw new CommandInterruptedException(InterruptReason.COMMAND_END, message);
        }

    }

    // ------------------------------------------------------------------------------------------ //

    static class SCmdMessage extends Subcommand<FlexMotd> {

        public SCmdMessage(AbstractCommand<FlexMotd> parent) {
            super(parent, new CommandDescriptor("message").permission(PermissionNodes.PING_MESSAGE_LIST).description("Select or list ping messages"));

            addArgument(new PingMessageArgument("name", false));
        }

        @Override
        public void handleExecute(CommandContext context) {
            final CommandSender sender = context.getSender();
            final List<String> args = getRelativeArgs(context);

            final PingManager pingManager = FlexPlugin.getGlobalModule(PingManager.class);

            if (args.isEmpty()) {
                // List

                ListBuilder builder = new ListBuilder("subtitle", "Ping Info", "Message", context.getLabel());

                builder.addMessage(StringUtils.collectionToString(pingManager.getMessages().keySet(), ChatColor.DARK_GRAY + ", ", new StringConverter<String>() {
                    @Override
                    public String toString(String string) {
                        return (pingManager.getSelectedMessage().equals(string) ? ChatColor.GREEN : ChatColor.RED) + string;
                    }
                }, "" + ChatColor.RED + ChatColor.ITALIC + "Nothing here"));

                builder.sendTo(sender, 1);
                return;
            }

            CommandUtils.performPermissionCheck(context, PermissionNodes.PING_MESSAGE_SET);

            // Set message
            MessageReference message;

            String name = args.get(0);
            try {
                if (pingManager.setMessage(name)) {
                    message = MessageManager.getMessage(FlexMotd.class, "notices.ping_message_set", new ReplacementMap("{NAME}", name).getMap());
                } else {
                    message = MessageManager.getMessage(FlexMotd.class, "errors.ping_message_already_set", new ReplacementMap("{NAME}", name).getMap());
                }
            } catch (IllegalArgumentException ex) {
                message = MessageManager.getMessage(FlexMotd.class, "errors.ping_message_not_found", new ReplacementMap("{NAME}", name).getMap());
            }

            throw new CommandInterruptedException(InterruptReason.COMMAND_END, message);
        }

    }

}