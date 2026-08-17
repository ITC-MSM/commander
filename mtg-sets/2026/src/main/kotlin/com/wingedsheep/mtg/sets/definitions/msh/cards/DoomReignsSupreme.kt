package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Doom Reigns Supreme — Marvel Super Heroes #96
 * {1}{B} · Enchantment — Plan
 *
 * Whenever a Villain you control enters, each opponent loses 1 life and you gain 1 life. Put a
 * plan counter on this enchantment.
 * When the fifth plan counter is put on this enchantment, sacrifice it. When you do, target
 * opponent exiles the top five cards of their library. You may cast up to two spells from among
 * the exiled cards without paying their mana costs.
 *
 * Modeling notes:
 *  - The accumulator's filter is `GameObjectFilter.Permanent.withSubtype(VILLAIN).youControl()`,
 *    not `Creature`: "a Villain you control" selects by **subtype**, so any permanent carrying
 *    the subtype counts — including a noncreature permanent that gains it. (Every Villain
 *    printed so far happens to be a creature, so the two filters agree on today's card pool;
 *    the subtype-shaped one is the one that matches the wording.) Bound [TriggerBinding.ANY]:
 *    the enchantment is a Plan, never a Villain, so no OTHER is needed.
 *  - "Each opponent loses 1 life **and** you gain 1 life" is *not* a drain — the gain is a flat 1
 *    however many opponents lost life, and it happens even if the loss is prevented. So it is
 *    [Effects.LoseLife] at `Player.EachOpponent` plus a fixed [Effects.GainLife], the Kang,
 *    Temporal Tyrant idiom, rather than `Effects.DrainLife`.
 *  - "When the **fifth** plan counter is put on this enchantment" composes from existing
 *    vocabulary, as with the rest of the Plan cycle: a SELF-bound [Triggers.countersPlacedOn] on
 *    [Counters.PLAN] gated by `triggerRestriction = `[Conditions.SourceCounterCountAtLeast]`(PLAN,
 *    5)`. The at-least gate is behaviourally exact because the payoff **sacrifices its own
 *    source**, so the enchantment is gone before a sixth counter could ever land — the threshold
 *    can never fire twice.
 *  - "Sacrifice it. When you do, …" is a mandatory [ReflexiveTriggerEffect] (`optional = false`);
 *    the opponent is targeted as that second stack object is put on the stack (CR 603.12), after
 *    the enchantment is already gone.
 *  - The payoff is the Villainous Wealth pipeline — [Patterns.Library.exileTop] for the top five
 *    of the *target opponent's* library, then keep the nonland cards — but capped at two casts.
 *    (The three older cards of this shape predate the pattern and still hand-roll the
 *    gather + move pair; this one reaches for it.) Filtering to
 *    [GameObjectFilter.Nonland] is what makes "spells" literal: a land among the five is exiled
 *    but can never be cast. The casts happen during this reflexive ability's resolution, so the
 *    controller cannot wait until later in the turn, and anything left uncast stays in exile.
 *  - The cap itself is the one thing that did not exist:
 *    `CastAnyNumberFromCollectionWithoutPayingCostEffect` had no bound, so "up to two" is a new
 *    `maxCasts` parameter on that same primitive (facade
 *    [Effects.CastUpToNFromCollectionWithoutPayingCost]) rather than a new effect type.
 */
val DoomReignsSupreme = card("Doom Reigns Supreme") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Plan"
    oracleText = "Whenever a Villain you control enters, each opponent loses 1 life and you gain " +
        "1 life. Put a plan counter on this enchantment.\n" +
        "When the fifth plan counter is put on this enchantment, sacrifice it. When you do, " +
        "target opponent exiles the top five cards of their library. You may cast up to two " +
        "spells from among the exiled cards without paying their mana costs."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.VILLAIN).youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Composite(
            Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(1),
            Effects.AddCounters(Counters.PLAN, 1, EffectTarget.Self),
        )
        description = "Whenever a Villain you control enters, each opponent loses 1 life and you " +
            "gain 1 life. Put a plan counter on this enchantment."
    }

    triggeredAbility {
        trigger = Triggers.countersPlacedOn(
            filter = GameObjectFilter.Any,
            counterType = Counters.PLAN,
            firstTimeEachTurn = false,
            binding = TriggerBinding.SELF,
        )
        triggerRestriction = Conditions.SourceCounterCountAtLeast(Counters.PLAN, 5)
        effect = ReflexiveTriggerEffect(
            action = Effects.SacrificeTarget(EffectTarget.Self),
            optional = false,
            reflexiveEffect = Effects.Composite(
                Patterns.Library.exileTop(5, EffectTarget.PlayerRef(Player.TargetOpponent)),
                FilterCollectionEffect(
                    from = "exiled_top",
                    filter = CollectionFilter.MatchesFilter(GameObjectFilter.Nonland),
                    storeMatching = "castable",
                ),
                Effects.CastUpToNFromCollectionWithoutPayingCost("castable", maxCasts = 2),
            ),
            reflexiveTargetRequirements = listOf(Targets.Opponent),
            descriptionOverride = "Sacrifice this enchantment. When you do, target opponent " +
                "exiles the top five cards of their library. You may cast up to two spells from " +
                "among the exiled cards without paying their mana costs.",
        )
        description = "When the fifth plan counter is put on this enchantment, sacrifice it. " +
            "When you do, target opponent exiles the top five cards of their library. You may " +
            "cast up to two spells from among the exiled cards without paying their mana costs."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "96"
        artist = "Alexander Gering"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b99894b-c774-45c2-9ee9-4d6a8e7522f5.jpg?1783902944"
    }
}
