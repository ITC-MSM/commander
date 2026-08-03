package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.EntersWithKeywords
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModeOption
import com.wingedsheep.sdk.scripting.conditions.SourceChosenModeIs
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/** The chosen-mode id for riot's "+1/+1 counter" branch (CR 702.136a). */
const val RIOT_MODE_COUNTER: String = "riot-counter"

/** The chosen-mode id for riot's "haste" branch (CR 702.136a). */
const val RIOT_MODE_HASTE: String = "riot-haste"

/**
 * The two options riot offers as the permanent enters. The printed reminder text frames riot as a
 * straight choice ("with your choice of a +1/+1 counter or haste"), which is what these labels
 * say; CR 702.136a words the same thing as an optional counter with haste as the fallback.
 */
private val RIOT_MODES: List<ModeOption> = listOf(
    ModeOption(
        id = RIOT_MODE_COUNTER,
        label = "A +1/+1 counter",
        description = "It enters with an additional +1/+1 counter on it."
    ),
    ModeOption(
        id = RIOT_MODE_HASTE,
        label = "Haste",
        description = "It gains haste."
    ),
)

/** Reminder text for riot, matching the printed wording. */
const val RIOT_REMINDER: String =
    "Riot (This creature enters with your choice of a +1/+1 counter or haste.)"

/**
 * The three enters-with replacements that *are* riot, scoped to whichever permanents [appliesTo]
 * describes. [otherOnly] picks the rail: false = the card's own entry (read straight off its
 * definition), true = a group grant consulted from the battlefield as some *other* permanent
 * enters.
 *
 * The choice is written to [com.wingedsheep.sdk.scripting.ChoiceSlot.MODE] on the entering
 * permanent, and the counter/haste branches read it back with [SourceChosenModeIs] — so both
 * riders resolve against the same recorded answer without any riot-specific engine vocabulary.
 */
private fun riotReplacements(
    appliesTo: EventPattern,
    otherOnly: Boolean,
): List<com.wingedsheep.sdk.scripting.ReplacementEffect> = listOf(
    EntersWithChoice(
        choiceType = ChoiceType.MODE,
        modeOptions = RIOT_MODES,
        otherOnly = otherOnly,
        appliesTo = appliesTo,
    ),
    EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 1,
        selfOnly = !otherOnly,
        otherOnly = otherOnly,
        condition = SourceChosenModeIs(RIOT_MODE_COUNTER),
        appliesTo = appliesTo,
    ),
    EntersWithKeywords(
        keywords = listOf(Keyword.HASTE),
        condition = SourceChosenModeIs(RIOT_MODE_HASTE),
        selfOnly = !otherOnly,
        otherOnly = otherOnly,
        appliesTo = appliesTo,
    ),
)

/**
 * Add Riot (CR 702.136, Ravnica Allegiance) to this card: "You may have this permanent enter with
 * an additional +1/+1 counter on it. If you don't, it gains haste."
 *
 * The keyword itself is display-only; the behavior is composed from the existing enters-with
 * family — an [EntersWithChoice]`(ChoiceType.MODE)` for the choice, plus an [EntersWithCounters]
 * and an [EntersWithKeywords] gated on the chosen mode. Riot is a replacement effect, so the
 * choice happens as the permanent enters, can't be responded to, and the haste it grants is
 * indefinite rather than until-end-of-turn.
 */
fun CardBuilder.riot() {
    keywordSet.add(Keyword.RIOT)
    riotReplacements(
        appliesTo = EventPattern.ZoneChangeEvent(
            filter = GameObjectFilter.Any,
            to = Zone.BATTLEFIELD,
        ),
        otherOnly = false,
    ).forEach { replacementEffect(it) }
}

/**
 * Grant riot to *other* permanents matching [filter] as they enter — "Other Spiders you control
 * have riot" (Spider-Punk). The source itself is never covered (add [riot] as well when the card
 * also has riot printed on it, as Spider-Punk does).
 *
 * Modelled as the same trio [riot] builds, on the `otherOnly` + `appliesTo` global-replacement
 * rail: the granter stamps them into its battlefield replacement-source component, and every
 * matching permanent that enters while the granter is on the battlefield gets the riot choice.
 */
fun CardBuilder.riotFor(filter: GameObjectFilter) {
    riotReplacements(
        appliesTo = EventPattern.ZoneChangeEvent(filter = filter, to = Zone.BATTLEFIELD),
        otherOnly = true,
    ).forEach { replacementEffect(it) }
}
