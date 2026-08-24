package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Oakenform
 * {2}{G}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +3/+3.
 */
val Oakenform = card("Oakenform") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +3/+3."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(3, 3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "197"
        artist = "Wayne Reynolds"
        flavorText = "\"When the beast cloaks itself in the mighty oak, what good is a bow? When the oak wraps itself around the snarling beast, what good is a hatchet?\"\n" +
            "—Dionus, elvish archdruid"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42df372a-af2b-464a-b54c-039132f70d00.jpg"
    }
}
