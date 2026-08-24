package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Stone Spirit
 * {4}{R}
 * Creature — Elemental Spirit
 * 4/3
 *
 * This creature can't be blocked by creatures with flying.
 *
 * `CantBeBlockedBy` is the "excluded blockers" static — its `blockerFilter` names the creatures that
 * *can't* block, and its own `filter` defaults to `GroupFilter.source()`, which is this creature. So
 * the whole card is one facade call over `Creature.withKeyword(FLYING)`.
 */
val StoneSpirit = card("Stone Spirit") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Spirit"
    power = 4
    toughness = 3
    oracleText = "This creature can't be blocked by creatures with flying."

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.withKeyword(Keyword.FLYING))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "218"
        artist = "Jeff A. Menges"
        flavorText = "\"The spirit of the stone is the spirit of strength.\"\n—Lovisa Coldeyes, Balduvian Chieftain"
        imageUri = "https://cards.scryfall.io/normal/front/7/8/789dfae7-fe23-4e2e-9f5f-304535d22a78.jpg"
    }
}
