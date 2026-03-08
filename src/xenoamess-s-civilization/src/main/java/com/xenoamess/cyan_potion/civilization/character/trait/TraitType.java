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

/**
 * Enum representing different types of traits a person can have.
 * Traits can be characteristics, states, past behaviors, or future behavior patterns.
 * Some traits are cleared on death, while others persist.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public enum TraitType {

    // ==================== Physical States (cleared on death) ====================

    /**
     * Pregnant - the person is carrying a child.
     * Cleared on death or after giving birth.
     */
    PREGNANT(
        "pregnant",
        "怀孕",
        "此人物正在怀孕中。",
        true,
        TraitCategory.PHYSICAL_STATE
    ),

    /**
     * Injured - the person has physical injuries.
     * Cleared on death or when healed.
     */
    INJURED(
        "injured",
        "受伤",
        "此人物身上有伤。",
        true,
        TraitCategory.PHYSICAL_STATE
    ),

    /**
     * Sick - the person is ill.
     * Cleared on death or when recovered.
     */
    SICK(
        "sick",
        "生病",
        "此人物正在生病。",
        true,
        TraitCategory.PHYSICAL_STATE
    ),

    // ==================== Character Traits (persist after death) ====================

    /**
     * Brave - the person shows courage in difficult situations.
     * Persists after death.
     */
    BRAVE(
        "brave",
        "勇敢",
        "此人物在面对危险时表现出非凡的勇气。",
        false,
        TraitCategory.CHARACTER
    ),

    /**
     * Coward - the person tends to avoid danger.
     * Persists after death.
     */
    COWARD(
        "coward",
        "怯懦",
        "此人物倾向于逃避危险。",
        false,
        TraitCategory.CHARACTER
    ),

    /**
     * Intelligent - the person has exceptional mental capabilities.
     * Persists after death.
     */
    INTELLIGENT(
        "intelligent",
        "聪慧",
        "此人物拥有出众的智力。",
        false,
        TraitCategory.CHARACTER
    ),

    /**
     * Stubborn - the person is resistant to changing their mind.
     * Persists after death.
     */
    STUBBORN(
        "stubborn",
        "固执",
        "此人物很难改变自己的想法。",
        false,
        TraitCategory.CHARACTER
    ),

    // ==================== Social Traits (persist after death) ====================

    /**
     * Charismatic - the person naturally attracts others.
     * Persists after death.
     */
    CHARISMATIC(
        "charismatic",
        "有魅力",
        "此人物天生具有吸引他人的气质。",
        false,
        TraitCategory.SOCIAL
    ),

    /**
     * Loyal - the person is faithful to their commitments.
     * Persists after death.
     */
    LOYAL(
        "loyal",
        "忠诚",
        "此人物对承诺保持忠诚。",
        false,
        TraitCategory.SOCIAL
    ),

    /**
     * Treacherous - the person is prone to betrayal.
     * Persists after death.
     */
    TREACHEROUS(
        "treacherous",
        "背信弃义",
        "此人物有背叛他人的倾向。",
        false,
        TraitCategory.SOCIAL
    ),

    // ==================== Historical Traits (persist after death) ====================

    /**
     * War Hero - the person distinguished themselves in battle.
     * Persists after death.
     */
    WAR_HERO(
        "war_hero",
        "战争英雄",
        "此人物在战场上建立了卓越的功勋。",
        false,
        TraitCategory.HISTORICAL
    ),

    /**
     * Traitor - the person betrayed their faction.
     * Persists after death.
     */
    TRAITOR(
        "traitor",
        "叛徒",
        "此人物曾经背叛过自己的阵营。",
        false,
        TraitCategory.HISTORICAL
    ),

    /**
     * Founder - the person founded something significant.
     * Persists after death.
     */
    FOUNDER(
        "founder",
        "创立者",
        "此人物是某项重要事业或组织的创立者。",
        false,
        TraitCategory.HISTORICAL
    );

    @Getter
    private final String id;

    @Getter
    private final String chineseName;

    @Getter
    private final String description;

    /**
     * Whether this trait is cleared when the person dies.
     * Physical states are typically cleared, while character and historical traits persist.
     */
    @Getter
    private final boolean clearedOnDeath;

    @Getter
    private final TraitCategory category;

    TraitType(String id, String chineseName, String description, boolean clearedOnDeath, TraitCategory category) {
        this.id = id;
        this.chineseName = chineseName;
        this.description = description;
        this.clearedOnDeath = clearedOnDeath;
        this.category = category;
    }

    /**
     * Gets the display name for this trait.
     *
     * @return the Chinese name
     */
    public String getDisplayName() {
        return chineseName;
    }

    /**
     * Finds a trait type by its ID.
     *
     * @param id the trait ID
     * @return the trait type, or null if not found
     */
    public static TraitType fromId(String id) {
        for (TraitType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Gets all trait types in a specific category.
     *
     * @param category the category to filter by
     * @return array of trait types in that category
     */
    public static TraitType[] byCategory(TraitCategory category) {
        return java.util.Arrays.stream(values())
            .filter(t -> t.category == category)
            .toArray(TraitType[]::new);
    }
}
