package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * The Cave of Two Lovers
 * {3}{R}
 * Enchantment — Saga
 *
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 * I — Create two 1/1 white Ally creature tokens.
 * II — Search your library for a Mountain or Cave card, reveal it, put it into your hand, then shuffle.
 * III — Earthbend 3. (Target land you control becomes a 0/0 creature with haste that's still a land.
 *       Put three +1/+1 counters on it. When it dies or is exiled, return it to the battlefield tapped.)
 *
 * Chapter II tutors a card with the Mountain or Cave land subtype to hand (revealed), then shuffles.
 * Chapter III's earthbend is a keyword action composed from existing primitives via [Effects.Earthbend]
 * and needs a target land chosen as the chapter ability goes on the stack.
 */
val TheCaveOfTwoLovers = card("The Cave of Two Lovers") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I — Create two 1/1 white Ally creature tokens.\n" +
        "II — Search your library for a Mountain or Cave card, reveal it, put it into your hand, then shuffle.\n" +
        "III — Earthbend 3. (Target land you control becomes a 0/0 creature with haste that's still a land. " +
        "Put three +1/+1 counters on it. When it dies or is exiled, return it to the battlefield tapped.)"

    sagaChapter(1) {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Ally"),
            count = 2
        )
    }

    sagaChapter(2) {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withAnySubtype("Mountain", "Cave"),
            count = 1,
            destination = SearchDestination.HAND,
            reveal = true,
            shuffleAfter = true
        )
    }

    sagaChapter(3) {
        val land = target("target land you control", TargetObject(filter = TargetFilter.Land.youControl()))
        effect = Effects.Earthbend(3, land)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "126"
        artist = "Ittoku"
        imageUri = "https://cards.scryfall.io/normal/front/5/0/50bf5c8b-f218-46b7-843c-8d8083f02fd2.jpg?1782135432"
    }
}
