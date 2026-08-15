package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Burning Vengeance
 * {2}{R}
 * Enchantment
 * Whenever you cast a spell from your graveyard, this enchantment deals 2 damage to any target.
 *
 * The flashback payoff: the cast trigger is gated by [SpellCastPredicate.CastFromZone] on the
 * graveyard, so any spell cast from the graveyard (flashback, etc.) fires it.
 */
val BurningVengeance = card("Burning Vengeance") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "Whenever you cast a spell from your graveyard, this enchantment deals 2 damage to any target."

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            requires = setOf(SpellCastPredicate.CastFromZone(Zone.GRAVEYARD))
        )
        val t = target("target", AnyTarget())
        effect = DealDamageEffect(2, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "133"
        artist = "Raymond Swanland"
        flavorText = "Mist is the geists' sorrow. Wind is their pain. Fire is their vengeance."
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd403810-840b-46ac-ae6e-5df23ce16fec.jpg?1782714753"
    }
}
