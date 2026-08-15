package com.wingedsheep.mtg.sets

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty

/**
 * Corpus-wide gate for the CR 603.2 / CR 603.4 split.
 *
 * `TriggeredAbility` carries a trigger's condition in one of two fields, and the engine treats them
 * differently: [com.wingedsheep.sdk.scripting.TriggeredAbility.interveningIf] is re-checked as the
 * ability resolves (CR 603.4), [com.wingedsheep.sdk.scripting.TriggeredAbility.triggerRestriction]
 * is not (CR 603.2). Putting a card in the wrong field is a silent rules bug in *either* direction —
 * an "if" filed as a restriction resolves abilities that should do nothing, and a "while" filed as
 * an "if" fizzles abilities that should resolve — and neither shows up as a compile error or a
 * snapshot diff.
 *
 * Removing the old shared `triggerCondition` setter is what forces an author to choose. This test is
 * what checks the choice against the printed card, on the one population where the pairing is
 * unambiguous: a card with exactly one triggered ability whose Oracle text prints an intervening
 * "if" — "When/Whenever/At [event], if [condition], [effect]" — must model it as `interveningIf`.
 *
 * Deliberately narrow, and the two exclusions are the reason it needs no allowlist:
 *
 *  - **More than one triggered ability** — the sentence can't be paired with an ability by counting.
 *    That also excludes every keyword-derived ability (gift, offspring, impending, suspend,
 *    soulbond, increment), whose intervening "if" is stated by the Comprehensive Rules and printed
 *    only as reminder text, so there is no sentence on the card to check it against.
 *  - **An `if` printed after the effect** — "…, create a token *if* you control a creature with
 *    power 4 or greater" is not this rule at all (CR 603.4: it "only applies to an 'if' that
 *    immediately follows a trigger condition"), so the regex requires the `if` to open the clause
 *    right after the event and before the effect.
 */
class InterveningIfClassificationTest : FunSpec({

    // An ability word ("Raid — ", "Descend 4 — ", "Celebration — ") sits in front of the trigger
    // word and carries no rules meaning; so does a mode bullet.
    val prefix = Regex("^(?:[••]\\s*)?(?:[A-Z][A-Za-z’' -]{0,30}?\\s[—-]\\s)?")
    val reminder = Regex("\\([^()]*\\)")
    val triggerWord = Regex("^(?:When|Whenever|At)\\b")
    // The `if` opens the clause immediately after the trigger event: everything between the trigger
    // word and it is the event, which may itself carry commas ("At the beginning of each
    // opponent's upkeep, if that player has two or fewer cards in hand, …").
    val interveningIf = Regex("^(?:When|Whenever|At)\\b[^.]*?,\\s*(?:and\\s+)?if\\s", RegexOption.IGNORE_CASE)
    // "Only if" is a restriction on the trigger event (CR 603.2), not CR 603.4's clause.
    val onlyIf = Regex("\\bonly\\s+if\\b", RegexOption.IGNORE_CASE)

    fun triggerSentences(oracle: String): List<String> =
        reminder.replace(oracle, "")
            .split(Regex("\n+|(?<=[.]) (?=[A-Z])"))
            .map { prefix.replace(it.trim(), "") }
            .filter { triggerWord.containsMatchIn(it) }

    val offenders = MtgSetCatalog.all.flatMap { set ->
        set.cards.mapNotNull { card ->
            val ability = card.script.triggeredAbilities.singleOrNull() ?: return@mapNotNull null
            if (ability.interveningIf != null || ability.triggerRestriction == null) return@mapNotNull null
            val sentence = triggerSentences(card.oracleText.orEmpty())
                .singleOrNull { interveningIf.containsMatchIn(it) && !onlyIf.containsMatchIn(it) }
                ?: return@mapNotNull null
            "[${set.code}] ${card.name}: prints an intervening \"if\" but models it as a " +
                "triggerRestriction — \"$sentence\""
        }
    }

    test("a printed intervening-\"if\" is modelled as interveningIf, not as a trigger restriction") {
        if (offenders.isNotEmpty()) {
            println("=== intervening-\"if\" misclassifications: ${offenders.size} ===")
            offenders.forEach { println("  $it") }
        }
        offenders.shouldBeEmpty()
    }

    // The check above can only fail on cards it recognises, so its value is the size of the
    // population it recognises. Pinning a floor means a change that quietly stops the sentence
    // matching — a normalization tweak, an ability word the prefix doesn't know — fails here
    // instead of turning the gate green by matching nothing.
    test("the gate covers the cards it is supposed to cover") {
        val guarded = MtgSetCatalog.all.sumOf { set ->
            set.cards.count { card ->
                val ability = card.script.triggeredAbilities.singleOrNull()
                ability?.interveningIf != null &&
                    triggerSentences(card.oracleText.orEmpty())
                        .any { interveningIf.containsMatchIn(it) && !onlyIf.containsMatchIn(it) }
            }
        }
        println("intervening-\"if\" cards under this gate: $guarded")
        check(guarded >= 200) { "only $guarded cards matched; the sentence regex has stopped working" }
    }
})
