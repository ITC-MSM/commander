package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Felothar, Dawn of the Abzan — Tarkir: Dragonstorm #184
 * {W}{B}{G} · Legendary Creature — Human Warrior · 3/3
 *
 * Trample
 * Whenever Felothar enters or attacks, you may sacrifice a nonland permanent. When you do,
 * put a +1/+1 counter on each creature you control.
 */
val FelotharDawnOfTheAbzan = card("Felothar, Dawn of the Abzan") {
    manaCost = "{W}{B}{G}"
    colorIdentity = "WBG"
    typeLine = "Legendary Creature — Human Warrior"
    power = 3
    toughness = 3
    oracleText = "Trample\n" +
        "Whenever Felothar enters or attacks, you may sacrifice a nonland permanent. When you do, " +
        "put a +1/+1 counter on each creature you control."

    keywords(Keyword.TRAMPLE)

    // Whenever Felothar enters ...
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = felotharSacrificeEffect()
    }

    // ... or attacks
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = felotharSacrificeEffect()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "184"
        artist = "Victor Adame Minguez"
        imageUri = "https://cards.scryfall.io/normal/front/8/3/83e11f20-6524-4fba-9603-0b97e2d69aac.jpg?1743697572"
    }
}

/**
 * "You may sacrifice a nonland permanent. When you do, put a +1/+1 counter on each
 * creature you control." Modeled as a reflexive trigger: the reflexive counter effect
 * happens only if the controller actually sacrifices.
 */
private fun felotharSacrificeEffect(): Effect = ReflexiveTriggerEffect(
    action = Effects.Sacrifice(
        filter = GameObjectFilter.NonlandPermanent,
        count = 1,
        target = EffectTarget.Controller
    ),
    optional = true,
    reflexiveEffect = Effects.ForEachInGroup(
        filter = GroupFilter.AllCreaturesYouControl,
        effect = AddCountersEffect(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            count = 1,
            target = EffectTarget.Self
        )
    )
)
