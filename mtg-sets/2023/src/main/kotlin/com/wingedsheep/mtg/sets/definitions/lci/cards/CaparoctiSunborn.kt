package com.wingedsheep.mtg.sets.definitions.lci.cards

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
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Caparocti Sunborn
 * {2}{R}{W}
 * Legendary Creature — Human Soldier
 * 4/4
 * Whenever Caparocti Sunborn attacks, you may tap two untapped artifacts and/or creatures you
 * control. If you do, discover 3.
 *
 * "You may tap two … If you do" is an [OptionalCostEffect] whose payable cost is the
 * Gather → Select-exactly-2 → Tap pipeline (same shape as Aziza, Mage Tower Captain).
 */
val CaparoctiSunborn = card("Caparocti Sunborn") {
    manaCost = "{2}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Human Soldier"
    power = 4
    toughness = 4
    oracleText = "Whenever Caparocti Sunborn attacks, you may tap two untapped artifacts and/or creatures you control. If you do, discover 3."

    triggeredAbility {
        trigger = Triggers.Attacks
        val tapCost = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.ControlledPermanents(
                        player = Player.You,
                        filter = (GameObjectFilter.Artifact or GameObjectFilter.Creature).untapped(),
                    ),
                    storeAs = "caparoctiTapPool",
                ),
                SelectFromCollectionEffect(
                    from = "caparoctiTapPool",
                    selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(2)),
                    storeSelected = "caparoctiToTap",
                    prompt = "Tap two untapped artifacts and/or creatures you control",
                    useTargetingUI = true,
                ),
                TapUntapCollectionEffect("caparoctiToTap", tap = true),
            ),
        )
        effect = OptionalCostEffect(
            cost = tapCost,
            ifPaid = Effects.Discover(3),
            descriptionOverride = "You may tap two untapped artifacts and/or creatures you control. If you do, discover 3.",
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "226"
        artist = "Donato Giancola"
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8ea82964-fd9c-48e3-962f-94954476b31f.jpg?1782694429"
    }
}
