package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Biotech Specialist
 * {R}{G}
 * Creature — Insect Scientist
 * 1/3
 *
 * When this creature enters, create a Lander token. (It's an artifact with "{2}, {T}, Sacrifice this token: Search your library for a basic land card, put it onto the battlefield tapped, then shuffle.")
 * Whenever you sacrifice an artifact, this creature deals 2 damage to target opponent.
 */
val BiotechSpecialist = card("Biotech Specialist") {
    manaCost = "{R}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Insect Scientist"
    power = 1
    toughness = 3
    oracleText = "When this creature enters, create a Lander token. (It's an artifact with \"{2}, {T}, Sacrifice this token: Search your library for a basic land card, put it onto the battlefield tapped, then shuffle.\")\nWhenever you sacrifice an artifact, this creature deals 2 damage to target opponent."

    // ETB: create a Lander token
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateLander()
    }

    // Whenever you sacrifice an artifact, this creature deals 2 damage to target opponent
    triggeredAbility {
        trigger = Triggers.YouSacrificeOneOrMore(GameObjectFilter.Artifact)
        val target = target("target opponent", Targets.Opponent)
        effect = Effects.DealDamage(2, target)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "214"
        artist = "Alexandre Honoré"
        flavorText = "\"Modify these alleles and try again.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/2/127c221f-94e7-4a0e-a7a6-79ef399862d3.jpg?1752947432"
    }
}
