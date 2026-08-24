package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Cooperation
 * {2}{W}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has banding.
 *
 * The plain keyword-granting Aura shape: `auraTarget = Targets.Creature` carries the enchant
 * restriction, and `GrantKeyword` needs no filter because its default is
 * `GroupFilter.attachedCreature()` — exactly "enchanted creature". Banding is engine-live, so the
 * grant is real combat behaviour rather than a display keyword.
 */
val Cooperation = card("Cooperation") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has banding. (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding a player controls are blocking or being blocked by a creature, that player divides that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)"

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(Keyword.BANDING)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Phil Foglio"
        flavorText = "\"The Elves train our healers, and we keep the Orcs at bay. Most Elvish bargains aren't as fair.\"\n—General Jarkeld, the Arctic Fox"
        imageUri = "https://cards.scryfall.io/normal/front/2/1/21a815ed-c8b4-4414-8b27-ea612e2977e2.jpg"
    }
}
