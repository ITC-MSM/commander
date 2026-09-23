package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * X counts creatures you control as the attack trigger resolves (this one included if it's still
 * there); creating the Jace token first doesn't change it, since the token isn't a creature.
 */
val RepurposedEnforcer = card("Repurposed Enforcer") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    oracleText = "Whenever this creature attacks, empower Jace X, where X is the number of creatures you control. (Put that many loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")"
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Patterns.Mechanic.empowerJace(DynamicAmounts.creaturesYouControl())
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "19"
        artist = "Chris Rallis"
        flavorText = "A face half remembered, a form fit to serve."
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35000e93-85d3-44f8-976a-5918ee4c71e0.jpg?1788878108"
        inBooster = false
    }
}
