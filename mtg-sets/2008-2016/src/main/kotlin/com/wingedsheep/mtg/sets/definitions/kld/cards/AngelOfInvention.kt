package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Angel of Invention
 * {3}{W}{W}
 * Creature — Angel
 * 2/1
 * Flying, vigilance, lifelink
 * Fabricate 2 (When this creature enters, put two +1/+1 counters on it or create two 1/1 colorless
 * Servo artifact creature tokens.)
 * Other creatures you control get +1/+1.
 *
 * Three plain keywords plus [KeywordAbility.fabricate] — the engine derives fabricate's
 * enters-the-battlefield choice, so the card never spells that modal trigger out. The anthem is a
 * plain [ModifyStats] over [GroupFilter.OtherCreaturesYouControl] (Benalish Marshal); note the
 * Angel's own 2/1 is *not* pumped by it, which is why its printed body is so small.
 */
val AngelOfInvention = card("Angel of Invention") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    oracleText = "Flying, vigilance, lifelink\n" +
        "Fabricate 2 (When this creature enters, put two +1/+1 counters on it or create two 1/1 colorless Servo artifact creature tokens.)\n" +
        "Other creatures you control get +1/+1."
    power = 2
    toughness = 1

    keywords(Keyword.FLYING, Keyword.VIGILANCE, Keyword.LIFELINK)

    keywordAbility(KeywordAbility.fabricate(2))

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter.OtherCreaturesYouControl
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "4"
        artist = "Volkan Baǵa"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f3920f7d-8559-40f8-95be-860c16bf7700.jpg?1783937236"
    }
}
