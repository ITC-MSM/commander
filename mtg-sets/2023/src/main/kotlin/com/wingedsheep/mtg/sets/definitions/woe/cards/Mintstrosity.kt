package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mintstrosity
 * {1}{B}
 * Creature — Horror
 * 3/1
 *
 * When this creature dies, create a Food token.
 *
 * [Triggers.Dies] is the self battlefield→graveyard trigger (CR 700.4); its controller creates a
 * Food token via [Effects.CreateFood].
 */
val Mintstrosity = card("Mintstrosity") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror"
    power = 3
    toughness = 1
    oracleText = "When this creature dies, create a Food token. (It's an artifact with " +
        "\"{2}, {T}, Sacrifice this token: You gain 3 life.\")"

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateFood()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "100"
        artist = "Slawomir Maniak"
        flavorText = "If the sugary abominations of Sweettooth Village were ever under someone's control, " +
            "that time is long past."
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d902f154-6fe8-4b97-aa67-4d4696abf887.jpg?1783915104"
    }
}
