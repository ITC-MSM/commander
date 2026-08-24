package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Rathi Dragon
 * {2}{R}{R}
 * Creature — Dragon
 * 5/5
 * Flying (This creature can't be blocked except by creatures with flying or reach.)
 * When this creature enters, sacrifice it unless you sacrifice two Mountains.
 */
val RathiDragon = card("Rathi Dragon") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    power = 5
    toughness = 5
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)\n" +
        "When this creature enters, sacrifice it unless you sacrifice two Mountains."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = PayOrSufferEffect(
            cost = Costs.pay.Sacrifice(GameObjectFilter.Land.withSubtype(Subtype.MOUNTAIN), 2),
            suffer = SacrificeSelfEffect
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "196"
        artist = "Christopher Rush"
        flavorText = "Wrap the flame as twine Kingdoms will be thine."
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7df61bff-a459-4ddb-a084-f47859a43795.jpg"
    }
}
