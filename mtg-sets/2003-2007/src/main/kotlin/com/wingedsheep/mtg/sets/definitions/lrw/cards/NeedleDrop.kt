package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

val NeedleDrop = card("Needle Drop") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Needle Drop deals 1 damage to any target that was dealt damage this turn.\nDraw a card."

    spell {
        val recipient = target("target that was dealt damage this turn",
            Targets.Any(GameObjectFilter.Any.wasDealtDamageThisTurn()))
        effect = Effects.DealDamage(1, recipient) then Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "186"
        artist = "Greg Staples"
        flavorText = "\"First it was plovers and mulldrifters. Now it's knitting needles the size of javelins.\"\n—Calydd, kithkin farmer"
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d3f89bcf-46f8-4598-a949-7f10134606aa.jpg?1783942871"
    }
}
