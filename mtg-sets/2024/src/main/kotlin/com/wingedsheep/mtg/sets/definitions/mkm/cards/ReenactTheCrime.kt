package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Reenact the Crime — Murders at Karlov Manor #70
 * {1}{U}{U}{U} · Instant
 *
 * Exile target nonland card in a graveyard that was put there from anywhere this turn. Copy it.
 * You may cast the copy without paying its mana cost.
 *
 * The graveyard-history predicate deliberately accepts a card put there from any zone, not merely
 * one that died. The resolution then uses the shared exile → copy → optional free-cast pipeline.
 * The cast happens during this spell's resolution; declining leaves a phantom copy that the
 * state-based-action check removes.
 */
val ReenactTheCrime = card("Reenact the Crime") {
    manaCost = "{1}{U}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Exile target nonland card in a graveyard that was put there from anywhere this " +
        "turn. Copy it. You may cast the copy without paying its mana cost."

    spell {
        val reenacted = target(
            "target nonland card in a graveyard that was put there from anywhere this turn",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Nonland.putIntoGraveyardThisTurn(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.Composite(
            Effects.Move(reenacted, Zone.EXILE, fromZone = Zone.GRAVEYARD),
            Effects.CopyCardIntoCollection(reenacted, storeAs = "copy"),
            MayEffect(
                Effects.CastFromCollectionWithoutPayingCost("copy"),
                descriptionOverride = "You may cast the copy without paying its mana cost."
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "70"
        artist = "Daarken"
        flavorText = "At Proft's command, an ethereal model of the ransacked workshop sprang " +
            "into being, every detail perfectly recreated. He'd missed something—he knew it—but what?"
        imageUri = "https://cards.scryfall.io/normal/front/d/9/" +
            "d942e4ce-f582-4264-89aa-9b4a743e6b29.jpg?1783912905"

        ruling(
            "2024-02-02",
            "You cast the copy while Reenact the Crime is resolving and still on the stack. You " +
                "can't wait to cast it later in the turn."
        )
        ruling(
            "2024-02-02",
            "If a spell has {X} in its mana cost, you must choose 0 as the value of X when casting " +
                "it without paying its mana cost."
        )
        ruling(
            "2024-02-02",
            "If you cast a spell without paying its mana cost, you can't choose an alternative " +
                "cost, but you may pay optional additional costs and must pay mandatory additional costs."
        )
        ruling(
            "2024-02-02",
            "If you don't cast the copy, it ceases to exist the next time state-based actions are checked."
        )
    }
}
