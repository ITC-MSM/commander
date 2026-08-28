package com.wingedsheep.mtg.sets.definitions.pcy.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate


/**
 * Plague Wind
 * {7}{B}{B}
 * Sorcery
 * Destroy all creatures you don't control. They can't be regenerated.
 *
 * [Effects.DestroyAll], not a hand-rolled `ForEachInGroup`: "destroy all" destroys the whole set
 * **simultaneously** (CR 701.7b), and the pipeline gathers before it moves. Destroying one at a
 * time is a different card wherever a dies trigger can change what is still on the battlefield when
 * the next one would be destroyed.
 *
 * "You don't control" is `Not(ControlledByYou)` rather than `opponentControls()`. The two agree in a
 * duel and separate in Two-Headed Giant, where a teammate's creatures are ones you do not control
 * and are not ones an opponent controls — and this sweeper hits them.
 */
val PlagueWind = card("Plague Wind") {
    manaCost = "{7}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy all creatures you don't control. They can't be regenerated."
    spell {
        effect = Effects.DestroyAll(
            GameObjectFilter.Creature.withControllerPredicate(
                ControllerPredicate.Not(ControllerPredicate.ControlledByYou)
            ),
            noRegenerate = true
        )
    }
    metadata {
        rarity = Rarity.RARE
        collectorNumber = "74"
        artist = "Alan Pollack"
        flavorText = "\"The second wind of ascension is Reaver, slaying the unworthy.\"\n—Keld Triumphant"
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0d4bd20-7422-45ed-aa76-3ef055c556e7.jpg"
    }
}
