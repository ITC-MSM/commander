package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wolfir Avenger
 * {1}{G}{G}
 * Creature — Wolf Warrior
 * 3 / 3
 *
 * Flash (You may cast this spell any time you could cast an instant.)
 * {1}{G}: Regenerate this creature.
 *
 * Marrow Bats' shape with a mana price: [RegenerateEffect] on [EffectTarget.Self]. There is no
 * `Effects.Regenerate` facade, so the effect class is imported directly.
 */
val WolfirAvenger = card("Wolfir Avenger") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf Warrior"
    power = 3
    toughness = 3
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "{1}{G}: Regenerate this creature."

    keywords(Keyword.FLASH)

    activatedAbility {
        cost = Costs.Mana("{1}{G}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "205"
        artist = "Daniel Ljunggren"
        flavorText = "Released from a dark curse and bound to a higher calling."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88cc00e5-9683-4ccc-a914-c422b76f6014.jpg?1783940657"
    }
}
