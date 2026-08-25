package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.riot
import com.wingedsheep.sdk.model.Rarity

/**
 * Zhur-Taa Goblin — Ravnica Allegiance #215
 * {R}{G} · Creature — Goblin Berserker · 2 / 2
 *
 * Riot and nothing else. The keyword is display-only in the rules engine, so the mechanic is
 * wired by the [riot] DSL helper: an `EntersWithChoice(MODE)` recording the pick, a mode-gated
 * `EntersWithCounters` for the +1/+1 counter, and a mode-gated haste grant (CR 702.136). Never
 * write `keywordAbility(KeywordAbility.simple(Keyword.RIOT))` beside it — that ships a creature
 * whose riot does nothing.
 */
val ZhurTaaGoblin = card("Zhur-Taa Goblin") {
    manaCost = "{R}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Goblin Berserker"
    power = 2
    toughness = 2
    oracleText = "Riot (This creature enters with your choice of a +1/+1 counter or haste.)"

    riot()

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "215"
        artist = "Wayne Reynolds"
        flavorText = "Among the Zhur-Taa Clan, goblins are the first to enter battlefury. When the battle is over, the survivors are still frothing at the mouth, looking for someone to hit."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13070db2-cf89-4552-8b6c-76426274321a.jpg"
    }
}
