package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * The graveyard is counted after the surveil, so a card surveilled away can be the seventh. The
 * "then if" is checked on resolution only — it is not an intervening-if on the trigger.
 */
val EyeOfJace = card("Eye of Jace") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "At the beginning of your upkeep, surveil 1. Then if there are seven or more cards in your " +
        "graveyard, sacrifice this artifact, it deals 2 damage to each opponent, and you gain 2 life. " +
        "(To surveil 1, look at the top card of your library. You may put it into your graveyard.)"

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Composite(
            Patterns.Library.surveil(1),
            ConditionalEffect(
                condition = Conditions.CardsInGraveyardAtLeast(7),
                effect = Effects.Composite(
                    Effects.SacrificeTarget(EffectTarget.Self),
                    Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent)),
                    Effects.GainLife(2)
                )
            )
        )
        description = "At the beginning of your upkeep, surveil 1. Then if there are seven or more cards " +
            "in your graveyard, sacrifice this artifact, it deals 2 damage to each opponent, and you gain 2 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "170"
        artist = "Ioannis Fiore"
        flavorText = "All cadets wore the Eye. None discussed how it followed their every move."
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0edba64a-39cf-4a8d-ba20-4f7da10b6c3d.jpg?1789614864"
        inBooster = false
    }
}
