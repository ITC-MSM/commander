package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Sea Drake
 * {2}{U}
 * Creature — Drake
 * 4 / 3
 *
 * Flying
 * When this creature enters, return two target lands you control to their owner's hand.
 *
 * Two targets, one bounce each: [ForEachTargetEffect] fans the return out over the
 * chosen lands, so the body aims at [EffectTarget.ContextTarget] 0 — the current
 * iteration's target (the Rain of Salt shape).
 */
val SeaDrake = card("Sea Drake") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    oracleText =
        "Flying\n" +
        "When this creature enters, return two target lands you control to their owner's hand."
    power = 4
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target("target", TargetPermanent(count = 2, filter = TargetFilter.Land.youControl()))
        effect = ForEachTargetEffect(
            listOf(Effects.ReturnToHand(EffectTarget.ContextTarget(0)))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "45"
        artist = "Rebecca Guay"
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d87a40cb-b30d-41ce-8e11-5fe3136fdadd.jpg"
    }
}
