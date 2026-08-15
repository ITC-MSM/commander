package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.IncrementAbilityResolutionCountEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tannuk, Memorial Ensign
 * {1}{R}{G}
 * Legendary Creature — Kavu Pilot
 * Landfall — Whenever a land you control enters, Tannuk deals 1 damage to each opponent.
 * If this is the second time this ability has resolved this turn, draw a card.
 * 2/4
 */
val TannukMemorialEnsign = card("Tannuk, Memorial Ensign") {
    manaCost = "{1}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Legendary Creature — Kavu Pilot"
    power = 2
    toughness = 4
    oracleText = "Landfall — Whenever a land you control enters, Tannuk deals 1 damage to each opponent. If this is the second time this ability has resolved this turn, draw a card."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
            .then(IncrementAbilityResolutionCountEffect)
            .then(
                ConditionalEffect(
                    condition = Conditions.SourceAbilityResolvedNTimes(2),
                    effect = Effects.DrawCards(1)
                )
            )
        description = "Landfall — Whenever a land you control enters, Tannuk deals 1 damage to each opponent. If this is the second time this ability has resolved this turn, draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "233"
        artist = "David Auden Nash"
        flavorText = "Serving in the Memorial Navy was nothing like the posters made it out to be."
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52498b7b-0389-4e7b-b29f-7ac86aab9229.jpg?1752947510"
    }
}
