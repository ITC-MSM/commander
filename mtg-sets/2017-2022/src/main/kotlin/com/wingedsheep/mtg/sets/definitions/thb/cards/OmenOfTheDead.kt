package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Omen of the Dead
 * {B}
 * Enchantment
 *
 * Flash
 * When this enchantment enters, return target creature card from your graveyard to your hand.
 * {2}{B}, Sacrifice this enchantment: Scry 2.
 *
 * The black member of the Omen cycle: a flash enchantment that cashes in for its spell effect on
 * entry and later sacrifices itself to smooth the next draw.
 *
 * The recursion is [Effects.ReturnToHand] rather than the `fromZone`-guarded
 * [Effects.ReturnToHandFromGraveyard] twin, because the target requirement
 * ([Targets.CreatureCardInYourGraveyard]) already pins the card to your graveyard — a second guard
 * would be redundant on a targeted return.
 */
val OmenOfTheDead = card("Omen of the Dead") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "Flash\n" +
        "When this enchantment enters, return target creature card from your graveyard to your hand.\n" +
        "{2}{B}, Sacrifice this enchantment: Scry 2."

    keywords(Keyword.FLASH)

    // When this enchantment enters, return target creature card from your graveyard to your hand.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creatureCard = target("target", Targets.CreatureCardInYourGraveyard)
        effect = Effects.ReturnToHand(creatureCard)
    }

    // {2}{B}, Sacrifice this enchantment: Scry 2.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{B}"),
            Costs.SacrificeSelf
        )
        effect = Effects.Scry(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "110"
        artist = "Piotr Dura"
        flavorText = "\"My time will come, when life's frantic striving will fade into the boundless quiet of death.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/0/8023fc44-fb8e-420d-a68c-b45912c4e5bd.jpg"
    }
}
