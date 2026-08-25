package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Enraged Ceratok — Ravnica Allegiance #125
 * {2}{G}{G} · Creature — Rhino · 4 / 4
 *
 * A *filtered* evasion restriction, so it is a [CantBeBlockedBy] static ability rather than
 * the blanket `AbilityFlag.CANT_BE_BLOCKED`. The filter reads the blocker's power, which the
 * block-legality check evaluates against projected state — so a pumped 3-power blocker may
 * block even though its printed power is 2.
 */
val EnragedCeratok = card("Enraged Ceratok") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Rhino"
    power = 4
    toughness = 4
    oracleText = "This creature can't be blocked by creatures with power 2 or less."

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtMost(2))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "125"
        artist = "Lars Grant-West"
        flavorText = "\"There's no time to calm it down! Run!\""
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c44fc50f-8958-422f-933f-fd043d642c97.jpg"
    }
}
