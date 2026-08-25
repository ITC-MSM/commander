package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Viashino Bladescout
 * {1}{R}{R}
 * Creature — Lizard Scout
 * 2 / 1
 *
 * Flash (You may cast this spell any time you could cast an instant.)
 * When this creature enters, target creature gains first strike until end of turn.
 */
val ViashinoBladescout = card("Viashino Bladescout") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Lizard Scout"
    power = 2
    toughness = 1
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "When this creature enters, target creature gains first strike until end of turn."

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "185"
        artist = "Dany Orizio"
        flavorText = "\"Find your courage in a desperate moment, and you turn the tide of history. So sayeth the bey.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/1/41fce1b3-0961-4672-845f-e1c6ce101c1b.jpg"
    }
}
