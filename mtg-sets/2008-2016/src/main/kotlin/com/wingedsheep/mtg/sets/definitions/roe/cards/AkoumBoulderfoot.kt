package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Akoum Boulderfoot
 * {4}{R}{R}
 * Creature — Giant Warrior
 * 4 / 5
 *
 * When this creature enters, it deals 1 damage to any target.
 *
 * Modeling notes:
 *  - "**When** this creature enters" is a one-shot [Triggers.EntersBattlefield] on the card itself,
 *    not a `whenever another … enters` watcher — the printed word is "when", and the trigger binds
 *    to the source.
 *  - "Any target" is [Targets.Any] (creature, player, or planeswalker), matching Assay's
 *    `AnyTarget` requirement. The target is chosen as the ability goes on the stack, so it is a
 *    `target(...)` handle on the triggered ability rather than a resolution-time choice.
 *  - "**It** deals" — the damage source is the Boulderfoot, which is [Effects.DealDamage]'s default
 *    when no `damageSource` is passed, so the argument is omitted rather than restated.
 */
val AkoumBoulderfoot = card("Akoum Boulderfoot") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant Warrior"
    power = 4
    toughness = 5
    oracleText = "When this creature enters, it deals 1 damage to any target."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(1, t)
        description = "When this creature enters, it deals 1 damage to any target."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "134"
        artist = "Alex Horley-Orlandelli"
        flavorText = "\"If you're in Akoum and you glimpse a shadow overhead, the worst thing you can do is duck.\"\n—Chadir the Navigator"
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88e8f7b6-1214-447b-8665-ff3c85845564.jpg?1783941978"
    }
}
