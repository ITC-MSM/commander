package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Helm of the Host
 * {4}
 * Legendary Artifact — Equipment
 * At the beginning of combat on your turn, create a token that's a copy of equipped
 * creature, except the token isn't legendary. That token gains haste.
 * Equip {5}
 */
val HelmOfTheHost = card("Helm of the Host") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Legendary Artifact — Equipment"
    oracleText = "At the beginning of combat on your turn, create a token that's a copy of equipped creature, except the token isn't legendary. That token gains haste.\nEquip {5}"

    triggeredAbility {
        trigger = Triggers.BeginCombat
        effect = Effects.CreateTokenCopyOfEquippedCreature(
            removeLegendary = true,
            grantHaste = true
        )
    }

    equipAbility("{5}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "217"
        artist = "Igor Kieryluk"
        flavorText = "Forged out of flowstone for the queen of Vesuva."
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1d65d20c-09e5-4139-838b-7e0e48eb2b2b.jpg?1666094567"
    }
}
