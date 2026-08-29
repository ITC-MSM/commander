package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Glarewielder
 * {4}{R}
 * Creature — Elemental Shaman
 * 3/1
 * Haste
 * When this creature enters, up to two target creatures can't block this turn.
 * Evoke {1}{R}
 *
 * "Up to two target creatures" is one requirement of `count = 2, optional = true`, and the
 * restriction is applied per target — [ForEachTargetEffect] re-binds `ContextTarget(0)` to each
 * chosen creature in turn, so choosing zero or one target is as legal as choosing two.
 */
val Glarewielder = card("Glarewielder") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Shaman"
    power = 3
    toughness = 1
    oracleText = "Haste\nWhen this creature enters, up to two target creatures can't block this turn.\n" +
        "Evoke {1}{R} (You may cast this spell for its evoke cost. If you do, it's sacrificed when " +
        "it enters.)"

    keywords(Keyword.HASTE)

    evoke = "{1}{R}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target("target", TargetCreature(optional = true, count = 2, filter = TargetFilter.Creature))
        effect = ForEachTargetEffect(listOf(Effects.CantBlock(EffectTarget.ContextTarget(0))))
        description = "up to two target creatures can't block this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "171"
        artist = "Nils Hamm"
        imageUri = "https://cards.scryfall.io/normal/front/5/9/59fdc845-7165-40f3-a082-21a502b3f0f6.jpg?1783942876"
    }
}
