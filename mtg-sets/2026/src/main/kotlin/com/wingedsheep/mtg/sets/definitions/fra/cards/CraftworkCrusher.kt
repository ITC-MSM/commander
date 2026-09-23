package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreatureOrPlaneswalker

val CraftworkCrusher = card("Craftwork Crusher") {
    manaCost = "{3}{R}{R}{G}{G}"
    colorIdentity = "RG"
    typeLine = "Artifact Creature — Boar Construct"
    power = 7
    toughness = 5
    oracleText = "Trample\n" +
        "When this creature enters, choose two —\n" +
        "• This creature deals 4 damage to target creature or planeswalker.\n" +
        "• Create a 2/2 colorless Wizard Soldier creature token named Cadet.\n" +
        "• Draw a card."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ModalEffect.chooseTwo(
            Mode.withTarget(
                Effects.DealDamage(4, EffectTarget.ContextTarget(0)),
                TargetCreatureOrPlaneswalker(),
                "This creature deals 4 damage to target creature or planeswalker."
            ),
            Mode.noTarget(
                Effects.CreateToken(power = 2, toughness = 2, name = "Cadet", creatureTypes = setOf("Wizard", "Soldier"), imageUri = "https://cards.scryfall.io/normal/front/8/f/8f4534d8-2783-484f-8ebf-a47b1cc4c6df.jpg?1789734318"),
                "Create a 2/2 colorless Wizard Soldier creature token named Cadet."
            ),
            Mode.noTarget(Effects.DrawCards(1), "Draw a card.")
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "127"
        artist = "Ilse Gort"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/03f9839c-aa07-4ee7-847b-091e47ab80c4.jpg?1789470711"
        inBooster = false
    }
}
