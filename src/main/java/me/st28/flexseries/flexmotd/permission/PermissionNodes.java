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
package me.st28.flexseries.flexmotd.permission;

import me.st28.flexseries.flexcore.permission.PermissionNode;
import org.bukkit.permissions.Permissible;

public enum PermissionNodes implements PermissionNode {

    PING_GROUP_LIST,
    PING_GROUP_SET,

    PING_IMAGE_LIST,
    PING_IMAGE_SET,

    PING_MESSAGE_LIST,
    PING_MESSAGE_SET,

    MOTD,
    MOTD_LIST,
    MOTD_SET;

    private String node;

    PermissionNodes() {
        node = "flexmotd." + toString().toLowerCase().replace("_", ".");
    }

    @Override
    public String getNode() {
        return node;
    }

    @Override
    public boolean isAllowed(Permissible permissible) {
        return permissible.hasPermission(node);
    }

}