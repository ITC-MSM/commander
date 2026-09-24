package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Cruel Calculations — X counts every card put into the target player's graveyard from their
 * library this turn (mill, surveil, …), read when the spell resolves. The count is turn history,
 * so cards that have since left the graveyard still count. You draw; the target only names whose
 * graveyard is counted.
 */
val CruelCalculations = card("Cruel Calculations") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Draw X cards, where X is the number of cards that were put into target player's " +
        "graveyard from their library this turn."

    spell {
        target("target player", Targets.Player)
        effect = Effects.DrawCards(
            DynamicAmounts.cardsPutIntoGraveyardFromLibraryThisTurn(Player.ContextPlayer(0))
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "26"
        artist = "Lindsey Look"
        flavorText = "\"There is no curve. Either you succeeded, or you did not. Either way, this will be " +
            "informative.\"\n—Uldaros Theorix"
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f76c4d8e-3e1f-4264-99af-1b8adb9a06be.jpg?1789127482"
        inBooster = false
    }
}
