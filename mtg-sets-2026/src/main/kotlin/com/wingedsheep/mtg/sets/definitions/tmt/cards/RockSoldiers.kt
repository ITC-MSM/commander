package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Rock Soldiers
 * {3}{R}
 * Artifact Creature — Elemental Soldier
 * 4/3
 *
 * When this creature enters, destroy up to one target noncreature
 * artifact.
 */
val RockSoldiers = card("Rock Soldiers") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Elemental Soldier"
    oracleText = "When this creature enters, destroy up to one target noncreature artifact."
    power = 4
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val artifact = target(
            "noncreature artifact",
            TargetPermanent(
                optional = true,
                filter = TargetFilter(
                    GameObjectFilter(
                        cardPredicates = listOf(
                            CardPredicate.IsArtifact,
                            CardPredicate.Not(CardPredicate.IsCreature),
                        )
                    )
                )
            )
        )
        effect = Effects.Destroy(artifact)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "107"
        artist = "Miklós Ligeti"
        flavorText = "Catching enemies between a rock and a hard place."
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0fada65d-fd8d-4be9-b2bb-ea5cac78fdd7.jpg?1771586954"
    }
}
