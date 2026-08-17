package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cutthroat Contender
 * {B}
 * Creature — Vampire Warrior
 * 1 / 1
 * Pay 1 life: This creature gets +1/+0 until end of turn. Activate only once each turn.
 */
val CutthroatContender = card("Cutthroat Contender") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Warrior"
    oracleText = "Pay 1 life: This creature gets +1/+0 until end of turn. Activate only once each turn."
    power = 1
    toughness = 1

    activatedAbility {
        cost = Costs.PayLife(1)
        restrictions = listOf(ActivationRestriction.OncePerTurn)
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Mark Behm"
        flavorText = "The fights may sometimes be rigged, but the front-row spectators know the wounds are real."
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6b23b3e4-58cf-4b5d-bdcb-410a403b4987.jpg?1783923133"
    }
}
