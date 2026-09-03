package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tukatongue Thallid
 * {G}
 * Creature — Fungus
 * 1 / 1
 * When this creature dies, create a 1/1 green Saproling creature token.
 *
 * A plain [Triggers.Dies] self-trigger over [Effects.CreateToken]. The token's characteristics are
 * printed literally, so nothing is read off the dying Fungus — no last-known information is needed
 * here — and the token's controller defaults to the ability's controller.
 */
val TukatongueThallid = card("Tukatongue Thallid") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Fungus"
    power = 1
    toughness = 1
    oracleText = "When this creature dies, create a 1/1 green Saproling creature token."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling")
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "96"
        artist = "Vance Kovacs"
        flavorText = "Jund's thallids tried to disguise their deliciousness by covering themselves in spines harvested from the tukatongue tree."
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a84666a8-4ce5-46e7-9a39-f64a392515e7.jpg"
    }
}
