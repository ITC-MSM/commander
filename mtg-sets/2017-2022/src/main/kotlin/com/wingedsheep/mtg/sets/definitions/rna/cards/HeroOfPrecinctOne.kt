package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Hero of Precinct One — Ravnica Allegiance #11
 * {1}{W} · Creature — Human Warrior · 2 / 2
 *
 * The same multicolour cast trigger as [TomeOfTheGuildpact], paying off in 1/1 white Humans.
 * Token art resolves through `TokenArtData` from the set code, so no `imageUri` is authored
 * here.
 */
val HeroOfPrecinctOne = card("Hero of Precinct One") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 2
    oracleText = "Whenever you cast a multicolored spell, create a 1/1 white Human creature token."

    triggeredAbility {
        trigger = Triggers.youCastSpell(spellFilter = GameObjectFilter.Multicolored)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human")
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "11"
        artist = "Bram Sels"
        flavorText = "When the established order falters, what remains are ordinary people and their struggle to survive."
        imageUri = "https://cards.scryfall.io/normal/front/8/7/87732718-1067-4e5f-a76d-409539c9ef3f.jpg"
    }
}
