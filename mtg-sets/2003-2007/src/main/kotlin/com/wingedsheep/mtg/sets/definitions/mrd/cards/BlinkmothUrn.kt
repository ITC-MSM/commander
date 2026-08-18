package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Blinkmoth Urn — Mirrodin #145 (canonical and earliest real-expansion printing)
 * {5} · Artifact
 *
 * At the beginning of each player's first main phase, if this artifact is untapped, that player
 * adds {C} for each artifact they control.
 *
 * The trigger watches every player's precombat main phase. [Effects.ForEachPlayer] rebinds the
 * effect controller to the active player, so both [Player.You] in the artifact count and the mana
 * recipient mean "that player," even on an opponent's turn. [Conditions.SourceIsUntapped] is the
 * intervening-if gate, checked both when the ability would trigger and again when it resolves.
 */
val BlinkmothUrn = card("Blinkmoth Urn") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "At the beginning of each player's first main phase, if this artifact is untapped, " +
        "that player adds {C} for each artifact they control."

    triggeredAbility {
        trigger = Triggers.phase(Step.PRECOMBAT_MAIN, Player.Each)
        interveningIf = Conditions.SourceIsUntapped
        effect = Effects.ForEachPlayer(
            Player.TriggeringPlayer,
            listOf(
                Effects.AddColorlessMana(
                    DynamicAmounts.battlefield(Player.You, GameObjectFilter.Artifact).count()
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "145"
        artist = "David Martin"
        flavorText = "The vedalken embed such urns in their living artifact creations."
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0ff34d0b-a278-4990-beaf-5a64885460db.jpg?1783944528"
        ruling(
            "2004-10-04",
            "The precombat main phase is the first main phase of the turn. All others are postcombat " +
                "main phases, even if they technically occur before combat."
        )
    }
}
