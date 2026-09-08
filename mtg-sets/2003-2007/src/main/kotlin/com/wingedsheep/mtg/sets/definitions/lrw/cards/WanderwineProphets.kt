package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.champion
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SuccessCriterion

/**
 * Wanderwine Prophets
 * {4}{U}{U}
 * Creature — Merfolk Wizard
 * 4/4
 *
 * Champion a Merfolk
 * Whenever this creature deals combat damage to a player, you may sacrifice a Merfolk. If you do,
 * take an extra turn after this one.
 *
 * "You may sacrifice a Merfolk. If you do, …" is [MayEffect] over [IfYouDoEffect]: the yes/no is
 * the "may", and the extra turn is gated on the sacrifice actually happening — declining, or having
 * no Merfolk to give, takes no extra turn. The Prophets are themselves a Merfolk, so sacrificing
 * this creature to its own trigger is a legal (and printed) line.
 */
val WanderwineProphets = card("Wanderwine Prophets") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 4
    toughness = 4
    oracleText = "Champion a Merfolk (When this enters, sacrifice it unless you exile another " +
        "Merfolk you control. When this leaves the battlefield, that card returns to the battlefield.)\n" +
        "Whenever this creature deals combat damage to a player, you may sacrifice a Merfolk. If " +
        "you do, take an extra turn after this one."

    champion(Subtype.MERFOLK)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = MayEffect(
            IfYouDoEffect(
                action = Effects.SacrificeOwn(
                    GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK)
                ),
                ifYouDo = Effects.TakeExtraTurn(),
                successCriterion = SuccessCriterion.PermanentsSacrificed
            )
        )
        description = "Whenever this creature deals combat damage to a player, you may sacrifice " +
            "a Merfolk. If you do, take an extra turn after this one."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "95"
        artist = "Alex Horley-Orlandelli"
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6cdbb6ff-c945-4dd6-aba6-4fd4b77824c0.jpg?1783942895"
    }
}
