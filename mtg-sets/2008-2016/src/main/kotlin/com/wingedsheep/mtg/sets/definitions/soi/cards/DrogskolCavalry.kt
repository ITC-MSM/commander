package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Drogskol Cavalry (Shadows over Innistrad #15)
 * {5}{W}{W}
 * Creature — Spirit Knight
 * 4 / 4
 *
 * Flying
 * Whenever another Spirit you control enters, you gain 2 life.
 * {3}{W}: Create a 1/1 white Spirit creature token with flying.
 *
 * Modeling notes:
 *  - "Another Spirit you control" is the bare tribal noun, so the filter is
 *    `GameObjectFilter.Permanent.withSubtype(SPIRIT)` rather than the creature-only form —
 *    `TriggerBinding.OTHER` is what carries the "another".
 *  - The token is the set's own 1/1 white flying Spirit, the same shape [DauntlessCathar] makes.
 */
val DrogskolCavalry = card("Drogskol Cavalry") {
    manaCost = "{5}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Knight"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "Whenever another Spirit you control enters, you gain 2 life.\n" +
        "{3}{W}: Create a 1/1 white Spirit creature token with flying."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.SPIRIT).youControl(),
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.GainLife(2)
    }

    activatedAbility {
        cost = Costs.Mana("{3}{W}")
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "15"
        artist = "Igor Kieryluk"
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46c262c8-9daa-4ed8-b519-11b5895de89f.jpg?1783937821"
    }
}
