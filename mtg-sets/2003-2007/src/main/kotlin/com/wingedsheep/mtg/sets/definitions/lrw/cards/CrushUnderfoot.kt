package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.SelectTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Crush Underfoot
 * {1}{R}
 * Kindred Instant — Giant
 * Choose a Giant creature you control. It deals damage equal to its power to target creature.
 *
 * Two decisions at two different times, and the card is only right if they stay apart:
 *
 *  - **"target creature"** is a printed target, declared when Crush Underfoot goes on the stack
 *    (index 0), so shroud/hexproof/protection are checked at announcement and the spell fizzles
 *    if the creature is gone on resolution.
 *  - **"Choose a Giant creature you control"** is *not* a target. It is picked mid-resolution by
 *    [SelectTargetEffect], after priority has passed — so an opponent can't respond to which
 *    Giant you picked, and the choice is made against the board as it stands when the spell
 *    resolves rather than as it stood when you cast it.
 *
 * The chosen Giant lands in the resolution pipeline's `crushGiant` collection, which the damage
 * step then reads twice: [EntityReference.FromCostStorage] for "equal to its power" (the generic
 * `storedCollections` reader — the linter pairs it with `SelectTarget.storeAs`) and
 * [EffectTarget.PipelineTarget] as the `damageSource`, so the damage is dealt *by the Giant*.
 * That distinction is load-bearing: it makes the damage red-creature damage rather than spell
 * damage, so lifelink, deathtouch, and "prevent all damage a creature would deal" all read off
 * the Giant.
 *
 * Both zero-Giant and zero-power cases fizzle gracefully. With no Giant to choose, the select
 * step stores an empty collection and [DealDamageEffect] skips the whole instruction rather than
 * falling back to Crush Underfoot itself as the source (CR 608.2b); a 0-power Giant deals no
 * damage because the amount is not positive.
 *
 * Known deviation: the Giant is found through the engine's `TargetFinder`, which filters out
 * permanents with shroud. Because the Giant is *chosen* and not targeted, the rules would let you
 * pick a Giant you control that has shroud. Hexproof is unaffected (you control it), so this only
 * bites the vanishingly rare "my own Giant has shroud" board.
 *
 * "Kindred Instant — Giant" is the 2024 errata of the printed "Tribal Instant — Giant": the card
 * has the Giant creature type in every zone, so it is itself fetched by Lorwyn's Giant-matters
 * cards (Ancient Amphitheater, Boldwyr Intimidator).
 */
val CrushUnderfoot = card("Crush Underfoot") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Kindred Instant — Giant"
    oracleText = "Choose a Giant creature you control. It deals damage equal to its power to target creature."

    spell {
        val victim = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            SelectTargetEffect(
                requirement = TargetCreature(
                    filter = TargetFilter.Creature.youControl().withSubtype(Subtype.GIANT),
                    id = "a Giant creature you control"
                ),
                storeAs = "crushGiant"
            ),
            DealDamageEffect(
                amount = DynamicAmount.EntityProperty(
                    EntityReference.FromCostStorage("crushGiant"),
                    EntityNumericProperty.Power
                ),
                target = victim,
                damageSource = EffectTarget.PipelineTarget("crushGiant")
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "162"
        artist = "Steven Belledin"
        flavorText = "Five-toed grave\n—Kithkin phrase meaning \"a giant's footprint\""
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0386925-53bc-4902-ac30-cbdcb099936d.jpg?1783942877"
    }
}
