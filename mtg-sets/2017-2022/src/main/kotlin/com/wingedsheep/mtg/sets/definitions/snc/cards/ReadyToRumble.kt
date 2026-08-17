package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Ready to Rumble
 * {4}{R}
 * Sorcery
 * Choose one —
 * • Ready to Rumble deals 5 damage to target creature or planeswalker.
 * • Destroy target artifact.
 */
val ReadyToRumble = card("Ready to Rumble") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n• Ready to Rumble deals 5 damage to target creature or planeswalker.\n• Destroy target artifact."

    spell {
        modal(chooseCount = 1) {
            mode("Ready to Rumble deals 5 damage to target creature or planeswalker") {
                val t = target("target", Targets.CreatureOrPlaneswalker)
                effect = Effects.DealDamage(5, t)
            }
            mode("Destroy target artifact") {
                val t = target("target", TargetObject(filter = TargetFilter.Artifact))
                effect = Effects.Destroy(t)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "119"
        artist = "Josu Hernaiz"
        flavorText = "\"It's too quiet tonight. Go make trouble.\"\n—Ziatora"
        imageUri = "https://cards.scryfall.io/normal/front/1/6/16998689-345d-41e3-a368-e97b696ed689.jpg?1783923115"
    }
}
