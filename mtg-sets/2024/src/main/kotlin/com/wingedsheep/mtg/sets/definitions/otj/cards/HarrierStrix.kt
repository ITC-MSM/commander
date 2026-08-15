package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Harrier Strix
 * {U}
 * Creature — Bird
 * 1/1
 * Flying
 * When this creature enters, tap target permanent.
 * {2}{U}: Draw a card, then discard a card.
 */
val HarrierStrix = card("Harrier Strix") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    power = 1
    toughness = 1
    oracleText = "Flying\nWhen this creature enters, tap target permanent.\n{2}{U}: Draw a card, then discard a card."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target("permanent to tap", Targets.Permanent)
        effect = Effects.Tap(permanent)
    }

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        effect = Patterns.Hand.loot(1, 1)
        description = "{2}{U}: Draw a card, then discard a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "52"
        artist = "Brian Valeza"
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70ca61a9-8938-41bf-bd14-5759f4de6521.jpg?1712355435"

        ruling("2024-04-12", "You may target a permanent that is already tapped with Harrier Strix's second ability.")
    }
}
