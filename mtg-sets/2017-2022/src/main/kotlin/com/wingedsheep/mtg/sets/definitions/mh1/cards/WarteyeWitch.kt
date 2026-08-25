package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Warteye Witch
 * {2}{B}
 * Creature — Goblin Shaman
 * 3/2
 * Whenever this creature or another creature you control dies, scry 1.
 *
 * "This creature or another creature you control" is the ANY binding — Warteye Witch is inside its
 * own trigger's scope, so its own death fires it too.
 */
val WarteyeWitch = card("Warteye Witch") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Shaman"
    power = 3
    toughness = 2
    oracleText = "Whenever this creature or another creature you control dies, scry 1."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        effect = Effects.Scry(1)
        description = "Whenever this creature or another creature you control dies, scry 1."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Steve Prescott"
        flavorText = "Eyeballs that portend unpleasant futures become slimy snacks instead."
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0d4dd61-cc7e-4fc5-afce-73a4b326cfdb.jpg?1783933117"
    }
}
