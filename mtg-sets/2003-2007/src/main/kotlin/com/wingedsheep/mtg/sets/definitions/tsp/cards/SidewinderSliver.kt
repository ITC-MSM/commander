package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Sidewinder Sliver
 * {W}
 * Creature — Sliver
 * 1/1
 * All Sliver creatures have flanking. (Whenever a creature without flanking blocks a Sliver, the
 * blocking creature gets -1/-1 until end of turn.)
 *
 * Flanking's blocked trigger is synthesized by the engine from the keyword alone.
 */
val SidewinderSliver = card("Sidewinder Sliver") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "All Sliver creatures have flanking. (Whenever a creature without flanking blocks a Sliver, the blocking creature gets -1/-1 until end of turn.)"

    staticAbility {
        ability = GrantKeyword(
            Keyword.FLANKING,
            GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Ron Spencer"
        flavorText = "\"They encircled our patrol with the stealth of snakes, corralling us like livestock.\"\n—Merrik Aidar, Benalish patrol"
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b073b8f2-b42d-40f2-b5fb-e78ffb1733ea.jpg"
    }
}
