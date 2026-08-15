package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.sdk.scripting.conditions.Condition
import com.wingedsheep.sdk.scripting.conditions.YouWereAttackedThisStep
import com.wingedsheep.sdk.dsl.Conditions as SdkConditions

/**
 * The state tests a card's text names — the "if …" half of a conditional clause and the "only if …"
 * half of a cast restriction.
 *
 * The family opens with one member, which is what a condition family looks like the first time a
 * card needs one. Every rule builds through the SDK's `Conditions` facade rather than assembling a
 * `Compare` by hand: those facades are the curated surface, and a condition assembled here would be
 * a second spelling of one the SDK already names — the ambiguity this module refuses everywhere
 * else.
 *
 * That is also the honest reason the list is short. A condition in Oracle text is an ordinary
 * English clause with a vocabulary as large as the game's, and the SDK names only the ones cards
 * have needed; a rule per named condition is the shape that keeps the two in step, and a printed
 * condition the SDK cannot name declines and is counted.
 */
object Conditions {

    val all: List<Phrase<Condition>> = listOf(
        constant("an opponent controls more lands than you", SdkConditions.OpponentControlsMoreLands),
        // `YouWereAttackedThisStep` has no facade entry — it is a `data object` cards reference
        // directly, the same situation `Replacements` and the combat statics are in. Reported as the
        // small SDK finding it is rather than routed around.
        constant("you've been attacked this step", YouWereAttackedThisStep),
    )

    val condition: Phrase<Condition> = oneOf("a condition", all)
}
