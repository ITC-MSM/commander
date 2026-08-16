package com.wingedsheep.mtg.sets.definitions.frf.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Monastery Mentor
 * {2}{W}
 * Creature — Human Monk
 * 2/2
 * Prowess
 * Whenever you cast a noncreature spell, create a 1/1 white Monk creature token with prowess.
 *
 * Canonical printing: Fate Reforged, the card's earliest real-expansion printing. Reprinted in MOM
 * as a `Printing` row.
 *
 * The two abilities are independent triggers off the same event — `prowess()` lowers to the keyword
 * plus its own +1/+1 trigger, and the token ability is a second `YouCastNoncreature` trigger, so
 * casting one noncreature spell puts both on the stack (the printed ruling). The token carries the
 * PROWESS keyword; `CreateTokenExecutor` grants the matching triggered ability from it, and per the
 * ruling that token's own prowess does not see the spell that made it.
 */
val MonasteryMentor = card("Monastery Mentor") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Monk"
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until " +
        "end of turn.)\n" +
        "Whenever you cast a noncreature spell, create a 1/1 white Monk creature token with prowess."
    power = 2
    toughness = 2

    prowess()

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Monk"),
            keywords = setOf(Keyword.PROWESS),
            imageUri = "https://cards.scryfall.io/normal/front/3/1/3142cb28-23cc-405f-9db5-7c4d168aab19.jpg?1783938663"
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "20"
        artist = "Magali Villeneuve"
        flavorText = "\"Speak little. Do much.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abcd0e32-2e6b-419b-9e8a-af38f2b48a66.jpg?1783938710"
        ruling(
            "2014-11-24",
            "Casting a noncreature spell will cause both prowess and Monastery Mentor's other " +
                "ability to trigger. You can put these abilities on the stack in either order. " +
                "Whichever ability is put on the stack last will resolve first."
        )
        ruling(
            "2014-11-24",
            "The spell that causes Monastery Mentor's second ability to trigger will not cause the " +
                "prowess ability of the Monk token that's created to trigger."
        )
        ruling(
            "2014-11-24",
            "Any spell you cast that doesn't have the type creature will cause prowess to trigger. " +
                "If a spell has multiple types, and one of those types is creature (such as an " +
                "artifact creature), casting it won't cause prowess to trigger. Playing a land " +
                "also won't cause prowess to trigger."
        )
    }
}
