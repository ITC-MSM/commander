package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature


/**
 * Tidus, Blitzball Star
 * {1}{W}{U}
 * Legendary Creature — Human Warrior
 * 2/1
 * Whenever an artifact you control enters, put a +1/+1 counter on Tidus.
 * Whenever Tidus attacks, tap target creature an opponent controls.
 */
val TidusBlitzballStar = card("Tidus, Blitzball Star") {
    manaCost = "{1}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Human Warrior"
    oracleText = "Whenever an artifact you control enters, put a +1/+1 counter on Tidus.\nWhenever Tidus attacks, tap target creature an opponent controls."
    power = 2
    toughness = 1
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = AddCountersEffect(counterType = Counters.PLUS_ONE_PLUS_ONE, count = 1, target = EffectTarget.Self)
    }
    triggeredAbility {
        trigger = Triggers.Attacks
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.opponentControls()))
        effect = Effects.Tap(t)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "246"
        artist = "Nakamura8"
        flavorText = "\"Every blitzer knows: when you got the ball, you gotta score!\""
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa851d68-a7a4-48c0-9cd7-d3d2e079f3a1.jpg"
    }
}
