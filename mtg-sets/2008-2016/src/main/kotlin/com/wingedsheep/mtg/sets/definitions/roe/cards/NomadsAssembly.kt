package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nomads' Assembly
 * {4}{W}{W}
 * Sorcery
 *
 * Create a 1/1 white Kor Soldier creature token for each creature you control.
 * Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)
 *
 * Modeling notes:
 *  - Rebound has a real consumer — `StackResolver` reads [Keyword.REBOUND] off `cardDef.keywords`
 *    when the spell resolves — so the bare keyword is the whole of the second line and the
 *    assembly happens twice, a turn apart, from one [Effects.CreateToken].
 *  - "for each creature you control" is the token *count*, so this is the `DynamicAmount` overload
 *    of [Effects.CreateToken], not the `Int` one. [DynamicAmounts.creaturesYouControl] is exactly
 *    `AggregateBattlefield(You, Creature)`, the shape Assay compiles the clause to, and it is
 *    evaluated at resolution — the second, rebound cast counts the board as it stands *then*,
 *    including the tokens the first cast made.
 *  - The count includes the tokens' own controller's whole board, with no self-exclusion: a
 *    sorcery is not on the battlefield, so there is nothing to exclude.
 *  - No `imageUri` on the effect. ROE's token sheet (`troe`) never printed a Kor Soldier, so there
 *    is no set-scoped art to bake, and baking another set's art would freeze it across every
 *    printing — token art belongs on `MtgSet.tokenArt` / `tokens.json`, keyed by minting set.
 *  - The token's name falls out of its creature types ("Kor Soldier"), which is what the printed
 *    "1/1 white Kor Soldier creature token" wants, so no `name` override.
 */
val NomadsAssembly = card("Nomads' Assembly") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Create a 1/1 white Kor Soldier creature token for each creature you control.\n" +
        "Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)"

    keywords(Keyword.REBOUND)

    spell {
        effect = Effects.CreateToken(
            count = DynamicAmounts.creaturesYouControl(),
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Kor", "Soldier")
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "39"
        artist = "Erica Yang"
        imageUri = "https://cards.scryfall.io/normal/front/d/d/dd52f249-14bd-4489-aaba-03e50fe42c2e.jpg?1783942004"
    }
}
