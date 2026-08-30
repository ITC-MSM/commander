package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Golgari Germination
 * {1}{B}{G}
 * Enchantment
 *
 * Whenever a nontoken creature you control dies, create a 1/1 green Saproling creature token.
 *
 * The dies clause is `leavesBattlefield(… to = GRAVEYARD)` — a battlefield→graveyard move, not a
 * graveyard-scoped trigger. The `nontoken()` predicate is what stops the Saprolings it makes from
 * feeding it further; without it the enchantment would loop off its own tokens.
 *
 * [TriggerBinding.ANY] rather than OTHER, matching Ulvenwald Mysteries: a leaves-the-battlefield
 * trigger looks back in time (CR 603.10a / 608.2g), so a creature dying *simultaneously* with the
 * enchantment still triggers it, and if some effect has animated the enchantment its own death
 * counts too.
 *
 * The token takes the set's own Saproling art, declared as a [
 * com.wingedsheep.sdk.model.TokenPrinting] on `RavnicaCityOfGuildsSet` — Ravnica predates token
 * *cards*, so there is no `trav` printing to point `imageUri` at.
 */
val GolgariGermination = card("Golgari Germination") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Enchantment"
    oracleText = "Whenever a nontoken creature you control dies, create a 1/1 green Saproling " +
        "creature token."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.youControl().nontoken(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling")
        )
        description = "Whenever a nontoken creature you control dies, create a 1/1 green " +
            "Saproling creature token."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "209"
        artist = "Thomas M. Baxa"
        flavorText = "The Golgari don't bury their dead. They plant them."
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24a1db16-d936-4f92-9611-327a988ce04c.jpg?1783943620"
    }
}
