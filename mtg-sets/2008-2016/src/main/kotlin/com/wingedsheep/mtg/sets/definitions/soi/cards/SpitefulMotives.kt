package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Spiteful Motives (Shadows over Innistrad #183)
 * {3}{R}
 * Enchantment — Aura
 *
 * Flash (You may cast this spell any time you could cast an instant.)
 * Enchant creature
 * Enchanted creature gets +3/+0 and has first strike.
 */
val SpitefulMotives = card("Spiteful Motives") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "Enchant creature\n" +
        "Enchanted creature gets +3/+0 and has first strike."

    keywords(Keyword.FLASH)

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(3, 0)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "183"
        artist = "Marco Nelor"
        flavorText = "Having infiltrated the Lunarch Council, the Skirsdag await the perfect moment to strike."
        imageUri = "https://cards.scryfall.io/normal/front/5/9/592d7349-d066-49b0-9920-c1ec1595e00d.jpg?1783937741"
    }
}
