package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Stinging Shot
 * {G}
 * Instant
 * Put three -1/-1 counters on target creature with flying.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val StingingShot = card("Stinging Shot") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Put three -1/-1 counters on target creature with flying.\n" +
            "Cycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        val flier = target("target", Targets.CreatureWithKeyword(Keyword.FLYING))
        effect = Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 3, flier)
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "189"
        artist = "Scott Murphy"
        flavorText = "Initiates must train to resist the natural toxins they use as weapons."
        imageUri = "https://cards.scryfall.io/normal/front/5/4/547c4c26-1118-41fa-aec8-1b43a7792e59.jpg?1783936466"
    }
}
