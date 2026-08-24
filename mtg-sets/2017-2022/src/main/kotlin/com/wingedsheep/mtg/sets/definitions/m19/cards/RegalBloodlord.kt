package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Regal Bloodlord
 * {3}{W}{B}
 * Creature — Vampire Soldier
 * 2/4
 * Flying
 * At the beginning of each end step, if you gained life this turn, create a 1/1 black Bat creature token with flying.
 *
 * The Bat trigger is an intervening-"if" ability: it only triggers when you gained life at some
 * point during the turn before the end step began, and it re-checks that on resolution. Net life
 * change is irrelevant — losing life afterwards does not undo the gain.
 */
val RegalBloodlord = card("Regal Bloodlord") {
    manaCost = "{3}{W}{B}"
    colorIdentity = "BW"
    typeLine = "Creature — Vampire Soldier"
    power = 2
    toughness = 4
    oracleText = "Flying\n" +
        "At the beginning of each end step, if you gained life this turn, create a 1/1 black Bat creature token with flying."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EachEndStep
        interveningIf = Conditions.YouGainedLifeThisTurn
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Bat"),
            keywords = setOf(Keyword.FLYING)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "222"
        artist = "Winona Nelson"
        flavorText = "Those of esteemed birth earn a most esteemed death."
        imageUri = "https://cards.scryfall.io/normal/front/6/5/65a75d3a-58cb-4ee0-88d3-52099cb57ac3.jpg"
    }
}
