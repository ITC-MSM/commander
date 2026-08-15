package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Katara, Water Tribe's Hope
 * {2}{W}{U}{U}
 * Legendary Creature — Human Warrior Ally
 * 3/3
 *
 * Vigilance
 * When Katara enters, create a 1/1 white Ally creature token.
 * Waterbend {X}: Creatures you control have base power and toughness X/X until end of turn.
 * X can't be 0. Activate only during your turn. (While paying a waterbend cost, you can tap
 * your artifacts and creatures to help. Each one pays for {1}.)
 *
 * Implementation notes:
 *  - "Waterbend {X}" is an activated ability whose mana [cost] carries the waterbend
 *    alternative-cost flag ([com.wingedsheep.sdk.scripting.ActivatedAbility] `hasWaterbend`); the
 *    reminder text (tap artifacts/creatures to pay {1} each) is supplied by the flag. The chosen X
 *    is read at resolution via [DynamicAmount.XValue].
 *  - "Creatures you control have base power and toughness X/X until end of turn" is an
 *    [Effects.ForEachInGroup] over [GroupFilter.AllCreaturesYouControl] running a
 *    [Effects.SetBasePowerAndToughness] (Layer 7b set values) on each member ([EffectTarget.Self]
 *    binds to the current iteration creature). The group is snapshotted at resolution, so newly
 *    minted Allies created earlier in the turn are included.
 *  - "Activate only during your turn" → [ActivationRestriction.OnlyDuringYourTurn].
 *  - "X can't be 0" is not separately enforced by the SDK (no minimum-X primitive); following the
 *    Aladdin's Lamp precedent it is documented here. With X=0 every creature you control would be
 *    set to base 0/0.
 */
val KataraWaterTribesHope = card("Katara, Water Tribe's Hope") {
    manaCost = "{2}{W}{U}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Human Warrior Ally"
    oracleText = "Vigilance\n" +
        "When Katara enters, create a 1/1 white Ally creature token.\n" +
        "Waterbend {X}: Creatures you control have base power and toughness X/X until end of " +
        "turn. X can't be 0. Activate only during your turn. (While paying a waterbend cost, you " +
        "can tap your artifacts and creatures to help. Each one pays for {1}.)"
    power = 3
    toughness = 3

    keywords(Keyword.VIGILANCE)

    // When Katara enters, create a 1/1 white Ally creature token.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Ally")
        )
    }

    // Waterbend {X}: Creatures you control have base power and toughness X/X until end of turn.
    activatedAbility {
        cost = Costs.Mana("{X}")
        hasWaterbend = true
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreaturesYouControl,
            Effects.SetBasePowerAndToughness(
                power = DynamicAmount.XValue,
                toughness = DynamicAmount.XValue,
                target = EffectTarget.Self,
                duration = Duration.EndOfTurn
            )
        )
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
        description = "Waterbend {X}: Creatures you control have base power and toughness X/X " +
            "until end of turn. X can't be 0. Activate only during your turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "231"
        artist = "Toraji"
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9ec03308-59a2-417a-938f-bdda75080e43.jpg?1764121704"
    }
}
