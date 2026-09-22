package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

val PredictivePreparations = card("Predictive Preparations") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Put a +1/+1 counter on each of one or two target creatures.\nFlashback {3}{W} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        target("one or two creatures", TargetCreature(count = 2, minCount = 1))
        effect = ForEachTargetEffect(effects = listOf(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0))
        ))
    }
    keywordAbility(KeywordAbility.flashback("{3}{W}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "16"
        artist = "Nathaniel Himawan"
        flavorText = "Fatehold battlemages believe the best way to win a duel is to first study the outcome."
        imageUri = "https://cards.scryfall.io/normal/front/5/0/50a0e5f0-3c39-4f16-9a73-eec8ef71f12e.jpg?1789556696"
        inBooster = false
    }
}
