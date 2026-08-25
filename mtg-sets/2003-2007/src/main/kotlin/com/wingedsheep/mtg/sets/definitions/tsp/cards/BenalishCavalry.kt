package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Benalish Cavalry
 * {1}{W}
 * Creature — Human Knight
 * 2/2
 * Flanking (Whenever a creature without flanking blocks this creature, the blocking creature
 * gets -1/-1 until end of turn.)
 *
 * The keyword is the whole card: the engine derives flanking's triggered ability from the
 * projected [Keyword.FLANKING] (`TriggerAbilityResolver.getFlankingTriggeredAbilities`), the same
 * way it derives ward and suspend, so the -1/-1 trigger is never authored per card.
 */
val BenalishCavalry = card("Benalish Cavalry") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 2
    oracleText = "Flanking (Whenever a creature without flanking blocks this creature, the blocking creature gets -1/-1 until end of turn.)"

    keywords(Keyword.FLANKING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "Paolo Parente"
        flavorText = "\"My people swore to protect Benalia to the end. It is battered, but yet stands, as do we.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/0/1013ca9c-1d29-42f6-8665-92f98d076ff8.jpg"
    }
}
