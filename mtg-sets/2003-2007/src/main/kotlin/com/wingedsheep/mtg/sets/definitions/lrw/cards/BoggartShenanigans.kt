package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Boggart Shenanigans
 * {2}{R}
 * Kindred Enchantment — Goblin
 * Whenever another Goblin you control is put into a graveyard from the battlefield, you may have
 * this enchantment deal 1 damage to target player or planeswalker.
 *
 * The bare tribal noun "Goblin" names every permanent with the subtype, so the filter is
 * [GameObjectFilter.Permanent] rather than Creature — Boggart Shenanigans is itself a Goblin, and
 * that is what the "another" ([TriggerBinding.OTHER]) is guarding against. "A graveyard", not
 * "your graveyard": a Goblin you control but don't own still triggers this on the way to its own
 * owner's graveyard, so the filter reads control and the destination is left unqualified.
 *
 * The target is chosen when the ability goes on the stack; the "you may" is asked at resolution.
 */
val BoggartShenanigans = card("Boggart Shenanigans") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Kindred Enchantment — Goblin"
    oracleText = "Whenever another Goblin you control is put into a graveyard from the battlefield, you may have this enchantment deal 1 damage to target player or planeswalker."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Goblin").youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER,
        )
        optional = true
        val t = target("target player or planeswalker", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(1, t)
        description = "you may have this enchantment deal 1 damage to target player or planeswalker."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "155"
        artist = "Warren Mahy"
        flavorText = "Boggarts revel in discovering new sensations, from the texture of an otter pellet to the squeak of a dying warren mate."
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1b3c0ea9-270d-4738-9462-7f48fecb4bf4.jpg?1783942879"
    }
}
