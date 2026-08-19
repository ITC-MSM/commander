package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Giant's Ire
 * {3}{R}
 * Kindred Sorcery — Giant
 * Giant's Ire deals 4 damage to target player or planeswalker. If you control a Giant, draw a card.
 *
 * The draw is checked on resolution, so a Giant that dies in response costs you the card.
 */
val GiantsIre = card("Giant's Ire") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Kindred Sorcery — Giant"
    oracleText = "Giant's Ire deals 4 damage to target player or planeswalker. If you control a " +
        "Giant, draw a card."

    spell {
        val recipient = target("target player or planeswalker", Targets.PlayerOrPlaneswalker)
        effect = Effects.Composite(
            Effects.DealDamage(4, recipient),
            ConditionalEffect(
                condition = Conditions.ControlPermanentOfType(Subtype.GIANT),
                effect = Effects.DrawCards(1)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "170"
        artist = "Alex Horley-Orlandelli"
        flavorText = "The only feeling greater than hurling something a mile is crushing something else with it that was really, really irritating you."
        imageUri = "https://cards.scryfall.io/normal/front/0/4/046fa2db-4c73-401a-b9a4-b039554be625.jpg?1783942876"
    }
}
