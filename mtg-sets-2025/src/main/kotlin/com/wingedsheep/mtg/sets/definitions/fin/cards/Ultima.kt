package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Ultima
 * {3}{W}{W}
 * Sorcery
 * Destroy all artifacts and creatures. End the turn.
 *
 * The board wipe resolves first — regeneration still saves creatures (there is no "can't be
 * regenerated" clause). Then "end the turn" (CR 720): all spells and abilities on the stack are
 * exiled, including Ultima itself and the dies triggers from the wipe (even those that can't be
 * countered — they never reach the stack, CR 720.1c); creatures are removed from combat; and the
 * game skips straight to the cleanup step — the active player discards down to their maximum hand
 * size, marked damage wears off, and "this turn" / "until end of turn" effects end — before the
 * next turn begins.
 */
val Ultima = card("Ultima") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy all artifacts and creatures. End the turn. (Exile all spells and " +
        "abilities from the stack, including this card. The player whose turn it is discards down " +
        "to their maximum hand size. Damage wears off, and \"this turn\" and \"until end of " +
        "turn\" effects end.)"

    spell {
        effect = Effects.Composite(
            listOf(
                Effects.DestroyAll(GameObjectFilter.Artifact or GameObjectFilter.Creature),
                Effects.EndTheTurn,
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "38"
        artist = "Gintas Galvanauskas"
        flavorText = "\"Such devastation ... this was not my intention!\"\n—Gaius van Baelsar"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39504a0e-f63f-4907-afd7-c4492f6b8a3b.jpg?1782686568"
        ruling(
            "2025-06-06",
            "Ending the turn happens in order: (1) all spells and abilities on the stack are " +
                "exiled, including the dies triggers from destroying all artifacts and creatures " +
                "and Ultima itself, even ones that can't be countered; (2) attacking and blocking " +
                "creatures are removed from combat; (3) state-based actions are checked, no player " +
                "gets priority, and no triggered abilities are put onto the stack; (4) the game " +
                "skips straight to the cleanup step, which happens in its entirety."
        )
    }
}
