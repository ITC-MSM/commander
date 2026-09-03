package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Shapesharer — Lorwyn #85
 * {1}{U} · Creature — Shapeshifter · 1/1
 *
 * Changeling (This card is every creature type.)
 * {2}{U}: Target Shapeshifter becomes a copy of target creature until your next turn.
 *
 * Two target requirements in printed order: the Shapeshifter that changes (index 0) and the
 * creature it copies (index 1). [Effects.EachPermanentBecomesCopyOfTarget] carries both — the
 * `affected` parameter names the permanent that becomes the copy, `target` the copy source —
 * which is Fleeting Reflection's shape with a longer duration.
 *
 * **"Target Shapeshifter" is a bare tribal noun, so it reads *permanent*, not creature.** Lorwyn
 * prints no noncreature Shapeshifter permanent, but the reading is the one the rules give and the
 * one the corpus standardised on; a `Creature.withSubtype` spelling would silently narrow it the
 * day a Kindred artifact with changeling shows up. Changeling itself makes every changeling card a
 * Shapeshifter, so the ability can retarget any of them.
 *
 * [Duration.UntilYourNextTurn] is the printed "until your next turn" exactly: the copy is reverted
 * after the untap step of this ability's controller's next turn, which is the ruling's "doesn't
 * wear off until just before your next untap step (even if an effect will cause that untap step to
 * be skipped)".
 *
 * The rest of the rulings are Rule 707 falling out of the shared copy machinery rather than
 * anything this card spells: copiable values only (so counters, tapped state, attachments and
 * pump effects on the source are not copied, and effects already applied to the *affected*
 * Shapeshifter keep applying on top), copies-of-copies resolve to what the source is copying, and
 * a Shapesharer that copies something else loses changeling and this very ability along with the
 * rest of its printed card. Either target becoming illegal makes the whole ability do nothing.
 */
val Shapesharer = card("Shapesharer") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Shapeshifter"
    power = 1
    toughness = 1
    oracleText = "Changeling (This card is every creature type.)\n" +
        "{2}{U}: Target Shapeshifter becomes a copy of target creature until your next turn."

    keywords(Keyword.CHANGELING)

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        target(
            "target Shapeshifter",
            TargetPermanent(
                filter = TargetFilter(GameObjectFilter.Permanent.withSubtype("Shapeshifter"))
            )
        )
        target("target creature", Targets.Creature)
        effect = Effects.EachPermanentBecomesCopyOfTarget(
            target = EffectTarget.ContextTarget(1),
            duration = Duration.UntilYourNextTurn,
            affected = EffectTarget.ContextTarget(0),
        )
        description = "Target Shapeshifter becomes a copy of target creature until your next turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "85"
        artist = "Alan Pollack"
        flavorText = "One good mimic deserves another."
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e148481d-a969-40d9-9335-d0e06ad8245b.jpg?1783942897"
        ruling("2017-09-29", "If either target becomes an illegal target, the ability will resolve but have no effect.")
        ruling("2007-10-01", "If the targeted Shapeshifter copies a creature that's copying a creature, it will become whatever the chosen creature is copying.")
        ruling("2007-10-01", "The copy effect doesn't wear off until just before your next untap step (even if an effect will cause that untap step to be skipped).")
        ruling("2007-10-01", "If Shapesharer itself becomes a copy of another creature, it loses both changeling and its activated ability (unless it's copying another creature with changeling and/or another Shapesharer, of course).")
        ruling("2007-10-01", "The targeted Shapeshifter copies the printed values of the targeted creature, plus any copy effects that have been applied to it. It won't copy counters on that creature. It won't copy effects that have changed the creature's power, toughness, types, color, and so on.")
        ruling("2007-10-01", "If the targeted Shapeshifter becomes a copy of a face-down creature, it will become a 2/2 creature with no name, creature type, abilities, mana cost, or color. It will not become face down and thus can't be turned face up.")
        ruling("2007-10-01", "Effects that have already applied to the targeted Shapeshifter will continue to apply to it. For example, if Giant Growth had given it +3/+3 earlier in the turn, then this ability makes it a copy of Grizzly Bears, it will be a 5/5 Grizzly Bears.")
        ruling("2007-10-01", "This ability can cause a creature to become a copy of itself. This will usually have no visible effect.")
    }
}
