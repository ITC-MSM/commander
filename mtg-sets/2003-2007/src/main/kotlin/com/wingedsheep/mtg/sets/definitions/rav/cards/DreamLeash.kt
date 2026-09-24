package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ControlEnchantedPermanent
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Dream Leash
 * {3}{U}{U}
 * Enchantment — Aura
 * Enchant permanent
 * You can't choose an untapped permanent as this spell's target as you cast it.
 * You control enchanted permanent.
 *
 * [Confiscate] with a cast-time-only narrowing. `auraTarget` stays the printed "Enchant permanent"
 * — what the spell re-checks on resolution and what the enchant state-based action reads — and
 * `auraCastTarget` narrows only the choice made while casting to a tapped permanent. Per the
 * 2005-10-01 rulings, the permanent untapping later (in response, or once Dream Leash is on the
 * battlefield) changes nothing, and an Aura put onto the battlefield without being cast can attach
 * to an untapped permanent.
 */
val DreamLeash = card("Dream Leash") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant permanent\n" +
        "You can't choose an untapped permanent as this spell's target as you cast it.\n" +
        "You control enchanted permanent."

    auraTarget = Targets.Permanent
    auraCastTarget = TargetPermanent(filter = TargetFilter.Permanent.tapped())

    staticAbility {
        ability = ControlEnchantedPermanent
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "45"
        artist = "Alex Horley-Orlandelli"
        flavorText = "The tendrils of sorcery find easy purchase on the soft underbelly of dreams."
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0de4fa3d-a144-4918-b032-7495ab2e1c8c.jpg?1783943688"
        ruling(
            "2005-10-01",
            "The restriction that the permanent must be tapped is checked only if you cast Dream Leash " +
                "as a spell. Once Dream Leash is on the battlefield, it doesn't matter if the enchanted " +
                "permanent becomes untapped."
        )
        ruling(
            "2005-10-01",
            "If an effect puts Dream Leash onto the battlefield or moves it to another permanent, the " +
                "permanent it becomes attached to need not be tapped."
        )
    }
}
