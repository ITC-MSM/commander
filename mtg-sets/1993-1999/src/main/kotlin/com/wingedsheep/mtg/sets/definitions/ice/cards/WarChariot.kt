package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * War Chariot
 * {3}
 * Artifact
 *
 * {3}, {T}: Target creature gains trample until end of turn.
 *
 * Same shape as Fyndhorn Bow — mana + tap, then [Effects.GrantKeyword] at its default
 * end-of-turn duration; only the keyword differs.
 */
val WarChariot = card("War Chariot") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{3}, {T}: Target creature gains trample until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.TRAMPLE, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "348"
        artist = "Dameon Willich"
        flavorText = "\"I wouldn't advise using it with a Woolly Mammoth, but it's quite appropriate for many other beasts.\"\n—Arcum Dagsson, Soldevi Machinist"
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d0ea0c6c-aa76-4b16-bc99-2ff46dc56d4e.jpg"
    }
}
