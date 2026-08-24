package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kalastria Healer
 * {1}{B}
 * Creature — Vampire Cleric Ally
 * 1/2
 * Rally — Whenever this creature or another Ally you control enters, each opponent loses 1 life and you gain 1 life.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so the creature's own arrival fires it alongside every later Ally.
 *
 * "Each opponent loses 1 life and you gain 1 life" is a composite of the two halves, not
 * [Effects.DrainLife] — the life you gain is a printed 1, not the life lost this way.
 */
val KalastriaHealer = card("Kalastria Healer") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Cleric Ally"
    power = 1
    toughness = 2
    oracleText = "Rally — Whenever this creature or another Ally you control enters, each opponent loses 1 life " +
        "and you gain 1 life."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Composite(
            Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(1),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "114"
        artist = "Anthony Palumbo"
        flavorText = "\"Time and again, I have demonstrated my skills as a healer. Why, then, are you so ill at " +
            "ease?\""
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42c21cb3-21f4-4a8d-8068-52649f2a1846.jpg?1783938201"
    }
}
