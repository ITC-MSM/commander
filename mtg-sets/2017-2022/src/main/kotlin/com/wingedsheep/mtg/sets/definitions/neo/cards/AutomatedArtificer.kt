package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Automated Artificer — Kamigawa: Neon Dynasty #239 (canonical printing)
 * {2} · Artifact Creature — Artificer · 1/3
 *
 * {T}: Add {C}. Spend this mana only to activate an ability or cast an artifact spell.
 *
 * Two spend contexts joined by "or", which is [ManaRestriction.AnyOf] over the two atoms rather
 * than a bespoke restriction — the Purple Dragon Punks shape. [ManaRestriction.AbilityActivationOnly]
 * admits *any* activated ability, while the artifact half is spells only
 * ([ManaRestriction.CardTypeSpellsOrAbilitiesOnly] defaults `allowAbilities = false`), so the union
 * is exactly what the card prints.
 */
val AutomatedArtificer = card("Automated Artificer") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Artificer"
    power = 1
    toughness = 3
    oracleText = "{T}: Add {C}. Spend this mana only to activate an ability or cast an artifact spell."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(
            1,
            restriction = ManaRestriction.AnyOf(
                listOf(
                    ManaRestriction.AbilityActivationOnly,
                    ManaRestriction.CardTypeSpellsOrAbilitiesOnly(CardType.ARTIFACT),
                ),
            ),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Add {C}. Spend this mana only to activate an ability or cast an artifact spell."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "239"
        artist = "Izzy"
        flavorText = "\"I sometimes watch them build more of their kind and wonder: Will there " +
            "come a day when they no longer need us?\"\n—Saku, Futurist technician"
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8c30ffb-b4f5-40b0-87b8-800a42bded2b.jpg?1783923828"
    }
}
