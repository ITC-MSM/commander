package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.champion
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Boggart Mob
 * {3}{B}
 * Creature — Goblin Warrior
 * 5/5
 *
 * Champion a Goblin
 * Whenever a Goblin you control deals combat damage to a player, you may create a 1/1 black
 * Goblin Rogue creature token.
 *
 * "A Goblin you control" is a bare tribal noun (CR 109.2) — a Goblin *permanent*, matched with
 * `ANY` binding so the Mob's own combat damage triggers it too, alongside every other Goblin.
 * One trigger per damaging Goblin, not one per combat: the printed text is the per-source
 * "whenever a Goblin", not a batch wording.
 */
val BoggartMob = card("Boggart Mob") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Warrior"
    power = 5
    toughness = 5
    oracleText = "Champion a Goblin (When this enters, sacrifice it unless you exile another " +
        "Goblin you control. When this leaves the battlefield, that card returns to the battlefield.)\n" +
        "Whenever a Goblin you control deals combat damage to a player, you may create a 1/1 " +
        "black Goblin Rogue creature token."

    champion(Subtype.GOBLIN)

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.AnyPlayer,
            sourceFilter = GameObjectFilter.Permanent.withSubtype(Subtype.GOBLIN).youControl(),
            binding = TriggerBinding.ANY
        )
        effect = MayEffect(
            Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.BLACK),
                creatureTypes = setOf("Goblin", "Rogue"),
                imageUri = "https://cards.scryfall.io/normal/front/f/4/f44d5271-5d10-46b2-9ba2-5788d99de2e6.jpg?1783942839"
            )
        )
        description = "Whenever a Goblin you control deals combat damage to a player, you may " +
            "create a 1/1 black Goblin Rogue creature token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "104"
        artist = "Thomas Denmark"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a61990de-0daf-4340-8e6b-c49852d46980.jpg?1783942893"
    }
}
