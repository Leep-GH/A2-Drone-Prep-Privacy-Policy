package com.appquests.droneprep.data.model

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for Topic enum functionality.
 */
class TopicTest {
    
    @Test
    fun `test fromDisplayName returns correct topic`() {
        assertEquals(Topic.AIR_LAW, Topic.fromDisplayName("Air Law"))
        assertEquals(Topic.FLIGHT_OPERATIONS, Topic.fromDisplayName("Flight Operations"))
        assertEquals(Topic.HUMAN_PERFORMANCE, Topic.fromDisplayName("Human Performance"))
        assertEquals(Topic.WEATHER, Topic.fromDisplayName("Weather"))
        assertEquals(Topic.SAFETY_EMERGENCIES, Topic.fromDisplayName("Safety & Emergencies"))
    }
    
    @Test
    fun `test fromDisplayName returns null for invalid name`() {
        assertNull(Topic.fromDisplayName("Invalid Topic"))
        assertNull(Topic.fromDisplayName(""))
        assertNull(Topic.fromDisplayName("air law")) // Case sensitive
    }
    
    @Test
    fun `test fromKey returns correct topic`() {
        assertEquals(Topic.AIR_LAW, Topic.fromKey("airlaw"))
        assertEquals(Topic.FLIGHT_OPERATIONS, Topic.fromKey("operations"))
        assertEquals(Topic.HUMAN_PERFORMANCE, Topic.fromKey("human"))
        assertEquals(Topic.WEATHER, Topic.fromKey("weather"))
        assertEquals(Topic.SAFETY_EMERGENCIES, Topic.fromKey("safety"))
    }
    
    @Test
    fun `test fromKey returns null for invalid key`() {
        assertNull(Topic.fromKey("invalid"))
        assertNull(Topic.fromKey(""))
        assertNull(Topic.fromKey("AIRLAW")) // Case sensitive
    }
    
    @Test
    fun `test all topics have unique keys`() {
        val keys = Topic.values().map { it.key }
        val uniqueKeys = keys.toSet()
        assertEquals(keys.size, uniqueKeys.size)
    }
    
    @Test
    fun `test all topics have unique display names`() {
        val displayNames = Topic.values().map { it.displayName }
        val uniqueDisplayNames = displayNames.toSet()
        assertEquals(displayNames.size, uniqueDisplayNames.size)
    }
    
    @Test
    fun `test all topics have icons assigned`() {
        Topic.values().forEach { topic ->
            assertNotNull("Topic ${topic.displayName} should have an icon", topic.icon)
        }
    }
    
    @Test
    fun `test topic properties are not empty`() {
        Topic.values().forEach { topic ->
            assertTrue("Display name should not be empty", topic.displayName.isNotEmpty())
            assertTrue("Key should not be empty", topic.key.isNotEmpty())
        }
    }
}
