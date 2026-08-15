package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantCantLoseGame
import com.wingedsheep.sdk.scripting.GrantOpponentsCantWinGame

/**
 * Herald of Eternal Dawn
 * {4}{W}{W}{W}
 * Creature — Angel
 * 6/6
 *
 * Flash
 * Flying
 * You can't lose the game and your opponents can't win the game.
 *
 * The two clauses are modeled as separate static abilities: [GrantCantLoseGame] protects the
 * controller from losing (life 0, poison, empty draw, and card effects) and
 * [GrantOpponentsCantWinGame] fizzles any effect that would make an opponent win outright.
 */
val HeraldOfEternalDawn = card("Herald of Eternal Dawn") {
    manaCost = "{4}{W}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "Flying\n" +
        "You can't lose the game and your opponents can't win the game."
    power = 6
    toughness = 6

    keywords(Keyword.FLASH, Keyword.FLYING)

    staticAbility {
        ability = GrantCantLoseGame
    }
    staticAbility {
        ability = GrantOpponentsCantWinGame
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "17"
        artist = "Martina Fačková"
        flavorText = "\"I am the light that breaks the darkness.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c9fdfebf-98e0-4718-bac3-6eee1cd0623d.jpg?1782689249"
    }
}
