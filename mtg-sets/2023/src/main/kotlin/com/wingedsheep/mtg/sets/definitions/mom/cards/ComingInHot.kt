package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Coming In Hot
 * {R}
 * Instant
 * Target creature gets +1/+0 and gains first strike until end of turn. Scry 1.
 */
val ComingInHot = card("Coming In Hot") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+0 and gains first strike until end of turn. Scry 1. " +
        "(Look at the top card of your library. You may put that card on the bottom.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(1, 0, creature) then
            Effects.GrantKeyword(Keyword.FIRST_STRIKE, creature) then
            Patterns.Library.scry(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "136"
        artist = "Cristi Balanescu"
        flavorText = "\"I have a reckless idea,\" Koth offered. Chandra grinned and cracked her knuckles."
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f6eb966-67af-4b18-8899-4a99a5179aa3.jpg?1783916993"
    }
}
