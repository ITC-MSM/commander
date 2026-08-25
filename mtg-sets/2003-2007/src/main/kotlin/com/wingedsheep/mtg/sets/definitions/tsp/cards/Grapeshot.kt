package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Grapeshot
 * {1}{R}
 * Sorcery
 * Grapeshot deals 1 damage to any target.
 * Storm (When you cast this spell, copy it for each spell cast before it this turn. You may
 * choose new targets for the copies.)
 *
 * Storm (CR 702.40) copies the spell off `script.spellEffect`, so this stays a plain
 * `spell { effect = … }` — a modal or replacement shape would make the trigger resolve into
 * zero copies.
 */
val Grapeshot = card("Grapeshot") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Grapeshot deals 1 damage to any target.\n" +
        "Storm (When you cast this spell, copy it for each spell cast before it this turn. You may choose new targets for the copies.)"

    spell {
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(1, t)
    }

    keywords(Keyword.STORM)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "160"
        artist = "Pete Venters"
        flavorText = "Mages often seek to emulate the powerful relics lost to time and apocalypse."
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4ee33cb6-768e-44a0-b6f4-b8638aa84330.jpg"
    }
}
