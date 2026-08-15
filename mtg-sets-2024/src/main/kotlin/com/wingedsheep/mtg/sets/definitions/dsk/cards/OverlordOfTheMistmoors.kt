package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.impending
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Effect

/**
 * Overlord of the Mistmoors
 * {5}{W}{W}
 * Enchantment Creature — Avatar Horror
 * 6/6
 *
 * Impending 4—{2}{W}{W} (If you cast this spell for its impending cost, it enters with four
 * time counters and isn't a creature until the last is removed. At the beginning of your
 * end step, remove a time counter from it.)
 *
 * Whenever this permanent enters or attacks, create two 2/1 white Insect creature tokens with
 * flying.
 *
 * Impending is wired by the `impending(n, cost)` DSL helper (CR 702.176): the alternative cost,
 * the "isn't a creature while it has a time counter" type-removing static ability, and the
 * "remove a time counter at the beginning of your end step" trigger. The engine places the four
 * time counters when the spell is cast for its impending cost.
 *
 * The "enters or attacks" ability is one effect referenced by both the enters and attacks
 * triggers (sibling pattern to Overlord of the Hauntwoods). The created tokens are 2/1 white
 * flying Insects (DSK white Insect token).
 */
val OverlordOfTheMistmoors = card("Overlord of the Mistmoors") {
    manaCost = "{5}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment Creature — Avatar Horror"
    oracleText = "Impending 4—{2}{W}{W} (If you cast this spell for its impending cost, it enters with four time counters and isn't a creature until the last is removed. At the beginning of your end step, remove a time counter from it.)\n" +
        "Whenever this permanent enters or attacks, create two 2/1 white Insect creature tokens with flying."
    power = 6
    toughness = 6

    impending(4, "{2}{W}{W}")

    // "Create two 2/1 white Insect creature tokens with flying."
    // Shared by the enters and attacks triggers.
    val createInsects: Effect = Effects.CreateToken(
        power = 2,
        toughness = 1,
        colors = setOf(Color.WHITE),
        creatureTypes = setOf("Insect"),
        keywords = setOf(Keyword.FLYING),
        count = 2,
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b802aea8-0ca3-4d2c-a8ef-a80e16729c9b.jpg?1726236464"
    )

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = createInsects
        description = "Whenever this permanent enters, create two 2/1 white Insect creature tokens with flying."
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = createInsects
        description = "Whenever this permanent attacks, create two 2/1 white Insect creature tokens with flying."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "23"
        artist = "Steven Belledin"
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6bcafc2e-cef6-412d-8c5d-1658c3337292.jpg?1726285942"
        ruling("2024-09-20", "If you choose to pay the impending cost rather than the mana cost, you're still casting the spell. It goes on the stack and can be responded to, countered, and so on.")
        ruling("2024-09-20", "If you choose to pay the impending cost of a creature spell, it's still a creature spell on the stack. You can cast that spell for its impending cost only when you could normally cast that creature spell. Most of the time, this means during your main phase when the stack is empty.")
        ruling("2024-09-20", "If an object enters as a copy of a permanent that was cast with its impending cost, it won't enter with time counters, and it will be a creature.")
    }
}
