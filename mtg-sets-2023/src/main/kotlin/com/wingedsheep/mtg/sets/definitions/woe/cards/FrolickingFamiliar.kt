package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Frolicking Familiar // Blow Off Steam
 * {2}{U}
 * Creature — Otter Wizard
 * 2/2
 *
 * Flying
 * Whenever you cast an instant or sorcery spell, this creature gets +1/+1 until end of turn.
 *
 * Adventure: Blow Off Steam — {R}, Instant — Adventure
 * Blow Off Steam deals 1 damage to any target.
 *
 * The pump is a self-targeted [Effects.ModifyStats] (end-of-turn duration) fired by
 * [Triggers.YouCastInstantOrSorcery]. (CR 715: Adventure cards. Casting the Adventure exiles the
 * card on resolution and lets the caster cast it as the creature spell while it remains in exile.)
 */
val FrolickingFamiliar = card("Frolicking Familiar") {
    manaCost = "{2}{U}"
    colorIdentity = "UR"
    typeLine = "Creature — Otter Wizard"
    oracleText = "Flying\n" +
        "Whenever you cast an instant or sorcery spell, this creature gets +1/+1 until end of turn."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    adventure("Blow Off Steam") {
        manaCost = "{R}"
        typeLine = "Instant — Adventure"
        oracleText = "Blow Off Steam deals 1 damage to any target. " +
            "(Then exile this card. You may cast the creature later from exile.)"
        spell {
            val t = target("target", Targets.Any)
            effect = Effects.DealDamage(1, t)
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "226"
        artist = "Brian Valeza"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64c432d5-4f5b-44ac-9d61-891e78460d58.jpg?1783915065"
    }
}
