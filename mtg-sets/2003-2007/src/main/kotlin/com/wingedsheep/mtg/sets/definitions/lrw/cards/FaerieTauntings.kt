package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Faerie Tauntings
 * {2}{B}
 * Kindred Enchantment — Faerie
 * Whenever you cast a spell during an opponent's turn, you may have each opponent lose 1 life.
 *
 * "During an opponent's turn" is every turn that isn't yours — each other player is an opponent —
 * so the trigger restriction is [Conditions.IsNotYourTurn], the same reading Glen Elendra
 * Pranksters uses. The "may" is one decision for the whole ability, not one per opponent
 * (ruling 2007-10-01), which is exactly what wrapping the single `EachOpponent` life loss in a
 * [MayEffect] gives.
 *
 * Note: "Tribal" was errata'd to "Kindred" in 2024.
 */
val FaerieTauntings = card("Faerie Tauntings") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Kindred Enchantment — Faerie"
    oracleText = "Whenever you cast a spell during an opponent's turn, you may have each opponent lose 1 life."

    triggeredAbility {
        trigger = Triggers.YouCastSpell
        triggerRestriction = Conditions.IsNotYourTurn
        effect = MayEffect(Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)))
        description = "Whenever you cast a spell during an opponent's turn, you may have each opponent lose 1 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "112"
        artist = "Michael Sutfin"
        flavorText = "Beneath the fae's constant pranks runs a subtler undercurrent of mockery: the influence of Oona, their hidden queen."
        imageUri = "https://cards.scryfall.io/normal/front/3/3/33727782-dafd-4a7c-96dd-6ffaf667cc6b.jpg?1783942890"
        ruling(
            "2007-10-01",
            "You choose either to have each opponent lose 1 life or to have no opponent lose any life. " +
                "You don't choose for each opponent individually."
        )
    }
}
