package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fervent Cathar — Avacyn Restored #135
 * {2}{R} · Creature — Human Knight · 2/1
 *
 * Haste
 * When this creature enters, target creature can't block this turn.
 *
 * The enters trigger targets on announcement and applies [Effects.CantBlock] on resolution; the
 * default [com.wingedsheep.sdk.scripting.Duration.EndOfTurn] is the printed "this turn", so no
 * duration is written.
 */
val FerventCathar = card("Fervent Cathar") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 1
    oracleText = "Haste\n" +
        "When this creature enters, target creature can't block this turn."
    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Creature)
        effect = Effects.CantBlock(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "135"
        artist = "Steven Belledin"
        flavorText = "\"I'll put my sword down only to die. And I don't plan on doing that today.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0fceeb14-a22a-4ab6-9e6d-ba375b6865c0.jpg?1783940685"
    }
}
