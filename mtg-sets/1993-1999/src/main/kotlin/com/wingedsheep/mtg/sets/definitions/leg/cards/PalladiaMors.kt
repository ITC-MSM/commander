package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Palladia-Mors
 * {2}{R}{R}{G}{G}{W}{W}
 * Legendary Creature — Elder Dragon
 * 7/7
 *
 * Flying, trample
 * At the beginning of your upkeep, sacrifice Palladia-Mors unless you pay {R}{G}{W}.
 *
 * The upkeep tax is CR 118.3's "unless": [PayOrSufferEffect] asks the controller to
 * pay on resolution and sacrifices the dragon when they decline or cannot.
 */
val PalladiaMors = card("Palladia-Mors") {
    manaCost = "{2}{R}{R}{G}{G}{W}{W}"
    colorIdentity = "GRW"
    typeLine = "Legendary Creature — Elder Dragon"
    power = 7
    toughness = 7
    oracleText = "Flying, trample\n" +
        "At the beginning of your upkeep, sacrifice Palladia-Mors unless you pay {R}{G}{W}."

    keywords(Keyword.FLYING, Keyword.TRAMPLE)
    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = PayOrSufferEffect(cost = Costs.pay.Mana("{R}{G}{W}"), suffer = SacrificeSelfEffect)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "247"
        artist = "Edward P. Beard, Jr."
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad64874d-ce33-4e0a-bcca-723f129ef415.jpg?1783948035"
    }
}
