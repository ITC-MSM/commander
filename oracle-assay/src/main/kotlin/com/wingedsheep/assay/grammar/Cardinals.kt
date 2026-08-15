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
}
