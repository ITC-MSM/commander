package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Weftblade Enhancer
 * {5}{W}
 * Creature — Drix Artificer
 * When this creature enters, put a +1/+1 counter on each of up to two target creatures.
 * Warp {2}{W} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)
 * 3/4
 */
val WeftbladeEnhancer = card("Weftblade Enhancer") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Drix Artificer"
    oracleText = "When this creature enters, put a +1/+1 counter on each of up to two target creatures.\n" +
        "Warp {2}{W} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)"
    power = 3
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target("up to two target creatures", TargetCreature(count = 2, optional = true))
        effect = ForEachTargetEffect(
            listOf(Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0)))
        )
    }

    warp = "{2}{W}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "44"
        artist = "Nathaniel Himawan"
        flavorText = "His people watched the Eldrazi rise and the Fomori fall. Never will they watch again."
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d72b00c-5043-4630-949a-fc17eeb962bc.jpg?1752946724"
    }
}
