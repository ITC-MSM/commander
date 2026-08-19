package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.SpendAnyManaTypeForSpells
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Case File Auditor — Murders at Karlov Manor #7
 * {2}{W} · Creature — Human Detective · 1/4 · Uncommon
 *
 * When this creature enters and whenever you solve a Case, look at the top six cards of your
 * library. You may reveal an enchantment card from among them and put it into your hand. Put the
 * rest on the bottom of your library in a random order.
 * You may spend mana as though it were mana of any color to cast Case spells.
 *
 * **Two triggered abilities, not one.** The printed sentence joins them, but CR 603.1 makes "when
 * this creature enters" and "whenever you solve a Case" separate abilities with separate
 * conditions — the repo's Up the Beanstalk idiom, one `triggeredAbility` block each. They also
 * differ in binding: the enters trigger watches this creature, while the solve trigger watches
 * every Case its controller has, so it can't be folded into a single `Triggers.or`.
 *
 * `Triggers.WheneverYouSolveACase` fires when a Case's "To solve" trigger resolves and stamps the
 * designation, which is exactly what the printed ruling says. The solved-ness is sticky and one-way
 * (CR 719.3b), so each Case feeds this at most once — and a Case whose Solved ability sacrifices it
 * still counts, because the solving player rides the event rather than being read back off a
 * permanent that may already be gone.
 *
 * **The look is "up to one", not "one".** "You *may* reveal an enchantment card" is a declinable
 * choice, so it is `chooseUpToSplit(1, …)` filtered to enchantments — never `chooseExactly`, which
 * would force the pick whenever an enchantment happened to be there. Declining (or looking at six
 * non-enchantments) leaves the kept slot empty, and the reveal and hand move are no-ops over it.
 * All six are shown (`showAllCards`) because the card says *look at* the top six — the player sees
 * the lands and creatures they are burying, even though only an enchantment may be taken.
 *
 * The bottoming is `CardOrder.Random`, not the usual controller-chooses: "in a random order" means
 * the player does not get to stack their own library.
 *
 * The mana ability is [SpendAnyManaTypeForSpells] over the Case subtype — zone-agnostic and mana-
 * only, so a Case cast from anywhere gets its colored pips relaxed while any additional cost is
 * untouched. Note this is a *fixing* effect for the whole Case cycle (five colors of Case in this
 * set), not a discount.
 */
val CaseFileAuditor = card("Case File Auditor") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Detective"
    power = 1
    toughness = 4
    oracleText = "When this creature enters and whenever you solve a Case, look at the top six " +
        "cards of your library. You may reveal an enchantment card from among them and put it " +
        "into your hand. Put the rest on the bottom of your library in a random order.\n" +
        "You may spend mana as though it were mana of any color to cast Case spells."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = lookAtSixForAnEnchantment()
        description = "When this creature enters, look at the top six cards of your library. You " +
            "may reveal an enchantment card from among them and put it into your hand. Put the " +
            "rest on the bottom of your library in a random order."
    }

    triggeredAbility {
        trigger = Triggers.WheneverYouSolveACase
        effect = lookAtSixForAnEnchantment()
        description = "Whenever you solve a Case, look at the top six cards of your library. You " +
            "may reveal an enchantment card from among them and put it into your hand. Put the " +
            "rest on the bottom of your library in a random order."
    }

    staticAbility {
        ability = SpendAnyManaTypeForSpells(GameObjectFilter.Any.withSubtype(Subtype.CASE))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "7"
        artist = "Ryan Valle"
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70a52038-9d1c-4be1-8dbe-6f0ee916ba94.jpg?1783912929"

        ruling(
            "2024-02-02",
            "Case File Auditor's first ability triggers whenever a \"to solve\" ability you " +
                "control resolves."
        )
    }
}

/**
 * The shared payoff of both triggers, built fresh per call so the two abilities are independent
 * effect trees rather than one shared instance.
 */
private fun lookAtSixForAnEnchantment() = Effects.Pipeline {
    val looked = gather(CardSource.TopOfLibrary(DynamicAmount.Fixed(6)))
    val (kept, rest) = chooseUpToSplit(
        count = 1,
        from = looked,
        filter = GameObjectFilter.Enchantment,
        prompt = "You may reveal an enchantment card and put it into your hand",
        selectedLabel = "To hand",
        remainderLabel = "Bottom of library",
        showAllCards = true
    )
    reveal(kept)
    toHand(kept)
    toLibraryBottom(rest, order = CardOrder.Random)
}
