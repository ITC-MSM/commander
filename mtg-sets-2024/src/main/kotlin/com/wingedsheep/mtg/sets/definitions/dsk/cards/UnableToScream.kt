package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeTurnedFaceUp
import com.wingedsheep.sdk.scripting.GrantCardType
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.LoseAllAbilities
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessStatic
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Unable to Scream
 * {U}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature loses all abilities and is a Toy artifact creature with base power and
 * toughness 0/2 in addition to its other types.
 * As long as enchanted creature is face down, it can't be turned face up.
 *
 * Modeled as a stack of statics on the attached creature:
 *  - [LoseAllAbilities] (Layer 6)
 *  - "Toy artifact creature in addition to its other types": [GrantCardType] ARTIFACT +
 *    [GrantSubtype] Toy (Layer 4; it's already a creature, so no need to add CREATURE)
 *  - [SetBasePowerToughnessStatic] 0/2 (Layer 7b)
 *  - [CantBeTurnedFaceUp] — only meaningful while face down (a face-up permanent can't be
 *    "turned face up"), so applied unconditionally; the turn-face-up special action reads it.
 */
val UnableToScream = card("Unable to Scream") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature loses all abilities and is a Toy artifact " +
        "creature with base power and toughness 0/2 in addition to its other types.\nAs long as " +
        "enchanted creature is face down, it can't be turned face up."

    auraTarget = Targets.Creature

    staticAbility {
        ability = LoseAllAbilities()
    }

    staticAbility {
        ability = GrantCardType("ARTIFACT", filter = GroupFilter.attachedCreature())
    }

    staticAbility {
        ability = GrantSubtype("Toy", filter = GroupFilter.attachedCreature())
    }

    staticAbility {
        ability = SetBasePowerToughnessStatic(0, 2)
    }

    staticAbility {
        ability = CantBeTurnedFaceUp()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "78"
        artist = "Fariba Khamseh"
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c59e0cd-10a8-4a32-9c0a-a2c6ef1ed9a6.jpg?1726286143"
    }
}
