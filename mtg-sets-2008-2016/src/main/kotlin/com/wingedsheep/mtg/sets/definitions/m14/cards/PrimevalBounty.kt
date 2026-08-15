package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Primeval Bounty
 * {5}{G}
 * Enchantment
 *
 * Whenever you cast a creature spell, create a 3/3 green Beast creature token.
 * Whenever you cast a noncreature spell, put three +1/+1 counters on target creature you control.
 * Landfall — Whenever a land you control enters, you gain 3 life.
 *
 * Oracle note: original M14 wording said "Whenever a land enters the battlefield under
 * your control, you gain 3 life." Current Oracle text (post-Foundations) renames this to
 * a Landfall keyword ability — same mechanical behavior, so a single triggered-ability
 * implementation is used here.
 */
val PrimevalBounty = card("Primeval Bounty") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Whenever you cast a creature spell, create a 3/3 green Beast creature token.\n" +
        "Whenever you cast a noncreature spell, put three +1/+1 counters on target creature you control.\n" +
        "Landfall — Whenever a land you control enters, you gain 3 life."

    triggeredAbility {
        trigger = Triggers.YouCastCreature
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Beast"),
            imageUri = "https://cards.scryfall.io/normal/front/a/8/a8fc2dc9-40df-46d8-98c0-ca4919bd5524.jpg?1562497273"
        )
    }

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        val creature = target("creature", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 3, creature)
    }

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "190"
        artist = "Christine Choi"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e750d55d-d5e8-4abe-99cf-f6b8ba86cf16.jpg?1562836598"
        ruling("2024-11-08", "Primeval Bounty's first and second abilities will each resolve before the spell that caused them to trigger. They will resolve even if that spell is countered or otherwise leaves the stack without resolving.")
    }
}
