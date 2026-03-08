/**
 * Copyright (C) 2020 XenoAmess
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xenoamess.cyan_potion.civilization.character.trait;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Categories of traits.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public enum TraitCategory {
    PHYSICAL_STATE("身体状态", "人物当前的生理状态"),
    CHARACTER("性格特质", "人物的性格特点"),
    SOCIAL("社交特质", "人物在社交方面的特点"),
    HISTORICAL("历史特质", "人物的历史行为和成就");

    @Getter
    private final String chineseName;

    @Getter
    private final String description;

    TraitCategory(String chineseName, String description) {
        this.chineseName = chineseName;
        this.description = description;
    }
}
