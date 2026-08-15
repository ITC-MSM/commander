package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Besotted Knight // Betroth the Beast
 * {3}{W}
 * Creature — Human Knight
 * 3/3
 *
 * Adventure: Betroth the Beast — {W}, Sorcery — Adventure
 * Create a Royal Role token attached to target creature you control.
 * (Enchanted creature gets +1/+1 and has ward {1}.)
 *
 * (CR 715: Adventure cards. Casting the Adventure exiles the card on resolution and lets the
 * caster cast it as the creature spell while it remains in exile.)
 */
val BesottedKnight = card("Besotted Knight") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    oracleText = ""
    power = 3
    toughness = 3

    adventure("Betroth the Beast") {
        manaCost = "{W}"
        typeLine = "Sorcery — Adventure"
        oracleText = "Create a Royal Role token attached to target creature you control. " +
            "(If you control another Role on it, put that one into the graveyard. Enchanted creature gets +1/+1 and has ward {1}.) " +
            "(Then exile this card. You may cast the creature later from exile.)"
        spell {
            val t = target("target", Targets.CreatureYouControl)
            effect = Effects.CreateRoleToken("Royal Role", t)
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "Andreia Ugrai"
        flavorText = "\"You are no monster to me, my love.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/9/5980a930-c7f8-45e1-a18a-87734d9ed09e.jpg"
    }
}
