package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Zookeeper Mechan
 * {1}{R}
 * Artifact Creature — Robot
 * 1/3
 *
 * {T}: Add {R}.
 * {6}{R}: Target creature you control gets +4/+0 until end of turn. Activate only as a sorcery.
 */
val ZookeeperMechan = card("Zookeeper Mechan") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Robot"
    power = 1
    toughness = 3
    oracleText = "{T}: Add {R}.\n{6}{R}: Target creature you control gets +4/+0 until end of turn. Activate only as a sorcery."

    // {T}: Add {R}
    activatedAbility {
        cost = Costs.Tap
        manaAbility = true
        effect = Effects.AddMana(Color.RED)
    }

    // {6}{R}: Target creature you control gets +4/+0 until end of turn. Activate only as a sorcery.
    activatedAbility {
        cost = Costs.Mana("{6}{R}")
        timing = TimingRule.SorcerySpeed
        val target = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.ModifyStats(4, 0, target)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "170"
        artist = "Justyna Dura"
        flavorText = "Infinite Guideline's \"Fauna from Sothera\" exhibit proved popular. Unfortunately, the biological zookeepers kept getting eaten."
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d5cd0be-4337-4aba-a4f6-5adab7735a73.jpg?1753120476"
    }
}
