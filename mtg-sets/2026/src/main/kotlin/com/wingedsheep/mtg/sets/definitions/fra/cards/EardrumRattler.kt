package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

val EardrumRattler = card("Eardrum Rattler") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Bard"
    oracleText = "{1}, {T}: Another target creature you control with power 2 or less can't be blocked this turn."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        val creature = target("creature", TargetCreature(filter = TargetFilter.CreatureYouControl.other().powerAtMost(2)))
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "81"
        artist = "Javier Charro"
        flavorText = "She puts the harm in harmony."
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f8771f9-8128-4818-a11d-41ea368cf697.jpg?1789127173"
        inBooster = false
    }
}
