package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Elite Javelineer
 * {2}{W}
 * Creature — Human Soldier
 * 2/2
 * Whenever this creature blocks, it deals 1 damage to target attacking creature.
 */
val EliteJavelineer = card("Elite Javelineer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature blocks, it deals 1 damage to target attacking creature."

    triggeredAbility {
        trigger = Triggers.Blocks
        val attacker = target("target", Targets.AttackingCreature)
        effect = Effects.DealDamage(1, attacker)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "17"
        artist = "Mark Poole"
        flavorText = "\"Precision is frequently more valuable than force.\"\n" +
            "—Gerrard of the *Weatherlight*"
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea1c730f-76da-4eae-b3fc-b428b860ea93.jpg"
    }
}
