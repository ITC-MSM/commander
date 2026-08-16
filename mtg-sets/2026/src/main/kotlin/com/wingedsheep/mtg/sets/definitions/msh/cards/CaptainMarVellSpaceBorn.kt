package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantFlashToSpellType
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Captain Mar-Vell, Space-Born
 * {4}{W}
 * Legendary Creature — Kree Soldier Hero
 * 4/4
 * Flying, vigilance
 * Cosmic Awareness — As long as an opponent has cast a spell this turn, you may cast spells as
 * though they had flash.
 *
 * The third line is the unrestricted, you-only flash grant High Fae Trickster already uses
 * ([GrantFlashToSpellType] over `GameObjectFilter.Any` with `controllerOnly = true`), put behind an
 * "as long as" gate — so it is a `staticAbility { condition = … }`, which the DSL wraps in a
 * `ConditionalStaticAbility`. `FlashTypeGrants` unwraps that wrapper at both flash read sites, so
 * the permission appears the moment an opponent casts their first spell of the turn and disappears
 * again at the turn boundary.
 *
 * **The gate counts casts, not resolutions.** `DynamicAmount.SpellsCastThisTurn` reads the
 * per-player `CastSpellRecord` history captured at cast time, so an opponent's spell opens the
 * window even if it is countered, fizzles, or is still sitting on the stack — which is the point:
 * the usual line is to flash in a blocker or a trick while the opponent's spell is still on the
 * stack. `Player.EachOpponent` **sums** across every opponent, so `>= 1` is exactly "an opponent"
 * (any one of them) rather than "the opponent to your left" — multiplayer-correct without a
 * dedicated condition type.
 *
 * The grant covers *every* spell its controller casts, Mar-Vell's own controller only — an opponent
 * casting the spell that opens the window does not thereby gain flash themselves. It changes only
 * *timing*: flash (CR 702.8a) means "you may play this card any time you could cast an instant", so
 * the caster still needs priority, and playing a land — a special action, not casting a spell — is
 * unaffected.
 */
val CaptainMarVellSpaceBorn = card("Captain Mar-Vell, Space-Born") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Kree Soldier Hero"
    oracleText = "Flying, vigilance\n" +
        "Cosmic Awareness — As long as an opponent has cast a spell this turn, you may cast " +
        "spells as though they had flash."
    power = 4
    toughness = 4
    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    staticAbility {
        condition = Conditions.CompareAmounts(
            DynamicAmount.SpellsCastThisTurn(Player.EachOpponent),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(1)
        )
        ability = GrantFlashToSpellType(
            filter = GameObjectFilter.Any,
            controllerOnly = true
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "12"
        artist = "Gintas Galvanauskas"
        flavorText = "\"I came here to study the people of Earth, not serve as their executioner!\""
        imageUri = "https://cards.scryfall.io/normal/front/2/7/27924c2f-756c-42af-9830-18a5a2735137.jpg?1783902977"
    }
}
