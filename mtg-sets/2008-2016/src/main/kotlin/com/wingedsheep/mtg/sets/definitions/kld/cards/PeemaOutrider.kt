package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Peema Outrider
 * {2}{G}{G}
 * Creature — Elf Artificer
 * 3/3
 * Trample
 * Fabricate 1
 *
 * Both halves are printed keywords. [KeywordAbility.fabricate] is the whole of the second line —
 * the engine derives the "put a +1/+1 counter on it or create that many Servo tokens" enters
 * trigger from the keyword ability, so nothing is hand-expanded here.
 */
val PeemaOutrider = card("Peema Outrider") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Artificer"
    oracleText = "Trample\n" +
        "Fabricate 1 (When this creature enters, put a +1/+1 counter on it or create a 1/1 colorless Servo artifact creature token.)"
    power = 3
    toughness = 3

    keywords(Keyword.TRAMPLE)

    keywordAbility(KeywordAbility.fabricate(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "166"
        artist = "Craig J Spearing"
        flavorText = "Connected as artificer and invention, bonded as rider and steed."
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2ab38bd5-64bb-41aa-851b-c6bc6b44bcf0.jpg?1783937175"
    }
}
