package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Reputable Merchant — Tarkir: Dragonstorm #217
 * {2/W}{2/B}{2/G} · Creature — Human Citizen · 2/2
 *
 * When this creature enters or dies, put a +1/+1 counter on target creature you control.
 *
 * Two separate triggered abilities (enters; dies) since "enters or dies" is two distinct
 * trigger events (CR 603.2). Each targets a creature you control and places one +1/+1
 * counter on it via [EffectTarget.ContextTarget]. On death, last-known information determines
 * the controller for "you control"; if you control no creature at that point the trigger has
 * no legal target and is removed.
 *
 * The hybrid {2/W}{2/B}{2/G} cost lets each pip be paid with either two generic mana or one
 * mana of the listed color, so the card is castable in any of those three colors' decks.
 */
val ReputableMerchant = card("Reputable Merchant") {
    manaCost = "{2/W}{2/B}{2/G}"
    colorIdentity = "WBG"
    typeLine = "Creature — Human Citizen"
    power = 2
    toughness = 2
    oracleText = "When this creature enters or dies, put a +1/+1 counter on target creature you control."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0))
        description = "When this creature enters, put a +1/+1 counter on target creature you control."
    }

    triggeredAbility {
        trigger = Triggers.Dies
        target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0))
        description = "When this creature dies, put a +1/+1 counter on target creature you control."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "217"
        artist = "Craig J Spearing"
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b7d0591e-7fb7-40ea-ba2a-cfe544d40216.jpg?1743204858"
    }
}
