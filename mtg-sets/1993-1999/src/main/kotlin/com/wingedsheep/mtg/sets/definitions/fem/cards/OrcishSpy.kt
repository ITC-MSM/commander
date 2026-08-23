package com.wingedsheep.mtg.sets.definitions.fem.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPlayer
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Orcish Spy
 * {R}
 * Creature — Orc Rogue
 * 1/1
 * {T}: Look at the top three cards of target player's library.
 *
 * A pure information effect: gather the top three privately, then put them straight back in the
 * order they were in. `CardOrder.Preserve` is what makes it a look rather than a Sleight of Hand —
 * nothing about the library changes.
 */
val OrcishSpy = card("Orcish Spy") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Orc Rogue"
    oracleText = "{T}: Look at the top three cards of target player's library."
    power = 1
    toughness = 1

    activatedAbility {
        cost = Costs.Tap
        val t = target("target player", TargetPlayer())
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.TopOfLibrary(DynamicAmount.Fixed(3), Player.TargetPlayer),
                storeAs = "spied"
            ),
            MoveCollectionEffect(
                from = "spied",
                destination = CardDestination.ToZone(Zone.LIBRARY, Player.TargetPlayer, ZonePlacement.Top),
                order = CardOrder.Preserve
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "61a"
        artist = "Susan Van Camp"
        flavorText = "\"Yeah, they're ugly, they desert in droves, and their personal habits are enough to make you sick. But I'll say this for Orcs: they make great spies.\"\n—Ivra Jursdotter"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd3890d1-563d-4519-ab8c-913031d71918.jpg?1783947892"
    }
}
