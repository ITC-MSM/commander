package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Flying Octobot
 * {1}{U}
 * Artifact Creature — Robot Villain
 * 1/1
 * Flying
 * Whenever another Villain you control enters, put a +1/+1 counter on this creature. This ability triggers only once each turn.
 */
val FlyingOctobot = card("Flying Octobot") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Robot Villain"
    oracleText = "Flying\nWhenever another Villain you control enters, put a +1/+1 counter on this creature. This ability triggers only once each turn."
    power = 1
    toughness = 1
    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.withSubtype("Villain").youControl(),
            binding = TriggerBinding.OTHER
        )
        oncePerTurn = true
        effect = AddCountersEffect(counterType = Counters.PLUS_ONE_PLUS_ONE, count = 1, target = EffectTarget.Self)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "31"
        artist = "John Tyler Christopher"
        flavorText = "\"How many helping hands do you need, Doc?\"\n—Spider-Man"
        imageUri = "https://cards.scryfall.io/normal/front/e/b/ebadcd4a-f58f-4328-a765-0ea8d8028417.jpg?1757376933"
    }
}
