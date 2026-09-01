package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Garruk Wildspeaker - {2}{G}{G}
 * Legendary Planeswalker — Garruk
 * Starting Loyalty: 3
 *
 * +1: Untap two target lands.
 *
 * −1: Create a 3/3 green Beast creature token.
 *
 * −4: Creatures you control get +3/+3 and gain trample until end of turn.
 *
 * The +1 needs exactly two land targets — any two lands, tapped or not (2009-10-01 ruling) — so it
 * is a `count = 2` [TargetPermanent] fanned out through [Effects.UntapEachTarget]. The −4 is the
 * Overrun shape: a [Effects.ForEachInGroup] over the creatures you control *at resolution*, which
 * is exactly the set the other 2009-10-01 ruling says it affects.
 */
val GarrukWildspeaker = card("Garruk Wildspeaker") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Planeswalker — Garruk"
    startingLoyalty = 3
    oracleText = "+1: Untap two target lands.\n" +
        "−1: Create a 3/3 green Beast creature token.\n" +
        "−4: Creatures you control get +3/+3 and gain trample until end of turn."

    // +1: Untap two target lands.
    loyaltyAbility(+1) {
        target("two target lands", TargetPermanent(count = 2, filter = TargetFilter.Land))
        effect = Effects.UntapEachTarget()
    }

    // −1: Create a 3/3 green Beast creature token.
    loyaltyAbility(-1) {
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Beast"),
            imageUri = "https://cards.scryfall.io/normal/front/c/3/c3cbd01b-dbf5-424a-855d-f9a9d7e7e414.jpg?1783942838"
        )
    }

    // −4: Creatures you control get +3/+3 and gain trample until end of turn.
    loyaltyAbility(-4) {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.Composite(
                Effects.ModifyStats(3, 3, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "213"
        artist = "Aleksi Briclot"
        imageUri = "https://cards.scryfall.io/normal/front/c/a/ca6f13a2-9243-4ce9-9f71-bed74355b781.jpg?1783942863"
        ruling(
            "2009-10-01",
            "The third ability affects only creatures you control at the time it resolves. It " +
                "won't affect creatures that come under your control later in the turn.",
        )
        ruling(
            "2009-10-01",
            "The first ability can target any two lands. They don't have to be tapped.",
        )
    }
}
