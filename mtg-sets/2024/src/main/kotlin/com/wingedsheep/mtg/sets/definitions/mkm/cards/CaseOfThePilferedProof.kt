package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.toSolve
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CreateAdditionalToken
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.ControllerFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Case of the Pilfered Proof — Murders at Karlov Manor #9
 * {1}{W} · Enchantment — Case · Uncommon
 *
 * Whenever a Detective you control enters or is turned face up, put a +1/+1 counter on it.
 * To solve — You control three or more Detectives.
 * Solved — If one or more tokens would be created under your control, those tokens plus a Clue
 * token are created instead.
 *
 * The first line is two triggers, not one, and they differ in more than wording — the same split
 * Perimeter Enforcer needs. The "enters" half is an ordinary
 * [Triggers.entersBattlefield] with a Detective-you-control filter; the "turned face up" half is
 * [Triggers.CreatureTurnedFaceUp], whose filter reads the permanent's *post-flip* characteristics,
 * because a face-down creature is a nameless 2/2 and would never be a Detective at the moment it
 * flips. Both put the counter on `TriggeringEntity` — "on **it**", the creature that arrived or
 * flipped, not on the Case.
 *
 * The Solved line is a static ability in replacement-effect form (CR 702.169b), so its solved gate
 * rides in the replacement's own `restrictions` rather than through `solvedStaticAbility { }`:
 * replacement effects are declared outside the static-ability builder. `CreateAdditionalToken`
 * already models "those tokens plus an additional X instead" — one extra Clue per qualifying
 * creation event however many tokens that event made, which is exactly the printed reading.
 * `inheritTapped` stays false: the printed text has no tapped rider to inherit.
 */
val CaseOfThePilferedProof = card("Case of the Pilfered Proof") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Case"
    oracleText = "Whenever a Detective you control enters or is turned face up, put a +1/+1 " +
        "counter on it.\n" +
        "To solve — You control three or more Detectives. (If unsolved, solve at the beginning " +
        "of your end step.)\n" +
        "Solved — If one or more tokens would be created under your control, those tokens plus a " +
        "Clue token are created instead. (It's an artifact with \"{2}, Sacrifice this token: Draw " +
        "a card.\")"

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.DETECTIVE).youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.TriggeringEntity)
        description = "Whenever a Detective you control enters, put a +1/+1 counter on it."
    }

    triggeredAbility {
        trigger = Triggers.CreatureTurnedFaceUp(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.DETECTIVE)
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.TriggeringEntity)
        description = "Whenever a Detective you control is turned face up, put a +1/+1 counter on it."
    }

    toSolve(Conditions.YouControlAtLeast(3, GameObjectFilter.Creature.withSubtype(Subtype.DETECTIVE)))

    replacementEffect(
        CreateAdditionalToken(
            additionalTokenType = "Clue",
            additionalTokenCount = 1,
            appliesTo = EventPattern.TokenCreationEvent(controller = ControllerFilter.You),
            restrictions = listOf(Conditions.SourceIsSolved)
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "9"
        artist = "Joshua Cairos"
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32927bf2-63c1-4402-99dc-3a0f2f8e0f9c.jpg?1783912927"
    }
}
