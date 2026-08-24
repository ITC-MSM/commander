package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Grovetender Druids
 * {2}{G}{W}
 * Creature — Elf Druid Ally
 * 3/3
 * Rally — Whenever this creature or another Ally you control enters, you may pay {1}. If you do, create a 1/1 green Plant creature token.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so the creature's own arrival fires it alongside every later Ally.
 */
val GrovetenderDruids = card("Grovetender Druids") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Elf Druid Ally"
    power = 3
    toughness = 3
    oracleText = "Rally — Whenever this creature or another Ally you control enters, you may pay {1}. If you do, " +
        "create a 1/1 green Plant creature token."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{1}"),
            effect = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.GREEN),
                creatureTypes = setOf("Plant"),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "212"
        artist = "Chase Stone"
        flavorText = "\"The seedlings scream for us to loose them upon the Eldrazi, and we shall oblige.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c2fd3a4-e09a-4fe5-93eb-2276a65c4911.jpg?1783938179"
    }
}
