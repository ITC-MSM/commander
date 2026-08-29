package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Woodwraith Strangler
 * {2}{B}{G}
 * Creature — Plant Zombie
 * 2/2
 * Exile a creature card from your graveyard: Regenerate this creature.
 *
 * There is no `Effects.Regenerate` facade — [RegenerateEffect] on [EffectTarget.Self] is the
 * shipped spelling (Cudgel Troll, Asphodel Wanderer).
 */
val WoodwraithStrangler = card("Woodwraith Strangler") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Plant Zombie"
    oracleText = "Exile a creature card from your graveyard: Regenerate this creature."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.ExileFromGraveyard(1, GameObjectFilter.Creature)
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "241"
        artist = "Pat Lee"
        flavorText = "\"Nothing could be more natural than roots sucking nourishment from the dead.\"\n—Savra"
        imageUri = "https://cards.scryfall.io/normal/front/b/e/be72ff91-f810-46c3-884f-6e65827824bc.jpg"
    }
}
