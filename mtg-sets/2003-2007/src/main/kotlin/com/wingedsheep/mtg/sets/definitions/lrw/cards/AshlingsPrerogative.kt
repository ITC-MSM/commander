package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModeOption
import com.wingedsheep.sdk.scripting.PermanentsEnterTapped
import com.wingedsheep.sdk.scripting.conditions.SourceChosenModeIs
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Ashling's Prerogative
 * {1}{R} Enchantment
 *
 * As this enchantment enters, choose odd or even. (Zero is even.)
 * Each creature with mana value of the chosen quality has haste.
 * Each creature without mana value of the chosen quality enters tapped.
 *
 * The choice is [EntersWithChoice] with `ChoiceType.MODE` — the Sieges' shape — writing a stable
 * mode id onto the permanent that [SourceChosenModeIs] reads back. Both of the card's other lines
 * then come in mirrored pairs, one per mode, because "the chosen quality" is a *filter* selector
 * and the SDK's parity predicates (`manaValueIsOdd()` / `manaValueIsEven()`) are fixed:
 *
 * ```
 * odd chosen  ->  odd  creatures have haste,  even creatures enter tapped
 * even chosen ->  even creatures have haste,  odd  creatures enter tapped
 * ```
 *
 * Only one member of each pair is live at a time, which is exactly the 2007-10-01 ruling ("either
 * creatures with odd mana values enter tapped and creatures with even mana values have haste, or
 * vice versa").
 *
 * Two things worth stating because both have a plausible wrong reading:
 *
 * - **"Each creature" is every creature, not yours** — [GroupFilter] takes the bare
 *   `GameObjectFilter.Creature`, never `youControl()`. Your opponents' odd-cost creatures get
 *   haste too; that symmetry is the card.
 * - **The tapped clause is the *complement*, not the negation of the haste clause's controller
 *   scope.** "Without mana value of the chosen quality" is the other parity, so the pair of
 *   [PermanentsEnterTapped] effects uses the *opposite* predicate to the pair of [GrantKeyword]s.
 *   Swapping them compiles, reads right on the card, and inverts the whole enchantment.
 *
 * The tap clause needed the one piece of new vocabulary here: a `condition` gate on
 * [PermanentsEnterTapped]. That replacement is stamped into the source's replacement component
 * and consulted from the battlefield as *other* permanents enter, so unlike the haste halves it
 * cannot be wrapped in a `ConditionalStaticAbility` — the gate has to travel with the replacement.
 * The field mirrors the one `RedirectDamage` and `EntersWithCounters` already carry, and
 * `EnterTappedReplacements.entersTapped` evaluates it against the source.
 *
 * An {X} in a creature's mana cost is 0 on the battlefield (2007-10-01 ruling), and zero is even —
 * that falls out of the engine's own mana-value computation, no special case here.
 */
val AshlingsPrerogative = card("Ashling's Prerogative") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "As this enchantment enters, choose odd or even. (Zero is even.)\n" +
        "Each creature with mana value of the chosen quality has haste.\n" +
        "Each creature without mana value of the chosen quality enters tapped."

    replacementEffect(
        EntersWithChoice(
            choiceType = ChoiceType.MODE,
            modeOptions = listOf(
                ModeOption(
                    id = "odd",
                    label = "Odd",
                    description = "Odd-cost creatures have haste; even-cost creatures enter tapped."
                ),
                ModeOption(
                    id = "even",
                    label = "Even",
                    description = "Even-cost creatures have haste; odd-cost creatures enter tapped."
                )
            )
        )
    )

    staticAbility {
        condition = SourceChosenModeIs("odd")
        ability = GrantKeyword(
            Keyword.HASTE,
            GroupFilter(GameObjectFilter.Creature.manaValueIsOdd())
        )
    }

    staticAbility {
        condition = SourceChosenModeIs("even")
        ability = GrantKeyword(
            Keyword.HASTE,
            GroupFilter(GameObjectFilter.Creature.manaValueIsEven())
        )
    }

    replacementEffect(
        PermanentsEnterTapped(
            appliesTo = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Creature.manaValueIsEven(),
                to = Zone.BATTLEFIELD
            ),
            condition = SourceChosenModeIs("odd")
        )
    )

    replacementEffect(
        PermanentsEnterTapped(
            appliesTo = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Creature.manaValueIsOdd(),
                to = Zone.BATTLEFIELD
            ),
            condition = SourceChosenModeIs("even")
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "150"
        artist = "Warren Mahy"
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d850d95e-ba27-49cc-aa7a-42508852fe20.jpg?1783942880"
        ruling("2007-10-01", "The \"chosen value\" is either \"odd\" or \"even.\" So either creatures with odd mana values enter tapped and creatures with even mana values have haste, or vice versa.")
        ruling("2007-10-01", "If a creature has X in its mana cost, that X is treated as 0 for the purposes of these effects. It doesn't matter what the value of X was while the creature was on the stack.")
    }
}
