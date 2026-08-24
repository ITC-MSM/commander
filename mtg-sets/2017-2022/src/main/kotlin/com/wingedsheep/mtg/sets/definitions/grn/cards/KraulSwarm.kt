package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kraul Swarm
 * {4}{B}
 * Creature — Insect Warrior
 * 4/1
 * Flying
 * {2}{B}, Discard a creature card: Return this card from your graveyard to your hand.
 */
val KraulSwarm = card("Kraul Swarm") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect Warrior"
    oracleText = "Flying\n" +
        "{2}{B}, Discard a creature card: Return this card from your graveyard to your hand."
    power = 4
    toughness = 1

    keywords(Keyword.FLYING)
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{B}"), Costs.Discard(GameObjectFilter.Creature))
        effect = Effects.ReturnToHandFromGraveyard(EffectTarget.Self)
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "73"
        artist = "Jehan Choo"
        flavorText = "The hive has a long memory. It knows how every member ever died, and to whom it owes the grudge."
        imageUri = "https://cards.scryfall.io/normal/front/4/9/490dc165-b10d-4384-8c13-d7969844b2bb.jpg?1783934174"
    }
}
