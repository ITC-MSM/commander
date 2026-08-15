package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Canyon Drake
 * {2}{R}{R}
 * Creature — Drake (1/2)
 *
 * Flying
 * {1}, Discard a card at random: This creature gets +2/+0 until end of turn.
 */
val CanyonDrake = card("Canyon Drake") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Drake"
    power = 1
    toughness = 2
    oracleText = "Flying\n" +
        "{1}, Discard a card at random: This creature gets +2/+0 until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.DiscardAtRandom(1))
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
        description = "{1}, Discard a card at random: This creature gets +2/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "166"
        artist = "Quinton Hoover"
        flavorText = "\"These runes are tough enough without the distraction,\" Ertai muttered, one eye on the drake."
        imageUri = "https://cards.scryfall.io/normal/front/2/2/22f84143-5912-43ca-a274-f26ed0dbadd0.jpg?1562052811"
    }
}
