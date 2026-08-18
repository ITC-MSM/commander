package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.collectEvidence
import com.wingedsheep.sdk.dsl.namedFromVariable
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Deadly Cover-Up — Murders at Karlov Manor #83
 * {3}{B}{B} · Sorcery · Rare
 *
 * The optional collect-evidence cost records the standard cast choice. On resolution the wipe
 * always happens first. Only the paid branch gathers one card from all opponents' graveyards; its
 * owner is snapshotted before exile, then becomes the per-player context for the graveyard, hand,
 * and library search. Counting the hand subset before moving the chosen cards preserves the exact
 * number of replacement draws.
 */
val DeadlyCoverUp = card("Deadly Cover-Up") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, you may collect evidence 6.\n" +
        "Destroy all creatures. If evidence was collected, exile a card from an opponent's " +
        "graveyard. Then search its owner's graveyard, hand, and library for any number of cards " +
        "with that name and exile them. That player shuffles, then draws a card for each card " +
        "exiled from their hand this way."

    collectEvidence(6)

    spell {
        val extraction = Effects.Pipeline {
            val graveyardCards = gather(
                CardSource.FromZone(
                    zone = Zone.GRAVEYARD,
                    player = Player.EachOpponent,
                    filter = GameObjectFilter.Any,
                ),
                name = "opponentGraveyardCards",
            )
            val seed = chooseExactly(
                1,
                from = graveyardCards,
                chooser = Chooser.SourceController,
                prompt = "Choose a card from an opponent's graveyard to exile",
                name = "seed",
            )
            val owners = captureControllers(seed, name = "owners")
            val cardName = storeCardName(seed, name = "cardName")
            exile(seed)

            forEachCaptured(seed, seed, owners) {
                val matches = gather(
                    CardSource.FromMultipleZones(
                        zones = listOf(Zone.GRAVEYARD, Zone.HAND, Zone.LIBRARY),
                        player = Player.You,
                        filter = GameObjectFilter.Any.namedFromVariable(cardName.key),
                    ),
                    name = "matches",
                )
                val selected = chooseAnyNumber(
                    from = matches,
                    chooser = Chooser.SourceController,
                    prompt = "Choose any number of cards with that name to exile",
                    showAllCards = true,
                    alwaysPrompt = true,
                    name = "selected",
                )
                val selectedFromHand = filter(
                    selected,
                    CollectionFilter.InZone(Zone.HAND),
                    name = "selectedFromHand",
                )
                exile(selected)
                run(ShuffleLibraryEffect(target = EffectTarget.Controller))
                run(
                    Effects.DrawCards(
                        DynamicAmount.VariableReference("${selectedFromHand.key}_count"),
                        EffectTarget.Controller,
                    ),
                )
            }
        }

        effect = Effects.Composite(
            Effects.DestroyAll(GameObjectFilter.Creature),
            ConditionalEffect(Conditions.WasEvidenceCollected, extraction),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "83"
        artist = "Sam Guay"
        imageUri = "https://cards.scryfall.io/normal/front/3/8/3876aa0f-b199-43f5-8a91-c2d620b8ef84.jpg?1783912899"
        ruling(
            "2024-02-02",
            "Unlike many other effects of this kind, Deadly Cover-Up allows its controller to exile " +
                "a basic land.",
        )
        ruling(
            "2024-02-02",
            "If you can't exile enough cards to meet or exceed the required mana value, you can't " +
                "choose to collect evidence at all.",
        )
    }
}
