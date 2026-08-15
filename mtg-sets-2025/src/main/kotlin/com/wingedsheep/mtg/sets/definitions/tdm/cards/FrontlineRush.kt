package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Frontline Rush — Tarkir: Dragonstorm #186
 * {R}{W} · Instant · Uncommon
 *
 * Choose one —
 * • Create two 1/1 red Goblin creature tokens.
 * • Target creature gets +X/+X until end of turn, where X is the number of creatures you control.
 */
val FrontlineRush = card("Frontline Rush") {
    manaCost = "{R}{W}"
    colorIdentity = "RW"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Create two 1/1 red Goblin creature tokens.\n" +
        "• Target creature gets +X/+X until end of turn, where X is the number of creatures you control."

    spell {
        modal(chooseCount = 1) {
            mode("Create two 1/1 red Goblin creature tokens") {
                effect = Effects.CreateToken(
                    power = 1,
                    toughness = 1,
                    colors = setOf(Color.RED),
                    creatureTypes = setOf("Goblin"),
                    count = 2,
                    imageUri = "https://cards.scryfall.io/normal/front/e/2/e265ca24-96c0-4654-a8f3-bbffe288970a.jpg?1742506636"
                )
            }
            mode("Target creature gets +X/+X until end of turn, where X is the number of creatures you control") {
                val t = target("target creature", Targets.Creature)
                effect = Effects.ModifyStats(
                    DynamicAmounts.creaturesYouControl(),
                    DynamicAmounts.creaturesYouControl(),
                    t
                )
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "186"
        artist = "Filipe Pagliuso"
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2ce8a205-99d6-4a9c-83a7-18b7220177d3.jpg?1743204723"
    }
}
