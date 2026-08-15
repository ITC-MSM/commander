package com.wingedsheep.mtg.sets.definitions.big.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Legion Extruder
 * {1}{R}
 * Artifact
 *
 * When this artifact enters, it deals 2 damage to any target.
 * {2}, {T}, Sacrifice another artifact: Create a 3/3 colorless Golem artifact creature token.
 */
val LegionExtruder = card("Legion Extruder") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, it deals 2 damage to any target.\n" +
        "{2}, {T}, Sacrifice another artifact: Create a 3/3 colorless Golem artifact creature token."

    // ETB: deals 2 damage to any target (creature, player, or planeswalker).
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(2, t)
    }

    // {2}, {T}, Sacrifice another artifact: create a 3/3 colorless Golem artifact creature token.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Tap,
            Costs.SacrificeAnother(GameObjectFilter.Artifact)
        )
        // artifactToken flag isn't exposed on the Effects.CreateToken facade, so the raw
        // CreateTokenEffect constructor is used (allowed by FacadeBoundaryTest).
        effect = CreateTokenEffect(
            power = 3,
            toughness = 3,
            colors = setOf(),
            creatureTypes = setOf("Golem"),
            artifactToken = true,
            imageUri = "https://cards.scryfall.io/normal/front/4/0/406e2960-f560-48bb-b4a6-4bd35889a8f8.jpg?1712318018"
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "12"
        artist = "Anton Solovianchyk"
        flavorText = "When the vault door opened, ancient war machines rumbled to life."
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5a077de0-1893-40d0-a499-ee2e6e2258f1.jpg?1739804188"
    }
}
