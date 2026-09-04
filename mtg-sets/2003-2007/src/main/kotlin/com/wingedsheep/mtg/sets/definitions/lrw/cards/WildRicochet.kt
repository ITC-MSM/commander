package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wild Ricochet
 * {2}{R}{R}
 * Instant
 *
 * You may choose new targets for target instant or sorcery spell. Then copy that spell. You may
 * choose new targets for the copy.
 *
 * Three clauses, and the middle one is the reason the first can't be [Effects.ChangeTarget]: that
 * effect swaps a *single* target and silently no-ops on a spell with more than one, so a Fireball
 * split among three creatures would keep every original target. The all-targets retarget is
 * `ChangeTriggeringObjectTargets`, which until this card only ever read
 * `context.triggeringEntityId` — hence the `spell` parameter, pointing it at the spell this card
 * targeted instead. The chooser may change all, some or none of them (the 2013-07-01 ruling), which
 * is what that effect already does per slot.
 *
 * Order matters: the retarget resolves first, so the copy is made from the spell *as retargeted* and
 * inherits the new targets before the second "you may choose new targets" is offered.
 * [Effects.CopyTargetSpell] carries that second prompt itself, so the third clause needs no effect
 * of its own — and it is the copy's own prompt, so declining it leaves the copy pointed wherever the
 * first retarget left the original.
 *
 * The target is any instant or sorcery spell, not one you control: "it doesn't matter who controls
 * it", and the copy is controlled by you either way.
 */
val WildRicochet = card("Wild Ricochet") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "You may choose new targets for target instant or sorcery spell. Then copy that " +
        "spell. You may choose new targets for the copy."

    spell {
        val spell = target("target instant or sorcery spell", Targets.InstantOrSorcerySpell)
        effect = Effects.Composite(
            Effects.ChangeTriggeringObjectTargets(spell = spell),
            Effects.CopyTargetSpell(spell),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "196"
        artist = "Dan Murayama Scott"
        flavorText = "\"I knew that trick long before your great-grandmother's great-grandmother was born.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d76f09bc-b49a-4ad2-be2d-2a191d41b86d.jpg?1783942868"
        ruling("2013-07-01", "Wild Ricochet can target (and copy) any instant or sorcery spell, not just one with targets. It doesn't matter who controls it.")
        ruling("2013-07-01", "When Wild Ricochet resolves, it creates a copy of a spell. You control the copy. The controller of the original spell retains control of that spell. The copy is created on the stack, so it's not \"cast.\" Abilities that trigger when a player casts a spell won't trigger. The copy will then resolve like a normal spell, after players get a chance to cast spells and activate abilities. The copy resolves before the original spell.")
        ruling("2013-07-01", "The copy will have the same targets as the spell it's copying unless you choose new ones. You may change any number of the targets, including all of them or none of them. If, for one of the targets, you can't choose a new legal target, then it remains unchanged (even if the current target is illegal).")
        ruling("2013-07-01", "If the spell Wild Ricochet copies is modal (that is, it says \"Choose one —\" or the like), the copy will have the same mode. You can't choose a different one.")
        ruling("2013-07-01", "If the spell Wild Ricochet copies has an X whose value was determined as it was cast (like Volcanic Geyser does), the copy has the same value of X.")
        ruling("2013-07-01", "You can't choose to pay any additional costs for the copy. However, effects based on any additional costs that were paid for the original spell are copied as though those same costs were paid for the copy too. For example, if a player sacrifices a 3/3 creature to cast Fling, and you copy it with Wild Ricochet, the copy of Fling will also deal 3 damage to its target.")
        ruling("2013-07-01", "If the copy says that it affects \"you,\" it affects the controller of the copy, not the controller of the original spell. Similarly, if the copy says that it affects an \"opponent,\" it affects an opponent of the copy's controller, not an opponent of the original spell's controller.")
    }
}
