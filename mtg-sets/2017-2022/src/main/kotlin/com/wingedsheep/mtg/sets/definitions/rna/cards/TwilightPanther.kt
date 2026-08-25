package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Twilight Panther — Ravnica Allegiance #28
 * {W} · Creature — Cat Spirit · 1 / 2
 *
 * Deathtouch is *granted* by the activation, not printed, so it is an
 * [Effects.GrantKeyword] on the source rather than a `keywords(...)` line.
 */
val TwilightPanther = card("Twilight Panther") {
    manaCost = "{W}"
    colorIdentity = "BW"
    typeLine = "Creature — Cat Spirit"
    power = 1
    toughness = 2
    oracleText = "{B}: This creature gains deathtouch until end of turn."

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "28"
        artist = "Uriah Voth"
        flavorText = "A pet that can hunt both flesh and spirit is precious in a place where smiling assassins keep company with ghostly shadows."
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5fb149cc-74ca-4bc3-8efc-10ce872b59fb.jpg"
    }
}
