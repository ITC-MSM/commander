package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.solvedActivatedAbility
import com.wingedsheep.sdk.dsl.toSolve
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Case of the Filched Falcon — Murders at Karlov Manor #44
 * {U} · Enchantment — Case · Uncommon
 *
 * When this Case enters, investigate.
 * To solve — You control three or more artifacts.
 * Solved — {2}{U}, Sacrifice this Case: Put four +1/+1 counters on target noncreature artifact.
 * It becomes a 0/0 Bird creature with flying in addition to its other types.
 *
 * The Case investigates itself most of the way to solved: the Clue it makes is one of the three
 * artifacts, and it is still there to be animated later.
 *
 * "In addition to its other types" is why the animate keeps `addTypes = {"ARTIFACT"}` and no
 * `removeTypes`: per the ruling the artifact retains every type, subtype and supertype it had, so
 * a Clue stays a Clue and a Vehicle stays a Vehicle — it is only its base power and toughness that
 * are overwritten with 0/0 (which is why crewing an animated Vehicle afterwards can't restore its
 * printed P/T). The four counters are what keep it alive; without them the state-based actions
 * would bin a 0/0 immediately, so they are placed in the same resolution.
 *
 * `Duration.Permanent` — the printed text has no "until end of turn", and the Case is sacrificed
 * paying the cost, so nothing is left to hold a source-keyed duration open.
 */
val CaseOfTheFilchedFalcon = card("Case of the Filched Falcon") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Case"
    oracleText = "When this Case enters, investigate. (Create a Clue token. It's an artifact with " +
        "\"{2}, Sacrifice this token: Draw a card.\")\n" +
        "To solve — You control three or more artifacts. (If unsolved, solve at the beginning of " +
        "your end step.)\n" +
        "Solved — {2}{U}, Sacrifice this Case: Put four +1/+1 counters on target noncreature " +
        "artifact. It becomes a 0/0 Bird creature with flying in addition to its other types."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Investigate()
    }

    toSolve(Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact))

    solvedActivatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{U}"), Costs.SacrificeSelf)
        target = TargetPermanent(filter = TargetFilter(GameObjectFilter.Artifact.notCreature()))
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 4, EffectTarget.ContextTarget(0)),
            Effects.BecomeCreature(
                target = EffectTarget.ContextTarget(0),
                power = 0,
                toughness = 0,
                keywords = setOf(Keyword.FLYING),
                creatureTypes = setOf("Bird"),
                addTypes = setOf("ARTIFACT"),
                duration = Duration.Permanent
            )
        )
        description = "Put four +1/+1 counters on target noncreature artifact. It becomes a 0/0 " +
            "Bird creature with flying in addition to its other types."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "44"
        artist = "Julia Metzger"
        imageUri = "https://cards.scryfall.io/normal/front/2/6/266be5bd-71ba-4511-8b71-d0b03885a28d.jpg?1783912914"

        ruling("2024-02-09", "The artifact retains any types, subtypes, or supertypes it has.")
        ruling(
            "2024-02-09",
            "If the target artifact is an attached Equipment, it becomes unattached. If an " +
                "Equipment without reconfigure becomes an artifact creature, it can't be attached " +
                "to another creature."
        )
        ruling(
            "2024-02-09",
            "If the target noncreature artifact is a Vehicle, its power and toughness will be set " +
                "to 0/0. Crewing that Vehicle will not restore its power and toughness."
        )
        ruling(
            "2024-02-09",
            "The resulting artifact creature will be able to attack on your turn if it's been " +
                "under your control continuously since the turn began. That is, it doesn't matter " +
                "how long it's been a creature, just how long it's been on the battlefield."
        )
    }
}
