package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Germinating Wurm
 * {4}{G}
 * Creature — Plant Wurm
 * When this creature enters, you gain 2 life.
 * Warp {1}{G} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)
 * 5/5
 */
val GerminatingWurm = card("Germinating Wurm") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Wurm"
    oracleText = "When this creature enters, you gain 2 life.\n" +
        "Warp {1}{G} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)"
    power = 5
    toughness = 5

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2)
    }

    warp = "{1}{G}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "187"
        artist = "Monztre"
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fcde173a-6314-4904-bddd-68b2ab1e4867.jpg?1752947317"
    }
}
