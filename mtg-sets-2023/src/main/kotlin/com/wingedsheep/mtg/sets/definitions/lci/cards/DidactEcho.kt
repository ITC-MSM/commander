package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Didact Echo
 * {4}{U}
 * Creature — Spirit Cleric
 * 3/2
 *
 * When this creature enters, draw a card.
 * Descend 4 — This creature has flying as long as there are four or more permanent cards
 * in your graveyard.
 *
 * The ETB trigger is unconditional — it always draws a card when Didact Echo enters the
 * battlefield.
 *
 * The Descend 4 static ability is modelled as a [ConditionalStaticAbility] wrapping a
 * [GrantKeyword](FLYING) over [GroupFilter.source()], gated by
 * [Conditions.CardsInGraveyardMatchingAtLeast](4, Permanent). The condition is re-evaluated
 * continuously in Layer 6, so flying appears and disappears as the graveyard count changes.
 */
val DidactEcho = card("Didact Echo") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit Cleric"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, draw a card.\n" +
        "Descend 4 — This creature has flying as long as there are four or more permanent cards in your graveyard."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FLYING, GroupFilter.source()),
            condition = Conditions.CardsInGraveyardMatchingAtLeast(4, GameObjectFilter.Permanent)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "53"
        artist = "Irina Nordsol"
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3c068b2e-17cc-4fb8-bd79-b775a4713d74.jpg?1782694566"
    }
}
