package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.impending
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Effect

/**
 * Overlord of the Hauntwoods
 * {3}{G}{G}
 * Enchantment Creature — Avatar Horror
 * 6/5
 *
 * Impending 4—{1}{G}{G} (If you cast this spell for its impending cost, it enters with four
 * time counters and isn't a creature until the last is removed. At the beginning of your
 * end step, remove a time counter from it.)
 *
 * Whenever this permanent enters or attacks, create a tapped colorless land token named
 * Everywhere that is every basic land type.
 *
 * Impending is wired by the `impending(n, cost)` DSL helper (CR 702.176): the alternative
 * cost, the "isn't a creature while it has a time counter" type-removing static ability, and
 * the "remove a time counter at the beginning of your end step" trigger. The engine places
 * the four time counters when the spell is cast for its impending cost.
 *
 * The Everywhere token is a predefined token (see PredefinedTokens.Everywhere): a land with
 * all five basic land subtypes that taps for any color. Per the Scryfall ruling it has the
 * mana ability of each basic land type and does not have the basic supertype. The "enters or
 * attacks" ability is one effect referenced by both the enters and attacks triggers.
 */
val OverlordOfTheHauntwoods = card("Overlord of the Hauntwoods") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment Creature — Avatar Horror"
    oracleText = "Impending 4—{1}{G}{G} (If you cast this spell for its impending cost, it enters with four time counters and isn't a creature until the last is removed. At the beginning of your end step, remove a time counter from it.)\n" +
        "Whenever this permanent enters or attacks, create a tapped colorless land token named Everywhere that is every basic land type."
    power = 6
    toughness = 5

    impending(4, "{1}{G}{G}")

    // "Create a tapped colorless land token named Everywhere that is every basic land type."
    // Shared by the enters and attacks triggers.
    val createEverywhere: Effect = Effects.CreateEverywhere(count = 1, tapped = true)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = createEverywhere
        description = "Whenever this permanent enters, create a tapped colorless land token named Everywhere that is every basic land type."
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = createEverywhere
        description = "Whenever this permanent attacks, create a tapped colorless land token named Everywhere that is every basic land type."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "194"
        artist = "Tiffany Turrill"
        imageUri = "https://cards.scryfall.io/normal/front/0/5/05d08ff1-edcc-4c76-96e0-683b3da36ebb.jpg?1726286590"
        ruling("2024-09-20", "The Everywhere token created by this card's last ability has the land types Plains, Island, Swamp, Mountain, and Forest. It also has the mana ability of each basic land type (for example, Forests have \"{T}: Add {G}\"). It does not have the basic supertype.")
        ruling("2024-09-20", "If you choose to pay the impending cost rather than the mana cost, you're still casting the spell. It goes on the stack and can be responded to, countered, and so on.")
        ruling("2024-09-20", "If an object enters as a copy of a permanent that was cast with its impending cost, it won't enter with time counters, and it will be a creature.")
    }
}
