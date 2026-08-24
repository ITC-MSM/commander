package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Flowstone Wyvern
 * {3}{R}{R}
 * Creature — Drake
 * 3/3
 * Flying
 * {R}: This creature gets +2/-2 until end of turn.
 */
val FlowstoneWyvern = card("Flowstone Wyvern") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Drake"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "{R}: This creature gets +2/-2 until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(2, -2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "176"
        artist = "Stephen Daniele"
        flavorText = "\"Where I come from, stone stays on the ground.\"\n" +
            "—Tahngarth of the *Weatherlight*"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee7949c7-ab80-46a1-9cf7-d8e8c004df6e.jpg"
    }
}
