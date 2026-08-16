package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ornery Dilophosaur
 * {3}{G}
 * Creature — Dinosaur
 * 2/2
 * Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)
 * Whenever this creature attacks, if you control a creature with power 4 or greater, this
 * creature gets +2/+2 until end of turn.
 *
 * The "if" is printed straight after the trigger event, so it is a CR 603.4 intervening-if:
 * checked both when the attack trigger would fire and again on resolution. It is a plain
 * existence check — a single creature with power 4 or greater is enough, and it need not be
 * the same one at both checks.
 */
val OrneryDilophosaur = card("Ornery Dilophosaur") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    power = 2
    toughness = 2
    oracleText = "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)\nWhenever this creature attacks, if you control a creature with power 4 or greater, this creature gets +2/+2 until end of turn."

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.Attacks
        interveningIf = Conditions.YouControl(GameObjectFilter.Creature.powerAtLeast(4))
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "194"
        artist = "Jonathan Kuo"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2c4a0e7-9ca4-4291-94de-165cc2ded822.jpg?1783930671"
        ruling("2020-06-23", "If you don't control a creature with power 4 or greater immediately after Ornery Dilophosaur attacks, its ability doesn't trigger. If you don't control one as the ability resolves, it has no effect. It doesn't have to be the same creature at both times, however.")
        ruling("2020-06-23", "If Ornery Dilophosaur's power is raised to 4 or greater, its ability triggers when it attacks.")
        ruling("2020-06-23", "Ornery Dilophosaur gets just +2/+2, no matter how many creatures you control with power 4 or greater.")
        ruling("2020-06-23", "Once Ornery Dilophosaur's ability has resolved, it keeps +2/+2 for the rest of the turn even if you no longer control a creature with power 4 or greater.")
    }
}
