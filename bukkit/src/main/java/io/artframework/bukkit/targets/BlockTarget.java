/*
 *  Copyright 2020 ART-Framework Contributors (https://github.com/art-framework/)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.artframework.bukkit.targets;

import io.artframework.AbstractTarget;
import org.bukkit.block.Block;

public class BlockTarget extends AbstractTarget<Block> {

    public BlockTarget(Block source) {

        super(source);
    }

    @Override
    public String uniqueId() {
        return "BLOCK{" +
                "x=" + source().getLocation().getBlockX() + ";" +
                "y=" + source().getLocation().getBlockY() + ";" +
                "z=" + source().getLocation().getBlockZ() + ";" +
                "world=" + source().getWorld().getUID().toString()
                + "}";
    }
}
