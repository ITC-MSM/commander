package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Ominous Parcel
 * {1}
 * Artifact
 * {2}, {T}, Sacrifice this artifact: Search your library for a basic land card, reveal it, put it into your hand, then shuffle.
 * {5}, {T}, Sacrifice this artifact: It deals 4 damage to target creature.
 */
val OminousParcel = card("Ominous Parcel") {
    manaCost = "{1}"
    typeLine = "Artifact"
    oracleText = "{2}, {T}, Sacrifice this artifact: Search your library for a basic land card, reveal it, put it into your hand, then shuffle.\n{5}, {T}, Sacrifice this artifact: It deals 4 damage to target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            destination = SearchDestination.HAND,
            reveal = true
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap, Costs.SacrificeSelf)
        val t = target("target", Targets.Creature)
        effect = Effects.DealDamage(4, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "241"
        artist = "Joe Slucher"
        flavorText = "The rest of Anhelo's assistant arrived over the following week."
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e1691374-e9f2-4a8b-abdb-0bb1dbc96715.jpg?1783923059"
    }
}
