package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Keeper of Fables
 * {3}{G}{G}
 * Creature — Cat
 * 4/5
 *
 * Whenever one or more non-Human creatures you control deal combat damage to a player, draw a card.
 */
val KeeperOfFables = card("Keeper of Fables") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat"
    oracleText = "Whenever one or more non-Human creatures you control deal combat damage to a player, draw a card."
    power = 4
    toughness = 5

    triggeredAbility {
        // CR 603.2c batch trigger: one draw no matter how many of them connect at once.
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.AnyPlayer,
            sourceFilter = GameObjectFilter.Creature.youControl().notSubtype(Subtype.HUMAN),
            binding = TriggerBinding.ANY,
            batch = true,
        )
        effect = DrawCardsEffect(1)
        description = "Whenever one or more non-Human creatures you control deal combat damage to a " +
            "player, draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "163"
        artist = "Alex Konstad"
        flavorText = "\"Only the lion knows more stories than I do.\"\n—Chulane, Teller of Tales"
        imageUri = "https://cards.scryfall.io/normal/front/6/7/6754d6cf-3506-48b5-a0ef-8a90b8dd2701.jpg?1783932608"
    }
}
