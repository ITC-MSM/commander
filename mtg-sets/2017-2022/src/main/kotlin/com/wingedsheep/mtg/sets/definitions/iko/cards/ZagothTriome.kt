package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Zagoth Triome
 * Land — Swamp Forest Island
 * ({T}: Add {B}, {G}, or {U}.)
 * This land enters tapped.
 * Cycling {3} ({3}, Discard this card: Draw a card.)
 *
 * The mana line is reminder text: the three basic land subtypes grant their mana abilities
 * intrinsically, so the script carries only the tapped-entry replacement and cycling.
 */
val ZagothTriome = card("Zagoth Triome") {
    colorIdentity = "BGU"
    typeLine = "Land — Swamp Forest Island"
    oracleText = "({T}: Add {B}, {G}, or {U}.)\nThis land enters tapped.\nCycling {3} ({3}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{3}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "259"
        artist = "Eytan Zana"
        flavorText = "Hunters in the primeval wetlands become fluent in reading the ripples to tell when to pursue and when to flee."
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc520518-2063-4b57-a0d4-10cf62a7175e.jpg"
    }
}
