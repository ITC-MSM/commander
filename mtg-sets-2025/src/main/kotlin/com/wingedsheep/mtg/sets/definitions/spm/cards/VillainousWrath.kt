package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetOpponent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Villainous Wrath
 * {3}{B}{B}
 * Sorcery
 * Target opponent loses life equal to the number of creatures they control. Then destroy all creatures.
 */
val VillainousWrath = card("Villainous Wrath") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Target opponent loses life equal to the number of creatures they control. Then destroy all creatures."

    spell {
        val opp = target("target opponent", TargetOpponent())
        effect = Effects.LoseLife(
            DynamicAmount.AggregateBattlefield(Player.TargetOpponent, GameObjectFilter.Creature),
            opp
        ) then Effects.DestroyAll(GameObjectFilter.Creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "74"
        artist = "InHyuk Lee"
        flavorText = "\"You fool! You thought you had won?\""
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d78e36fd-5817-4c4a-8880-dabe6dd4ba81.jpg?1757377225"
    }
}
