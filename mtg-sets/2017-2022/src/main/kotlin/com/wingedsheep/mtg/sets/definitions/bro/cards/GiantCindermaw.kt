package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.PreventLifeGain
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Giant Cindermaw
 * {2}{R}
 * Creature — Dinosaur Beast
 * 4/3
 * Trample
 * Players can't gain life.
 */
val GiantCindermaw = card("Giant Cindermaw") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dinosaur Beast"
    power = 4
    toughness = 3
    oracleText = "Trample\nPlayers can't gain life."

    keywords(Keyword.TRAMPLE)

    replacementEffect(PreventLifeGain(appliesTo = EventPattern.LifeGainEvent(player = Player.Each)))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "136"
        artist = "Edgar Sánchez Hidalgo"
        flavorText = "\"Don't excavate too far into those canyons. I promised your parents I'd return you in one piece.\"\n—Tocasia"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/1349465f-d29f-4d4b-a653-f4388574c336.jpg?1782699769"
        ruling("2022-10-14", "Spells and abilities that cause players to gain life still resolve while Giant Cindermaw is on the battlefield. No player will gain life, but any other effects of that spell or ability will happen.")
        ruling("2022-10-14", "If an effect says to set a player's life total to a number that's higher than the player's current life total while Giant Cindermaw is on the battlefield, the player's life total doesn't change.")
    }
}
