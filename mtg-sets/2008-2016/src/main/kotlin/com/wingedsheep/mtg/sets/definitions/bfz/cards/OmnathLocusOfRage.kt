package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Omnath, Locus of Rage
 * {3}{R}{R}{G}{G}
 * Legendary Creature — Elemental
 * 5/5
 * Landfall — Whenever a land you control enters, create a 5/5 red and green Elemental creature token.
 * Whenever Omnath or another Elemental you control dies, Omnath deals 3 damage to any target.
 *
 * The dies trigger is ANY-bound over Elementals you control, so Omnath's own death and every
 * token it made fire it — the tokens are Elementals, which is what makes a board wipe
 * with Omnath out lethal.
 */
val OmnathLocusOfRage = card("Omnath, Locus of Rage") {
    manaCost = "{3}{R}{R}{G}{G}"
    colorIdentity = "GR"
    typeLine = "Legendary Creature — Elemental"
    power = 5
    toughness = 5
    oracleText = "Landfall — Whenever a land you control enters, create a 5/5 red and green Elemental creature " +
        "token.\n" +
        "Whenever Omnath or another Elemental you control dies, Omnath deals 3 damage to any target."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.CreateToken(
            power = 5,
            toughness = 5,
            colors = setOf(Color.RED, Color.GREEN),
            creatureTypes = setOf("Elemental"),
        )
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Elemental").youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        val victim = target("any target", Targets.Any)
        effect = Effects.DealDamage(3, victim)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "217"
        artist = "Brad Rigney"
        imageUri = "https://cards.scryfall.io/normal/front/5/8/58f311e7-7ebf-4428-b5a3-154255eb3ba1.jpg?1783938179"
    }
}
