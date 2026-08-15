package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Rise of the Varmints
 * {3}{G}
 * Sorcery
 *
 * Create X 2/1 green Varmint creature tokens, where X is the number of creature cards in your
 * graveyard.
 * Plot {2}{G}
 */
val RiseOfTheVarmints = card("Rise of the Varmints") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Create X 2/1 green Varmint creature tokens, where X is the number of creature " +
        "cards in your graveyard.\n" +
        "Plot {2}{G} (You may pay {2}{G} and exile this card from your hand. Cast it as a sorcery " +
        "on a later turn without paying its mana cost. Plot only as a sorcery.)"

    spell {
        effect = Effects.CreateToken(
            count = DynamicAmount.Count(
                player = Player.You,
                zone = Zone.GRAVEYARD,
                filter = GameObjectFilter.Creature
            ),
            power = 2,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Varmint"),
            imageUri = "https://cards.scryfall.io/normal/front/d/c/dcefb329-b93d-41a1-88d3-8058afebeca8.jpg?1712316688"
        )
    }

    keywordAbility(KeywordAbility.plot("{2}{G}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "179"
        artist = "Ralph Horsley"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f4e879d8-a058-48e8-9733-f58b1e0da4b9.jpg?1712355986"

        ruling("2024-04-12", "The number of creature cards in your graveyard is counted as Rise of the Varmints resolves.")
        ruling("2024-04-12", "Plot abilities are written \"Plot [cost],\" which means \"Any time you have priority during your main phase while the stack is empty, you may pay [cost] and exile this card from your hand. It becomes plotted.\"")
        ruling("2024-04-12", "You can't cast a plotted card on the same turn it became plotted. On any future turn, you may cast that card from exile without paying its mana cost during your main phase while the stack is empty.")
    }
}
