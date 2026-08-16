package com.wingedsheep.mtg.sets.definitions.dis.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Indrik Stomphowler
 * {4}{G}
 * Creature — Beast
 * 4/4
 *
 * When this creature enters, destroy target artifact or enchantment.
 */
val IndrikStomphowler = card("Indrik Stomphowler") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 4
    toughness = 4
    oracleText = "When this creature enters, destroy target artifact or enchantment."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target = Targets.ArtifactOrEnchantment
        effect = Effects.Destroy(EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "86"
        artist = "Carl Critchlow"
        flavorText = "\"An indrik's howl has destructive power much subtler than that of its crushing foot. The sound is mundane, but inaudible vibrations scatter and sunder magical contrivances.\"\n—Simic research notes"
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fe57b3a2-0fd9-4f99-bb2b-828979dbcfc3.jpg"
    }
}
