package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Momentary Blink
 * {1}{W}
 * Instant
 * Exile target creature you control, then return it to the battlefield under its owner's control.
 * Flashback {3}{U}
 */
val MomentaryBlink = card("Momentary Blink") {
    manaCost = "{1}{W}"
    colorIdentity = "WU"
    typeLine = "Instant"
    oracleText =
        "Exile target creature you control, then return it to the battlefield under its owner's control.\n" +
            "Flashback {3}{U} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.Exile(creature).then(Effects.Move(creature, Zone.BATTLEFIELD))
    }

    keywordAbility(KeywordAbility.flashback("{3}{U}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Anthony S. Waters"
        imageUri =
            "https://cards.scryfall.io/normal/front/0/3/032e072a-0630-472b-9106-5df554dff785.jpg?1783943252"
    }
}
