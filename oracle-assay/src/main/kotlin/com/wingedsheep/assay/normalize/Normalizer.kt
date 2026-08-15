package com.wingedsheep.assay.normalize

import com.wingedsheep.assay.corpus.OracleFace

/**
 * Scryfall Oracle text → canonical ability lines, **and back**.
 *
 * The touchstone compares against normalized text, so normalization is load-bearing: if a pass
 * throws information away, the round trip stops being a proof and becomes a formality — you can
 * always pass a round trip by normalizing hard enough. Every pass here is therefore *invertible by
 * construction*: what it removes or rewrites is recorded on the [NormalizedFace] it produces, and
 * [NormalizedFace.restore] replays the inverses in reverse order to rebuild the original bytes.
 *
 * Passes, in order:
 *
 * | Pass | Forward | Inverse |
 * |---|---|---|
 * | Reminder text | strip ` (…)` spans | re-insert at the recorded offsets (or regenerate — see [Reminders]) |
 * | Self-reference | the face's own name → `~`, longest-match first | put the recorded surface form back |
 * | Ability split | one ability per line | join with `\n` |
 *
 * Two passes named in the design are handled elsewhere on purpose:
 *
 * - **Faces** are split by the corpus reader ([OracleFace]), because the split is Scryfall's own
 *   and carries no information of ours to lose.
 * - **Symbols** (`{T}`, `{2}{U}`, `[+1]`) are lexed by the grammar's leaf rules rather than
 *   rewritten here. That is the stronger form of "never as prose": nothing moves, so there is no
 *   inverse to get wrong.
 *
 * Sentence case is likewise not a pass — see [com.wingedsheep.assay.syntax.SentenceCase].
 */
object Normalizer {

    /**
     * Reminder text is stripped with any *spaces* in front of it but never a newline, so a
     * reminder that occupies a whole line leaves an empty line behind rather than silently
     * changing the ability count.
     */
    private val REMINDER_RE = Regex("""[ ]*\([^)]*\)""")

    fun normalize(face: OracleFace): NormalizedFace {
        val (stripped, reminders) = stripReminders(face.oracleText)
        val (abstracted, selfRefs) = abstractSelfReference(stripped, selfReferenceForms(face.name))
        return NormalizedFace(
            faceName = face.name,
            lines = abstracted.split("\n"),
            reminders = reminders,
            selfReferences = selfRefs,
            raw = face.oracleText,
        )
    }

    /**
     * The permanent nouns a card uses to refer to *itself* — "When **this creature** enters".
     *
     * Modern templating replaced the card's own name with a type noun, so a self-reference has two
     * printed shapes and they mean the same thing: `TriggerBinding.SELF`, the source object. Both
     * therefore abstract to the same [SELF] token, which is what lets one trigger rule read
     * "When this creature enters" and "When ~ enters" without either spelling being privileged.
     *
     * **The noun is not recoverable from the model, and does not need to be.** It is a function of
     * the card's type line — an artifact creature prints "this creature", an Equipment prints "this
     * Equipment" — and the model has nowhere to put it. Recording the surface form and restoring it
     * positionally, exactly as the name pass does, keeps the printed word without the grammar ever
     * having to know it. The alternative, a rule per noun with one canonical spelling, would report
     * thousands of cards as VARIANT for information normalization can simply keep.
     *
     * "This **card**" is here beside the permanent nouns because a card refers to itself that way
     * from a zone where it is not a permanent — "When you cycle **this card**, …" is printed on a
     * creature and read from the graveyard — and a rule now reaches it. "This **spell**" is
     * deliberately still absent: [com.wingedsheep.assay.grammar.Restrictions] spells it as a literal
     * inside "Cast this spell only …", so abstracting it would break the rules that read it.
     */
    private val SELF_NOUNS = listOf(
        "creature", "artifact", "enchantment", "land", "permanent", "planeswalker",
        "Aura", "Equipment", "Vehicle", "token", "Saga", "Class", "Siege", "Contraption",
        "Spacecraft", "battle", "card",
    ).flatMap { listOf("this $it", "This $it") }

    /**
     * The surface forms that refer to the card itself, longest first so that
     * "Kenrith, the Returned King" wins over the bare "Kenrith" it contains — the Comprehensive
     * Rules' *legend name* rule lets a legendary card's own text refer to it by the short name.
     *
     * Known limitation, deliberately not papered over: a short name that occurs inside a *longer*
     * proper noun in the card's own text — Kher Keep making "Kobolds of Kher Keep" — is abstracted
     * too, and so is a [SELF_NOUNS] phrase inside a *granted* ability, where "this creature" means
     * the enchanted creature rather than the source ("Enchanted creature has 'When this creature
     * dies, …'"). The round trip is unaffected in both cases — the form is recorded and restored
     * verbatim — but the model would be wrong, so the rules that read `~` must not treat it as
     * authoritative inside a quoted ability. Nothing in the grammar does.
     */
    internal fun selfReferenceForms(faceName: String): List<String> {
        val forms = linkedSetOf(faceName)
        SHORT_NAME_SEPARATORS.forEach { separator ->
            val at = faceName.indexOf(separator)
            if (at > 0) forms.add(faceName.substring(0, at))
        }
        forms.addAll(SELF_NOUNS)
        return forms.filter { it.isNotBlank() }.sortedByDescending { it.length }
    }

    /**
     * Where a legendary card's **short name** ends — CR 201.3b's "shortened version of the name".
     *
     * Two conventions, and Oracle uses both: "Akroma, Angel of Wrath" refers to itself as "Akroma"
     * and "Phage the Untouchable" as "Phage". Deriving the second matters because Scryfall's current
     * Oracle text prints the short form — Phage's three abilities all say "Phage", and without this
     * the card's own name is never abstracted and every one of its lines declines.
     *
     * The full name is always offered first ([selfReferenceForms] sorts by length), so a card that
     * spells itself out in full is unaffected; and the surface form is recorded and restored
     * verbatim, so a false positive would still round-trip.
     */
    private val SHORT_NAME_SEPARATORS = listOf(", ", " the ")

    private fun stripReminders(text: String): Pair<String, List<Removal>> {
        val removals = mutableListOf<Removal>()
        val out = StringBuilder()
        var cursor = 0
        for (m in REMINDER_RE.findAll(text)) {
            out.append(text, cursor, m.range.first)
            removals.add(Removal(out.length, m.value))
            cursor = m.range.last + 1
        }
        out.append(text, cursor, text.length)
        return out.toString() to removals
    }

    private fun abstractSelfReference(text: String, forms: List<String>): Pair<String, List<String>> {
        if (forms.isEmpty()) return text to emptyList()
        val replaced = mutableListOf<String>()
        val out = StringBuilder()
        var i = 0
        while (i < text.length) {
            val hit = forms.firstOrNull { form ->
                text.startsWith(form, i) &&
                    !isNameChar(text.getOrNull(i - 1)) &&
                    !isNameChar(text.getOrNull(i + form.length))
            }
            if (hit != null) {
                out.append(SELF)
                replaced.add(hit)
                i += hit.length
            } else {
                out.append(text[i])
                i++
            }
        }
        return out.toString() to replaced
    }

    /**
     * Whether [c] continues a name, and therefore blocks a self-reference from matching next to it.
     *
     * An apostrophe **ends** one, which is what lets "this creature's base power" abstract to "~'s
     * base power" — Riptide Mangler's whole line, and every possessive self-reference after it. The
     * cost is that a card whose own name is a prefix of a possessive in its text would abstract
     * there too; no card in the corpus is, and the round trip is unaffected either way because the
     * surface form is recorded and restored verbatim.
     */
    private fun isNameChar(c: Char?): Boolean = c != null && c.isLetterOrDigit()

    /** The self-reference placeholder. Oracle text never contains a literal tilde. */
    const val SELF = "~"
}

/** A span removed by a normalization pass, plus where to put it back. */
data class Removal(val offset: Int, val text: String)

/**
 * A face's Oracle text as canonical ability lines, carrying everything needed to undo the
 * normalization exactly.
 */
data class NormalizedFace(
    val faceName: String,
    val lines: List<String>,
    val reminders: List<Removal>,
    val selfReferences: List<String>,
    /** The face's original Oracle text — the byte string the touchstone compares against. */
    val raw: String,
) {

    /** True for a face with no rules text: the vanilla case, which round-trips trivially. */
    val isVanilla: Boolean get() = raw.isBlank()

    /**
     * The inverse of the whole pipeline: printed lines → the face's original Oracle text.
     *
     * Passing [lines] straight back must reproduce [raw] exactly; that identity is itself a gate
     * (`assay gate --touchstone` checks it before it checks the grammar), because a normalization
     * that cannot round-trip its own output would let any grammar look correct.
     */
    fun restore(printedLines: List<String>): String {
        var text = printedLines.joinToString("\n")
        text = restoreSelfReferences(text)
        // Re-insert right to left so earlier offsets stay valid.
        for (removal in reminders.asReversed()) {
            if (removal.offset > text.length) return text  // printed text diverged; caller compares and fails
            text = text.substring(0, removal.offset) + removal.text + text.substring(removal.offset)
        }
        return text
    }

    private fun restoreSelfReferences(text: String): String {
        if (selfReferences.isEmpty()) return text
        val out = StringBuilder()
        var i = 0
        var next = 0
        while (i < text.length) {
            if (text.startsWith(Normalizer.SELF, i) && next < selfReferences.size) {
                out.append(selfReferences[next++])
                i += Normalizer.SELF.length
            } else {
                out.append(text[i])
                i++
            }
        }
        return out.toString()
    }
}
