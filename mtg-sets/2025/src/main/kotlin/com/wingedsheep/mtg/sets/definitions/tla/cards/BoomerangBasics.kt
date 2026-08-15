package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Boomerang Basics
 * {U}
 * Sorcery — Lesson
 *
 * Return target nonland permanent to its owner's hand. If you controlled that permanent, draw a card.
 *
 * The "If you controlled that permanent" draw is keyed on whether the spell's controller
 * controlled the bounced permanent — a fact that is only true *before* the permanent leaves
 * the battlefield. So unlike a trailing `.then(...)` clause (Failed Fording's surveil), the
 * control test must be evaluated up front, while the target is still in play. Modeled as a
 * [ConditionalEffect] whose [Conditions.TargetMatchesFilter] gate (resolution-only, over the
 * chosen target) selects between bounce-and-draw and bounce-only — both branches return the
 * permanent, the true branch additionally draws.
 */
val BoomerangBasics = card("Boomerang Basics") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Sorcery — Lesson"
    oracleText = "Return target nonland permanent to its owner's hand. If you controlled that permanent, draw a card."

    spell {
        val permanent = target("target nonland permanent", Targets.NonlandPermanent)
        effect = ConditionalEffect(
            condition = Conditions.TargetMatchesFilter(
                GameObjectFilter.NonlandPermanent.youControl()
            ),
            effect = Effects.ReturnToHand(permanent).then(Effects.DrawCards(1)),
            elseEffect = Effects.ReturnToHand(permanent),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "46"
        artist = "Tubaki Halsame"
        flavorText = "\"All right buddy, don't fail me now!\""
        imageUri = "https://cards.scryfall.io/normal/front/1/7/17ab958a-abc6-472e-ad6a-97c731d89c74.jpg?1764120201"
    }
}
