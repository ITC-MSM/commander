package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Knight-Captain of Eos
 * {4}{W}
 * Creature — Human Knight
 * 2 / 2
 * When this creature enters, create two 1/1 white Soldier creature tokens.
 * {W}, Sacrifice a Soldier: Prevent all combat damage that would be dealt this turn.
 *
 * The entry trigger is [Triggers.EntersBattlefield] with a single [Effects.CreateToken] carrying
 * `count = 2` — one effect making two tokens, not two effects. The fog is the tokens' payoff: "a
 * Soldier" is a bare tribal noun, so the sacrifice cost filters *permanents* with the subtype
 * (`GameObjectFilter.Permanent.withSubtype`) rather than creatures, and the Knight-Captain's own
 * Soldiers pay for it. [Effects.PreventAllCombatDamage] is the un-targeted, board-wide shield —
 * `PreventionScope.CombatOnly` for the turn, both players' creatures alike.
 */
val KnightCaptainOfEos = card("Knight-Captain of Eos") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, create two 1/1 white Soldier creature tokens.\n" +
        "{W}, Sacrifice a Soldier: Prevent all combat damage that would be dealt this turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Soldier"),
            count = 2,
            imageUri = "https://cards.scryfall.io/normal/front/9/2/927580e6-353a-46bb-ac05-52a5fa78a959.jpg"
        )
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{W}"),
            Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype("Soldier"))
        )
        effect = Effects.PreventAllCombatDamage()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "17"
        artist = "Chris Rahn"
        flavorText = "The strength of Bant's caste system is the unfailing loyalty of its meekest members."
        imageUri = "https://cards.scryfall.io/normal/front/0/2/02ef25ce-4d6a-45ff-8b6b-fd5c29307099.jpg"
    }
}
