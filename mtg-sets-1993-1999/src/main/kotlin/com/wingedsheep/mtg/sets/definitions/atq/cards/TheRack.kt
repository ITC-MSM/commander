package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The Rack
 * {1}
 * Artifact
 *
 * As this artifact enters, choose an opponent.
 * At the beginning of the chosen player's upkeep, this artifact deals X damage to that player,
 * where X is 3 minus the number of cards in their hand.
 *
 * The choose-an-opponent-as-it-enters half reuses the existing [EntersWithChoice]`(ChoiceType.OPPONENT)`
 * replacement (same as Cursed Rack / Jihad), storing the chosen player on the permanent under
 * `ChoiceSlot.OPPONENT`. The upkeep trigger is keyed to that chosen player via
 * [Player.ChosenOpponent]: [Triggers.ChosenOpponentUpkeep] (`StepEvent(UPKEEP, ChosenOpponent)`) now
 * resolves in `TriggerMatcher.matchesPlayerForStep` against the source's stored choice, so it fires
 * only on the chosen player's upkeep — not on every player's. Damage and the dynamic amount both read
 * `Player.ChosenOpponent` off the source, so "that player" / "their hand" resolve to the chosen player.
 * X = 3 − cards in their hand (a negative result deals no damage).
 */
val TheRack = card("The Rack") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "As this artifact enters, choose an opponent.\n" +
        "At the beginning of the chosen player's upkeep, this artifact deals X damage to that " +
        "player, where X is 3 minus the number of cards in their hand."

    replacementEffect(EntersWithChoice(ChoiceType.OPPONENT))

    triggeredAbility {
        trigger = Triggers.ChosenOpponentUpkeep
        effect = Effects.DealDamage(
            amount = DynamicAmount.Subtract(
                DynamicAmount.Fixed(3),
                DynamicAmounts.zone(Player.ChosenOpponent, Zone.HAND).count()
            ),
            target = EffectTarget.PlayerRef(Player.ChosenOpponent)
        )
        description = "At the beginning of the chosen player's upkeep, The Rack deals damage to " +
            "that player equal to 3 minus the number of cards in their hand."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "72"
        artist = "Richard Thomas"
        flavorText = "Invented in Mishra's earlier days, the Rack was once his most feared creation."
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec0686ba-1277-4412-a397-7a6227808311.jpg?1562944784"
    }
}
