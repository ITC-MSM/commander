package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Deathless Ancient
 * {4}{B}{B}
 * Creature — Vampire Knight
 * 4/4
 *
 * Flying
 * Tap three untapped Vampires you control: Return this card from your graveyard to your hand.
 *
 * The ability functions from the graveyard (CR 113.6), so `activateFromZone` is the graveyard;
 * the tap cost is still paid with Vampires on the battlefield.
 */
val DeathlessAncient = card("Deathless Ancient") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Knight"
    oracleText = "Flying\n" +
        "Tap three untapped Vampires you control: Return this card from your graveyard to your hand."
    power = 4
    toughness = 4

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.TapPermanents(3, GameObjectFilter.Permanent.withSubtype(Subtype.VAMPIRE))
        effect = Effects.Move(EffectTarget.Self, Zone.HAND, fromZone = Zone.GRAVEYARD)
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "100"
        artist = "Nils Hamm"
        flavorText = "\"Ancient one, we have reached the promised shore. The Immortal Sun is near. Drink and awake.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8adc1cd9-22ff-4263-856b-0aaa990a3893.jpg"
    }
}
