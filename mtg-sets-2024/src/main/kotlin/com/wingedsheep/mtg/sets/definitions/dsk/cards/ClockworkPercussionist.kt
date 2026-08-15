package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Clockwork Percussionist
 * {R}
 * Artifact Creature — Monkey Toy
 * 1/1
 * Haste
 * When this creature dies, exile the top card of your library. You may play it until the
 * end of your next turn.
 */
val ClockworkPercussionist = card("Clockwork Percussionist") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Monkey Toy"
    power = 1
    toughness = 1
    oracleText = "Haste\n" +
        "When this creature dies, exile the top card of your library. You may play it until the end of your next turn."

    keywords(Keyword.HASTE)

    // Impulse-exile on death: exile the top card and grant permission to play it until the
    // end of your next turn (GatherCards(top 1) -> MoveCollection(EXILE) -> GrantMayPlayFromExile).
    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1)),
                storeAs = "impulseExiled"
            ),
            MoveCollectionEffect(
                from = "impulseExiled",
                destination = CardDestination.ToZone(Zone.EXILE)
            ),
            GrantMayPlayFromExileEffect("impulseExiled", MayPlayExpiry.UntilEndOfNextTurn)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "130"
        artist = "Eric Wilkerson"
        flavorText = "Dance to its beat, or it will dance on your grave."
        imageUri = "https://cards.scryfall.io/normal/front/1/0/10986e5a-9fc6-41e2-8352-289328245171.jpg?1726286332"
    }
}
