package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.LookAtFaceDownCreatures

/**
 * Found Footage
 * {1}
 * Artifact — Clue
 * You may look at face-down creatures your opponents control any time.
 * {2}, Sacrifice this artifact: Surveil 2, then draw a card. (To surveil 2, look at the top two
 * cards of your library, then put any number of them into your graveyard and the rest on top of
 * your library in any order.)
 *
 * "Look at face-down creatures your opponents control" maps to the
 * [LookAtFaceDownCreatures] static ability (CR 707.10 — it grants visibility of face-down
 * creatures you don't control). The Clue subtype carries no built-in ability; the printed
 * sacrifice ability here is surveil-2-then-draw, modeled directly as an activated ability.
 */
val FoundFootage = card("Found Footage") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact — Clue"
    oracleText = "You may look at face-down creatures your opponents control any time.\n" +
        "{2}, Sacrifice this artifact: Surveil 2, then draw a card. (To surveil 2, look at the " +
        "top two cards of your library, then put any number of them into your graveyard and the " +
        "rest on top of your library in any order.)"

    staticAbility {
        ability = LookAtFaceDownCreatures
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.SacrificeSelf
        )
        effect = Patterns.Library.surveil(2) then Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "246"
        artist = "Jarel Threat"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b12eb087-762e-4e7d-a6e0-f48df603b7c7.jpg?1726286790"
    }
}
