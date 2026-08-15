package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mold Adder
 * {G}
 * Creature — Fungus Snake
 * 1/1
 * Whenever an opponent casts a blue or black spell, you may put a +1/+1 counter on this creature.
 *
 * The trigger watches any opponent spell whose color set includes blue or black
 * ([GameObjectFilter.withAnyColor]); the "you may" is the controller's optional choice
 * ([MayEffect]) to grow this creature with a +1/+1 counter on itself ([EffectTarget.Self]).
 */
val MoldAdder = card("Mold Adder") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Fungus Snake"
    power = 1
    toughness = 1
    oracleText = "Whenever an opponent casts a blue or black spell, you may put a +1/+1 counter on this creature."

    triggeredAbility {
        trigger = Triggers.opponentCasts(GameObjectFilter.Any.withAnyColor(Color.BLUE, Color.BLACK))
        effect = MayEffect(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "194"
        artist = "Matt Cavotta"
        flavorText = "The more you struggle against its coils, the tighter they get."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a216a729-6283-4c2b-90fe-ec8f3b9c570f.jpg?1782715707"
    }
}
