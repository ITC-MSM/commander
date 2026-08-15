package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vote Out
 * {3}{B}
 * Sorcery
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Destroy target creature.
 */
val VoteOut = card("Vote Out") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\nDestroy target creature."

    keywords(Keyword.CONVOKE)

    spell {
        val target = target("target creature", Targets.Creature)
        effect = Effects.Destroy(target)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "126"
        artist = "David Álvarez"
        flavorText = "Democracy is not always just."
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f50bb47-cc4d-4b81-b5f1-817ca8744d12.jpg?1752947062"
    }
}
