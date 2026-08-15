package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vantress Transmuter // Croaking Curse
 * {3}{U}
 * Creature — Human Wizard
 * 3/4
 *
 * Adventure: Croaking Curse — {1}{U}, Sorcery — Adventure
 * Tap target creature. Create a Cursed Role token attached to it. (Enchanted creature is 1/1.)
 *
 * (CR 715: Adventure cards. Casting the Adventure exiles the card on resolution and lets the
 * caster cast it as the creature spell while it remains in exile.)
 */
val VantressTransmuter = card("Vantress Transmuter") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    oracleText = ""
    power = 3
    toughness = 4

    adventure("Croaking Curse") {
        manaCost = "{1}{U}"
        typeLine = "Sorcery — Adventure"
        oracleText = "Tap target creature. Create a Cursed Role token attached to it. " +
            "(If you control another Role on it, put that one into the graveyard. Enchanted creature has base power and toughness 1/1.) " +
            "(Then exile this card. You may cast the creature later from exile.)"
        spell {
            val t = target("target", Targets.Creature)
            effect = Effects.Composite(
                Effects.Tap(t),
                Effects.CreateRoleToken("Cursed Role", t)
            )
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "75"
        artist = "Andrey Kuzinskiy"
        flavorText = "\"My orders were to capture you alive. They didn't specify in what form.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/1/11507fa1-ef9e-41c9-b987-be57a03bd0df.jpg"
    }
}
