package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Vivien's Arkbow — War of the Spark #181 (canonical printing)
 * {1}{G}
 * Legendary Artifact
 * {X}, {T}, Discard a card: Look at the top X cards of your library. You may put a creature card
 * with mana value X or less from among them onto the battlefield. Put the rest on the bottom of
 * your library in a random order.
 *
 * X appears twice and means the same thing both times: [DynamicAmount.XValue] sets how many
 * cards are looked at, and `manaValueAtMostX()` caps what may be taken. Both read the X paid for
 * the ability, so the cost and the filter stay in step without the card restating the number.
 * The rest going back "in a random order" is [Patterns.Library.lookAtTopAndTakeMatching]'s own
 * default; only the battlefield destination differs from the recipe's defaults.
 */
val ViviensArkbow = card("Vivien's Arkbow") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Artifact"
    oracleText = "{X}, {T}, Discard a card: Look at the top X cards of your library. You may put " +
        "a creature card with mana value X or less from among them onto the battlefield. Put the " +
        "rest on the bottom of your library in a random order."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{X}"), Costs.Tap, Costs.DiscardCard)
        effect = Patterns.Library.lookAtTopAndTakeMatching(
            count = DynamicAmount.XValue,
            filter = GameObjectFilter.Creature.manaValueAtMostX(),
            prompt = "You may put a creature card with mana value X or less from among them onto the battlefield",
            keepDestination = CardDestination.ToZone(Zone.BATTLEFIELD)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "181"
        artist = "Zack Stella"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/eecc846f-5b78-4d54-8d29-642ad7a852bc.jpg"
    }
}
