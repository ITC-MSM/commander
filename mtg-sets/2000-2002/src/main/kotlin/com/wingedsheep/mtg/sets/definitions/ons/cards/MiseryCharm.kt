package com.wingedsheep.mtg.sets.definitions.ons.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.targets.TargetPlayer
import com.wingedsheep.sdk.dsl.Effects

/**
 * Misery Charm
 * {B}
 * Instant
 * Choose one —
 * • Destroy target Cleric.
 * • Return target Cleric card from your graveyard to your hand.
 * • Target player loses 2 life.
 *
 * Modeling note: "Cleric" is a bare tribal noun in both of the first two modes, so it names every
 * *permanent* of that type rather than only a creature — [TargetFilter.Permanent] on the battlefield
 * and [TargetFilter.PermanentInYourGraveyard] for the card in the graveyard, which is the facade the
 * bare-noun migration created for exactly this phrase.
 */
val MiseryCharm = card("Misery Charm") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Destroy target Cleric.\n• Return target Cleric card from your graveyard to your hand.\n• Target player loses 2 life."

    spell {
        modal(chooseCount = 1) {
            mode("Destroy target Cleric") {
                val t = target("target", TargetPermanent(filter = TargetFilter.Permanent.withSubtype("Cleric")))
                effect = Effects.Move(
                    target = t,
                    destination = Zone.GRAVEYARD,
                    byDestruction = true
                )
            }
            mode("Return target Cleric card from your graveyard to your hand") {
                val t = target("target", TargetObject(
                    filter = TargetFilter.PermanentInYourGraveyard.withSubtype("Cleric")
                ))
                effect = Effects.Move(
                    target = t,
                    destination = Zone.HAND
                )
            }
            mode("Target player loses 2 life") {
                val t = target("target", TargetPlayer())
                effect = LoseLifeEffect(2, t)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "158"
        artist = "David Martin"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2be66eaf-222b-4c40-a9fa-aad56b9218e0.jpg?1562905282"
    }
}
