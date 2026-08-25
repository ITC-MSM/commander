package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Quakefoot Cyclops — Modern Horizons #142
 * {4}{R} · Creature — Cyclops · 4/4
 *
 * When this creature enters, up to two target creatures can't block this turn.
 * Cycling {1}{R}
 * When you cycle this card, target creature can't block this turn.
 *
 * "Up to two target creatures" is one requirement with `count = 2, optional = true` (CR 601.2c —
 * the minimum drops to zero), not two separate slots, so the restriction is applied through
 * [ForEachTargetEffect] with [EffectTarget.ContextTarget]`(0)`: each chosen target gets its own
 * iteration, and the number of iterations is owned by the requirement rather than duplicated in
 * the effect. The cycling trigger targets exactly one creature and can name it directly.
 *
 * The cycling trigger is a real triggered ability that goes on the stack from the graveyard-bound
 * discard (CR 702.29b), so it resolves — and its target is chosen — even though the Cyclops itself
 * never reaches the battlefield.
 */
val QuakefootCyclops = card("Quakefoot Cyclops") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Cyclops"
    power = 4
    toughness = 4
    oracleText = "When this creature enters, up to two target creatures can't block this turn.\n" +
        "Cycling {1}{R} ({1}{R}, Discard this card: Draw a card.)\n" +
        "When you cycle this card, target creature can't block this turn."

    keywordAbility(KeywordAbility.cycling("{1}{R}"))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target("up to two target creatures", TargetCreature(count = 2, optional = true))
        effect = ForEachTargetEffect(listOf(Effects.CantBlock(EffectTarget.ContextTarget(0))))
    }

    triggeredAbility {
        trigger = Triggers.YouCycleThis
        val creature = target("target creature", Targets.Creature)
        effect = Effects.CantBlock(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "142"
        artist = "Mike Bierek"
        imageUri = "https://cards.scryfall.io/normal/front/2/7/27573ee0-156a-4bf3-95eb-5e7b63c638e7.jpg?1783933105"
    }
}
