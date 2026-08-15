package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Two-Headed Hunter // Twice the Rage
 * {4}{R}
 * Creature — Giant
 * 5/4
 *
 * Menace
 *
 * Adventure: Twice the Rage — {1}{R}, Instant — Adventure
 * Target creature gains double strike until end of turn.
 *
 * (CR 715: Adventure cards. Casting the Adventure exiles the card on resolution and lets the
 * caster cast it as the creature spell while it remains in exile.)
 */
val TwoHeadedHunter = card("Two-Headed Hunter") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant"
    oracleText = "Menace"
    power = 5
    toughness = 4

    keywords(Keyword.MENACE)

    adventure("Twice the Rage") {
        manaCost = "{1}{R}"
        typeLine = "Instant — Adventure"
        oracleText = "Target creature gains double strike until end of turn. " +
            "(Then exile this card. You may cast the creature later from exile.)"
        spell {
            val t = target("target creature", Targets.Creature)
            effect = Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, t)
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "155"
        artist = "Filip Burburan"
        flavorText = "\"I point, you club!\""
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70c12e75-7e65-4706-b976-e47835910928.jpg?1783915087"
    }
}
