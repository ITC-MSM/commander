package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * D'Avenant Trapper
 * {2}{W}
 * Creature — Human Archer
 * 3/2
 * Whenever you cast a historic spell, tap target creature an opponent controls.
 * (Artifacts, legendaries, and Sagas are historic.)
 */
val DAvenantTrapper = card("D'Avenant Trapper") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Archer"
    power = 3
    toughness = 2
    oracleText = "Whenever you cast a historic spell, tap target creature an opponent controls. (Artifacts, legendaries, and Sagas are historic.)"

    triggeredAbility {
        trigger = Triggers.YouCastHistoric
        target = Targets.CreatureOpponentControls
        effect = Effects.Tap(EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Winona Nelson"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5881ab3-566b-4999-997e-cd5ecb84282b.jpg?1615334479"
    }
}
