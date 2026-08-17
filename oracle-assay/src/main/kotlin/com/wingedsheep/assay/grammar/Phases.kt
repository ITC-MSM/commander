package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.alternate
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.dsl.Triggers as SdkTriggers

/**
 * "At the beginning of **your upkeep**, …" — the *when* clause of a step trigger, as one vocabulary.
 *
 * ## Why this is a vocabulary and not thirteen rules
 *
 * The SDK has already done the factoring. `dsl.Triggers.phase(step, player, binding)` is the one
 * language for "at the beginning of a step", its named constants (`YourUpkeep`, `EachEndStep`,
 * `BeginCombat`, …) are calls to it with every argument frozen, and its own KDoc says to "reach for
 * this factory for any other combination". [Triggers] was calling the frozen constants — thirteen
 * whole-prefix rules, one per printed sentence — so "at the beginning of each opponent's end step"
 * was a rule nobody had written rather than a *pair of words* nobody had slotted. This file is that
 * pair of words: a step noun, a whose-turn layer, and their product.
 *
 * It is the [Library] band's lesson on a `TriggerSpec` rather than on a `Patterns` recipe. Before
 * adding a rule beside an existing one, check whether the two differ only in an argument the facade
 * already takes.
 *
 * ## The whose-turn layer is `Player.possessive`, not a table
 *
 * Every spelling this position needs — "your", "each player's", "each opponent's", "the chosen
 * player's", "enchanted player's" — is already derived by `Player.possessive`, which exists so zone
 * and step descriptions do not each restate it. So [possessiveRule] takes the `Player` and asks the
 * SDK how it is spelled, and the two halves of the rule are one definition. Restating the table here
 * would agree with the SDK exactly until someone added a reference to it.
 *
 * What the layer deliberately does *not* do is range over the whole of `Player`: the members are an
 * explicit list, because `possessive` is total ("target player's", "defending player's") and a step
 * whose turn belongs to a *target* is a sentence no card writes. The derivation owns the spelling;
 * the family owns the membership.
 *
 * ## Three frames, because English has three
 *
 * - **Possessive** — `"{whose} {step}"`. The ordinary form, and the only one that takes the
 *   whose-turn layer as a slot.
 * - **All players** — `"each upkeep"`, `"each end step"`, `"each combat"`. `Player.Each` is spelled
 *   *both* ways ("each upkeep" beside "each player's upkeep"), and which is the majority flips with
 *   the step: 100 lines to 84 for the upkeep, 98 to 22 for the end step, and 0 to 13 for the draw
 *   step, where only the possessive form is ever printed. One rule per step with its own
 *   [com.wingedsheep.assay.syntax.PhraseBuilder.alsoSpelled] list is therefore the honest shape —
 *   the same reasoning [TopOfLibrary]'s impulse durations record, where the canonical word order
 *   flips with the duration. `Player.Each` is consequently absent from [POSSESSIVE], so exactly one
 *   rule can print each of these models.
 * - **Combat** — `"combat on your turn"`, `"combat on each opponent's turn"`. The beginning-of-combat
 *   trigger names the *phase* rather than a step, and Oracle puts whose turn it is in a trailing
 *   clause instead of a possessive: no card prints "your combat". So `Step.BEGIN_COMBAT` is absent
 *   from [stepNoun] and reachable only here, which is what stops the possessive frame printing a
 *   sentence that does not exist.
 *
 * ## Two rows that are one model with the wrong number of words
 *
 * - **`"the end step"`** (62 lines) is the pre-2015 templating for `Player.Each` — Skizzik's golden
 *   reads it as `Triggers.EachEndStep` — so it is a spelling of the all-players rule and not a
 *   fourth frame.
 * - **`"each of your postcombat main phases"`** (9 lines) is `Player.You` again: the SDK has one
 *   `Step.POSTCOMBAT_MAIN`, so the distributive plural and "your second main phase" (32 lines) are
 *   the same model, and the majority spelling prints. If a card ever turns on the difference between
 *   *the second* main phase and *each* postcombat one, that is an SDK gap to report and not a second
 *   rule to add here.
 *
 * ## The attached frame, and what it declines
 *
 * `"the upkeep of enchanted creature's controller"` is `TriggerBinding.ATTACHED` — the SDK's own
 * worked example for [SdkTriggers.phase], and Unstable Mutation's and Lingering Death's goldens read
 * it exactly that way. The binding is a third axis of the same factory, so it is one more frame
 * rather than a family of its own.
 *
 * Nine lines of it still decline, and the reason is not in this file: they name a different
 * attachment noun — "enchanted **land**'s controller", "enchanted **enchantment**'s controller" —
 * and the model has nowhere to put that word, because which noun an Aura prints is decided by its
 * `enchant` line. It is printed shape, so it belongs to `normalize/`, whose attachment pass
 * canonicalizes the *adjective* ("equipped" → "enchanted") but fixes the noun at "creature". Widening
 * that pass is a normalization change that reaches every "enchanted creature" in the grammar, so it
 * is named here rather than approximated: this is a write-off with an expiry date, in the sense
 * [Replacements]' condition write-off was.
 *
 * ## What this family is worth, measured rather than claimed
 *
 * "At the beginning …" is the third row of the tail ranking — 197 cards blocked, 104 sole-blocked,
 * 200 lines — and it is a **clause position at the front of its line**, which is the shape the
 * [Durations] band warns about: the ranking counts the cards whose *first* unreadable clause is this
 * one, and for an opening clause that is every card whose sentence the grammar cannot read at all.
 * Substituting a known-good prefix into those 200 lines leaves 17 of them parsing. The payloads
 * behind them are four other bands: "sacrifice ~" as a bare step (the grammar has only "sacrifice ~
 * unless you pay …"), the delayed trigger ("at the beginning of the **next** end step", 91 lines +
 * 55 for "your next upkeep", which is a `CreateDelayedTriggerEffect` inside a sentence rather than a
 * line prefix), the triggering **player** as a subject ("that player draws a card" — the each-player
 * steps almost all have one), and an intervening-if over a step trigger.
 *
 * So this band ships the position, not the 197. What it buys is that all four of those families land
 * on every step spelling the day one of them is written.
 */
object Phases {

    /**
     * The step nouns a possessive or attached clause can name.
     *
     * `Step.BEGIN_COMBAT` is deliberately absent — see the combat frame above — and so is
     * `Step.UNTAP`, which no card in the corpus triggers on. The main phases carry both of their
     * printed names: "first"/"second" is the majority by 64:3 and 32:9, and "precombat"/"postcombat"
     * parses to the same model without printing.
     */
    private val stepNoun: Phrase<Step> = oneOf(
        "a step",
        constant("upkeep", Step.UPKEEP),
        constant("draw step", Step.DRAW),
        constant("end step", Step.END),
        constant("first main phase", Step.PRECOMBAT_MAIN),
        constant("precombat main phase", Step.PRECOMBAT_MAIN, canonical = false),
        constant("second main phase", Step.POSTCOMBAT_MAIN),
        constant("postcombat main phase", Step.POSTCOMBAT_MAIN, canonical = false),
    )

    /**
     * The same nouns pluralized, for the distributive spelling.
     *
     * Reachable only from [eachOfYours], which never prints, so nothing here is canonical in the
     * sense the rest of the grammar is — but each row still round-trips inside its own leaf, which is
     * what stops "each of your first main phases" being read as a step it does not name.
     */
    private val pluralStepNoun: Phrase<Step> = oneOf(
        "a step, plural",
        constant("upkeeps", Step.UPKEEP),
        constant("draw steps", Step.DRAW),
        constant("end steps", Step.END),
        constant("first main phases", Step.PRECOMBAT_MAIN),
        constant("precombat main phases", Step.PRECOMBAT_MAIN, canonical = false),
        constant("second main phases", Step.POSTCOMBAT_MAIN),
        constant("postcombat main phases", Step.POSTCOMBAT_MAIN, canonical = false),
    )

    /**
     * The players whose turn a possessive clause can name.
     *
     * `Player.Each` is absent on purpose: it is spelled two ways and the majority flips with the
     * step, so its rows live in [allPlayers] where each one can choose. Every other member here has
     * exactly one printed spelling, which is what makes them a slot rather than rows.
     */
    private val POSSESSIVE: List<Player> = listOf(
        Player.You,
        Player.EachOpponent,
        Player.ChosenOpponent,
        Player.EnchantedPlayer,
    )

    /**
     * `"{whose} {step}"` — one rule per player, with the step as the slot.
     *
     * The `match` half **reconstructs the whole `TriggerSpec`** rather than reading the step out of
     * it: a spec whose binding is `ATTACHED`, or whose event is not a `StepEvent` at all, has to
     * refuse to print here rather than print a sentence that drops the difference. That is the same
     * fail-closed shape [Triggers.triggerRule] uses, one level down.
     */
    private fun possessiveRule(player: Player): Phrase<TriggerSpec> =
        phrase("${player.possessive} {step}", name = "${player.possessive} step") {
            slot("step", stepNoun)
            build { SdkTriggers.phase(it.value("step"), player) }
            match { spec ->
                val step = (spec.event as? EventPattern.StepEvent)?.step ?: return@match null
                if (SdkTriggers.phase(step, player) != spec) return@match null
                bind("step" to step)
            }
        }

    /**
     * One all-players step, in the spelling the corpus prints most and the ones it prints as well.
     *
     * A row rather than a slot because the choice of canonical is a property of the *step*, not of
     * the player — and [com.wingedsheep.assay.syntax.PhraseBuilder.alsoSpelled] rather than sibling
     * rules because the extra spellings are the same rule with a word somewhere else, and a copied
     * `build`/`match` pair is two halves that agree until someone edits one.
     */
    private fun allPlayersRule(step: Step, canonicalSurface: String, vararg spellings: String): Phrase<TriggerSpec> =
        phrase(canonicalSurface, name = canonicalSurface) {
            spellings.forEach { alsoSpelled(it, it) }
            build { SdkTriggers.phase(step, Player.Each) }
            match { if (it == SdkTriggers.phase(step, Player.Each)) bind() else null }
        }

    /**
     * The steps that print an all-players form, each with its own canonical.
     *
     * The counts behind the choices are in this file's header. "each combat" is the only spelling of
     * `phase(BEGIN_COMBAT, Each)` anyone prints, and the draw step and first main phase are only ever
     * possessive — which is exactly why the canonical cannot be one template with the step slotted.
     */
    private val allPlayers: List<Phrase<TriggerSpec>> = listOf(
        allPlayersRule(Step.UPKEEP, "each upkeep", "each player's upkeep"),
        allPlayersRule(Step.END, "each end step", "each player's end step", "the end step"),
        allPlayersRule(Step.BEGIN_COMBAT, "each combat"),
        allPlayersRule(Step.DRAW, "each player's draw step"),
        allPlayersRule(Step.PRECOMBAT_MAIN, "each player's first main phase"),
    )

    /**
     * `"combat on {whose} turn"` — the beginning-of-combat trigger's own frame.
     *
     * The whose-turn layer is the same `Player.possessive` derivation the possessive frame uses, so
     * the two frames disagree about word order and about nothing else.
     */
    private fun combatRule(player: Player): Phrase<TriggerSpec> =
        constant("combat on ${player.possessive} turn", SdkTriggers.phase(Step.BEGIN_COMBAT, player))

    /**
     * `"the {step} of enchanted creature's controller"` — `TriggerBinding.ATTACHED`.
     *
     * `Player.You` is the SDK's own reading of this sentence and not an approximation of it: the
     * binding is what re-scopes "you" to the attached permanent's controller, which is why
     * `phase(Step.UPKEEP, Player.You, ATTACHED)` is what Unstable Mutation's golden writes. Building
     * the player into the row rather than slotting it is therefore a statement about the model, not a
     * shortcut — a step whose binding is `ATTACHED` has no second player it could name.
     */
    private val attached: Phrase<TriggerSpec> =
        phrase("the {step} of enchanted creature's controller", name = "an attached step") {
            slot("step", stepNoun)
            build { SdkTriggers.phase(it.value("step"), Player.You, TriggerBinding.ATTACHED) }
            match { spec ->
                val step = (spec.event as? EventPattern.StepEvent)?.step ?: return@match null
                if (SdkTriggers.phase(step, Player.You, TriggerBinding.ATTACHED) != spec) return@match null
                bind("step" to step)
            }
        }

    /**
     * `"each of your postcombat main phases"` — the distributive plural, which parses and never
     * prints.
     *
     * It is `Player.You` and the possessive frame's model, so the whole rule is the possessive one
     * with the noun pluralized and the determiner moved. Registering it as a sibling rather than as
     * an `alsoSpelled` on [possessiveRule] is what the shape forces: the two spellings differ in both
     * halves of the template, so there is no derivation from one to the other of the kind
     * [Durations.fronted] is.
     */
    private val eachOfYours: Phrase<TriggerSpec> = alternate(
        phrase("each of your {step}", name = "each of your steps") {
            slot("step", pluralStepNoun)
            build { SdkTriggers.phase(it.value("step"), Player.You) }
            match { spec ->
                val step = (spec.event as? EventPattern.StepEvent)?.step ?: return@match null
                if (SdkTriggers.phase(step, Player.You) != spec) return@match null
                bind("step" to step)
            }
        }
    )

    /**
     * The whole *when* clause of a step trigger — everything an "at the beginning of …" prefix can
     * name.
     *
     * [Triggers] slots this once, so a row added here reaches the plain step trigger and the
     * graveyard-zoned one together, and will reach every later sentence that opens on a step.
     */
    val phase: Phrase<TriggerSpec> = oneOf(
        "a step",
        POSSESSIVE.map(::possessiveRule) +
            allPlayers +
            listOf(
                combatRule(Player.You),
                combatRule(Player.EachOpponent),
                attached,
                eachOfYours,
            ),
    )
}
