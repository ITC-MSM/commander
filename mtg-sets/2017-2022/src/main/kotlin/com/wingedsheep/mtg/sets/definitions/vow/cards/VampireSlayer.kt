package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vampire Slayer — Innistrad: Crimson Vow #43
 * {1}{W} · Creature — Human Soldier · 2/2
 *
 * Whenever this creature deals damage to a Vampire, destroy that creature.
 *
 * The East-Mark Cavalier shape with the subtype swapped: a SELF-bound
 * [Triggers.dealsDamage] whose recipient is a [RecipientFilter.Matching] over Vampire permanents.
 * The default `damageType` is `Any`, which is what the printed text says — combat damage in the
 * usual case, but also the damage from a fight or a "this creature deals damage to target
 * creature" ability. `EffectTarget.TriggeringEntity` on a recipient-filtered damage trigger is the
 * *damaged* permanent, so "that creature" needs no targeting clause of its own.
 *
 * The filter is `Permanent.withSubtype`, not `Creature.withSubtype`: a bare creature-type noun names
 * every permanent with the subtype and the adjectival "Vampire creature" is what would narrow it
 * (Zombie Master prints both lines to make the distinction). Here the two select the same objects —
 * only creatures, planeswalkers, battles and players are ever dealt damage — but the spelling is the
 * one the corpus settled on, and it is what Assay reads the sentence as.
 *
 * Destroy rather than a damage rider means it kills a Vampire whose toughness the 2 damage never
 * threatened — and, being a destroy, it is stopped by indestructible and by regeneration.
 */
val VampireSlayer = card("Vampire Slayer") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature deals damage to a Vampire, destroy that creature."

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            recipient = RecipientFilter.Matching(
                GameObjectFilter.Permanent.withSubtype("Vampire")
            )
        )
        effect = Effects.Destroy(EffectTarget.TriggeringEntity)
        description = "Whenever this creature deals damage to a Vampire, destroy that creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Tomas Duchek"
        flavorText = "When Kaya saved her village, Berit realized even vampires could be killed. " +
            "When she brought one down herself, she realized she had found her calling."
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8f6f03b-5a4e-4532-9c9e-24c75df2769f.jpg?1783924905"
    }
}
