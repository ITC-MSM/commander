package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Necrogen Mists
 * {2}{B}
 * Enchantment
 * At the beginning of each player's upkeep, that player discards a card.
 *
 * Drafted by mtgish-tooling (Triggers.EachUpkeep — the each-player upkeep scope) and verified
 * against Scryfall; behaviour pinned by NecrogenMistsScenarioTest.
 */
val NecrogenMists = card("Necrogen Mists") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "At the beginning of each player's upkeep, that player discards a card."
    triggeredAbility {
        trigger = Triggers.EachUpkeep
        effect = Patterns.Hand.discardCards(1, EffectTarget.PlayerRef(Player.TriggeringPlayer))
    }
    metadata {
        rarity = Rarity.RARE
        collectorNumber = "69"
        artist = "Alex Horley-Orlandelli"
        imageUri = "https://cards.scryfall.io/normal/front/1/8/18291514-9ffb-4032-9c77-cec0200bf1b6.jpg?1562137104"
    }
}
