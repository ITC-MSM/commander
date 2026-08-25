package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Swashbuckling
 * {1}{R}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +2/+2 and has haste.
 */
val Swashbuckling = card("Swashbuckling") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +2/+2 and has haste."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.HASTE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "167"
        artist = "Josu Hernaiz"
        flavorText = "The pirates of the Brazen Coalition are the descendants of those displaced by the Legion of Dusk, and they are eager for vengeance."
        imageUri = "https://cards.scryfall.io/normal/front/0/1/01ed59b1-968b-4297-9e98-42d940f9478c.jpg"
    }
}
