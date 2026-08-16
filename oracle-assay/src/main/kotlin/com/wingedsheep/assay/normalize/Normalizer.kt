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
 * | Attachment noun | "equipped creature" → "enchanted creature" | put the recorded surface word back |
 * | Ability split | one ability per line | join with `\n` |
 * | Ability word | strip a line's `Landfall — ` prefix | put the recorded word back on that line |
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
        val (canonicalNoun, attachmentNouns) = canonicalizeAttachmentNoun(abstracted)
        val split = canonicalNoun.split("\n")
        return NormalizedFace(
            faceName = face.name,
            lines = split.map(::stripAbilityWord),
            reminders = reminders,
            selfReferences = selfRefs,
            attachmentNouns = attachmentNouns,
            abilityWords = split.map(::abilityWordOf),
            raw = face.oracleText,
        )
    }

    /**
     * "**Landfall —** Whenever a land you control enters, …" → the ability the line actually states.
     *
     * CR 207.2c: *"An ability word appears in italics at the beginning of some abilities. … they have
     * no special rules meaning and no individual entries in the Comprehensive Rules."* So the word is
     * printed-shape information of exactly the kind this file owns — the model has nowhere to put it,
     * and the alternative is a grammar rule per ability word wrapping every sentence the grammar can
     * already read, which is the multiplicative cost the module's "lift, don't re-spell" rule exists
     * to refuse. Nine BLB cards and hundreds elsewhere are one prefix away from an ability the rest of
     * the grammar reads whole.
     *
     * **Enumerated, and from the rule rather than from the corpus.** CR 207.2d's *flavor words* have
     * the identical printed shape — an italic prefix and a spaced em dash — and are "tailored to the
     * specific ability", so they are unbounded and mean nothing as a class. A pattern match on
     * `<Word> — ` would strip those too, along with a Saga's `I —` and a Class's `Level 2 —`, and
     * would be reading punctuation instead of a rule. So the list is CR 207.2c's, verbatim; an
     * ability word printed after that rule was last read declines until the list is updated, which
     * is the fail-closed direction.
     *
     * **Line-initial only.** Oracle puts one ability on a line and the word at its start, which is
     * what makes the pass positional and its inverse exact.
     */
    private fun abilityWordOf(line: String): String? =
        ABILITY_WORDS.firstOrNull { line.startsWith("$it$ABILITY_WORD_DASH") }

    private fun stripAbilityWord(line: String): String =
        abilityWordOf(line)?.let { line.substring(it.length + ABILITY_WORD_DASH.length) } ?: line

    internal const val ABILITY_WORD_DASH = " — "

    /**
     * CR 207.2c's list, in the sentence case Oracle prints it at the start of a line. Sorted longest
     * first so "Descend 8" is not read as a prefix of nothing and "Council's dilemma" wins over any
     * shorter member it contains.
     */
    private val ABILITY_WORDS: List<String> = listOf(
        "Adamant", "Addendum", "Alliance", "Battalion", "Bloodrush", "Celebration", "Channel",
        "Chroma", "Cohort", "Constellation", "Converge", "Council's dilemma", "Coven", "Delirium",
        "Descend 4", "Descend 8", "Domain", "Eerie", "Eminence", "Enrage", "Fateful hour",
        "Fathomless descent", "Ferocious", "Formidable", "Grandeur", "Hellbent", "Heroic", "Imprint",
        "Inspired", "Join forces", "Kinship", "Landfall", "Lieutenant", "Magecraft", "Metalcraft",
        "Morbid", "Pack tactics", "Paradox", "Parley", "Radiance", "Raid", "Rally", "Revolt",
        "Secret council", "Spell mastery", "Strive", "Survival", "Sweep", "Tempting offer",
        "Threshold", "Undergrowth", "Valiant", "Will of the council",
    ).sortedByDescending { it.length }

    /**
     * "**Equipped** creature gets +2/+0." → "**Enchanted** creature gets +2/+0.", the surface word
     * recorded and put back on the way out.
     *
     * An Aura and an Equipment print *different words* for the **same value**. The static's affected
     * set is `GroupFilter.attachedCreature()` — `Permanent` scoped to `AttachedTo`, which says "the
     * thing this is attached to" and nothing about auras — so Bonesplitter's golden and Holy
     * Strength's carry the identical `ModifyStats`, and Behemoth Sledge's carries the identical
     * `GrantKeyword` Spectral Flight's does. Which of the two words a card prints is a function of
     * its type line, exactly like the self-reference noun above, and the model has nowhere to keep
     * it.
     *
     * That leaves two possible homes, and [com.wingedsheep.assay.grammar.Statics] argued the point
     * before this pass existed: a second grammar rule spelling "equipped creature" would give one
     * value two printed forms and leave `unparse` to choose between them — ambiguity by
     * construction, and the one thing the module's second invariant forbids. Normalization is the
     * other home, and it is where every other printed-shape fact already lives. So the aura spelling
     * is canonical because that is the rule that exists, the equipment spelling abstracts onto it,
     * and the cards come back byte-exact rather than as variants — the reading was never in doubt,
     * only the noun.
     *
     * **Only the exact phrase, and only these two nouns.** "Fortified land" is a third word for a
     * third scope and still declines; "equipped creatures" (Ajani Vengeant's emblem) does not match,
     * because the boundary check refuses a letter after the phrase, and its plural sentence is not
     * one the grammar reads either way.
     */
    private fun canonicalizeAttachmentNoun(text: String): Pair<String, List<String>> {
        val recorded = mutableListOf<String>()
        val out = StringBuilder()
        var i = 0
        while (i < text.length) {
            val hit = ATTACHMENT_ADJECTIVES.firstOrNull { (surface, _) -> attachmentPhraseAt(text, i, surface) }
            if (hit != null) {
                out.append(hit.second).append(' ').append(ATTACHED_NOUN)
                recorded.add(hit.first)
                i += hit.first.length + 1 + ATTACHED_NOUN.length
            } else {
                out.append(text[i])
                i++
            }
        }
        return out.toString() to recorded
    }

    /**
     * The adjectives that mean "the permanent this is attached to", each paired with the canonical
     * one it abstracts onto. The aura spellings map to themselves so that [NormalizedFace.restore]
     * can walk the canonical occurrences positionally and put every surface word back — including
     * the ones that never moved.
     */
    private val ATTACHMENT_ADJECTIVES = listOf(
        "Equipped" to "Enchanted",
        "equipped" to "enchanted",
        "Enchanted" to "Enchanted",
        "enchanted" to "enchanted",
    )

    /** The one noun the pass covers; see [canonicalizeAttachmentNoun] for why it is not a slot. */
    internal const val ATTACHED_NOUN = "creature"

    /** The canonical adjectives [NormalizedFace.restore] scans for, longest-match irrelevant. */
    internal val CANONICAL_ATTACHMENT_ADJECTIVES = listOf("Enchanted", "enchanted")

    /**
     * Does "[adjective] creature" stand at [at] as a whole phrase? Shared by the forward pass and by
     * [NormalizedFace.restore] so the two can never disagree about which occurrences they count —
     * a restore that matched one the forward pass skipped would put every later word back one slot
     * off, and the touchstone would report the mismatch far from its cause.
     */
    internal fun attachmentPhraseAt(text: String, at: Int, adjective: String): Boolean =
        text.startsWith("$adjective $ATTACHED_NOUN", at) &&
            !isNameChar(text.getOrNull(at - 1)) &&
            !isNameChar(text.getOrNull(at + adjective.length + 1 + ATTACHED_NOUN.length))

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
    /**
     * The adjective each "enchanted creature" in [lines] was printed with, in order — "Equipped" on
     * an Equipment, "Enchanted" on an Aura. See [Normalizer.canonicalizeAttachmentNoun].
     */
    val attachmentNouns: List<String> = emptyList(),
    /**
     * The ability word each line was printed with, positionally — one entry per line, null where the
     * line had none. See [Normalizer.abilityWordOf].
     */
    val abilityWords: List<String?> = emptyList(),
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
        // First, because every later inverse works on offsets measured before the words came off.
        var text = restoreAbilityWords(printedLines).joinToString("\n")
        // Before the self-references, and therefore while card names are still `~`: a card called
        // "Enchanted Evening" would otherwise put its own name back and have this scan read it as an
        // attachment noun, shifting every later surface word by one.
        text = restoreAttachmentNouns(text)
        text = restoreSelfReferences(text)
        // Re-insert right to left so earlier offsets stay valid.
        for (removal in reminders.asReversed()) {
            if (removal.offset > text.length) return text  // printed text diverged; caller compares and fails
            text = text.substring(0, removal.offset) + removal.text + text.substring(removal.offset)
        }
        return text
    }

    /**
     * Put each line's recorded ability word back in front of it.
     *
     * By line index rather than by scanning, because the word was stripped by index: the pass has one
     * entry per line and nothing about the printed sentence can move it. A printed-line count that
     * disagrees with the recorded one means the grammar produced a different number of abilities than
     * it read, so the lines are returned untouched and the caller's byte comparison reports it.
     */
    private fun restoreAbilityWords(printedLines: List<String>): List<String> {
        if (abilityWords.size != printedLines.size) return printedLines
        return printedLines.mapIndexed { index, line ->
            abilityWords[index]?.let { "$it${Normalizer.ABILITY_WORD_DASH}$line" } ?: line
        }
    }

    /**
     * Put each recorded adjective back on the canonical "enchanted creature" it was abstracted from,
     * positionally — the same treatment [restoreSelfReferences] gives a recorded name.
     */
    private fun restoreAttachmentNouns(text: String): String {
        if (attachmentNouns.isEmpty()) return text
        val out = StringBuilder()
        var i = 0
        var next = 0
        while (i < text.length) {
            // The same boundary test the forward pass makes, or the plural it declined to abstract
            // ("enchanted creatures") would consume a recorded word here and shift every later one.
            val canonical = Normalizer.CANONICAL_ATTACHMENT_ADJECTIVES.firstOrNull {
                next < attachmentNouns.size && Normalizer.attachmentPhraseAt(text, i, it)
            }
            if (canonical != null) {
                out.append(attachmentNouns[next++]).append(' ').append(Normalizer.ATTACHED_NOUN)
                i += canonical.length + 1 + Normalizer.ATTACHED_NOUN.length
            } else {
                out.append(text[i])
                i++
            }
        }
        return out.toString()
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
