package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Mindmoil
 * {4}{R}
 * Enchantment
 * Whenever you cast a spell, put the cards in your hand on the bottom of your library in any
 * order, then draw that many cards.
 *
 * A Gather → Move → Draw pipeline: the whole hand is gathered into one collection, moved to the
 * bottom of the library with [CardOrder.ControllerChooses] for the printed "in any order", and
 * the draw counts that same collection. Counting the collection rather than the hand is what
 * makes "that many" the hand size *before* the shuffle-back — the count is read by entity id, so
 * it survives the cards having already left the hand.
 *
 * The spell that triggered this is on the stack, not in hand, so it is never bottomed — and the
 * trigger resolves before the spell does.
 */
val Mindmoil = card("Mindmoil") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "Whenever you cast a spell, put the cards in your hand on the bottom of your " +
        "library in any order, then draw that many cards."

    triggeredAbility {
        trigger = Triggers.YouCastSpell
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.HAND, Player.You),
                    storeAs = "hand"
                ),
                MoveCollectionEffect(
                    from = "hand",
                    destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
                    order = CardOrder.ControllerChooses
                ),
                Effects.DrawCards(DynamicAmount.DistinctEntitiesInCollections(listOf("hand")))
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "135"
        artist = "Alex Horley-Orlandelli"
        flavorText = "\"My criticism of the Izzet is that their impulse for learning seems too " +
            "much like impulse and too little like learning.\"\n—Trigori, Azorius senator"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0ccc79f7-2f35-4daf-a0c2-775f5fa6c249.jpg"
    }
}
