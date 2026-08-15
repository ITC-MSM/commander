package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Agent Bishop, Man in Black
 * {2}{W}
 * Legendary Creature — Human Soldier
 * 1/2
 *
 * At the beginning of combat on your turn, put a +1/+1 counter on each of up
 * to two target creatures.
 */
val AgentBishopManInBlack = card("Agent Bishop, Man in Black") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Soldier"
    oracleText = "At the beginning of combat on your turn, put a +1/+1 counter on each of up to two target creatures."
    power = 1
    toughness = 2

    triggeredAbility {
        trigger = Triggers.BeginCombat
        target("up to two target creatures", TargetCreature(count = 2, optional = true))
        effect = ForEachTargetEffect(
            listOf(Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0)))
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "2"
        artist = "Adrián Rodríguez Pérez"
        flavorText = "\"Utroms . . . Triceratons . . . there's little difference to me. I help one destroy the other, pick up the pieces, then get them the hell off of my planet.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c769202-178c-442a-a9e2-aa08b5ae5c9a.jpg?1769005601"
    }
}
