package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Triumphant Chomp — {R}
 * Sorcery
 * Triumphant Chomp deals damage to target creature equal to 2 or the greatest
 * power among Dinosaurs you control, whichever is greater.
 *
 * The variable half is a MAX aggregate over the power of Dinosaur creatures you
 * control (0 when you control none), floored at 2 via [DynamicAmount.Max].
 */
val TriumphantChomp = card("Triumphant Chomp") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Triumphant Chomp deals damage to target creature equal to 2 or the greatest power among Dinosaurs you control, whichever is greater."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(
            amount = DynamicAmount.Max(
                DynamicAmount.Fixed(2),
                DynamicAmounts.battlefield(
                    Player.You,
                    GameObjectFilter.Creature.withSubtype(Subtype.DINOSAUR),
                ).maxPower(),
            ),
            target = t,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "170"
        artist = "Simon Dominic"
        flavorText = "With crunchy outer shells and juicy innards, cave crawlers quickly became a favorite snack of the Sun Empire's dinosaur companions."
        imageUri = "https://cards.scryfall.io/normal/front/0/6/06b28139-efa7-4818-a012-cf8150692b43.jpg?1782694475"
    }
}
