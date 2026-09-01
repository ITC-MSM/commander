package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Abrupt Decay
 * {B}{G}
 * Instant
 *
 * This spell can't be countered.
 * Destroy target nonland permanent with mana value 3 or less.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * "This spell can't be countered" is the intrinsic [cantBeCountered] flag, not an effect. The
 * mana-value ceiling rides the target filter — [TargetFilter.manaValueAtMost] — so an illegal
 * choice is refused at targeting rather than fizzling on resolution.
 */
val AbruptDecay = card("Abrupt Decay") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Instant"
    oracleText = "This spell can't be countered.\n" +
        "Destroy target nonland permanent with mana value 3 or less."

    cantBeCountered = true

    spell {
        val t = target(
            "target nonland permanent with mana value 3 or less",
            TargetPermanent(filter = TargetFilter.NonlandPermanent.manaValueAtMost(3))
        )
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "141"
        artist = "Svetlin Velinov"
        flavorText = "The Izzet quickly suspended their policy of lifetime guarantees."
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3b1e92b4-6e53-4dba-a572-c67e01965ac5.jpg?1783940344"
    }
}
