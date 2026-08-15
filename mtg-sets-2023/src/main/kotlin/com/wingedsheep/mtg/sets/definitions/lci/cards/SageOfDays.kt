package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Sage of Days
 * {2}{U}
 * Creature — Human Wizard
 * 3/2
 *
 * When this creature enters, look at the top three cards of your library. You may put one of those
 * cards back on top of your library. Put the rest into your graveyard.
 *
 * Pipeline (surveil-style keep-one-on-top dig; the look is private — no reveal):
 *   1. GatherCards(TopOfLibrary(3)) → "looked"          — pulled off the library so the controller
 *                                                          sees them privately during selection.
 *   2. SelectFromCollection("looked", ChooseUpTo(1))     — the optional "you may put one back on top".
 *        storeSelected = "toTop", storeRemainder = "toGraveyard"
 *   3. MoveCollection("toTop"       → LIBRARY, Top)       — the kept card returns to the top.
 *   4. MoveCollection("toGraveyard" → GRAVEYARD)          — everything not kept is put into the yard.
 *
 * ChooseUpTo(1) honors the "may" — the controller can keep zero (all three go to the graveyard).
 * If the library has fewer than three cards, the gather simply collects what's there.
 */
val SageOfDays = card("Sage of Days") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, look at the top three cards of your library. You may put " +
        "one of those cards back on top of your library. Put the rest into your graveyard."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(DynamicAmount.Fixed(3)),
                    storeAs = "looked"
                ),
                SelectFromCollectionEffect(
                    from = "looked",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    storeSelected = "toTop",
                    storeRemainder = "toGraveyard",
                    selectedLabel = "Put on top of library",
                    remainderLabel = "Put into graveyard",
                    prompt = "You may put one card back on top of your library"
                ),
                MoveCollectionEffect(
                    from = "toTop",
                    destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Top)
                ),
                MoveCollectionEffect(
                    from = "toGraveyard",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Aldo Domínguez"
        flavorText = "\"After this age, another.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a6e53495-c76c-4cd4-b93e-b2c491d2c13a.jpg?1782694551"
    }
}
