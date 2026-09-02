package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Springmane Cervin — Strixhaven: School of Mages #144 (canonical printing)
 * {2}{G} · Creature — Elk · 3/2
 *
 * When this creature enters, you gain 2 life.
 *
 * A plain ETB trigger ([Triggers.EntersBattlefield]) whose effect is [Effects.GainLife] for the
 * controller.
 */
val SpringmaneCervin = card("Springmane Cervin") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elk"
    oracleText =
        "When this creature enters, you gain 2 life."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "144"
        artist = "Ilse Gort"
        flavorText = "Cervins feed on the mana-rich plants around the star arches, imbuing their bodies with extraordinary grace and vitality."
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f5b0eac4-0262-4eed-97d4-0f2e6f06c8e1.jpg?1783927337"
    }
}
