package com.wingedsheep.mtg.sets.definitions.mbs.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Blue Sun's Zenith — Mirrodin Besieged #20 (canonical / earliest real printing, 2011)
 * {X}{U}{U}{U} · Instant
 *
 * Target player draws X cards. Shuffle Blue Sun's Zenith into its owner's library.
 *
 * Note the target is a *player*, not "you" — the card is as happy decking an opponent as refilling
 * your own hand. `selfShuffleIntoLibrary()` replaces the CR 608.2n destination; if the spell is
 * countered or its target becomes illegal it goes to the graveyard as usual.
 */
val BlueSunsZenith = card("Blue Sun's Zenith") {
    manaCost = "{X}{U}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Target player draws X cards. Shuffle Blue Sun's Zenith into its owner's library."

    spell {
        target("target player", Targets.Player)
        effect = Effects.DrawCards(DynamicAmount.XValue, EffectTarget.ContextTarget(0))
        selfShuffleIntoLibrary()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "20"
        artist = "Izzy"
        flavorText = "\"The Origin Query will wait. We must ensure we survive to return to it.\"\n" +
            "—Pelyus, vedalken ordinar"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8150f78-e187-4949-9746-fec64d1675d1.jpg?1783941389"
        ruling(
            "2011-06-01",
            "If this spell doesn't resolve, none of its effects occur. In particular, it will go " +
                "to the graveyard rather than to its owner's library."
        )
        ruling(
            "2018-03-16",
            "Because you follow the spell's instructions in order, you won't be able to draw the " +
                "same Blue Sun's Zenith that you cast."
        )
    }
}
