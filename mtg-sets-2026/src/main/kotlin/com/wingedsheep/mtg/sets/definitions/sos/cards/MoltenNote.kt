package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Molten Note
 * {X}{R}{W}
 * Sorcery
 *
 * Molten Note deals damage to target creature equal to the amount of mana spent to cast this
 * spell. Untap all creatures you control.
 * Flashback {6}{R}{W}
 *
 * "The amount of mana spent to cast this spell" reads [DynamicAmount.TotalManaSpent] — the full
 * mana paid for the resolving spell (the `{X}` portion is already included, e.g. 4 when cast for
 * X=2, or 8 when cast via its {6}{R}{W} flashback). The untap is a `ForEachInGroup` over
 * [Filters.Group.creaturesYouControl] (see Blinkmoth Infusion). Flashback is a keyword ability so
 * the card can be re-cast from the graveyard for {6}{R}{W}.
 */
val MoltenNote = card("Molten Note") {
    manaCost = "{X}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Sorcery"
    oracleText = "Molten Note deals damage to target creature equal to the amount of mana spent " +
        "to cast this spell. Untap all creatures you control.\n" +
        "Flashback {6}{R}{W} (You may cast this card from your graveyard for its flashback cost. " +
        "Then exile it.)"

    spell {
        target = Targets.Creature
        effect = Effects.DealDamage(DynamicAmount.TotalManaSpent, EffectTarget.ContextTarget(0))
            .then(
                Effects.ForEachInGroup(
                    Filters.Group.creaturesYouControl,
                    Effects.Untap(EffectTarget.Self),
                )
            )
    }

    keywordAbility(KeywordAbility.flashback("{6}{R}{W}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "204"
        artist = "David Álvarez"
        flavorText = "It's characteristic of the Blood Age for instruments of music to also be " +
            "tools of war."
        imageUri = "https://cards.scryfall.io/normal/front/5/0/506f69aa-7dc4-4dd7-990a-7371fc1762c0.jpg?1775938416"
    }
}
