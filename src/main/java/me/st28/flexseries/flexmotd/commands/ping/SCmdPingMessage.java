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
package me.st28.flexseries.flexmotd.commands.ping;

import me.st28.flexseries.flexcore.command.*;
import me.st28.flexseries.flexcore.command.exceptions.CommandInterruptedException;
import me.st28.flexseries.flexcore.list.ListBuilder;
import me.st28.flexseries.flexcore.message.MessageReference;
import me.st28.flexseries.flexcore.message.ReplacementMap;
import me.st28.flexseries.flexcore.plugin.FlexPlugin;
import me.st28.flexseries.flexcore.util.StringConverter;
import me.st28.flexseries.flexcore.util.StringUtils;
import me.st28.flexseries.flexmotd.FlexMotd;
import me.st28.flexseries.flexmotd.backend.PingManager;
import me.st28.flexseries.flexmotd.permission.PermissionNodes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class SCmdPingMessage extends FlexSubcommand<FlexMotd> implements FlexTabCompleter {

    public SCmdPingMessage(FlexCommand<FlexMotd> parent) {
        super(parent, "message", Collections.singletonList(new CommandArgument("name", false)), new FlexCommandSettings().permission(PermissionNodes.PING_MESSAGE_LIST).description("Select or list ping messages"));
    }

    @Override
    public void runCommand(CommandSender sender, String command, String label, String[] args, Map<String, String> parameters) {
        final PingManager pingManager = FlexPlugin.getRegisteredModule(PingManager.class);

        if (args.length == 0) {
            // List

            ListBuilder builder = new ListBuilder("subtitle", "Ping Info", "Message", label);

            builder.addMessage(StringUtils.collectionToString(pingManager.getMessages().keySet(), new StringConverter<String>() {
                @Override
                public String toString(String string) {
                    return (pingManager.getSelectedMessage().equals(string) ? ChatColor.GREEN : ChatColor.RED) + string;
                }
            }, ChatColor.DARK_GRAY + ", ", "" + ChatColor.RED + ChatColor.ITALIC + "Nothing here"));

            builder.sendTo(sender, 1);
            return;
        }

        CommandUtils.performPermissionTest(sender, PermissionNodes.PING_MESSAGE_SET);

        // Set message
        String name = args[0];
        try {
            if (pingManager.setMessage(name)) {
                MessageReference.create(FlexMotd.class, "notices.ping_message_set", new ReplacementMap("{NAME}", name).getMap()).sendTo(sender);
            } else {
                MessageReference.create(FlexMotd.class, "errors.ping_message_already_set", new ReplacementMap("{NAME}", name).getMap()).sendTo(sender);
            }
        } catch (IllegalArgumentException ex) {
            throw new CommandInterruptedException(MessageReference.create(FlexMotd.class, "errors.ping_message_not_found", new ReplacementMap("{NAME}", name).getMap()));
        }
    }

    @Override
    public List<String> getTabOptions(CommandSender sender, String[] args) {
        return new ArrayList<>(FlexPlugin.getRegisteredModule(PingManager.class).getMessages().keySet());
    }

}