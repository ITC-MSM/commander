package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.scripting.ChoiceSlot
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * The permanents bargain lets you sacrifice (CR 702.166a): an artifact, an enchantment, or a token
 * you control. Exposed so a card that *references* the bargain cost (rather than having it) can
 * reuse the exact same set.
 */
val BargainSacrificeFilter: GameObjectFilter = GameObjectFilter.ArtifactEnchantmentOrToken

/**
 * Add Bargain (CR 702.166, Wilds of Eldraine) — "You may sacrifice an artifact, enchantment, or
 * token as you cast this spell."
 *
 * A static ability that functions while the spell is on the stack (CR 702.166a): "As an additional
 * cost to cast this spell, you may sacrifice an artifact, enchantment, or token." Declaring that
 * intention as the spell is cast (CR 601.2b) makes the spell *bargained* (CR 702.166b) — a fact the
 * card's own linked abilities (CR 702.166c) branch on, and one that rides the resolving permanent
 * for the rest of its life so an enters-the-battlefield ability can still read it.
 *
 * Wired entirely by the shared optional-additional-cost rail
 * ([KeywordAbility.OptionalAdditionalCost]) with `declaredSlot = `[ChoiceSlot.BARGAINED]: the
 * legal-action enumerator offers a "Cast … (Bargained)" variant whenever the caster controls
 * something sacrificeable, the cast handler collects the sacrifice through the ordinary additional
 * cost payment flow, and the engine stamps [ChoiceSlot.BARGAINED] on the spell and on the permanent
 * it becomes. Because the slot — not a shared boolean — carries the mechanic's identity, a bargained
 * spell never satisfies a "whenever you cast a kicked spell" payoff and a kicked spell never
 * satisfies [Conditions.WasBargained].
 *
 * The card supplies its own payoff; bargain derives nothing:
 * - A spell rider — gate the extra clause on [Conditions.WasBargained]
 *   (Archon's Glory: "If this spell was bargained, that creature also gains flying and lifelink"),
 *   or give the bargained cast a wholly different effect / target set with
 *   `kickerEffect` / `kickerTargets` (CR 702.166d — Brave the Wilds' bargain-only target).
 * - A permanent — an enters-the-battlefield trigger with an intervening-if on
 *   [Conditions.WasBargained] (Agatha's Champion: "When this creature enters, if it was bargained,
 *   it fights …"), which per CR 603.4 never goes on the stack when the spell wasn't bargained.
 * - A cheaper bargained cast — `ModifySpellCost(SelfCast, ReduceGeneric(n),
 *   CostGating.OnlyIf(Conditions.WasBargained))` (Hamlet Glutton: "This spell costs {2} less to cast
 *   if it's bargained").
 */
fun CardBuilder.bargain() {
    keywordAbilityList.add(
        KeywordAbility.OptionalAdditionalCost(
            additionalCost = Costs.additional.SacrificePermanent(BargainSacrificeFilter),
            displayPrefix = "Bargain",
            keyword = Keyword.BARGAIN,
            declaredSlot = ChoiceSlot.BARGAINED,
        )
    )
}

/**
 * This card's bargain keyword ability, or null when it has none — the single check for "does this
 * card have bargain", used by tooling and tests rather than re-deriving the shape.
 */
fun CardDefinition.bargainKeyword(): KeywordAbility.OptionalAdditionalCost? =
    keywordAbilities.filterIsInstance<KeywordAbility.OptionalAdditionalCost>()
        .firstOrNull { it.declaredSlot == ChoiceSlot.BARGAINED }
