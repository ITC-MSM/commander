package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Geist of Saint Traft
 * {1}{W}{U}
 * Legendary Creature — Spirit Cleric
 * 2/2
 * Hexproof
 * Whenever this creature attacks, create a 4/4 white Angel creature token with flying that's
 * tapped and attacking. Exile that token at end of combat.
 */
val GeistOfSaintTraft = card("Geist of Saint Traft") {
    manaCost = "{1}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Spirit Cleric"
    oracleText =
        "Hexproof\n" +
            "Whenever this creature attacks, create a 4/4 white Angel creature token with flying " +
            "that's tapped and attacking. Exile that token at end of combat."
    power = 2
    toughness = 2

    keywords(Keyword.HEXPROOF)

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = CreateTokenEffect(
            power = 4,
            toughness = 4,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Angel"),
            keywords = setOf(Keyword.FLYING),
            tapped = true,
            attacking = true,
        ).then(
            CreateDelayedTriggerEffect(
                step = Step.END_COMBAT,
                effect = Effects.Move(
                    target = EffectTarget.PipelineTarget(CREATED_TOKENS, 0),
                    destination = Zone.EXILE,
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "213"
        artist = "Igor Kieryluk"
        imageUri =
            "https://cards.scryfall.io/normal/front/3/5/35b57113-b39a-460b-b4aa-02606b40bbd0.jpg?1783940907"
    }
}
