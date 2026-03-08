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
import lombok.ToString;

import java.time.LocalDate;

/**
 * Represents a trait instance on a person.
 * Contains the trait type and additional metadata like acquisition date and intensity.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Getter
@Setter
@ToString
public class PersonTrait {

    /**
     * The type of this trait.
     */
    private final TraitType type;

    /**
     * The date when this trait was acquired.
     */
    private final LocalDate acquiredDate;

    /**
     * The date when this trait expires (if applicable).
     * Null means the trait does not expire naturally.
     */
    private LocalDate expiryDate;

    /**
     * Intensity/strength of the trait (0.0 - 1.0).
     * Default is 1.0 (full strength).
     */
    private double intensity;

    /**
     * Additional notes or context about this trait instance.
     * Can be used to store specific details (e.g., "injured by wolf attack").
     */
    private String notes;

    /**
     * Creates a new trait with default intensity (1.0).
     *
     * @param type the trait type
     */
    public PersonTrait(TraitType type) {
        this(type, LocalDate.now(), 1.0);
    }

    /**
     * Creates a new trait with specified acquisition date and intensity.
     *
     * @param type the trait type
     * @param acquiredDate when the trait was acquired
     * @param intensity the intensity (0.0 - 1.0)
     */
    public PersonTrait(TraitType type, LocalDate acquiredDate, double intensity) {
        this.type = type;
        this.acquiredDate = acquiredDate;
        this.intensity = Math.max(0.0, Math.min(1.0, intensity));
    }

    /**
     * Creates a new trait with an expiry date.
     *
     * @param type the trait type
     * @param acquiredDate when the trait was acquired
     * @param expiryDate when the trait expires
     * @param intensity the intensity (0.0 - 1.0)
     */
    public PersonTrait(TraitType type, LocalDate acquiredDate, LocalDate expiryDate, double intensity) {
        this(type, acquiredDate, intensity);
        this.expiryDate = expiryDate;
    }

    /**
     * Checks if this trait has expired as of the given date.
     *
     * @param currentDate the current date to check against
     * @return true if the trait has expired
     */
    public boolean isExpired(LocalDate currentDate) {
        return expiryDate != null && !currentDate.isBefore(expiryDate);
    }

    /**
     * Checks if this trait is cleared when the person dies.
     *
     * @return true if cleared on death
     */
    public boolean isClearedOnDeath() {
        return type.isClearedOnDeath();
    }

    /**
     * Gets the display name of this trait.
     *
     * @return the Chinese name
     */
    public String getDisplayName() {
        return type.getDisplayName();
    }

    /**
     * Gets the description of this trait.
     *
     * @return the description
     */
    public String getDescription() {
        return type.getDescription();
    }

    /**
     * Gets the category of this trait.
     *
     * @return the trait category
     */
    public TraitCategory getCategory() {
        return type.getCategory();
    }

    /**
     * Factory method to create a pregnancy trait.
     *
     * @param conceptionDate the date of conception
     * @param dueDate the expected due date
     * @return a new pregnancy trait
     */
    public static PersonTrait pregnant(LocalDate conceptionDate, LocalDate dueDate) {
        PersonTrait trait = new PersonTrait(TraitType.PREGNANT, conceptionDate, dueDate, 1.0);
        trait.setNotes("预产期: " + dueDate);
        return trait;
    }

    /**
     * Factory method to create an injured trait.
     *
     * @param injuryDate when the injury occurred
     * @param severity injury severity (0.0 - 1.0)
     * @param cause description of what caused the injury
     * @return a new injured trait
     */
    public static PersonTrait injured(LocalDate injuryDate, double severity, String cause) {
        PersonTrait trait = new PersonTrait(TraitType.INJURED, injuryDate, severity);
        trait.setNotes(cause);
        return trait;
    }
}
