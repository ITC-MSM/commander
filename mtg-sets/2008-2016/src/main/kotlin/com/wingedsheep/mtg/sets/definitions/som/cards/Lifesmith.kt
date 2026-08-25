package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Lifesmith — Scars of Mirrodin #124
 * {1}{G} · Creature — Human Artificer · 2 / 1
 *
 * Whenever you cast an artifact spell, you may pay {1}. If you do, you gain 3 life.
 *
 * A cast trigger ([Triggers.youCastSpell] over [GameObjectFilter.Artifact]), so it goes on the
 * stack above the artifact and resolves first — the payment is offered whether or not the artifact
 * ever resolves, and a countered artifact spell still gained you the life. The optional payment is
 * [MayPayManaEffect], the flat mana [com.wingedsheep.sdk.scripting.effects.Gate.MayPay] shape the
 * engine recognizes for manual mana-source selection at resolution.
 */
val Lifesmith = card("Lifesmith") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Artificer"
    power = 2
    toughness = 1
    oracleText = "Whenever you cast an artifact spell, you may pay {1}. If you do, you gain 3 life."

    triggeredAbility {
        trigger = Triggers.youCastSpell(spellFilter = GameObjectFilter.Artifact)
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{1}"),
            effect = Effects.GainLife(3)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "124"
        artist = "Eric Deschamps"
        flavorText = "The Sylvok see the artificer as a gardener, preparing the world for hardy growth."
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28e5dcac-0d59-4bcc-8a0e-036cc23065b5.jpg?1783941716"
    }
}
