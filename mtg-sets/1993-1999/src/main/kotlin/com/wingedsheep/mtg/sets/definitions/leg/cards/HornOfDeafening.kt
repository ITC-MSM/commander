package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PreventionScope

/**
 * Horn of Deafening
 * {4}
 * Artifact
 *
 * {2}, {T}: Prevent all combat damage that would be dealt by target creature this turn.
 */
val HornOfDeafening = card("Horn of Deafening") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{2}, {T}: Prevent all combat damage that would be dealt by target creature this turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.PreventAllDamageDealtBy(creature, scope = PreventionScope.CombatOnly)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "280"
        artist = "Dan Frazier"
        flavorText = "\"A blast, an echo . . . then silence.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/7/17eff8d9-86de-4f19-bf00-5f20dc1373d4.jpg?1783948028"
    }
}
