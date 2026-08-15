package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Mouser Foundry
 * {1}{R}
 * Artifact
 *
 * When this artifact enters or leaves the battlefield, create a 1/1
 * colorless Robot artifact creature token.
 * {4}{R}, Sacrifice this artifact: It deals 3 damage to target creature.
 */
val MouserFoundry = card("Mouser Foundry") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Artifact"
    oracleText = "When this artifact enters or leaves the battlefield, create a 1/1 colorless Robot artifact creature token.\n{4}{R}, Sacrifice this artifact: It deals 3 damage to target creature."

    val createRobot = CreateTokenEffect(
        power = 1,
        toughness = 1,
        colors = setOf(),
        creatureTypes = setOf("Robot"),
        artifactToken = true,
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08497fc5-1c0e-4c3c-a356-bf4b34bd4c45.jpg?1771590585"
    )

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = createRobot
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = createRobot
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{4}{R}"),
            Costs.SacrificeSelf
        )
        val creature = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(3, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "96"
        artist = "Jakob Eirich"
        flavorText = "\"Hmmm . . . How many should I send? Three? Five? Ten, maybe!\"\n—Baxter Stockman"
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8aae3bd6-a935-44a5-aa4d-a525b00cbf50.jpg?1771502649"
    }
}
