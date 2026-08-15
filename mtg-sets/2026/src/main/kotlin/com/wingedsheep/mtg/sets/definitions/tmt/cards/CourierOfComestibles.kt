package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Courier of Comestibles
 * {1}{G}
 * Creature — Human Citizen
 * 1/2
 *
 * When this creature enters, you may search your library for a Food card,
 * reveal it, put it into your hand, then shuffle. If you don't put a card into
 * your hand this way, create a Food token.
 */
val CourierOfComestibles = card("Courier of Comestibles") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Citizen"
    oracleText = "When this creature enters, you may search your library for a Food card, reveal it, put it into your hand, then shuffle. If you don't put a card into your hand this way, create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"
    power = 1
    toughness = 2

    // The search is optional (ChooseUpTo 0..1); if no Food card ends up in hand
    // (declined or none found), the IfYouDont branch creates a Food token instead.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = IfYouDoEffect(
            action = Patterns.Library.searchLibrary(
                filter = GameObjectFilter.Any.withSubtype("Food"),
                count = 1,
                destination = SearchDestination.HAND,
                shuffleAfter = true,
                reveal = true
            ),
            ifYouDo = Effects.Composite(),
            ifYouDont = Effects.CreateFood()
        )
        description = "When this creature enters, you may search your library for a Food card, reveal it, put it into your hand, then shuffle. If you don't put a card into your hand this way, create a Food token."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "112"
        artist = "Mirko Failoni"
        flavorText = "Forgiveness is divine, but never pay full price for late pizza."
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53f5f704-2265-42bb-bff5-fd9d85bc2bfb.jpg?1771342389"
    }
}
