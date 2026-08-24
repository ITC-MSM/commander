package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Electrostatic Field
 * {1}{R}
 * Creature — Wall
 * 0/4
 * Defender
 * Whenever you cast an instant or sorcery spell, this creature deals 1 damage to each opponent.
 */
val ElectrostaticField = card("Electrostatic Field") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Wall"
    oracleText = "Defender\n" +
        "Whenever you cast an instant or sorcery spell, this creature deals 1 damage to each opponent."
    power = 0
    toughness = 4

    keywords(Keyword.DEFENDER)
    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "97"
        artist = "Dan Murayama Scott"
        flavorText = "\"It's both an ingress-denial mechanism and an attractive hallway light!\"\n—Daxiver, Izzet electromancer"
        imageUri = "https://cards.scryfall.io/normal/front/7/0/7096c9a6-2e73-41f8-b20a-b29a9f0b760c.jpg?1783934165"
    }
}
