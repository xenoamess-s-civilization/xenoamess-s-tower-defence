package com.xenoamess.cyan_potion.civilization.character;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChildrenUpdateTest {
    
    @Test
    void testChildrenUpdatedWhenCreatingChildWithBuilder() {
        Person father = Person.builder("f1", "Father", Gender.MALE).build();
        Person mother = Person.builder("m1", "Mother", Gender.FEMALE).build();
        
        System.out.println("Before creating child:");
        System.out.println("Father children count: " + father.getChildrenCount());
        System.out.println("Mother children count: " + mother.getChildrenCount());
        
        Person child = Person.builder("c1", "Child", Gender.MALE)
            .parents(father, mother)
            .build();
        
        System.out.println("After creating child:");
        System.out.println("Father children count: " + father.getChildrenCount());
        System.out.println("Mother children count: " + mother.getChildrenCount());
        System.out.println("Child's father: " + child.getFather());
        System.out.println("Child's mother: " + child.getMother());
        System.out.println("Father's children contains child: " + father.getChildren().contains(child));
        System.out.println("Mother's children contains child: " + mother.getChildren().contains(child));
        
        assertTrue(father.getChildren().contains(child), "Father should have child");
        assertTrue(mother.getChildren().contains(child), "Mother should have child");
    }
    
    @Test
    void testChildrenUpdatedWithAddChild() {
        Person father = Person.builder("f2", "Father", Gender.MALE).build();
        Person child = Person.builder("c2", "Child", Gender.MALE).build();
        
        // Direct add should fail since child doesn't have father set
        boolean result = father.addChild(child);
        assertFalse(result, "Should not add child without parent reference");
        
        // Create child properly
        Person child2 = Person.builder("c3", "Child2", Gender.MALE)
            .father(father)
            .build();
        
        assertTrue(father.getChildren().contains(child2), "Father should have child2");
    }
}
