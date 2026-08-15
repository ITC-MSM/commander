package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.OptionalCostEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.TapUntapCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Aziza, Mage Tower Captain — Secrets of Strixhaven #174
 * {R}{W} · Legendary Creature — Djinn Sorcerer · 2/2
 *
 * Whenever you cast an instant or sorcery spell, you may tap three untapped creatures you
 * control. If you do, copy that spell. You may choose new targets for the copy.
 *
 * "you may tap three untapped creatures you control. If you do, [copy]" is an
 * [OptionalCostEffect] whose payable cost is the Gather → Select-exactly-3 → Tap pipeline (same
 * idiom as Rent Is Due): the player may decline, and the cost only "pays" if three untapped
 * creatures they control are tapped — otherwise nothing happens. When paid, the triggering spell
 * ([EffectTarget.TriggeringEntity]) is copied via [Effects.CopyTargetSpell], which by default lets
 * the controller choose new targets for the copy (CR 707.10). The copy is created on the stack, so
 * it is not "cast" and does not re-trigger Aziza.
 */
val AzizaMageTowerCaptain = card("Aziza, Mage Tower Captain") {
    manaCost = "{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Djinn Sorcerer"
    power = 2
    toughness = 2
    oracleText = "Whenever you cast an instant or sorcery spell, you may tap three untapped " +
        "creatures you control. If you do, copy that spell. You may choose new targets for the copy."

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        val tapCost = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.ControlledPermanents(
                        player = Player.You,
                        filter = GameObjectFilter.Creature.untapped(),
                    ),
                    storeAs = "azizaTapPool",
                ),
                SelectFromCollectionEffect(
                    from = "azizaTapPool",
                    selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(3)),
                    storeSelected = "azizaToTap",
                    prompt = "Tap three untapped creatures you control",
                    useTargetingUI = true,
                ),
                TapUntapCollectionEffect("azizaToTap", tap = true),
            ),
        )
        effect = OptionalCostEffect(
            cost = tapCost,
            ifPaid = Effects.CopyTargetSpell(target = EffectTarget.TriggeringEntity),
            descriptionOverride = "You may tap three untapped creatures you control. If you do, " +
                "copy that spell. You may choose new targets for the copy.",
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "174"
        artist = "Aurore Folny"
        flavorText = "\"Good play, team! Now let's run it again until it's perfect!\""
        imageUri = "https://cards.scryfall.io/normal/front/6/2/6261e89a-dbf1-481a-823e-6bb00be57195.jpg?1775938194"
    }
}
