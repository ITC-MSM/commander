package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.solvedActivatedAbility
import com.wingedsheep.sdk.dsl.toSolve
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Case of the Stashed Skeleton — Murders at Karlov Manor #80
 * {1}{B} · Enchantment — Case · Rare
 *
 * When this Case enters, create a 2/1 black Skeleton creature token and suspect it.
 * To solve — You control no suspected Skeletons.
 * Solved — {1}{B}, Sacrifice this Case: Search your library for a card, put it into your hand,
 * then shuffle. Activate only as a sorcery.
 *
 * The Case builds its own obstacle: the Skeleton it makes is the suspected Skeleton that keeps it
 * unsolved, so solving it means getting rid of the Skeleton (chumping with it, sacrificing it) or
 * clearing the designation (Airtight Alibi, Absolving Lammasu). The token is addressed through the
 * `CREATED_TOKENS` pipeline slot, so the suspect lands on the token this resolution just made
 * rather than on any Skeleton.
 *
 * "You control no suspected Skeletons" is a negated existence check, which is the right shape for
 * the corner cases too: a Skeleton that stops being suspected, or a suspected creature that stops
 * being a Skeleton, both satisfy it — and so does controlling no Skeletons at all, which is why an
 * opponent's removal spell solves the Case for you.
 *
 * The Solved tutor is unrestricted ("a card") and sorcery-speed, and sacrifices the Case as part
 * of the cost, so the Case is gone before the search begins.
 */
val CaseOfTheStashedSkeleton = card("Case of the Stashed Skeleton") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Case"
    oracleText = "When this Case enters, create a 2/1 black Skeleton creature token and suspect " +
        "it. (It has menace and can't block.)\n" +
        "To solve — You control no suspected Skeletons. (If unsolved, solve at the beginning of " +
        "your end step.)\n" +
        "Solved — {1}{B}, Sacrifice this Case: Search your library for a card, put it into your " +
        "hand, then shuffle. Activate only as a sorcery."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Effects.CreateToken(
                power = 2,
                toughness = 1,
                colors = setOf(Color.BLACK),
                creatureTypes = setOf("Skeleton"),
                name = "Skeleton",
                imageUri = "https://cards.scryfall.io/normal/front/0/2/02404852-5788-4adb-b2dc-98bf705d8d96.jpg?1783912608"
            ),
            Effects.Suspect(EffectTarget.PipelineTarget(CREATED_TOKENS, 0))
        )
        description = "When this Case enters, create a 2/1 black Skeleton creature token and suspect it."
    }

    toSolve(
        Conditions.YouControl(
            GameObjectFilter.Creature.withSubtype(Subtype.SKELETON).suspected(),
            negate = true
        )
    )

    solvedActivatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.SacrificeSelf)
        timing = TimingRule.SorcerySpeed
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any,
            destination = SearchDestination.HAND
        )
        description = "Search your library for a card, put it into your hand, then shuffle."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "80"
        artist = "Camille Alquier"
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b120cbe-f0af-46c5-863f-03ecadf0435c.jpg?1783912902"

        ruling(
            "2024-02-09",
            "When an effect suspects a creature, it becomes suspected. It gains menace and \"This " +
                "creature can't block\" for as long as it's suspected. It stays suspected until it " +
                "leaves the battlefield or another effect causes it to no longer be suspected."
        )
        ruling(
            "2024-02-09",
            "If a suspected creature loses all abilities, it will lose menace and \"This creature " +
                "can't block\", but it won't stop being suspected."
        )
        ruling(
            "2024-02-09",
            "Being suspected isn't a copiable value. If a permanent becomes a copy of a suspected " +
                "creature, it won't be suspected."
        )
    }
}
