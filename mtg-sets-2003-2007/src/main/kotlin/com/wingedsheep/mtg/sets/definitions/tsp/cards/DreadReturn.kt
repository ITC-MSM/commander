package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Dread Return
 * {2}{B}{B}
 * Sorcery
 * Return target creature card from your graveyard to the battlefield.
 * Flashback—Sacrifice three creatures.
 */
val DreadReturn = card("Dread Return") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText =
        "Return target creature card from your graveyard to the battlefield.\n" +
            "Flashback—Sacrifice three creatures. (You may cast this card from your graveyard " +
            "for its flashback cost. Then exile it.)"

    spell {
        val card = target(
            "target creature card from your graveyard",
            Targets.CreatureCardInYourGraveyard,
        )
        effect = Effects.Move(card, Zone.BATTLEFIELD)
    }

    keywordAbility(
        KeywordAbility.flashback(
            "",
            Costs.additional.SacrificePermanent(Filters.Creature, count = 3),
        ),
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "104"
        artist = "Kev Walker"
        flavorText = "Those who forget the horrors of the past are doomed to re-meet them."
        imageUri =
            "https://cards.scryfall.io/normal/front/d/7/d7e304fc-0ace-459e-8d2f-376f1899639c.jpg?1783943234"
    }
}
