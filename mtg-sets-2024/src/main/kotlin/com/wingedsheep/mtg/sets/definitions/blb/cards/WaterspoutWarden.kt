package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Waterspout Warden
 * {2}{U}
 * Creature — Frog Soldier
 * 3/2
 * Whenever this creature attacks, if another creature entered the battlefield under your
 * control this turn, this creature gains flying until end of turn.
 */
val WaterspoutWarden = card("Waterspout Warden") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Frog Soldier"
    oracleText = "Whenever this creature attacks, if another creature entered the battlefield under your control this turn, this creature gains flying until end of turn."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Exists(
            player = Player.You,
            zone = Zone.BATTLEFIELD,
            filter = GameObjectFilter.Creature
                .enteredThisTurn()
                .youControl(),
            excludeSelf = true
        )
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "80"
        artist = "J.P. Targete"
        flavorText = "The escape route of many a birdfolk thief has been less surefire than they'd thought."
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35898b39-98e2-405b-8f18-0e054bd2c29e.jpg?1722207696"
    }
}
