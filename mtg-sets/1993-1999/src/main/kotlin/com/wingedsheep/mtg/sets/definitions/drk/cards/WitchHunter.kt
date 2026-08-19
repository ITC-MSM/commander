package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Witch Hunter
 * {2}{W}{W}
 * Creature — Human Cleric
 * 1/1
 * {T}: This creature deals 1 damage to target player or planeswalker.
 * {1}{W}{W}, {T}: Return target creature an opponent controls to its owner's hand.
 */
val WitchHunter = card("Witch Hunter") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 1
    oracleText = "{T}: This creature deals 1 damage to target player or planeswalker.\n" +
        "{1}{W}{W}, {T}: Return target creature an opponent controls to its owner's hand."

    activatedAbility {
        cost = Costs.Tap
        val victim = target("target player or planeswalker", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(1, victim)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}{W}"), Costs.Tap)
        val creature = target("target creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.ReturnToHand(creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "19"
        artist = "Jesper Myrfors"
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4eef9bb7-cd3c-422e-a93b-90d98684675a.jpg?1783947945"
    }
}
