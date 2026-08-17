package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AdditionalManaOnTap
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Buried in the Garden — Murders at Karlov Manor #191
 * {2}{G}{W} · Enchantment — Aura
 *
 * Enchant land
 * When this Aura enters, exile target nonland permanent you don't control until this Aura leaves
 * the battlefield.
 * Whenever enchanted land is tapped for mana, its controller adds an additional one mana of any color.
 *
 * A Banishing Light stapled to a Fertile Ground, and both halves already have their shape here.
 * The removal is the standard [Effects.ExileUntilLeaves] / [Effects.ReturnLinkedExileUnderOwnersControl]
 * pair — the enters trigger links the exiled permanent to this Aura and the leaves trigger returns
 * it, which is what makes the three rulings fall out for free: the exile never happens if the Aura
 * is gone before the trigger resolves, the returning card is a new object (so counters and Auras on
 * it are lost), and an exiled token simply ceases to exist with nothing to return.
 *
 * The mana half is [AdditionalManaOnTap] with `anyColor = true` — the Fertile Ground shape, where
 * the controller picks the colour on each manual tap and the auto-tapper treats the bonus as
 * flexible when paying a cost. Note who gets the mana: the ability says *its controller*, so
 * enchanting an opponent's land hands them the bonus. That falls out of the static being keyed to
 * the tap rather than to this Aura's controller.
 */
val BuriedInTheGarden = card("Buried in the Garden") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant land\n" +
        "When this Aura enters, exile target nonland permanent you don't control until this Aura " +
        "leaves the battlefield.\n" +
        "Whenever enchanted land is tapped for mana, its controller adds an additional one mana of " +
        "any color."

    auraTarget = Targets.Land

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val exiled = target("exiled", TargetPermanent(filter = TargetFilter.NonlandPermanentOpponentControls))
        effect = Effects.ExileUntilLeaves(exiled)
        description = "When this Aura enters, exile target nonland permanent you don't control " +
            "until this Aura leaves the battlefield."
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileUnderOwnersControl()
    }

    staticAbility {
        ability = AdditionalManaOnTap(amount = DynamicAmount.Fixed(1), anyColor = true)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "191"
        artist = "Tom Babbey"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e144609-e1f6-4bdc-8d14-b735ef4140d3.jpg?1783912854"
        ruling(
            "2024-02-02",
            "If Buried in the Garden leaves the battlefield before its first triggered ability " +
                "resolves, the target permanent won't be exiled."
        )
        ruling(
            "2024-02-02",
            "Auras attached to the exiled permanent will be put into their owners' graveyards. Any " +
                "Equipment will become unattached and remain on the battlefield. Any counters on the " +
                "exiled permanent will cease to exist. When the card returns to the battlefield, it " +
                "will be a new object with no connection to the card that was exiled."
        )
        ruling(
            "2024-02-02",
            "If a token is exiled this way, it will cease to exist and won't return to the battlefield."
        )
    }
}
