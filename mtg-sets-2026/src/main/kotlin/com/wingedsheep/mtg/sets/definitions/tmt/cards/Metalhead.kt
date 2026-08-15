package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Metalhead
 * {4}{U}
 * Legendary Artifact Creature — Robot Turtle
 * 4/4
 *
 * When Metalhead enters, return up to one other target artifact or
 * creature to its owner's hand.
 * {R}, Sacrifice another artifact: Put a +1/+1 counter on Metalhead.
 * He gains menace and haste until end of turn.
 */
val Metalhead = card("Metalhead") {
    manaCost = "{4}{U}"
    colorIdentity = "UR"
    typeLine = "Legendary Artifact Creature — Robot Turtle"
    oracleText = "When Metalhead enters, return up to one other target artifact or creature to its owner's hand.\n{R}, Sacrifice another artifact: Put a +1/+1 counter on Metalhead. He gains menace and haste until end of turn."
    power = 4
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val perm = target(
            "other artifact or creature",
            TargetPermanent(optional = true, filter = TargetFilter.CreatureOrArtifact.other())
        )
        effect = Effects.ReturnToHand(perm)
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{R}"),
            Costs.SacrificeAnother(GameObjectFilter.Artifact)
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            .then(Effects.GrantKeyword(Keyword.MENACE, EffectTarget.Self, Duration.EndOfTurn))
            .then(Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self, Duration.EndOfTurn))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "44"
        artist = "Daniel Romanovsky"
        flavorText = "Metalhead simulates dozens of fighting styles, including Foot ninjutsu."
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3c2d8b09-8694-45ab-be01-f8dc17378cf0.jpg?1771586807"
    }
}
