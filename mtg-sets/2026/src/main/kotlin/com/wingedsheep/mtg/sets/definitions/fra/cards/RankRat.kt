package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val RankRat = card("Rank Rat") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Rat"
    oracleText = "When this creature enters, each opponent discards a card."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Hand.eachOpponentDiscards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "61"
        artist = "Johann Bodin"
        flavorText = "The scrabbling of claws across stone was a symphony to Liliana's ears as she ordered the swarm against her sickeningly nice twin."
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4ff6da82-d7dd-4b59-b7e6-30670cea7169.jpg?1789556727"
        inBooster = false
    }
}
