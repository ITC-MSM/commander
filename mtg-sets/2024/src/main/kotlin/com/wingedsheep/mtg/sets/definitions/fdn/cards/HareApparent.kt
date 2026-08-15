package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Hare Apparent
 * {1}{W}
 * Creature — Rabbit Noble
 * 2/2
 * When this creature enters, create a number of 1/1 white Rabbit creature tokens equal to
 * the number of other creatures you control named Hare Apparent.
 * A deck can have any number of cards named Hare Apparent.
 *
 * The "any number in a deck" clause is a deck-construction rule (CR 100.2a exception); like
 * Rat Colony it carries no in-game behavior, so it lives only in the oracle text.
 */
val HareApparent = card("Hare Apparent") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Rabbit Noble"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, create a number of 1/1 white Rabbit creature tokens equal to the number of other creatures you control named Hare Apparent.\nA deck can have any number of cards named Hare Apparent."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            count = DynamicAmount.AggregateBattlefield(
                Player.You,
                GameObjectFilter.Creature.named("Hare Apparent"),
                excludeSelf = true
            ),
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Rabbit"),
            imageUri = "https://cards.scryfall.io/normal/front/8/1/81de52ef-7515-4958-abea-fb8ebdcef93c.jpg?1721431122"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Milivoj Ćeran"
        flavorText = "Most families have trees. His has an entire forest."
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9fc6f0e9-eb5f-4bc0-b3d7-756644b66d12.jpg?1782689251"
    }
}
