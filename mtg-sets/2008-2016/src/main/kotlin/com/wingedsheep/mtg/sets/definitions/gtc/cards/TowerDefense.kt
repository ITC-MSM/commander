package com.wingedsheep.mtg.sets.definitions.gtc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Tower Defense — Gatecrash #137
 * {1}{G} · Instant
 *
 * The canonical lives here, in Gatecrash — its earliest real printing — and RNA carries a
 * Printing row. `pumpAndGrantToAll` names the group **once**: gathering it twice (a pump pass
 * plus a grant pass) would let a filter the first pass changes match a different set the second
 * time, which is not what the printed sentence says.
 */
val TowerDefense = card("Tower Defense") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Creatures you control get +0/+5 and gain reach until end of turn."

    spell {
        effect = Patterns.Group.pumpAndGrantToAll(
            power = 0,
            toughness = 5,
            keyword = Keyword.REACH,
            filter = GroupFilter.AllCreaturesYouControl
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "137"
        artist = "Seb McKinnon"
        flavorText = "\"The drakes are practice. We may one day need to bring down a sky swallower, or maybe even Rakdos himself.\"\n" +
        "—Korun Nar, Rubblebelt hunter"
        imageUri = "https://cards.scryfall.io/normal/front/8/5/857e1eb2-f3f2-4c7f-9965-da9d7e385223.jpg"
    }
}
