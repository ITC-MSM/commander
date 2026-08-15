package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.AddCountersToCollectionEffect
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Aerith Rescue Mission
 * {3}{W}
 * Sorcery
 * Choose one —
 * • Take the Elevator — Create three 1/1 colorless Hero creature tokens.
 * • Take 59 Flights of Stairs — Tap up to three target creatures. Put a stun counter on
 *   one of them. (If a permanent with a stun counter would become untapped, remove one from
 *   it instead.)
 *
 * Mode A creates three 1/1 colorless Hero tokens via a single [Effects.CreateToken]
 * (count = 3, the Job-select Hero token shared across this set — same token image URI as
 * Dwarven Castle Guard / White Mage's Staff et al.).
 *
 * Mode B is a single "up to three target creatures" requirement
 * (`TargetCreature(count = 3, optional = true)`) — those targets are locked at cast time, then
 * at resolution:
 *   1. [Effects.TapEachTarget] taps every chosen target.
 *   2. [GatherCardsEffect] from [CardSource.ChosenTargets] collects exactly those tapped
 *      creatures (the mode's `EffectContext.targets`).
 *   3. [SelectFromCollectionEffect] (`ChooseExactly(1)`, controller-chosen) honors the ruling
 *      that the controller picks *which* of the tapped creatures gets the stun, and that the
 *      choice is made at resolution — not at cast time. If zero creatures were targeted (the
 *      "up to" lower bound), the gather is empty and the selection is a silent no-op.
 *   4. [AddCountersToCollectionEffect] places the single stun counter on the chosen creature.
 *
 * This is the faithful "act on N targets, then a player-chosen sub-choice among them at
 * resolution" shape — composed entirely from existing pipeline primitives (cf. Bolster /
 * Blight in `MechanicPatterns`), so no determinism shortcut is taken.
 */
val AerithRescueMission = card("Aerith Rescue Mission") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Take the Elevator — Create three 1/1 colorless Hero creature tokens.\n" +
        "• Take 59 Flights of Stairs — Tap up to three target creatures. Put a stun counter on one of them. " +
        "(If a permanent with a stun counter would become untapped, remove one from it instead.)"

    spell {
        modal(chooseCount = 1) {
            mode("Take the Elevator — Create three 1/1 colorless Hero creature tokens.") {
                effect = Effects.CreateToken(
                    power = 1,
                    toughness = 1,
                    creatureTypes = setOf("Hero"),
                    count = 3,
                    imageUri = "https://cards.scryfall.io/normal/front/d/0/d0657ce1-bf75-4007-ac1b-0623eb263357.jpg?1748704030",
                )
            }
            mode(
                "Take 59 Flights of Stairs — Tap up to three target creatures. Put a stun counter on one of them."
            ) {
                target("creatures", TargetCreature(count = 3, optional = true))
                effect = Effects.Composite(
                    Effects.TapEachTarget(),
                    GatherCardsEffect(source = CardSource.ChosenTargets, storeAs = "aerithTapped"),
                    SelectFromCollectionEffect(
                        from = "aerithTapped",
                        selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                        chooser = Chooser.Controller,
                        storeSelected = "aerithStunned",
                        prompt = "Put a stun counter on one of the tapped creatures",
                        useTargetingUI = true,
                    ),
                    AddCountersToCollectionEffect("aerithStunned", Counters.STUN, 1),
                )
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "5"
        artist = "Hokuyuu"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/3123d16c-e1e6-4659-a7a3-2ec6efc6bf08.jpg?1748705771"
    }
}
