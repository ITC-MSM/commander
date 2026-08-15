package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Great Arashin City — Tarkir: Dragonstorm #257
 * Land · Rare
 *
 * This land enters tapped unless you control a Forest or a Plains.
 * {T}: Add {B}.
 * {1}{B}, {T}, Exile a creature card from your graveyard: Create a 1/1 white Spirit creature token.
 *
 * Conditional enters-tapped is the check-land replacement [EntersTapped] gated on controlling a
 * Forest or a Plains (same shape as Kishla Village). {T}: Add {B} is a mana ability. The Spirit
 * ability is a non-mana activated ability whose cost composes {1}{B}, a tap, and exiling a
 * creature card from the graveyard ([AbilityCost.ExileFromGraveyard]); it has no timing
 * restriction, so it can be activated at instant speed.
 */
val GreatArashinCity = card("Great Arashin City") {
    typeLine = "Land"
    colorIdentity = "B"
    oracleText = "This land enters tapped unless you control a Forest or a Plains.\n" +
        "{T}: Add {B}.\n" +
        "{1}{B}, {T}, Exile a creature card from your graveyard: Create a 1/1 white Spirit creature token."

    replacementEffect(EntersTapped(
        unlessCondition = Conditions.Any(
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Forest")),
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Plains"))
        )
    ))

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Composite(
            listOf(
                Costs.Mana(ManaCost.parse("{1}{B}")),
                AbilityCost.Tap,
                Costs.ExileFromGraveyard(count = 1, filter = GameObjectFilter.Creature)
            )
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            imageUri = "https://cards.scryfall.io/normal/front/f/2/f22410b3-5c0b-4282-9b0b-5ba61229b6e7.jpg?1743176224"
        )
        description = "Create a 1/1 white Spirit creature token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "257"
        artist = "Josu Solano"
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ecba23b6-9f3a-431e-bc22-f1fb04d27b68.jpg?1743205015"
    }
}
