package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Rumbling Rockslide — {3}{R}
 * Sorcery
 * Rumbling Rockslide deals damage to target creature equal to the number of lands you control.
 */
val RumblingRockslide = card("Rumbling Rockslide") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Rumbling Rockslide deals damage to target creature equal to the number of lands you control."

    spell {
        val t = target("target creature", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.DealDamage(
            amount = DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter.Land,
            ),
            target = t,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "134"
        artist = "Adam Paquette"
        flavorText = "\"When monsters walk, the earth knows its place and yields.\"\n—Rielle, the Everwise"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/96f9aaa7-11c7-4cd0-9803-9471c14ab846.jpg"
    }
}
