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

import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Person;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the trait system.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class TraitSystemTest {

    @Test
    void testAddAndRemoveTrait() {
        Person person = Person.builder("Test Person", Gender.MALE).build();

        // Add a trait
        assertTrue(person.addTrait(TraitType.BRAVE));
        assertTrue(person.hasTrait(TraitType.BRAVE));
        assertEquals(1, person.getTraits().size());

        // Cannot add duplicate
        assertFalse(person.addTrait(TraitType.BRAVE));
        assertEquals(1, person.getTraits().size());

        // Remove trait
        assertTrue(person.removeTrait(TraitType.BRAVE));
        assertFalse(person.hasTrait(TraitType.BRAVE));
        assertEquals(0, person.getTraits().size());
    }

    @Test
    void testPregnancy() {
        Person female = Person.builder("Test Female", Gender.FEMALE).build();
        Person male = Person.builder("Test Male", Gender.MALE).build();

        // Female can get pregnant
        LocalDate conception = LocalDate.now();
        LocalDate dueDate = conception.plusMonths(9);
        assertTrue(female.setPregnant(conception, dueDate));
        assertTrue(female.isPregnant());
        assertTrue(female.hasTrait(TraitType.PREGNANT));

        PersonTrait pregnancy = female.getTrait(TraitType.PREGNANT);
        assertNotNull(pregnancy);
        assertEquals("怀孕", pregnancy.getDisplayName());
        assertTrue(pregnancy.isClearedOnDeath());
        assertEquals(TraitCategory.PHYSICAL_STATE, pregnancy.getCategory());
        assertTrue(pregnancy.getNotes().contains(dueDate.toString()));

        // Cannot get pregnant again while already pregnant
        assertFalse(female.setPregnant(conception));

        // Male cannot get pregnant
        assertFalse(male.setPregnant(conception, dueDate));
        assertFalse(male.isPregnant());
    }

    @Test
    void testTraitsClearedOnDeath() {
        Person person = Person.builder("Test Person", Gender.MALE).build();

        // Add traits - physical state (cleared on death) and character (persisted)
        person.addTrait(TraitType.INJURED);
        person.addTrait(TraitType.BRAVE);
        person.addTrait(TraitType.LOYAL);

        assertEquals(3, person.getTraits().size());
        assertTrue(person.hasTrait(TraitType.INJURED));
        assertTrue(person.hasTrait(TraitType.BRAVE));
        assertTrue(person.hasTrait(TraitType.LOYAL));

        // Clear traits on death
        person.clearTraitsOnDeath();

        // Physical state cleared
        assertFalse(person.hasTrait(TraitType.INJURED));

        // Character traits persist
        assertTrue(person.hasTrait(TraitType.BRAVE));
        assertTrue(person.hasTrait(TraitType.LOYAL));
        assertEquals(2, person.getTraits().size());
    }

    @Test
    void testTraitCategories() {
        Person person = Person.builder("Test Person", Gender.MALE).build();

        person.addTrait(TraitType.SICK); // PHYSICAL_STATE
        person.addTrait(TraitType.INTELLIGENT); // CHARACTER
        person.addTrait(TraitType.CHARISMATIC); // SOCIAL
        person.addTrait(TraitType.WAR_HERO); // HISTORICAL

        assertEquals(4, person.getTraits().size());
        assertEquals(1, person.getTraitsByCategory(TraitCategory.PHYSICAL_STATE).size());
        assertEquals(1, person.getTraitsByCategory(TraitCategory.CHARACTER).size());
        assertEquals(1, person.getTraitsByCategory(TraitCategory.SOCIAL).size());
        assertEquals(1, person.getTraitsByCategory(TraitCategory.HISTORICAL).size());
    }

    @Test
    void testExpiredTraits() {
        Person person = Person.builder("Test Person", Gender.MALE).build();
        LocalDate past = LocalDate.now().minusDays(10);
        LocalDate future = LocalDate.now().plusDays(10);

        // Add trait with expiry in the past
        PersonTrait expiredTrait = new PersonTrait(TraitType.INJURED, past, past.plusDays(5), 1.0);
        person.addTrait(expiredTrait);

        // Add trait with expiry in the future
        PersonTrait activeTrait = new PersonTrait(TraitType.SICK, past, future, 1.0);
        person.addTrait(activeTrait);

        assertEquals(2, person.getTraits().size());

        // Clear expired traits
        person.clearExpiredTraits(LocalDate.now());

        // Expired trait should be gone
        assertEquals(1, person.getTraits().size());
        assertFalse(person.hasTrait(TraitType.INJURED));
        assertTrue(person.hasTrait(TraitType.SICK));
    }

    @Test
    void testTraitIntensity() {
        Person person = Person.builder("Test Person", Gender.MALE).build();

        PersonTrait trait = new PersonTrait(TraitType.INJURED, LocalDate.now(), 0.75);
        person.addTrait(trait);

        PersonTrait retrieved = person.getTrait(TraitType.INJURED);
        assertNotNull(retrieved);
        assertEquals(0.75, retrieved.getIntensity(), 0.001);
    }
}
