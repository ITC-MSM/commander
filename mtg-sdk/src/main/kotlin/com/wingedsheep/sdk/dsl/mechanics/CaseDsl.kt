package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.conditions.Condition

/**
 * The Case card mechanic (CR 719, Murders at Karlov Manor).
 *
 * A Case is an enchantment with two special abilities printed before a long dash (CR 719.3):
 *
 * ```
 * To solve — [condition]. (If unsolved, solve at the beginning of your end step.)
 * Solved — [ability text]
 * ```
 *
 * Both lower onto vocabulary the engine already has, which is why Cases add no new ability kind:
 *
 *  - **"To solve"** (CR 719.3a) is exactly "At the beginning of your end step, if [condition] and
 *    this Case is not solved, this Case becomes solved" — a triggered ability with an
 *    intervening-if. [toSolve] writes it.
 *  - **"Solved"** (CR 702.169) is not one ability shape but three, each with its own gate:
 *    a static ability applies "as long as this Case is solved" (702.169b), a triggered ability
 *    "triggers only if this Case is solved" (702.169c — a trigger restriction, so an ability that
 *    triggered while solved still resolves if the Case somehow stops being solved), and an
 *    activated ability may be activated "only if this Case is solved" (702.169d). The three
 *    `solved*Ability` helpers below are the ordinary ability builders with that gate pre-applied,
 *    so a Case author never hand-writes `Conditions.SourceIsSolved`.
 *
 * The solved designation itself (CR 719.3b) is engine state, not an ability and not a copiable
 * value: `Effects.BecomeSolved` stamps it, `Conditions.SourceIsSolved` / `StatePredicate.IsSolved`
 * read it, and it sticks until the permanent leaves the battlefield.
 *
 * A Case's remaining lines — the "When this Case enters" ability, or an always-on static like
 * Case of the Ransacked Lab's cost reduction — are plain `triggeredAbility { }` /
 * `staticAbility { }` blocks: they work whether or not the Case is solved.
 *
 * Example (Case of the Uneaten Feast):
 * ```
 * triggeredAbility {                                   // the unconditional first line
 *     trigger = Triggers.CreatureYouControlEnters
 *     effect = Effects.GainLife(1)
 * }
 * toSolve(Conditions.YouGainedLifeThisTurn(5))
 * solvedActivatedAbility {
 *     cost = AbilityCost.Atom(CostAtom.SacrificeSelf)
 *     effect = …
 * }
 * ```
 */

/**
 * Add a Case's "To solve — [condition]" ability (CR 719.3a): "At the beginning of your end step,
 * if [condition] and this Case is not solved, this Case becomes solved."
 *
 * Both halves of that intervening-if matter and both are checked twice (CR 603.4) — when the
 * trigger would go on the stack and again as it resolves. The `not solved` half is what keeps a
 * solved Case from re-triggering every end step; the [condition] half is what makes a Case that
 * met its condition at end of turn but had it undone before resolution stay unsolved.
 */
fun CardBuilder.toSolve(condition: Condition) {
    triggeredAbility {
        trigger = Triggers.YourEndStep
        interveningIf = Conditions.All(condition, Conditions.Not(Conditions.SourceIsSolved))
        effect = Effects.BecomeSolved()
        description = "To solve — ${condition.description}"
    }
}

/**
 * Add a "Solved — [static ability]" (CR 702.169b): the ability applies as long as this Case is
 * solved. Any [StaticAbilityBuilder.condition] the block sets is ANDed with the solved gate, so a
 * solved ability that carries its own condition keeps both.
 */
fun CardBuilder.solvedStaticAbility(init: StaticAbilityBuilder.() -> Unit) {
    staticAbility {
        init()
        condition = condition?.let { Conditions.All(Conditions.SourceIsSolved, it) }
            ?: Conditions.SourceIsSolved
    }
}

/**
 * Add a "Solved — [triggered ability]" (CR 702.169c): the ability triggers only if this Case is
 * solved.
 *
 * The gate is a [TriggeredAbilityBuilder.triggerRestriction] rather than an intervening-if,
 * because the rule restricts *triggering*, not resolution — an ability that triggered while the
 * Case was solved resolves even if the Case has left the battlefield by then. Any restriction the
 * block sets itself is ANDed with the solved gate.
 */
fun CardBuilder.solvedTriggeredAbility(init: TriggeredAbilityBuilder.() -> Unit) {
    triggeredAbility {
        init()
        triggerRestriction = triggerRestriction?.let { Conditions.All(Conditions.SourceIsSolved, it) }
            ?: Conditions.SourceIsSolved
    }
}

/**
 * Add a "Solved — [activated ability]" (CR 702.169d): the ability may be activated only if this
 * Case is solved. Appends the solved gate to whatever [ActivatedAbilityBuilder.restrictions] the
 * block declares, so a solved ability that is also sorcery-speed-only keeps both.
 */
fun CardBuilder.solvedActivatedAbility(init: ActivatedAbilityBuilder.() -> Unit) {
    activatedAbility {
        init()
        restrictions = restrictions + ActivationRestriction.OnlyIfCondition(Conditions.SourceIsSolved)
    }
}
