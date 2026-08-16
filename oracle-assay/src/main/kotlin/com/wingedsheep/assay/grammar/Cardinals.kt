package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf

/**
 * Number words — the dependency root of the whole pipeline family, because almost every step in it
 * counts something.
 *
 * Oracle text spells small numbers as words ("Draw **two** cards") and writes larger ones as digits,
 * which is why this is separate from [Primitives.cardinal]: that leaf reads the *digits* a keyword
 * parameter uses ("Annihilator 2"), and the two are not interchangeable in either direction. A rule
 * that printed "Draw 2 cards." would round-trip against nothing.
 *
 * The list stops at ten because that is where Oracle's own convention stops — "Draw twenty cards"
 * is not printed, "Draw 20 cards" is. Anything past the end declines, which is the honest answer
 * rather than a number nobody prints.
 *
 * Declared before it is used, per the ordering rule [Primitives] states: object initializers run in
 * declaration order.
 */
object Cardinals {

    private val WORDS: List<Pair<String, Int>> = listOf(
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
        "ten" to 10,
    )

    /**
     * The number *words*, two and up.
     *
     * One is deliberately absent. English does not write "draw one card"; it writes "draw **a**
     * card", and the article is part of the surrounding phrase rather than of the number — "a card"
     * versus "two cards" also changes the noun. Rules that count therefore spell the singular
     * separately, and refuse 1 here, so that exactly one surface form exists per value and printing
     * stays determined. See [Steps.draw] for the shape that follows from it.
     */
    val word: Phrase<Int> = oneOf(
        "a number word",
        WORDS.map { (text, value) -> constant(text, value) },
    )

    /** True for a count this vocabulary can spell as a word — the guard a counting rule builds on. */
    fun spellable(n: Int): Boolean = WORDS.any { it.second == n }

    /**
     * The number words in a position whose **noun is elided** — "put **one** of them into your
     * hand", "put **two** of them into your graveyard".
     *
     * One is present here for exactly the reason it is absent from [word], rather than in spite of
     * it. That rule's argument is about the *article*: English writes "draw a card" because there is
     * a noun for "a" to attach to. Here the noun has already been said and the phrase refers back to
     * it with "of them", so there is nothing to take an article and English spells the number —
     * "put one of them", never "put a of them". Same domain, different sentence position, and the
     * two never stand in the same slot.
     *
     * It is therefore not a second vocabulary over one English: [word] and this one are disjoint on
     * the surface they read, so nothing has two spellings and nothing is left for a printer to
     * choose. See [TopOfLibrary.lookAtTopAndKeep], the position that named it.
     */
    val pronominal: Phrase<Int> = oneOf(
        "a number word for an elided noun",
        listOf(constant("one", 1)) + WORDS.map { (text, value) -> constant(text, value) },
    )

    /** [spellable]'s sibling for [pronominal] — one, and everything [word] can spell. */
    fun spellablePronominally(n: Int): Boolean = n == 1 || spellable(n)

    /**
     * The ordinals — "your **second** spell each turn".
     *
     * A separate vocabulary rather than a suffix over [word], because English's ordinals are not
     * derived from its cardinals by any rule a grammar could invert ("two" → "second", "three" →
     * "third"), and because the two never stand in the same slot: a cardinal counts objects, an
     * ordinal picks one out of a sequence. It starts at *one*, which is the other difference — "your
     * first spell" is written, where "draw one card" is not.
     *
     * It stops at three for [word]'s reason: that is where the corpus stops. No card counts a
     * fourth spell in a turn, and a rule that spelled one would round-trip against nothing.
     */
    val ordinal: Phrase<Int> = oneOf(
        "an ordinal",
        listOf("first" to 1, "second" to 2, "third" to 3).map { (text, value) -> constant(text, value) },
    )
}
