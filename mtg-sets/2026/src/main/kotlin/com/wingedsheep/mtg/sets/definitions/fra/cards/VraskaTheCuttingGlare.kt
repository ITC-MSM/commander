package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vraska, the Cutting Glare — "They" is the destroyed permanent's controller, read through
 * [EffectTarget.TargetController] (last-known information once the permanent has left). The Treasure
 * is created whether or not the destroy actually happens (indestructible, regeneration), as long as
 * the target is still legal on resolution. The six-lands check is an intervening "if": it must hold
 * both when Vraska enters and when the trigger resolves.
 */
val VraskaTheCuttingGlare = card("Vraska, the Cutting Glare") {
    manaCost = "{B}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Gorgon Assassin"
    power = 4
    toughness = 4
    oracleText = "Deathtouch\n" +
        "When Vraska enters, if you control six or more lands, destroy target permanent an opponent " +
        "controls. They create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: " +
        "Add one mana of any color.\")"

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        interveningIf = Conditions.ControlLandsAtLeast(6)
        val permanent = target("target permanent an opponent controls", Targets.PermanentOpponentControls)
        effect = Effects.Composite(
            Effects.Destroy(permanent),
            Effects.CreateTreasure(controller = EffectTarget.TargetController),
        )
        description = "When Vraska enters, if you control six or more lands, destroy target permanent " +
            "an opponent controls. They create a Treasure token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "278"
        artist = "Kieran Yanner"
        flavorText = "\"No one can remake me. Not Bolas, not Phyrexia, and not you, Jace.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c28b012-5efb-488f-a1c1-09e2dddfd6ee.jpg?1789127776"
        inBooster = false
    }
}
