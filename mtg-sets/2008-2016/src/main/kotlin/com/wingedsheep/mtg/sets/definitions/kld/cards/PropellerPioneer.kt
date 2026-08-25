package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Propeller Pioneer
 * {3}{W}
 * Creature — Human Artificer
 * 2/1
 * Flying
 * Fabricate 1 (When this creature enters, put a +1/+1 counter on it or create a 1/1 colorless Servo
 * artifact creature token.)
 *
 * Both lines are printed keywords. Fabricate is [KeywordAbility.fabricate] and nothing else — the
 * engine derives the enters-the-battlefield "counter or Servo" choice from the keyword ability, so
 * hand-expanding it into a modal ETB trigger here would give the creature the ability twice.
 */
val PropellerPioneer = card("Propeller Pioneer") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Artificer"
    oracleText = "Flying\n" +
        "Fabricate 1 (When this creature enters, put a +1/+1 counter on it or create a 1/1 colorless Servo artifact creature token.)"
    power = 2
    toughness = 1

    keywords(Keyword.FLYING)

    keywordAbility(KeywordAbility.fabricate(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "24"
        artist = "Winona Nelson"
        flavorText = "\"The sky isn't the limit. It's the starting point.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/e/cee60224-960a-4f92-996a-7b0b878109e4.jpg?1783937229"
    }
}
