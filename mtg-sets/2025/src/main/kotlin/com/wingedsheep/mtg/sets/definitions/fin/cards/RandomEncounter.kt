package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.ForEachInCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Random Encounter
 * {4}{R}{R}
 * Sorcery
 * Shuffle your library, then mill four cards. Put each creature card milled this way onto
 * the battlefield. They gain haste. At the beginning of the next end step, return those
 * creatures to their owner's hand.
 * Flashback {6}{R}{R}
 *
 * Resolution pipeline:
 *   - [ShuffleLibraryEffect] shuffles the controller's library.
 *   - Gather the top four cards as "milled" and move them all to the graveyard (the mill).
 *   - Move only the creature cards among "milled" from the graveyard onto the battlefield,
 *     stamping each as entering via this resolution and stashing them as "reanimated".
 *     Non-creature milled cards stay in the graveyard.
 *   - For each reanimated creature, grant haste and schedule a delayed trigger that returns
 *     it to its owner's hand at the beginning of the next end step. Per-creature delayed
 *     triggers (like Morningtide's Light) capture each creature as a concrete entity at
 *     creation time, so the return fires for exactly the creatures put onto the battlefield.
 *
 * Edge cases: zero creatures milled -> nothing enters and no delayed triggers are scheduled;
 * a creature that has already left the battlefield by the next end step is simply not returned
 * (the baked MoveToZone only acts on it if it's still around).
 */
val RandomEncounter = card("Random Encounter") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Shuffle your library, then mill four cards. Put each creature card milled this " +
        "way onto the battlefield. They gain haste. At the beginning of the next end step, return " +
        "those creatures to their owner's hand.\n" +
        "Flashback {6}{R}{R} (You may cast this card from your graveyard for its flashback cost. " +
        "Then exile it.)"

    spell {
        effect = Effects.Composite(
            listOf(
                ShuffleLibraryEffect(),
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(DynamicAmount.Fixed(4)),
                    storeAs = "milled"
                ),
                MoveCollectionEffect(
                    from = "milled",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD)
                ),
                MoveCollectionEffect(
                    from = "milled",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                    filter = GameObjectFilter.Creature,
                    markEnteredViaSourceAbility = true,
                    storeMovedAs = "reanimated"
                ),
                ForEachInCollectionEffect(
                    collection = "reanimated",
                    effect = Effects.Composite(
                        Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self, Duration.Permanent),
                        CreateDelayedTriggerEffect(
                            step = Step.END,
                            effect = Effects.Move(EffectTarget.Self, Zone.HAND)
                        )
                    )
                )
            )
        )
    }

    keywordAbility(KeywordAbility.flashback("{6}{R}{R}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "150"
        artist = "Ben Wootten"
        imageUri = "https://cards.scryfall.io/normal/front/3/6/3618e283-2df9-4eb9-97b0-96b55ee31cc0.jpg?1748706320"
    }
}
