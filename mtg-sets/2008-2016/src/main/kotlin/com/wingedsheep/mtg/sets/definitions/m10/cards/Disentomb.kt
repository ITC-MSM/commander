package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Disentomb
 * {B}
 * Sorcery
 *
 * Return target creature card from your graveyard to your hand.
 *
 * The target requirement carries both halves of the noun phrase — the owner ("your graveyard")
 * and the zone — via [Targets.CreatureCardInYourGraveyard]. The move itself writes no `fromZone`
 * guard: a targeted graveyard-to-hand return is the unguarded [Effects.ReturnToHand], unlike the
 * graveyard-to-battlefield return, which keeps it.
 */
val Disentomb = card("Disentomb") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Return target creature card from your graveyard to your hand."

    spell {
        val t = target("target", Targets.CreatureCardInYourGraveyard)
        effect = Effects.ReturnToHand(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "92"
        artist = "Alex Horley-Orlandelli"
        flavorText = "\"Stop complaining. You can rest when you're dead. Oh—sorry.\"\n" +
            "—Liliana Vess"
        imageUri = "https://cards.scryfall.io/normal/front/9/9/99a329a0-a14a-49b9-adcd-397b566211ee.jpg?1783942383"
    }
}
