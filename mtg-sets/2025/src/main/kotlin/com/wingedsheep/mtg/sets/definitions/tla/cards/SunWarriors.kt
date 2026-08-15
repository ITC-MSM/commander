package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.ManaExpiry

/**
 * Sun Warriors — {2}{R}{W}
 * Creature — Human Warrior Ally
 * 3/5
 * Firebending X, where X is the number of creatures you control. (Whenever this creature
 * attacks, add X {R}. This mana lasts until end of combat.)
 * {5}: Create a 1/1 white Ally creature token.
 *
 * Firebending here is *dynamic* — X is computed on attack — so it can't use the fixed
 * `firebending(n)` helper. Instead the "Firebending X" keyword tag is shown via
 * [KeywordAbility.Variable] and the behaviour is hand-wired as an attack-triggered
 * [AddManaEffect] producing X red mana ([DynamicAmounts.creaturesYouControl]) with
 * [ManaExpiry.END_OF_COMBAT] — exactly how the `firebending` DSL models the fixed case.
 */
val SunWarriors = card("Sun Warriors") {
    manaCost = "{2}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Creature — Human Warrior Ally"
    power = 3
    toughness = 5
    oracleText = "Firebending X, where X is the number of creatures you control. " +
        "(Whenever this creature attacks, add X {R}. This mana lasts until end of combat.)\n" +
        "{5}: Create a 1/1 white Ally creature token."

    keywordAbility(KeywordAbility.Variable(Keyword.FIREBENDING, "X"))

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = AddManaEffect(
            Color.RED,
            DynamicAmounts.creaturesYouControl(),
            expiry = ManaExpiry.END_OF_COMBAT
        )
        description = "Whenever this creature attacks, add X {R}, where X is the number of " +
            "creatures you control. Until end of combat, you don't lose this mana as steps " +
            "and phases end."
    }

    activatedAbility {
        cost = Costs.Mana("{5}")
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Ally")
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "244"
        artist = "Boell Oyino"
        flavorText = "A secretive people who protect the ancient origins of firebending."
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e477d750-42d2-48c8-bc1f-8148d15d5f53.jpg?1774851714"
    }
}
