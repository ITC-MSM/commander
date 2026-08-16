package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wing Commando
 * {2}{U}
 * Creature — Human Soldier
 * 2/2
 * Flying
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 */
val WingCommando = card("Wing Commando") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 2
    oracleText = "Flying\nProwess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)"

    keywords(Keyword.FLYING)
    prowess()

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "David Auden Nash"
        flavorText = "\"The welds are weaker at the top.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/3/43e640c3-02bb-453c-a609-05d8214e2e2e.jpg?1783920103"
        ruling("2022-10-14", "Any spell you cast that doesn't have the type creature will cause prowess to trigger. If a spell has multiple types, and one of those types is creature (such as an artifact creature), casting it won't cause prowess to trigger. Playing a land also won't cause prowess to trigger.")
        ruling("2022-10-14", "Prowess goes on the stack on top of the spell that caused it to trigger. It will resolve before that spell.")
        ruling("2022-10-14", "Once it triggers, prowess isn't connected to the spell that caused it to trigger. If that spell is countered, prowess will still resolve.")
    }
}
