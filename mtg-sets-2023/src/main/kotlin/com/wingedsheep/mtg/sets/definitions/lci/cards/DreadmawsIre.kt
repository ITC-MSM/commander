package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.GrantTriggeredAbilityEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Dreadmaw's Ire
 * {R}
 * Instant
 * Until end of turn, target attacking creature gets +2/+2 and gains trample and "Whenever this
 * creature deals combat damage to a player, destroy target artifact that player controls."
 */
val DreadmawsIre = card("Dreadmaw's Ire") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Until end of turn, target attacking creature gets +2/+2 and gains trample and " +
        "\"Whenever this creature deals combat damage to a player, destroy target artifact that player controls.\""

    spell {
        val t = target("target", Targets.AttackingCreature)
        effect = Effects.Composite(
            listOf(
                Effects.ModifyStats(2, 2, t),
                Effects.GrantKeyword(Keyword.TRAMPLE, t),
                GrantTriggeredAbilityEffect(
                    ability = TriggeredAbility.create(
                        trigger = Triggers.DealsCombatDamageToPlayer.event,
                        binding = Triggers.DealsCombatDamageToPlayer.binding,
                        effect = Effects.Destroy(EffectTarget.ContextTarget(0)),
                        // "that player controls" — the player just dealt combat damage, resolved
                        // via ControllerPredicate.ControlledByTriggeringPlayer.
                        targetRequirement = TargetPermanent(
                            filter = TargetFilter(GameObjectFilter.Artifact.controlledByTriggeringPlayer())
                        ),
                        descriptionOverride = "Whenever this creature deals combat damage to a player, " +
                            "destroy target artifact that player controls."
                    ),
                    target = t
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "147"
        artist = "Crystal Sully"
        flavorText = "\"That's the fifth ship lost in three days! Can't we build them out of something stronger?\"\n—Malcolm Lee"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/062e00a1-f1dc-4089-b640-800ab781c590.jpg?1782694492"
    }
}
