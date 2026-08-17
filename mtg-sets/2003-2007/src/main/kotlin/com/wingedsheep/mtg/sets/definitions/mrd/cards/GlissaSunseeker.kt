package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Glissa Sunseeker — Mirrodin #120
 * {2}{G}{G} · Legendary Creature — Elf Warrior · 3/2
 *
 * The activated ability can target any artifact. Its mana value is compared with the controller's
 * unspent mana only as the ability resolves, so changing the mana pool in response can make the
 * conditional destruction succeed or fail.
 */
val GlissaSunseeker = card("Glissa Sunseeker") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Elf Warrior"
    oracleText = "First strike\n" +
        "{T}: Destroy target artifact if its mana value is equal to the amount of unspent mana you have."
    power = 3
    toughness = 2

    keywords(Keyword.FIRST_STRIKE)

    activatedAbility {
        cost = Costs.Tap
        target = Targets.Artifact
        effect = ConditionalEffect(
            condition = Compare(
                left = DynamicAmount.EntityProperty(
                    EntityReference.Target(0),
                    EntityNumericProperty.ManaValue,
                ),
                operator = ComparisonOperator.EQ,
                right = DynamicAmount.UnspentMana(Player.You),
            ),
            effect = Effects.Destroy(EffectTarget.ContextTarget(0)),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "120"
        artist = "Brom"
        flavorText = "\"There's a secret at the heart of this world, and I will unlock it.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/7/670c3106-71fc-464e-8c94-81bf7fafc3e6.jpg?1783944533"
        ruling(
            "2004-12-01",
            "The artifact's mana value must be exactly equal to the amount of unspent mana you " +
                "have when the ability resolves. If there's less mana or more mana, the artifact " +
                "won't be destroyed.",
        )
        ruling(
            "2004-12-01",
            "You need to have the unspent mana before the ability resolves. The ability doesn't " +
                "allow you to activate mana abilities while it's resolving.",
        )
    }
}
