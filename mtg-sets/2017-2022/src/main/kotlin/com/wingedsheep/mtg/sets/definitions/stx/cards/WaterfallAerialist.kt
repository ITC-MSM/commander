package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Waterfall Aerialist — Strixhaven: School of Mages #61 (canonical printing)
 * {3}{U} · Creature — Djinn Wizard · 3/1
 *
 * Flying
 * Ward {2} (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays {2}.)
 *
 * Two keywords and nothing else: a plain [Keyword.FLYING] marker plus `Ward {2}` as
 * [KeywordAbility.ward] (CR 702.21a) — the bare `Keyword.WARD` marker is derived from that ability
 * by the builder, so it is not restated.
 */
val WaterfallAerialist = card("Waterfall Aerialist") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Djinn Wizard"
    oracleText =
        "Flying\n" +
        "Ward {2} (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays {2}.)"
    power = 3
    toughness = 1

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.ward("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "61"
        artist = "Lie Setiawan"
        flavorText = "Form and function in perfect unity."
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7bc6966c-18d9-4675-9594-6f0b2d11c405.jpg?1783927372"
    }
}
