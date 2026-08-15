package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.MayCastSelfFromZones

/**
 * Squee, the Immortal
 * {1}{R}{R}
 * Legendary Creature — Goblin
 * 2/1
 * You may cast this card from your graveyard or from exile.
 */
val SqueeTheImmortal = card("Squee, the Immortal") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Goblin"
    power = 2
    toughness = 1
    oracleText = "You may cast this card from your graveyard or from exile."

    staticAbility {
        ability = MayCastSelfFromZones(zones = listOf(Zone.GRAVEYARD, Zone.EXILE))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "146"
        artist = "Svetlin Velinov"
        flavorText = "\"You gotta be pretty smart to live long as me, but not being able to die helps.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3974c62-a524-454e-9ce7-2c23b704e5cb.jpg?1562740600"
        ruling("2018-04-27", "Squee's ability doesn't prevent you from casting Squee from any other zone.")
        ruling("2018-04-27", "You must follow the normal timing permissions and restrictions and pay its cost to cast Squee from your graveyard or from exile.")
    }
}
