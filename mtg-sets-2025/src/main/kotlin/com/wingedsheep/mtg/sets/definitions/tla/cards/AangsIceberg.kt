package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Aang's Iceberg
 * {2}{W}
 * Enchantment
 *
 * Flash
 * When this enchantment enters, exile up to one other target nonland permanent until this
 * enchantment leaves the battlefield.
 * Waterbend {3}: Sacrifice this enchantment. If you do, scry 2. (While paying a waterbend
 * cost, you can tap your artifacts and creatures to help. Each one pays for {1}.)
 *
 * Implementation notes:
 *  - Banishing Light / Static Snare family: an ETB trigger exiles the chosen permanent with a
 *    source link via [Effects.ExileUntilLeaves], and a LTB trigger returns it with
 *    [Effects.ReturnLinkedExileUnderOwnersControl]. "up to one other target nonland permanent"
 *    is an optional target (`optional = true`) over [TargetFilter.NonlandPermanent] restricted
 *    to permanents other than this enchantment (`.other()` / excludeSelf).
 *  - "Waterbend {3}" is an activated ability whose mana cost carries the waterbend
 *    alternative-cost flag ([com.wingedsheep.sdk.scripting.ActivatedAbility] `hasWaterbend`).
 *    The reminder text (tap artifacts/creatures to pay {1} each) is supplied by the flag.
 *  - "Sacrifice this enchantment. If you do, scry 2" sequences [SacrificeSelfEffect] then
 *    [Effects.Scry]; the chained `then` only scries when the sacrifice resolves.
 */
val AangsIceberg = card("Aang's Iceberg") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Flash\n" +
        "When this enchantment enters, exile up to one other target nonland permanent until " +
        "this enchantment leaves the battlefield.\n" +
        "Waterbend {3}: Sacrifice this enchantment. If you do, scry 2. (While paying a " +
        "waterbend cost, you can tap your artifacts and creatures to help. Each one pays for {1}.)"

    keywords(Keyword.FLASH)

    // ETB: exile up to one other target nonland permanent until this leaves.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target(
            "up to one other nonland permanent",
            TargetPermanent(optional = true, filter = TargetFilter.NonlandPermanent.other())
        )
        effect = Effects.ExileUntilLeaves(permanent)
    }

    // LTB: return the exiled card under its owner's control.
    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileUnderOwnersControl()
    }

    // Waterbend {3}: Sacrifice this enchantment. If you do, scry 2.
    activatedAbility {
        cost = Costs.Mana("{3}")
        hasWaterbend = true
        effect = SacrificeSelfEffect then Effects.Scry(2)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "5"
        artist = "Matteo Bassini"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/720fbd87-b1c1-4b3b-97a1-46b943b115e3.jpg?1764119900"
    }
}
