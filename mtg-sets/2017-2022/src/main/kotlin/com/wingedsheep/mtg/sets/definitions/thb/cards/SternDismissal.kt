package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Stern Dismissal
 * {U}
 * Instant
 *
 * Return target creature or enchantment an opponent controls to its owner's hand.
 *
 * One target, not two: the "creature or enchantment" is a single `Or` predicate on the target
 * filter, and "an opponent controls" is the filter's controller predicate rather than a separate
 * clause. Type and controller are both read off projected state when target legality is checked,
 * so a creature that became an enchantment — or changed hands — this turn is judged as it stands.
 */
val SternDismissal = card("Stern Dismissal") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target creature or enchantment an opponent controls to its owner's hand."

    spell {
        val victim = target(
            "target",
            TargetPermanent(filter = TargetFilter.CreatureOrEnchantment.opponentControls()),
        )
        effect = Effects.ReturnToHand(victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "68"
        artist = "Lie Setiawan"
        flavorText = "Cities offer tribute to Ephara and carve her image into their walls, imploring her to " +
            "protect them from the dangers of the wild."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0aec4d0f-ba1e-45f8-9764-9bcc3fa50e51.jpg"
    }
}
