package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.MayCastSelfFromZones
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Gravecrawler
 * {B}
 * Creature — Zombie
 * 2/1
 * This creature can't block.
 * You may cast this card from your graveyard as long as you control a Zombie.
 */
val Gravecrawler = card("Gravecrawler") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 2
    toughness = 1
    oracleText = "This creature can't block.\n" +
        "You may cast this card from your graveyard as long as you control a Zombie."

    staticAbility {
        ability = CantBlock()
    }

    // "You may cast this card from your graveyard as long as you control a Zombie."
    // Self-referential, conditional cast-from-graveyard permission gated on controlling any
    // Zombie permanent. Normal (sorcery-speed) timing and the {B} mana cost still apply.
    staticAbility {
        ability = MayCastSelfFromZones(
            zones = listOf(Zone.GRAVEYARD),
            condition = Conditions.YouControl(GameObjectFilter.Permanent.withSubtype(Subtype.ZOMBIE))
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "64"
        artist = "Steven Belledin"
        imageUri = "https://cards.scryfall.io/normal/front/4/8/48d73cb5-22ac-43df-9c4b-0c860bb80b3e.jpg?1782714614"
    }
}
