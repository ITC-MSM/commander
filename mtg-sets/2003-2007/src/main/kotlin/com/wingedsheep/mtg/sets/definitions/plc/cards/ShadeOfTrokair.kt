package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shade of Trokair
 * {3}{W}
 * Creature — Shade
 * 1/2
 * {W}: This creature gets +1/+1 until end of turn.
 * Suspend 3—{W}
 */
val ShadeOfTrokair = card("Shade of Trokair") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Shade"
    power = 1
    toughness = 2
    oracleText = "{W}: This creature gets +1/+1 until end of turn.\n" +
        "Suspend 3—{W} (Rather than cast this card from your hand, you may pay {W} and exile it with three time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)"

    keywordAbility(KeywordAbility.suspend("{W}", 3))

    activatedAbility {
        cost = Costs.Mana("{W}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        description = "{W}: This creature gets +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "William O'Connor"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b90d62c-5b3e-4830-bdc9-c3e342fc8389.jpg"
    }
}
