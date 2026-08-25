package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tin Street Dodger — Ravnica Allegiance #120
 * {R} · Creature — Goblin Rogue · 1 / 1
 *
 * "Can't be blocked … except by creatures with defender" is the *filtered exception* form,
 * so it is [Effects.GrantCantBeBlockedExceptBy] with a blocker filter rather than the blanket
 * `AbilityFlag.CANT_BE_BLOCKED` — a defender-having blocker is still legal.
 */
val TinStreetDodger = card("Tin Street Dodger") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Rogue"
    power = 1
    toughness = 1
    oracleText = "Haste\n" +
        "{R}: This creature can't be blocked this turn except by creatures with defender."

    keywords(Keyword.HASTE)
    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.GrantCantBeBlockedExceptBy(
            target = EffectTarget.Self,
            blockerFilter = GameObjectFilter.Creature.withKeyword(Keyword.DEFENDER)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "120"
        artist = "Yeong-Hao Han"
        flavorText = "\"That giant didn't even see me, let alone catch me! And I was close enough to smell him! Of course, that's not saying much.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3815ab6-87cd-4310-8068-ec721ee10a24.jpg"
    }
}
