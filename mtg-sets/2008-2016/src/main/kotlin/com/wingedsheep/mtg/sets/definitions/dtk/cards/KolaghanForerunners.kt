package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Kolaghan Forerunners
 * {2}{R}
 * Creature — Human Berserker
 * * / 3
 *
 * Trample
 * Kolaghan Forerunners's power is equal to the number of creatures you control.
 * Dash {2}{R} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)
 *
 * The printed `*` power is characteristic-defining, so it is a power-only
 * [com.wingedsheep.sdk.dsl.CardBuilder.dynamicPower] over
 * [DynamicAmount.AggregateBattlefield] counting creatures you control — layer 7b, the power *is*
 * the base value — while toughness stays the fixed 3. `dash` is a builder property rather than a
 * [Keyword] constant, and setting it is what adds the `KeywordAbility.Dash` the cast enumerator
 * reads. A dashed Forerunners counts itself, so it is never smaller than a 1/3.
 */
val KolaghanForerunners = card("Kolaghan Forerunners") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Berserker"
    // power is characteristic-defining — see the CDA below
    toughness = 3
    oracleText = "Trample\n" +
        "Kolaghan Forerunners's power is equal to the number of creatures you control.\n" +
        "Dash {2}{R} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)"

    keywords(Keyword.TRAMPLE)

    dynamicPower(
        DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature)
    )

    dash = "{2}{R}"

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "144"
        artist = "Jason A. Engle"
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2c56a8de-8ae8-4672-8119-9e6a1f4cf5cd.jpg?1783938589"
    }
}
