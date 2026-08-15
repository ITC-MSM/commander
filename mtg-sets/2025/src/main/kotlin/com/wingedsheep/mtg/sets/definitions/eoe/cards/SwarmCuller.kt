package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Swarm Culler
 * {3}{B}
 * Creature — Insect Warrior
 * Flying
 * Whenever this creature becomes tapped, you may sacrifice another creature or artifact. If you do, draw a card.
 * 2/4
 */
val SwarmCuller = card("Swarm Culler") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect Warrior"
    power = 2
    toughness = 4
    oracleText = "Flying\nWhenever this creature becomes tapped, you may sacrifice another creature or artifact. If you do, draw a card."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.BecomesTapped
        val sacrificeTarget = target(
            "another creature or artifact",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.Creature.youControl().or(GameObjectFilter.Artifact.youControl())
                ).other()
            )
        )
        effect = MayEffect(
            Effects.SacrificeTarget(sacrificeTarget) then Effects.DrawCards(1)
        )
        description = "Whenever this creature becomes tapped, you may sacrifice another creature or artifact. If you do, draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "119"
        artist = "April Prime"
        flavorText = "The swarm lacked a natural predator, so the Eumidians became one."
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a8f583c-88b6-4797-b93e-3086845fc326.jpg?1752947034"
    }
}
