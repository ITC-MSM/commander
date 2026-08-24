package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Reckless Rage — Rivals of Ixalan #110 (canonical printing)
 * {R} · Instant
 *
 * Reckless Rage deals 4 damage to target creature you don't control and 2 damage to target
 * creature you control.
 *
 * Two required targets with different amounts, so the spell is uncastable unless both a creature
 * you control and a creature you don't are available — that drawback is the whole cost of a
 * one-mana 4-damage removal spell, and it falls out of the targeting rules for free: a required
 * `target(...)` with no legal object makes the spell illegal to cast (CR 601.2c).
 *
 * "Creature you don't control" is modelled as [Targets.CreatureOpponentControls]. Every permanent
 * on the battlefield is controlled by some player and this engine has no teams, so the two
 * readings pick out the same set of creatures.
 *
 * Both damage sub-effects run in one [Effects.Composite] so they resolve together, and each is
 * dealt by the spell itself (no `damageSource`) — matching "Reckless Rage deals".
 */
val RecklessRage = card("Reckless Rage") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Reckless Rage deals 4 damage to target creature you don't control and 2 damage " +
        "to target creature you control."

    spell {
        val theirs = target("target creature you don't control", Targets.CreatureOpponentControls)
        val yours = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.DealDamage(4, theirs) then Effects.DealDamage(2, yours)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "110"
        artist = "Slawomir Maniak"
        flavorText = "\"Hard to starboard! Starb— Abandon ship! Abandon ship!\""
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0e4b97a3-8f6f-461e-aa55-ab752752f539.jpg?1783935296"
    }
}
