package com.wingedsheep.mtg.sets.definitions.sth.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bottomless Pit
 * {1}{B}{B}
 * Enchantment
 * At the beginning of each player's upkeep, that player discards a card at random.
 *
 * Drafted by mtgish-tooling (Triggers.EachUpkeep — the each-player upkeep scope) and verified
 * against Scryfall; behaviour pinned by BottomlessPitScenarioTest.
 */
val BottomlessPit = card("Bottomless Pit") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "At the beginning of each player's upkeep, that player discards a card at random."
    triggeredAbility {
        trigger = Triggers.EachUpkeep
        effect = Patterns.Hand.discardRandom(1, EffectTarget.PlayerRef(Player.TriggeringPlayer))
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "51"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/91f05fc3-da6e-45d4-8566-f4e7bdce1fe5.jpg?1562596885"
        ruling("2004-10-04", "The ability is controlled by the player who controls Bottomless Pit. This means that Bottomless Pit can trigger abilities which trigger off an opponent forcing you to discard.")
    }
}
