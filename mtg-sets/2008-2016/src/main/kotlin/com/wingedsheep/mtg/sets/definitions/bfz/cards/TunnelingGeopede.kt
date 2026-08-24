package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tunneling Geopede
 * {2}{R}
 * Creature — Insect
 * 3/2
 * Landfall — Whenever a land you control enters, this creature deals 1 damage to each opponent.
 *
 * Landfall is a plain [Triggers.LandYouControlEnters] — ANY binding, because the printed line never says "another".
 */
val TunnelingGeopede = card("Tunneling Geopede") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Insect"
    power = 3
    toughness = 2
    oracleText = "Landfall — Whenever a land you control enters, this creature deals 1 damage to each opponent."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "158"
        artist = "Tomasz Jedruszek"
        flavorText = "As Ulamog's brood reduces the earth to dust, geopedes burst from their tunnels in search of " +
            "solid ground."
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d4071152-5e64-4133-88a2-8fa5cb0eeb6c.jpg?1783938191"
    }
}
