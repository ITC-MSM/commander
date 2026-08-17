package com.wingedsheep.mtg.sets.definitions.frf.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mardu Strike Leader
 * {2}{B}
 * Creature — Human Warrior
 * 3/2
 * Whenever this creature attacks, create a 2/1 black Warrior creature token.
 * Dash {3}{B} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)
 *
 * Ragavan's dash shape: `dash` is a builder property rather than a keyword constant, and setting it
 * is what adds the `KeywordAbility.Dash` the cast enumerator reads. The printed body is one
 * [Triggers.Attacks] trigger over [Effects.CreateToken]; the token's art comes from the set's
 * token sheet, so no image is spelled here.
 */
val MarduStrikeLeader = card("Mardu Strike Leader") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Warrior"
    power = 3
    toughness = 2
    oracleText = "Whenever this creature attacks, create a 2/1 black Warrior creature token.\n" +
        "Dash {3}{B} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)"

    dash = "{3}{B}"

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.CreateToken(
            power = 2,
            toughness = 1,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Warrior")
        )
        description = "Whenever this creature attacks, create a 2/1 black Warrior creature token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "75"
        artist = "Jason Rainville"
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c93fd5f0-fb62-42cc-8c7d-c0368e191c4b.jpg?1783938696"
    }
}
