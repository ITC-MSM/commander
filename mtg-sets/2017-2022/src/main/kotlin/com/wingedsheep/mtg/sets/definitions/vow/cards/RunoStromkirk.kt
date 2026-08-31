package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenCopyOfTargetEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.targets.TargetOther
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Runo Stromkirk // Krothuss, Lord of the Deep — Innistrad: Crimson Vow #246
 * {1}{U}{B} · Legendary Creature — Vampire Cleric 1/4 // Legendary Creature — Kraken Horror 3/5
 *
 * Front — Runo Stromkirk
 *   Flying
 *   When Runo enters, put up to one target creature card from your graveyard on top of your
 *   library.
 *   At the beginning of your upkeep, look at the top card of your library. You may reveal that
 *   card. If a creature card with mana value 6 or greater is revealed this way, transform Runo.
 *
 * Back — Krothuss, Lord of the Deep
 *   Flying
 *   Whenever Krothuss attacks, create a tapped and attacking token that's a copy of another target
 *   attacking creature. If that creature is a Kraken, Leviathan, Octopus, or Serpent, create two of
 *   those tokens instead.
 *
 * Modeling notes:
 *
 *  - **The upkeep trigger is Delver of Secrets' sentence with a different filter.** Nothing moves
 *    zones — the card is looked at and stays on top whether or not it is revealed — so the pipeline
 *    is gather-only, and the "you may reveal" is a `ChooseUpTo(1)` over the looked-at collection
 *    (`showAllCards = true` makes the overlay the *look*, selecting is the *reveal*). The transform
 *    is gated on the **revealed** collection, not the looked-at one: declining to reveal a
 *    six-drop must not flip Runo, and the printed text says "revealed this way".
 *  - **"a creature card with mana value 6 or greater"** is one filter,
 *    `GameObjectFilter.Creature.manaValueAtLeast(6)`, not two clauses — the card must satisfy both
 *    to flip.
 *  - **The ETB target is `optional = true`, not a separate "you may".** "Up to one target" is a
 *    target-count shape (CR 115.2c): casting Runo with an empty graveyard is legal and the trigger
 *    simply chooses nothing. `TargetFilter.CreatureInYourGraveyard` is the same slot Retrieve uses.
 *  - **Krothuss targets "another" attacking creature**, so the requirement is wrapped in
 *    [TargetOther] — Krothuss is itself an attacking creature and would otherwise be a legal
 *    choice for its own trigger.
 *  - **The doubled count is a property of the *target*, read at resolution.**
 *    [CreateTokenCopyOfTargetEffect.count] takes a [DynamicAmount], so the "if that creature is a
 *    Kraken, Leviathan, Octopus, or Serpent" rider is `DynamicAmount.Conditional` over
 *    [Conditions.TargetMatchesFilter] at target index 0 rather than two branches of a
 *    [ConditionalEffect] — one effect, one token template, two possible counts. The subtype test
 *    is a single `withAnySubtype` (an OR), which is what the printed comma list means.
 *  - Per its own ruling the token is *put onto the battlefield* attacking rather than declared as
 *    an attacker, which is exactly what `attacking = true` models — it emits no "attacks" trigger.
 *    The token is **not** sacrificed at end of turn: unlike Calamity, Krothuss prints no such
 *    clause, so `sacrificeAtStep` stays null.
 */
private val RunoStromkirkFront = card("Runo Stromkirk") {
    manaCost = "{1}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Vampire Cleric"
    power = 1
    toughness = 4
    oracleText = "Flying\n" +
        "When Runo enters, put up to one target creature card from your graveyard on top of your " +
        "library.\n" +
        "At the beginning of your upkeep, look at the top card of your library. You may reveal " +
        "that card. If a creature card with mana value 6 or greater is revealed this way, " +
        "transform Runo."

    keywords(Keyword.FLYING)

    // When Runo enters, put up to one target creature card from your graveyard on top of your library.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val fromGraveyard = target(
            "creature card from your graveyard",
            TargetObject(optional = true, filter = TargetFilter.CreatureInYourGraveyard)
        )
        effect = Effects.Move(
            target = fromGraveyard,
            destination = Zone.LIBRARY,
            placement = ZonePlacement.Top,
        )
        description = "When Runo enters, put up to one target creature card from your graveyard " +
            "on top of your library."
    }

    // At the beginning of your upkeep, look at the top card of your library. You may reveal that
    // card. If a creature card with mana value 6 or greater is revealed this way, transform Runo.
    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1)),
                storeAs = "runoLooked",
            ),
            SelectFromCollectionEffect(
                from = "runoLooked",
                selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                storeSelected = "runoRevealed",
                showAllCards = true,
                prompt = "You may reveal the top card of your library",
                selectedLabel = "Reveal",
            ),
            RevealCollectionEffect(from = "runoRevealed", revealToSelf = false),
            ConditionalEffect(
                condition = Conditions.CollectionContainsMatch(
                    "runoRevealed",
                    GameObjectFilter.Creature.manaValueAtLeast(6),
                ),
                effect = TransformEffect(EffectTarget.Self),
            ),
        )
        description = "At the beginning of your upkeep, look at the top card of your library. You " +
            "may reveal that card. If a creature card with mana value 6 or greater is revealed " +
            "this way, transform Runo."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "246"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6c0fca5-b759-4543-95e2-8d712aae5281.jpg?1783924792"
    }
}

private val KrothussLordOfTheDeep = card("Krothuss, Lord of the Deep") {
    manaCost = ""
    colorIdentity = "UB"
    colorIndicator = "UB" // Transformed back face, no mana cost (CR 204).
    typeLine = "Legendary Creature — Kraken Horror"
    power = 3
    toughness = 5
    oracleText = "Flying\n" +
        "Whenever Krothuss attacks, create a tapped and attacking token that's a copy of another " +
        "target attacking creature. If that creature is a Kraken, Leviathan, Octopus, or Serpent, " +
        "create two of those tokens instead."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        val copied = target(
            "another attacking creature",
            TargetOther(baseRequirement = Targets.AttackingCreature)
        )
        effect = CreateTokenCopyOfTargetEffect(
            target = copied,
            count = DynamicAmount.Conditional(
                condition = Conditions.TargetMatchesFilter(
                    GameObjectFilter.Creature.withAnySubtype(
                        "Kraken", "Leviathan", "Octopus", "Serpent"
                    ),
                    targetIndex = 0,
                ),
                ifTrue = DynamicAmount.Fixed(2),
                ifFalse = DynamicAmount.Fixed(1),
            ),
            tapped = true,
            attacking = true,
        )
        description = "Whenever Krothuss attacks, create a tapped and attacking token that's a " +
            "copy of another target attacking creature. If that creature is a Kraken, Leviathan, " +
            "Octopus, or Serpent, create two of those tokens instead."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "246"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/back/f/6/f6c0fca5-b759-4543-95e2-8d712aae5281.jpg?1783924792"

        ruling(
            "2021-11-19",
            "You choose which opponent or opposing planeswalker the token is attacking as you put " +
                "it onto the battlefield. It doesn't have to be the same player or planeswalker " +
                "Krothuss, Lord of the Deep is attacking."
        )
        ruling(
            "2021-11-19",
            "Although the token is attacking, it was never declared as an attacking creature (for " +
                "purposes of abilities that trigger whenever a creature attacks, for example, " +
                "like training)."
        )
        ruling(
            "2021-11-19",
            "The token copies exactly what was printed on the original creature and nothing else " +
                "(unless that permanent is copying something else or is a token). It doesn't copy " +
                "whether that creature has any counters on it or Auras and/or Equipment attached " +
                "to it, or any non-copy effects that changed its power, toughness, types, color, " +
                "and so on."
        )
        ruling(
            "2021-11-19",
            "Any enters-the-battlefield abilities of the copied creature will trigger when the " +
                "token enters the battlefield."
        )
    }
}

val RunoStromkirk: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = RunoStromkirkFront,
    backFace = KrothussLordOfTheDeep,
)
