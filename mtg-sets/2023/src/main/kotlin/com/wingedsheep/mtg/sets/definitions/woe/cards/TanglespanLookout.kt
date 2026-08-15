package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Tanglespan Lookout
 * {2}{G}
 * Creature — Satyr
 * 2/3
 *
 * Whenever an Aura you control enters, draw a card.
 *
 * The trigger fires once per Aura, so it uses the generic `entersBattlefield` factory with an
 * `ANY` binding rather than one of the SELF-bound constants. WOE's Role tokens are
 * "Enchantment — Aura Role" tokens, so they match this filter too — that's the card's main
 * payoff in limited.
 */
val TanglespanLookout = card("Tanglespan Lookout") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Satyr"
    power = 2
    toughness = 3
    oracleText = "Whenever an Aura you control enters, draw a card."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.withSubtype(Subtype.AURA).youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "188"
        artist = "Dmitry Burmak"
        flavorText = "\"The trolls have your scent, traveler. Hurry on, and let me take care of them.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3bc5c32d-be0a-4a5f-a8c7-9767a895bc76.jpg?1783915077"
    }
}
