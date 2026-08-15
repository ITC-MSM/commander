package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Glóin, Dwarf Emissary
 * {2}{R}
 * Legendary Creature — Dwarf Advisor
 * 3/3
 *
 * Whenever you cast a historic spell, create a Treasure token. This ability triggers only once each turn.
 * (Artifacts, legendaries, and Sagas are historic.)
 * {T}, Sacrifice a Treasure: Goad target creature. (Until your next turn, that creature attacks each
 * combat if able and attacks a player other than you if able.)
 */
val GloinDwarfEmissary = card("Glóin, Dwarf Emissary") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Dwarf Advisor"
    power = 3
    toughness = 3
    oracleText = "Whenever you cast a historic spell, create a Treasure token. This ability triggers " +
        "only once each turn. (Artifacts, legendaries, and Sagas are historic.)\n" +
        "{T}, Sacrifice a Treasure: Goad target creature. (Until your next turn, that creature attacks " +
        "each combat if able and attacks a player other than you if able.)"

    triggeredAbility {
        trigger = Triggers.YouCastHistoric
        oncePerTurn = true
        effect = Effects.CreateTreasure(1)
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.Artifact.withSubtype("Treasure"))
        )
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Goad(creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "132"
        artist = "Tomas Duchek"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/868a2aa7-bcaf-409b-8802-d00ee1f2ae77.jpg?1686968993"
    }
}
