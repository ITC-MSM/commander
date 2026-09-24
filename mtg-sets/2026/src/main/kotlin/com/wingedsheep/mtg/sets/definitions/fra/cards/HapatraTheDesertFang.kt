package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * "For each opponent, … up to one target creature that player controls" is Kaya, Spirits'
 * Justice's one-per-player distribution (same as Hapatra, the Desert Frost). X — the greatest mana
 * value among cards in your graveyard — is read at resolution, so an empty graveyard puts no
 * counters.
 */
val HapatraTheDesertFang = card("Hapatra, the Desert Fang") {
    manaCost = "{2}{B}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Human Cleric"
    power = 3
    toughness = 3
    oracleText = "When Hapatra enters, for each opponent, put X -1/-1 counters on up to one target " +
        "creature that player controls, where X is the greatest mana value among cards in your graveyard."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target(
            "up to one target creature each opponent controls",
            TargetCreature(
                filter = TargetFilter.CreatureOpponentControls,
                optional = true,
                dynamicMaxCount = DynamicAmount.PlayerCount(Player.EachOpponent),
                differentControllers = true,
            )
        )
        effect = ForEachTargetEffect(
            listOf(
                Effects.AddDynamicCounters(
                    Counters.MINUS_ONE_MINUS_ONE,
                    DynamicAmounts.zone(Player.You, Zone.GRAVEYARD).maxManaValue(),
                    EffectTarget.ContextTarget(0),
                )
            )
        )
        description = "When Hapatra enters, for each opponent, put X -1/-1 counters on up to one target " +
            "creature that player controls, where X is the greatest mana value among cards in your graveyard."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "271"
        artist = "Jodie Muir"
        flavorText = "\"Your people stand strong, Rhonas. Rest easy, you have prepared us well.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cf7c1534-af41-4991-b3c3-f0a34ae330b5.jpg?1789385991"
        inBooster = false
    }
}
