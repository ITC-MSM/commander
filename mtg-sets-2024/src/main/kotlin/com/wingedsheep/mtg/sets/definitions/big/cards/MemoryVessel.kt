package com.wingedsheep.mtg.sets.definitions.big.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Memory Vessel
 * {3}{R}{R}
 * Artifact
 * {T}, Exile this artifact: Each player exiles the top seven cards of their library. Until your
 * next turn, players may play cards they exiled this way, and they can't play cards from their
 * hand. Activate only as a sorcery.
 *
 * Per-player impulse: for each player (APNAP order, CR 101.4) gather the top seven of *their*
 * library and exile them, then grant that player permission to play those exiled cards and bar
 * them from playing cards from their hand. Both windows last "until your next turn" — the
 * *activating* player's next turn, the same for everyone. `ForEachPlayer` rebinds the body's
 * controller to each player so `Player.You` and `EffectTarget.Controller` resolve to them, while
 * the grant/restriction executors key the turn-boundary expiry off the source's controller (the
 * activating player) so an opponent's window doesn't lift at the start of their own turn.
 *
 * "Until your next turn" (not "until the end of your next turn") → the may-play window uses the
 * "next upkeep, not this turn's" expiry shape; the can't-play-from-hand restriction uses the
 * matching `Duration.UntilYourNextTurn`, both cleared on the activating player's next untap.
 */
val MemoryVessel = card("Memory Vessel") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Artifact"
    oracleText = "{T}, Exile this artifact: Each player exiles the top seven cards of their " +
        "library. Until your next turn, players may play cards they exiled this way, and they " +
        "can't play cards from their hand. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.ExileSelf)
        timing = TimingRule.SorcerySpeed
        effect = Effects.ForEachPlayer(
            Player.ActivePlayerFirst,
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(DynamicAmount.Fixed(7), Player.You),
                    storeAs = "memoryVesselExiled"
                ),
                MoveCollectionEffect(
                    from = "memoryVesselExiled",
                    destination = CardDestination.ToZone(Zone.EXILE)
                ),
                GrantMayPlayFromExileEffect(
                    from = "memoryVesselExiled",
                    expiry = MayPlayExpiry.UntilControllerStep(Step.UPKEEP, includeCurrentTurn = false)
                ),
                Effects.CantPlayCardsFromHand(EffectTarget.Controller, Duration.UntilYourNextTurn)
            )
        )
        description = "{T}, Exile this artifact: Each player exiles the top seven cards of their " +
            "library. Until your next turn, players may play cards they exiled this way, and " +
            "they can't play cards from their hand. Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "13"
        artist = "Diego Gisbert"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e37a5cd-887d-4b41-97f7-ae0bba85436b.jpg?1739804192"
    }
}
