package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Minecart Daredevil // Ride the Rails
 * {2}{R}
 * Creature — Dwarf Knight
 * 4/2
 *
 * Adventure: Ride the Rails — {1}{R}, Instant — Adventure
 * Target creature gets +2/+1 until end of turn.
 *
 * (CR 715: Adventure cards. Casting the Adventure exiles the card on resolution and lets the
 * caster cast it as the creature spell while it remains in exile.)
 */
val MinecartDaredevil = card("Minecart Daredevil") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dwarf Knight"
    oracleText = ""
    power = 4
    toughness = 2

    adventure("Ride the Rails") {
        manaCost = "{1}{R}"
        typeLine = "Instant — Adventure"
        oracleText = "Target creature gets +2/+1 until end of turn. " +
            "(Then exile this card. You may cast the creature later from exile.)"
        spell {
            val t = target("target", Targets.Creature)
            effect = Effects.ModifyStats(2, 1, t)
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "141"
        artist = "Edgar Sánchez Hidalgo"
        flavorText = "Mines that once rang with the strikes of pickaxes now echo with shouts of reckless glee."
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5b2a02f3-3921-4f40-9ffa-70bc08b052e1.jpg"
    }
}
