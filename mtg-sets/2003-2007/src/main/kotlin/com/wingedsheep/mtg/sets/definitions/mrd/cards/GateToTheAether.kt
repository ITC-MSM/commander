package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Gate to the Aether
 * {6}
 * Artifact
 * At the beginning of each player's upkeep, that player reveals the top card of their library. If
 * it's an artifact, creature, enchantment, or land card, the player may put it onto the battlefield.
 */
val GateToTheAether = card("Gate to the Aether") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "At the beginning of each player's upkeep, that player reveals the top card of " +
        "their library. If it's an artifact, creature, enchantment, or land card, the player may " +
        "put it onto the battlefield."

    triggeredAbility {
        trigger = Triggers.EachUpkeep
        effect = Effects.Pipeline {
            val top = gather(CardSource.TopOfLibrary(DynamicAmount.Fixed(1), Player.TriggeringPlayer))
            reveal(top)
            val permanent = filter(top, GameObjectFilter.Permanent)
            ifNotEmpty(permanent) {
                run(
                    MayEffect(
                        effect = Effects.Move(
                            EffectTarget.PipelineTarget(permanent.key),
                            Zone.BATTLEFIELD,
                            controllerOverride = EffectTarget.PlayerRef(Player.TriggeringPlayer)
                        ),
                        descriptionOverride = "Put the revealed permanent card onto the battlefield?",
                        decisionMaker = EffectTarget.PlayerRef(Player.TriggeringPlayer)
                    )
                )
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "174"
        artist = "Pete Venters"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c28fd840-e633-46d3-991a-b9fea95a2f28.jpg?1783944521"
    }
}
