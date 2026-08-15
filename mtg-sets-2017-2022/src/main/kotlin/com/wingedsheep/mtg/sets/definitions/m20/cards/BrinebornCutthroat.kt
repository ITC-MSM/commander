package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Brineborn Cutthroat
 * {1}{U}
 * Creature — Merfolk Pirate
 * 2/1
 *
 * Flash
 * Whenever you cast a spell during an opponent's turn, put a +1/+1 counter on this creature.
 *
 * The grow trigger uses [Triggers.YouCastSpell] gated by [Conditions.IsNotYourTurn] as a
 * fire-time trigger condition, so it only fires for spells cast on a turn that isn't the
 * controller's — the "during an opponent's turn" rider. Flash lets it be cast at instant
 * speed to enable those responses in the first place.
 *
 * Canonical printing: Core Set 2020 (earliest real printing). Reprinted in Jumpstart 2022
 * (J22 #278) and Foundations (FDN #152).
 */
val BrinebornCutthroat = card("Brineborn Cutthroat") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Pirate"
    power = 2
    toughness = 1
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "Whenever you cast a spell during an opponent's turn, put a +1/+1 counter on this creature."

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.YouCastSpell
        triggerCondition = Conditions.IsNotYourTurn
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever you cast a spell during an opponent's turn, put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "50"
        artist = "Caio Monteiro"
        flavorText = "\"I always attack from where their spyglasses can't see.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/8/0857765f-afd7-418a-a93b-c0bd1b1f037e.jpg?1782708357"
    }
}
