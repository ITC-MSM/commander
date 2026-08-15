package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Add Flurry (Tarkir: Dragonstorm, Jeskai) — keyword tag + a "second spell each turn"
 * triggered ability.
 *
 * "Flurry — Whenever you cast your second spell each turn, [effect]." The [Keyword.FLURRY]
 * tag is display-only (the engine has no dedicated Flurry handler); the behavior lives
 * entirely in the triggered ability wired here on the [Triggers.NthSpellCast] (n=2, you)
 * event, which the [com.wingedsheep.sdk.scripting.EventPattern.NthSpellCastEvent] matcher
 * already fires when its controller casts their second spell of the turn.
 *
 * Author the effect/target/optional inside the block exactly like `triggeredAbility`
 * (the [TriggeredAbilityBuilder.trigger] is ignored — it is always forced to the
 * second-spell trigger). This helper composes the rendered reminder text as
 * "Flurry — Whenever you cast your second spell each turn, <effect>." A custom
 * [TriggeredAbilityBuilder.description] replaces only the `<effect>` portion, keeping
 * the "Flurry — Whenever you cast your second spell each turn," prefix.
 */
fun CardBuilder.flurry(init: TriggeredAbilityBuilder.() -> Unit) {
    keywordSet.add(Keyword.FLURRY)
    val builder = TriggeredAbilityBuilder()
    builder.init()
    builder.trigger = Triggers.NthSpellCast(2, Player.You)
    val ability = builder.build()
    // No "you may " prefix to add: `optional = true` has already been lowered into a consent gate,
    // and a gated effect's own description opens with "You may …".
    val effectText = (builder.description ?: buildString {
        ability.targetRequirement?.let { append(it.description); append(" ") }
        append(ability.effect.description.replaceFirstChar { it.lowercase() })
        ability.elseEffect?.let {
            append(". If you don't, ")
            append(it.description.replaceFirstChar { c -> c.lowercase() })
        }
    }).trimEnd().trimEnd('.')
    triggeredAbilities.add(
        ability.copy(
            descriptionOverride = "Flurry — Whenever you cast your second spell each turn, $effectText."
        )
    )
}
