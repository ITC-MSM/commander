package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.normalize.Normalizer
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.assay.syntax.separated
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode

/**
 * "Choose one —" and the rows beneath it: a spell, a trigger or an activated ability that offers
 * its controller a choice between whole effects.
 *
 * ## It is a *clause position*, not a line, and that is where the leverage is
 *
 * A modal block is printed as a header row and a run of bullets, and normalization keeps them
 * together as one line (see [Normalizer]) — but nothing about the construct is line-shaped. Oracle
 * prints the identical header mid-sentence, after a trigger's comma ("When this creature enters,
 * choose one —") and after an activated ability's colon ("Sacrifice this artifact: Choose one —").
 * So this family is written the way [Steps]' clauses are, mid-sentence and self-terminating, and it
 * is offered at [Steps.step] — which means every context that already slots a step gains modal
 * abilities without being told, and the ~440 cards that print the header inside a trigger cost the
 * same nothing the 540 that print it alone do.
 *
 * The other half of "lift, don't re-spell" is what a *mode* is made of: a whole sentence from the
 * enclosing cascade. Every verb the grammar can read is a mode for free, and a mode's own targets
 * come with it, which is the shape `Mode` is built for — per-mode requirements are why the type
 * exists (Cryptic Command is the SDK's own worked example).
 *
 * ## A mode is a sentence, and never another modal
 *
 * The bullets slot the cascade's `sentence`, not its `step`, so a mode cannot itself be modal. That
 * is not a restriction imposed for safety — no printed card nests a modal inside a mode — it is what
 * keeps the rule constructible at all: a family reading its own enclosing rule is left recursion,
 * and the kernel would report it as a decline on every modal card in the corpus.
 *
 * ## Four headers, one shape, disjoint by what they can spell
 *
 * `ModalEffect` says "how many" with two numbers, and English says it with four phrases:
 *
 * | Printed | `chooseCount` | `minChooseCount` |
 * |---|---|---|
 * | Choose one — | 1 | 1 |
 * | Choose two — | 2 | 2 |
 * | Choose one or both — | 2 | 1 |
 * | Choose one or more — | *n* | 1 |
 * | Choose up to one — | 1 | 0 |
 *
 * "Choose one or both" and "choose one or more" collide at two modes — "one or both" and "one or more" would both denote `(2, 1)` —
 * so they are made **disjoint by mode count** rather than by ordering an alternation, which is the
 * fix this module's second invariant asks for. "Both" is a word about exactly two things and the
 * corpus agrees without exception: all 56 cards printing "one or both" have two modes and all 21
 * printing "one or more" have three or more. A three-mode `(2, 1)` is therefore a model no header
 * can print, and it declines rather than being spelled with the wrong English.
 *
 * ## What is deliberately not here
 *
 * The header variants that reach a *different* `ModalEffect` field — "choose one that hasn't been
 * chosen", "choose any number", "choose one at random" — are not rows in this list. They are a
 * different question ("which modes may be picked") from the one this family answers ("how many"),
 * and reading them as this one would be the reversible-but-wrong class. They decline and are
 * counted, and "choose one at random" has no field at all, which is an SDK finding rather than a gap
 * in a rule.
 *
 * "Choose up to one" **was** on that list and should never have been: it is the pair (1, 0), which is
 * the same two fields every row above sets, and nothing about which modes may be picked. The write-off
 * had grouped it with its neighbours by where it sits in the printed header rather than by what it
 * denotes. It is a row now — 9 cards, Dreamshackle Geist among them. Its sibling "choose any number"
 * ((*n*, 0), 3 cards) is the same shape and stays out only because nothing has needed it yet.
 */
object Modal {

    /**
     * The modal clauses over one anaphor position.
     *
     * A function of the enclosing cascade's sentence rule for the reason [SelfSteps.retargetable] is
     * a function of its subject: "it" means a different object inside a filtered trigger than it
     * does anywhere else, the distinction exists only while parsing, and a family that registered
     * both readings would be two models for one text. So the vocabulary is written once and
     * instantiated per position — the same treatment, one level up.
     *
     * @param sentence one whole effect sentence in the calling cascade, full stop included.
     * @param tag suffixes the rule names so an ambiguity diagnostic can say which cascade it found.
     */
    fun clauses(sentence: Phrase<CardScript>, tag: String): List<Phrase<CardScript>> {
        val mode = mode(sentence, tag)
        return HEADERS.map { header -> modalRule(header, mode, tag) }
    }

    /**
     * How many modes a header lets its controller pick, given how many are printed.
     *
     * A function of the mode count rather than a pair of constants, because two of the four headers
     * are only English at particular counts — see the disjointness argument on [Modal]. Returning
     * null is a header declining to spell that shape at all.
     */
    private class Header(
        val surface: String,
        val name: String,
        val counts: (modes: Int) -> Pair<Int, Int>?,
    )

    private val HEADERS = listOf(
        Header("choose one", "choose one") { _ -> 1 to 1 },
        Header("choose two", "choose two") { modes -> (2 to 2).takeIf { modes >= 2 } },
        Header("choose one or both", "choose one or both") { modes -> (2 to 1).takeIf { modes == 2 } },
        Header("choose one or more", "choose one or more") { modes -> (modes to 1).takeIf { modes >= 3 } },
        // (1, 0) — declining every mode is legal, and the ability leaves the stack having done
        // nothing (CR 700.2b). Disjoint from every row above by `minChooseCount` alone, at any mode
        // count, so it needs no count guard.
        Header("choose up to one", "choose up to one") { _ -> 1 to 0 },
    )

    /**
     * "• Destroy target artifact." — one row.
     *
     * The bullet is spelled here rather than as the run's separator so that the *first* mode carries
     * one too; a separator-only spelling would print "Choose one —\nDestroy…\n• …", which parses
     * back to nothing and would surface as a mismatch rather than as the missing character it is.
     */
    private fun mode(sentence: Phrase<CardScript>, tag: String): Phrase<Mode> =
        phrase("${Normalizer.BULLET} {body}", name = "a mode$tag") {
            slot("body", sentence)
            build { modeFor(it.value("body")) }
            match { mode -> scriptFor(mode)?.let { bind("body" to it) } }
        }

    private fun modalRule(header: Header, mode: Phrase<Mode>, tag: String): Phrase<CardScript> =
        phrase("${header.surface} —\n{modes}", name = "${header.name}$tag") {
            // `min = 2`: every printed modal card offers at least two modes, and a one-mode run has
            // no separator in it, so all four headers would read it and report as redundancy on
            // every card in the family.
            slot("modes", separated("modes$tag", mode, separator = "\n", min = 2))
            build { bindings ->
                val modes = bindings.value<List<Mode>>("modes")
                val (chooseCount, minChooseCount) = header.counts(modes.size) ?: return@build null
                CardScript(spellEffect = modalEffect(modes, chooseCount, minChooseCount))
            }
            match { script ->
                val modal = script.spellEffect as? ModalEffect ?: return@match null
                val (chooseCount, minChooseCount) = header.counts(modal.modes.size) ?: return@match null
                val rebuilt = CardScript(spellEffect = modalEffect(modal.modes, chooseCount, minChooseCount))
                if (rebuilt != script) return@match null
                bind("modes" to modal.modes)
            }
        }

    /**
     * The two spellings the SDK publishes, and the one it does not.
     *
     * `ModalEffect.chooseOne` and `chooseTwo` are the curated surface for the exact-count headers and
     * this goes through them, as the module's rule asks. There is no facade for the `min < max`
     * shape — no `chooseOneOrMore` exists — so those two headers construct directly, which is the
     * same accommodation [Targets.player] makes for a requirement whose facade exposes no id. Worth
     * a facade; noted rather than added, because adding one is `add-feature` work with the SDK's own
     * bar and this rule reads the same model either way.
     */
    private fun modalEffect(modes: List<Mode>, chooseCount: Int, minChooseCount: Int): ModalEffect = when {
        chooseCount == 1 && minChooseCount == 1 -> ModalEffect.chooseOne(*modes.toTypedArray())
        chooseCount == 2 && minChooseCount == 2 -> ModalEffect.chooseTwo(*modes.toTypedArray())
        else -> ModalEffect(modes, chooseCount = chooseCount, minChooseCount = minChooseCount)
    }

    /**
     * A sentence's script as a mode — its effect and the targets that sentence declared.
     *
     * A mode is exactly the pair `Mode` holds, so this is a destructuring rather than a translation:
     * the sentence's `spellEffect` becomes the mode's effect and its requirements become the mode's,
     * which is what puts Boros Charm's three targets on three modes instead of on the card. A script
     * carrying anything else — a trigger, a cast restriction — is not a mode and refuses.
     *
     * **The description is left at the SDK's default**, and that is a decision rather than an
     * omission. `Mode.description` is "Human-readable description of the mode" whose default is
     * `effect.description`: presentation, never executed, and derived by the SDK from the very
     * effect this rule just read. The alternative — reproducing the printed bullet — is unavailable
     * *by construction*, because the text reaching the grammar has had the card's own name
     * abstracted to `~` and the mode would carry a tilde into a string shown to a player. Cards
     * written by hand spell the printed row out with the name in it, so the differential folds the
     * field; see `Folds.dropModeDescriptions`.
     */
    private fun modeFor(script: CardScript): Mode? {
        val effect = script.spellEffect ?: return null
        if (script != CardScript(spellEffect = effect, targetRequirements = script.targetRequirements)) return null
        return Mode(effect, script.targetRequirements)
    }

    /**
     * …and back, fail-closed: the script [mode] came from, or null when nothing here could have
     * produced it.
     *
     * The reconstruct-and-compare is what refuses a mode carrying a per-mode cost
     * (`additionalManaCost`, Spree's and the tiered spells') or a hand-authored description. Both
     * round-trip invisibly under a matcher that only read the fields it expected, and both mean
     * something the printed row above does not say.
     */
    private fun scriptFor(mode: Mode): CardScript? =
        CardScript(spellEffect = mode.effect, targetRequirements = mode.targetRequirements)
            .takeIf { modeFor(it) == mode }
}
