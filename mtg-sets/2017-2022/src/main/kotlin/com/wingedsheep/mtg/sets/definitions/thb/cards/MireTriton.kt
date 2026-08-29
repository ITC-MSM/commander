package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mire Triton
 * {1}{B}
 * Creature — Zombie Merfolk
 * 2/1
 *
 * Deathtouch
 * When this creature enters, mill two cards and you gain 2 life. (To mill a card, put the top card of your library into your graveyard.)
 *
 * Two printed clauses, so two members of the trigger's [Effects.Composite]: the mill recipe
 * ([Patterns.Library.mill], a Gather → MoveCollection pair that stays nested as its own composite)
 * and the life gain. Written with `Effects.Composite` rather than `then` because `then` flattens a
 * composite receiver, which would splice the mill's two pipeline steps into the outer list.
 */
val MireTriton = card("Mire Triton") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Merfolk"
    power = 2
    toughness = 1
    oracleText = "Deathtouch\n" +
        "When this creature enters, mill two cards and you gain 2 life. (To mill a card, put the top card of your library into your graveyard.)"

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Patterns.Library.mill(2),
            Effects.GainLife(2),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "105"
        artist = "Seb McKinnon"
        flavorText = "Caught between life and death, between land and sea, between thought and oblivion."
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3f8427d3-4d9e-48c9-838b-239fd1357d95.jpg"
    }
}
