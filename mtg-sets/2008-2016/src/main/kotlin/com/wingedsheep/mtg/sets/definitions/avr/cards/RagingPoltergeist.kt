package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Raging Poltergeist
 * {4}{R}
 * Creature — Spirit
 * 6/1
 *
 * Vanilla — no rules text.
 */
val RagingPoltergeist = card("Raging Poltergeist") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Spirit"
    power = 6
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "150"
        artist = "Slawomir Maniak"
        flavorText = "Some tried cremating their dead to stop the ghoulcallers. But the dead returned, furious about their fate."
        imageUri = "https://cards.scryfall.io/normal/front/7/8/78833788-ffb2-43fc-9345-975f1cd46f38.jpg?1783940678"
    }
}
