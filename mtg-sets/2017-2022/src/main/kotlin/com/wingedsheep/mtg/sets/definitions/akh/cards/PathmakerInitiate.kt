package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pathmaker Initiate
 * {1}{R}
 * Creature — Human Wizard
 * 2/1
 * {T}: Target creature with power 2 or less can't be blocked this turn.
 */
val PathmakerInitiate = card("Pathmaker Initiate") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Wizard"
    oracleText = "{T}: Target creature with power 2 or less can't be blocked this turn."
    power = 2
    toughness = 1

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target creature with power 2 or less", Targets.CreatureWithPowerAtMost(2))
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, creature)
        description = "{T}: Target creature with power 2 or less can't be blocked this turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "146"
        artist = "Josu Hernaiz"
        flavorText = "\"The expected way through the trial is far too tedious.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4ccc6b34-aaa7-4ec1-9f37-b03f9b5919f2.jpg?1783936484"
    }
}
