package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Chromium
 * {2}{W}{W}{U}{U}{B}{B}
 * Legendary Creature — Elder Dragon
 * 7/7
 *
 * Flying
 * Rampage 2 (Whenever this creature becomes blocked, it gets +2/+2 until end of turn for each creature blocking it beyond the first.)
 * At the beginning of your upkeep, sacrifice Chromium unless you pay {W}{U}{B}.
 *
 * The upkeep tax is CR 118.3's "unless": [PayOrSufferEffect] asks the controller to
 * pay on resolution and sacrifices the dragon when they decline or cannot.
 */
val Chromium = card("Chromium") {
    manaCost = "{2}{W}{W}{U}{U}{B}{B}"
    colorIdentity = "BUW"
    typeLine = "Legendary Creature — Elder Dragon"
    power = 7
    toughness = 7
    oracleText = "Flying\n" +
        "Rampage 2 (Whenever this creature becomes blocked, it gets +2/+2 until end of turn for each " +
        "creature blocking it beyond the first.)\n" +
        "At the beginning of your upkeep, sacrifice Chromium unless you pay {W}{U}{B}."

    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = PayOrSufferEffect(cost = Costs.pay.Mana("{W}{U}{B}"), suffer = SacrificeSelfEffect)
    }

    rampage(2)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "224"
        artist = "Edward P. Beard, Jr."
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8cd7d7e1-f928-4429-9a59-ba0590a78e98.jpg?1783948040"
    }
}
