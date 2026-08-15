package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Deadly Embrace
 * {3}{B}{B}
 * Sorcery
 * Destroy target creature an opponent controls. Then draw a card for each creature that
 * died this turn.
 *
 * The destroy resolves first, so the creature it kills has already died this turn by the time
 * the draw counts — it is included in the count (per the Scryfall ruling). "Each creature that
 * died this turn" is controller-agnostic, so the count sums [Player.Each]: the number of
 * creatures that died under any player's control this turn.
 */
val DeadlyEmbrace = card("Deadly Embrace") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature an opponent controls. Then draw a card for each creature that died this turn."

    spell {
        val creature = target("target creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.Destroy(creature) then
            Effects.DrawCards(DynamicAmounts.creaturesDiedThisTurn(Player.Each))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "557"
        artist = "Lius Lasahido"
        flavorText = "\"Come, little lamb... to the slaughter with you!\""
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a11cb85c-85dd-435c-8303-4d0d18bdb1e9.jpg?1748707601"
    }
}
