package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Frontier Seeker
 * {1}{W}
 * Creature — Human Scout
 * 2/1
 *
 * When this creature enters, look at the top five cards of your library. You may reveal a
 * Mount creature card or a Plains card from among them and put it into your hand. Put the
 * rest on the bottom of your library in a random order.
 *
 * "Plains card" matches any card with the Plains land type (including nonbasic duals), per
 * the standard land-type ruling.
 */
val FrontierSeeker = card("Frontier Seeker") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Scout"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, look at the top five cards of your library. You may " +
        "reveal a Mount creature card or a Plains card from among them and put it into your hand. " +
        "Put the rest on the bottom of your library in a random order."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(DynamicAmount.Fixed(5)),
                    storeAs = "looked"
                ),
                SelectFromCollectionEffect(
                    from = "looked",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    filter = GameObjectFilter(
                        cardPredicates = listOf(
                            CardPredicate.Or(
                                listOf(
                                    CardPredicate.And(
                                        listOf(
                                            CardPredicate.HasSubtype(Subtype("Mount")),
                                            CardPredicate.IsCreature
                                        )
                                    ),
                                    CardPredicate.HasSubtype(Subtype.PLAINS)
                                )
                            )
                        )
                    ),
                    storeSelected = "kept",
                    storeRemainder = "rest",
                    selectedLabel = "Put in hand",
                    remainderLabel = "Put on bottom"
                ),
                MoveCollectionEffect(
                    from = "kept",
                    destination = CardDestination.ToZone(Zone.HAND),
                    revealed = true
                ),
                MoveCollectionEffect(
                    from = "rest",
                    destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
                    order = CardOrder.Random
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "13"
        artist = "Raluca Marinescu"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/9368cc76-bee5-4b46-a309-981106c3addf.jpg?1712355276"
    }
}
