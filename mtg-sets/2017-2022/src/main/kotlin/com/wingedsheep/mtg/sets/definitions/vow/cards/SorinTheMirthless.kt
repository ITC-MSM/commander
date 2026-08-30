package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.ConditionalOnCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Sorin the Mirthless — Innistrad: Crimson Vow #131
 * {2}{B}{B} · Legendary Planeswalker — Sorin · Mythic · Starting loyalty 4
 *
 * +1: Look at the top card of your library. You may reveal that card and put it into your hand.
 *     If you do, you lose life equal to its mana value.
 * −2: Create a 2/3 black Vampire creature token with flying and lifelink.
 * −7: Sorin deals 13 damage to any target. You gain 13 life.
 *
 * Modeling notes:
 *
 *  - **The +1 is the [Patterns.Library.lookAtTopRevealMatchingToHand] sentence with the "rest"
 *    destination moved back to the *top* of the library.** The printed ruling is explicit: "If you
 *    choose not to reveal the card you looked at with Sorin's first loyalty ability, it stays on
 *    top of your library" — so the declined card goes to `ZonePlacement.Top` (and
 *    `CardOrder.Preserve`, though a one-card pile makes the order moot) rather than the recipe's
 *    default bottom. The filter is [GameObjectFilter.Any]: *any* card may be revealed, so the
 *    choice is purely "do I want it at this life cost", not a type test.
 *  - **"If you do" is a fold over the pipeline's own output, not a second decision.** The recipe
 *    stores the taken card in the `kept` collection, so the life loss is a
 *    [ConditionalOnCollectionEffect] over `kept` — declining the reveal leaves it empty and no
 *    life-loss event is emitted at all (rather than a cosmetic "lose 0 life"). The amount reads
 *    that same collection through [DynamicAmount.StoredCardManaValue], which reads the *first* card
 *    of the collection — "the mana value of **that** card" — the same amount Darkstar Augur uses for
 *    the mandatory version of this sentence. The entity-id read survives the card's move to hand.
 *  - **The −7 targets** ("any target" — creature, player, planeswalker or battle), and the life
 *    gain is unconditional: it is a separate sentence, not an "if you do" rider, so Sorin's
 *    controller gains 13 even if the damage is prevented.
 */
val SorinTheMirthless = card("Sorin the Mirthless") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Planeswalker — Sorin"
    startingLoyalty = 4
    oracleText = "+1: Look at the top card of your library. You may reveal that card and put it " +
        "into your hand. If you do, you lose life equal to its mana value.\n" +
        "−2: Create a 2/3 black Vampire creature token with flying and lifelink.\n" +
        "−7: Sorin deals 13 damage to any target. You gain 13 life."

    // +1: Look at the top card of your library. You may reveal that card and put it into your hand.
    //     If you do, you lose life equal to its mana value.
    loyaltyAbility(+1) {
        effect = Effects.Composite(
            Patterns.Library.lookAtTopRevealMatchingToHand(
                count = DynamicAmount.Fixed(1),
                filter = GameObjectFilter.Any,
                prompt = "You may reveal the top card of your library and put it into your hand " +
                    "(you lose life equal to its mana value)",
                // Printed ruling: a declined card stays on top of your library.
                restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Top),
                restOrder = CardOrder.Preserve
            ),
            // "If you do" — only when a card was actually revealed and taken.
            ConditionalOnCollectionEffect(
                collection = "kept",
                ifNotEmpty = Effects.LoseLife(
                    DynamicAmount.StoredCardManaValue("kept"),
                    EffectTarget.Controller
                )
            )
        )
        description = "Look at the top card of your library. You may reveal that card and put it " +
            "into your hand. If you do, you lose life equal to its mana value."
    }

    // −2: Create a 2/3 black Vampire creature token with flying and lifelink.
    loyaltyAbility(-2) {
        effect = Effects.CreateToken(
            power = 2,
            toughness = 3,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Vampire"),
            keywords = setOf(Keyword.FLYING, Keyword.LIFELINK),
            imageUri = "https://cards.scryfall.io/normal/front/e/b/ebd12ac1-d1b2-4e22-9a17-8f3964294e35.jpg?1783924698"
        )
        description = "Create a 2/3 black Vampire creature token with flying and lifelink."
    }

    // −7: Sorin deals 13 damage to any target. You gain 13 life.
    loyaltyAbility(-7) {
        val victim = target("any target", Targets.Any)
        effect = Effects.DealDamage(13, victim) then Effects.GainLife(13)
        description = "Sorin deals 13 damage to any target. You gain 13 life."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "131"
        artist = "Martina Fačková"
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc7ff5f4-a7cc-41a1-a22b-8cf67ad18707.jpg?1783924850"

        ruling("2021-11-19", "If you choose not to reveal the card you looked at with Sorin's first loyalty ability, it stays on top of your library.")
    }
}
