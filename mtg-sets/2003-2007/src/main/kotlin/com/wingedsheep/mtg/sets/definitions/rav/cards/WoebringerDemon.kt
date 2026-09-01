package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Woebringer Demon
 * {3}{B}{B}
 * Creature — Demon
 * 4/4
 *
 * Flying
 * At the beginning of each player's upkeep, that player sacrifices a creature of their choice.
 * If the player can't, sacrifice this creature.
 *
 * "If the player can't" is read off the sacrifice that just happened, not off a board scan:
 * [DynamicAmount.PermanentsSacrificedThisWay] counts what the edict earlier in this same
 * composite actually took, so the fallback fires exactly when nothing was sacrificed. A board
 * count would have to re-derive the same answer a step later, after the creature is already
 * gone.
 *
 * On its own controller's upkeep the Demon is itself a legal sacrifice, so the edict always has
 * something to take and the self-sacrifice clause only bites on an opponent's empty board.
 */
val WoebringerDemon = card("Woebringer Demon") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    oracleText = "Flying\n" +
        "At the beginning of each player's upkeep, that player sacrifices a creature of their " +
        "choice. If the player can't, sacrifice this creature."
    power = 4
    toughness = 4

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EachUpkeep
        effect = Effects.Composite(
            Effects.Sacrifice(
                filter = GameObjectFilter.Creature,
                count = 1,
                target = EffectTarget.PlayerRef(Player.TriggeringPlayer)
            ),
            GatedEffect(
                gate = Gate.WhenCondition(
                    Conditions.CompareAmounts(
                        DynamicAmount.PermanentsSacrificedThisWay,
                        ComparisonOperator.EQ,
                        DynamicAmount.Fixed(0)
                    )
                ),
                then = Effects.SacrificeTarget(EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "113"
        artist = "Daren Bader"
        flavorText = "Each soul he devours adds its hunger to his own."
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f0c758c9-3138-49b2-bebf-cdab5dc68a3d.jpg?1783943660"
    }
}
