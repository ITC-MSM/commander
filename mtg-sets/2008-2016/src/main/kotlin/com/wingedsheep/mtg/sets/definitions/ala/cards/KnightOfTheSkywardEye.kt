package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Knight of the Skyward Eye
 * {1}{W}
 * Creature — Human Knight
 * 2 / 2
 * {3}{G}: This creature gets +3/+3 until end of turn. Activate only once each turn.
 *
 * A plain [Costs.Mana] pump on itself — [Effects.ModifyStats] over [EffectTarget.Self], whose
 * default `Duration.EndOfTurn` is the printed "until end of turn", so no duration is spelled.
 * "Activate only once each turn" is [ActivationRestriction.OncePerTurn], a restriction checked at
 * activation rather than a triggered-ability flag. The off-colour {G} in the cost is why the card's
 * colour identity is green-white even though the card itself is mono-white.
 */
val KnightOfTheSkywardEye = card("Knight of the Skyward Eye") {
    manaCost = "{1}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 2
    oracleText = "{3}{G}: This creature gets +3/+3 until end of turn. Activate only once each turn."

    activatedAbility {
        cost = Costs.Mana("{3}{G}")
        effect = Effects.ModifyStats(3, 3, EffectTarget.Self)
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Matt Stewart"
        flavorText = "The Order of the Skyward Eye does the bidding of an evil force, unwittingly stirring fear and mistrust across Bant in accordance with his plans."
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1d56e2bf-1937-42c1-8f61-1fd93e84cef7.jpg"
    }
}
