package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AdditionalSourceTriggers
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Katara, the Fearless
 * {G}{W}{U}
 * Legendary Creature — Human Warrior Ally
 * 3/3
 *
 * If a triggered ability of an Ally you control triggers, that ability triggers an additional time.
 *
 * Note: the oracle text says "an Ally" (not "another"), so Katara's own triggered abilities are
 * doubled too — `excludeSelf = false`.
 */
val KataraTheFearless = card("Katara, the Fearless") {
    manaCost = "{G}{W}{U}"
    colorIdentity = "GWU"
    typeLine = "Legendary Creature — Human Warrior Ally"
    power = 3
    toughness = 3
    oracleText = "If a triggered ability of an Ally you control triggers, that ability triggers an additional time."

    staticAbility {
        ability = AdditionalSourceTriggers(
            sourceFilter = GameObjectFilter.Creature.withSubtype(Subtype.ALLY),
            excludeSelf = false,
            description = "If a triggered ability of an Ally you control triggers, that ability triggers an additional time",
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "230"
        artist = "Hisashi Momose"
        flavorText = "\"I will never, ever turn my back on people who need me!\""
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0a18f8b-7364-4375-b2e1-e2f15978517f.jpg?1764121694"
    }
}
