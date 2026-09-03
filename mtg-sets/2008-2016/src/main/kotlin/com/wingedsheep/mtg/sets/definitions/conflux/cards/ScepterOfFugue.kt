package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction

/**
 * Scepter of Fugue
 * {B}{B}
 * Artifact
 * {1}{B}, {T}: Target player discards a card. Activate only during your turn.
 *
 * The discard is the shared [Patterns.Hand.discardCards] recipe (gather the target's hand →
 * they choose one → move it to their graveyard as a discard). Pointing it at the bound target
 * is the whole of "target player discards": the pattern derives both the hand it gathers and
 * the player who chooses from that one argument.
 *
 * "Activate only during your turn" is [ActivationRestriction.OnlyDuringYourTurn].
 */
val ScepterOfFugue = card("Scepter of Fugue") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Artifact"
    oracleText = "{1}{B}, {T}: Target player discards a card. Activate only during your turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.Tap)
        val victim = target("target", Targets.Player)
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
        effect = Patterns.Hand.discardCards(1, victim)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "53"
        artist = "Franz Vohwinkel"
        flavorText = "One goes to Tidehollow either to forget or to be forgotten. Either way, the scullers will oblige."
        imageUri = "https://cards.scryfall.io/normal/front/2/1/21f7e17c-45df-45e9-8dcf-7fc90fa4d65d.jpg"
    }
}
