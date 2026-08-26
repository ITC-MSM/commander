package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Geyser Glider
 * {3}{R}{R}
 * Creature — Elemental Beast
 * 4/4
 * Landfall — Whenever a land you control enters, this creature gains flying until end of turn.
 *
 * Landfall is [Triggers.LandYouControlEnters] — the `ZoneChangeEvent` over
 * `GameObjectFilter.Land.youControl()` with `TriggerBinding.ANY`.
 */
val GeyserGlider = card("Geyser Glider") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Beast"
    power = 4
    toughness = 4
    oracleText = "Landfall — Whenever a land you control enters, this creature gains flying until end of turn."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "124"
        artist = "Warren Mahy"
        flavorText = "\"Quit pontificating, mage. The last thing we want to do is give it more hot air.\"\n—Tala Vertan, Makindi shieldmate"
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8aec169-4c62-4d53-a19c-68baa20c8e59.jpg"
    }
}
