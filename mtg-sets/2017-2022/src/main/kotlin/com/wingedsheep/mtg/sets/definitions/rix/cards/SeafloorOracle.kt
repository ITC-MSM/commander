package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Seafloor Oracle
 * {2}{U}{U}
 * Creature — Merfolk Wizard
 * 2/3
 * Whenever a Merfolk you control deals combat damage to a player, draw a card.
 *
 * The bare tribal noun "Merfolk" names every *permanent* with the subtype, so the source filter
 * is [GameObjectFilter.Permanent]; [TriggerBinding.ANY] lets Seafloor Oracle's own combat damage
 * trigger it too.
 */
val SeafloorOracle = card("Seafloor Oracle") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 2
    toughness = 3
    oracleText = "Whenever a Merfolk you control deals combat damage to a player, draw a card."

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.AnyPlayer,
            sourceFilter = GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK).youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.DrawCards(1)
        description = "Whenever a Merfolk you control deals combat damage to a player, draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "51"
        artist = "Simon Dominic"
        flavorText = "Where the light falls dim and blue on broken ships, secrets lie unclaimed."
        imageUri = "https://cards.scryfall.io/normal/front/4/3/4316eacf-4b78-4b99-833c-53ecf49a0ae5.jpg?1783935319"
        ruling("2018-01-19", "If Seafloor Oracle is dealt lethal damage at the same time a Merfolk you control deals combat damage to a player, you'll draw a card.")
    }
}
