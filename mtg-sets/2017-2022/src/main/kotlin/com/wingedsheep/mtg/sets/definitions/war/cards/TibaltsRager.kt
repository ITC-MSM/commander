package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tibalt's Rager
 * {1}{R}
 * Creature — Devil
 * 1/2
 *
 * When this creature dies, it deals 1 damage to any target.
 * {1}{R}: This creature gets +2/+0 until end of turn.
 */
val TibaltsRager = card("Tibalt's Rager") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Devil"
    oracleText = "When this creature dies, it deals 1 damage to any target.\n{1}{R}: This creature gets +2/+0 until end of turn."
    power = 1
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Dies
        val victim = target("any target", Targets.Any)
        effect = Effects.DealDamage(1, victim)
        description = "When this creature dies, it deals 1 damage to any target."
    }

    activatedAbility {
        cost = Costs.Mana("{1}{R}")
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
        description = "{1}{R}: This creature gets +2/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "147"
        artist = "Yongjae Choi"
        flavorText = "\"Find out whose that is. I like its energy.\"\n—Judith"
        imageUri = "https://cards.scryfall.io/normal/front/2/9/2985520d-dfce-414e-a4ac-61695be67406.jpg?1783933420"
    }
}
