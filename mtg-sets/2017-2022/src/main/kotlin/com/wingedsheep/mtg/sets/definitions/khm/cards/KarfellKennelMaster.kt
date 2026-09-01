package com.wingedsheep.mtg.sets.definitions.khm.cards

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
 * Karfell Kennel-Master
 * {4}{B}
 * Creature — Zombie Berserker
 * 4/4
 * When this creature enters, up to two target creatures each get +1/+0 and gain indestructible until end of turn. (Damage and effects that say "destroy" don't destroy them.)
 *
 * "Up to two target creatures **each** get …" is a multi-slot target: the effect runs once per
 * chosen creature. [ForEachTargetEffect] with [EffectTarget.ContextTarget] `0` is the shape that
 * binds — a single effect naming the multi-slot handle directly resolves against nothing.
 */
val KarfellKennelMaster = card("Karfell Kennel-Master") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Berserker"
    oracleText = "When this creature enters, up to two target creatures each get +1/+0 and gain indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy them.)"
    power = 4
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target = TargetPermanent(count = 2, optional = true, filter = TargetFilter.Creature)
        effect = ForEachTargetEffect(
            listOf(
                Effects.ModifyStats(1, 0, EffectTarget.ContextTarget(0)),
                Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.ContextTarget(0))
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "101"
        artist = "Izzy"
        flavorText = "Trapped in a realm of dead flesh and scarce hunting, the ravenous wolves of Karfell follow the Dread Marn to warm game."
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f670c380-6faa-42ec-ab41-6be8137169b2.jpg"
    }
}
