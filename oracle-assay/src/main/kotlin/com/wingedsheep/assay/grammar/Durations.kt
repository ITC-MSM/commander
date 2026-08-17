package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.PhraseBuilder

/**
 * Where a durational sentence puts the words "until end of turn".
 *
 * Oracle prints one meaning in two positions. The duration usually trails —
 * "Target creature gets +3/+3 **until end of turn**." — and 823 lines on 810 cards front it instead:
 * "**Until end of turn,** target creature gets +1/+1 for each creature you control and gains
 * trample." Against 5,370 trailing occurrences on 4,862 cards, trailing is the canonical spelling by
 * a factor of six, so this file's whole job is to let the *same rule* read the fronted spelling
 * without ever printing it. A card printed that way comes back as a
 * [com.wingedsheep.assay.gate.LineVerdict.VARIANT] — the reading was right, only the word order
 * moved.
 *
 * ## Why this is a position and not a family of rules
 *
 * The fronted duration is a **clause position**, in the sense [SelfSteps]' three anaphors are: the
 * distinction exists in the text and nowhere in the model, so no remap on a built script could
 * recover it, and every durational sentence in the grammar has both spellings whether or not some
 * card happens to print each one. Writing the fronted forms as sibling rules would mean a second
 * copy of every `build` and `match` in [Steps], [SelfSteps], [Continuations], [Amounts] and
 * [CreatureTypes] — halves that agree until someone edits one of them, which is the drift the
 * bidirectional discipline exists to make impossible.
 *
 * So the second spelling is *derived* from the first ([fronted]) and registered on the same rule
 * ([PhraseBuilder.alsoSpelled]). One line per durational rule, the derivation checked at
 * construction, and a rule that later changes what it means cannot change one spelling and miss the
 * other. The kernel knows only that a rule may have more than one surface form; the words "until
 * end of turn" are this file's business alone.
 *
 * ## What this does *not* cover, and why each is separate
 *
 * - **A rule whose only printed spelling is fronted.** [Combat]'s attack-and-block tax and
 *   [TopOfLibrary]'s two cross-turn impulse durations print fronted on every card that has them, so
 *   there fronting is the *canonical* form and stays spelled in the template. Which order is
 *   canonical is a fact about the corpus and it flips with the duration — see `TopOfLibrary`'s
 *   `impulseRule` for the counts.
 * - **Other durations.** "Until your next turn," (67 declined lines), "Until the end of your next
 *   turn," (44) and "Until end of combat," (2) are different `Duration`s, and today they are
 *   different sentences rather than a slot in this one — the same reasoning `Steps.pumpTargetPermanent`
 *   records for the trailing side. When a rule takes a duration as a slot, this derivation is what
 *   it should grow a row for, not what it should route around.
 * - **The payload.** Fronting is this family's own span and nothing more, and measuring that was
 *   worth more than the rules: of the 266 corpus lines that decline *at* "Until end of turn,", only
 *   five parse once the duration is moved to the back. The rest decline again on what follows — 54
 *   animate a permanent into a creature, 42 grant a quoted ability, 32 set base power and toughness,
 *   14 pump by a count. Those are the bands behind this one, and each of them lands in both
 *   positions the day it is written, which is what this file buys.
 */
object Durations {

    /** The duration as a durational rule's template spells it. */
    const val TRAILING = " until end of turn"

    /** The same duration fronted, as the sentence it opens spells it. */
    private const val FRONTED = "until end of turn, "

    /** Whether [template] is a sentence this position applies to. */
    fun isDurational(template: String): Boolean = template.endsWith(TRAILING)

    /**
     * The fronted spelling of a trailing durational [template].
     *
     * Fronting is into the template's **last** sentence rather than onto its front, because a rule
     * may span two of them: "Choose a creature type. Each creature you control becomes that type
     * until end of turn." fronts as "Choose a creature type. Until end of turn, each creature you
     * control becomes that type." — which is what the duration scopes over and what Oracle prints.
     * Prefixing the whole template would produce a sentence no card prints and a scope no card
     * means. For the one-sentence rules that are the majority, the two definitions coincide.
     */
    fun fronted(template: String): String {
        require(isDurational(template)) {
            "\"$template\" is not a durational template — it must end in \"$TRAILING\""
        }
        val body = template.dropLast(TRAILING.length)
        val lastSentence = body.lastIndexOf(SENTENCE_BREAK)
        val at = if (lastSentence < 0) 0 else lastSentence + SENTENCE_BREAK.length
        return body.substring(0, at) + FRONTED + body.substring(at)
    }

    /**
     * The same sentence break [com.wingedsheep.assay.syntax.SentenceCase] knows about, as it appears
     * inside a template. Templates are written mid-sentence, so a full stop in one starts a second
     * sentence exactly as it does in a printed line.
     */
    private const val SENTENCE_BREAK = ". "
}

/**
 * Registers this durational rule's fronted spelling: one line, derived from the rule's own template,
 * parsing to the same model and never printing.
 *
 * It *requires* a trailing duration rather than quietly doing nothing, so a rule that is not
 * durational fails at construction — every rule here is built during object initialization, which
 * makes that failure the first thing any test run reports. A generic helper whose template may or
 * may not be durational asks [Durations.isDurational] first; see `Steps.parameterizedGroupStep`,
 * which is the one shape both kinds of sentence go through.
 */
fun PhraseBuilder<*>.frontedDuration() {
    alsoSpelled(Durations.fronted(template), "${ruleName ?: template} (duration fronted)")
}
