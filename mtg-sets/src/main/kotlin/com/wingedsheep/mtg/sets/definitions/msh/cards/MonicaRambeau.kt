package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Monica Rambeau // Photon, Living Light — Marvel Super Heroes #23 (mythic)
 *
 * Front — Monica Rambeau · {2}{W} · Legendary Creature — Human Hero · 3/3
 *   Flying, prowess
 *   {2}{R}{W}{W}: Transform Monica Rambeau. Activate only as a sorcery.
 *
 * Back — Photon, Living Light · Legendary Creature — Elemental Hero · 4/4
 *   Flying, hexproof, prowess
 *   Whenever you cast a noncreature spell, put a +1/+1 counter on each other creature you control.
 *
 * A transforming double-faced creature ([CardDefinition.doubleFacedCreature]); the front owns the
 * sorcery-speed [TransformEffect] flip ([TimingRule.SorcerySpeed]). The back is reached only via
 * that flip, so it carries no castable mana cost — its R/W colors come from a color indicator
 * (CR 204).
 *
 *  - Both faces use the [prowess] DSL helper, not a bare [Keyword.PROWESS] in `keywords(...)`: the
 *    keyword on its own is display-only, and the +1/+1 behavior comes from the intrinsic triggered
 *    ability the helper adds alongside it. The back's own cast trigger is a *separate* ability, so
 *    casting a noncreature spell both pumps Photon (prowess) and puts counters on your other
 *    creatures.
 *  - "put a +1/+1 counter on each other creature you control" is [Effects.ForEachInGroup] over a
 *    [GroupFilter] of `Creature.youControl()` with `excludeSelf` — the Web-Warriors idiom — so no
 *    targeting is involved and Photon itself is skipped.
 */

private val MonicaRambeauFront = card("Monica Rambeau") {
    manaCost = "{2}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Human Hero"
    power = 3
    toughness = 3
    oracleText = "Flying, prowess (Whenever you cast a noncreature spell, this creature gets " +
        "+1/+1 until end of turn.)\n" +
        "{2}{R}{W}{W}: Transform Monica Rambeau. Activate only as a sorcery."

    keywords(Keyword.FLYING)
    prowess()

    // {2}{R}{W}{W}: Transform Monica Rambeau. Activate only as a sorcery.
    activatedAbility {
        cost = Costs.Mana("{2}{R}{W}{W}")
        effect = TransformEffect(EffectTarget.Self)
        timing = TimingRule.SorcerySpeed
        description = "Transform Monica Rambeau. Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "23"
        artist = "Xabi Gaztelua"
        flavorText = "Above the city, she glows . . ."
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3f995518-b12a-4623-9ab3-b79a5cef3cba.jpg?1783902975"
    }
}

private val PhotonLivingLightBack = card("Photon, Living Light") {
    manaCost = ""
    colorIdentity = "RW"
    colorIndicator = "RW" // Transformed back face, no mana cost (CR 204).
    typeLine = "Legendary Creature — Elemental Hero"
    power = 4
    toughness = 4
    oracleText = "Flying, hexproof, prowess\n" +
        "Whenever you cast a noncreature spell, put a +1/+1 counter on each other creature you " +
        "control."

    keywords(Keyword.FLYING, Keyword.HEXPROOF)
    prowess()

    // Whenever you cast a noncreature spell, put a +1/+1 counter on each other creature you control.
    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl(), excludeSelf = true),
            AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
        )
        description = "Whenever you cast a noncreature spell, put a +1/+1 counter on each other " +
            "creature you control."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "23"
        artist = "Marta Nael"
        flavorText = ". . . her power beyond anyone's wildest dreams."
        imageUri = "https://cards.scryfall.io/normal/back/3/f/3f995518-b12a-4623-9ab3-b79a5cef3cba.jpg?1783902975"
    }
}

val MonicaRambeau: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = MonicaRambeauFront,
    backFace = PhotonLivingLightBack,
)
