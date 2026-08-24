package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vivien's Jaguar
 * {2}{G}
 * Creature — Cat Spirit
 * 3/2
 * Reach (This creature can block creatures with flying.)
 * {2}{G}: Return this card from your graveyard to your hand. Activate only if you control a Vivien planeswalker.
 *
 * The recursion ability functions in the graveyard, so it carries `activateFromZone` — and the move
 * itself names `fromZone = Zone.GRAVEYARD` so it can only ever pull the card out of that zone.
 *
 * "Activate only if you control a Vivien planeswalker" is an ordinary
 * [ActivationRestriction.OnlyIfCondition]; `GraveyardAbilityEnumerator` runs the same restriction
 * check the battlefield enumerator does, resolving `Player.You` to the card's owner while it sits
 * in their graveyard. The planeswalker is matched by its *subtype* (CR 205.3j), case-exact.
 */
val ViviensJaguar = card("Vivien's Jaguar") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat Spirit"
    power = 3
    toughness = 2
    oracleText = "Reach (This creature can block creatures with flying.)\n" +
        "{2}{G}: Return this card from your graveyard to your hand. Activate only if you control a Vivien planeswalker."

    keywords(Keyword.REACH)

    activatedAbility {
        cost = Costs.Mana("{2}{G}")
        effect = Effects.Move(EffectTarget.Self, Zone.HAND, fromZone = Zone.GRAVEYARD)
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.YouControl(GameObjectFilter.Planeswalker.withSubtype("Vivien"))
            )
        )
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "305"
        artist = "Magali Villeneuve"
        flavorText = "Each of Vivien's arrows is an invocation of a species."
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d4b406cf-738b-4e65-ac00-b1c36ded5f96.jpg"
    }
}
