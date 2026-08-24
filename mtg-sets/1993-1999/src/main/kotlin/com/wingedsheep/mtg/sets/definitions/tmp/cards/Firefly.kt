package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Firefly
 * {3}{R}
 * Creature — Insect
 * 1/1
 * Flying
 * {R}: This creature gets +1/+0 until end of turn.
 */
val Firefly = card("Firefly") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Insect"
    power = 1
    toughness = 1
    oracleText = "Flying\n" +
        "{R}: This creature gets +1/+0 until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "172"
        artist = "Stephen Daniele"
        flavorText = "\"If they don't pinch, they burn. Can't ya eat any of da bugs here?\"\n" +
            "—Squee, goblin cabin hand"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a312f0cf-225a-4f3d-b9a7-c47dd03b25c3.jpg"
    }
}
