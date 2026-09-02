package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Sisters of Stone Death — Ravnica: City of Guilds #231
 * {4}{B}{B}{G}{G} · Legendary Creature — Gorgon 7/5 · Rare
 *
 * {G}: Target creature blocks Sisters of Stone Death this turn if able.
 * {B}{G}: Exile target creature blocking or blocked by Sisters of Stone Death.
 * {2}{B}: Put a creature card exiled with Sisters of Stone Death onto the battlefield under your
 * control.
 *
 * Modelling notes:
 * - The first ability is [Effects.ForceBlock] with the default `attacker = Self` — the same
 *   "blocks *it* this turn if able" requirement Avalanche Tusker's attack trigger creates, here
 *   behind a mana cost instead. It is a requirement, not a guarantee (CR 509.1c): a creature that
 *   can't legally block the Sisters simply doesn't, and the requirement is only read while the
 *   Sisters are actually attacking. The ability may be activated before attackers are declared
 *   (the effect lasts the turn), so the executor must not demand that the Sisters already be
 *   attacking when it resolves.
 * - "Blocking or blocked by" is [GameObjectFilter.blockingOrBlockedBySource] — the live
 *   combat-pairing predicate Spitting Slug uses, read at targeting time and again on resolution.
 * - The second and third abilities are *linked* (CR 607): the exile writes the Sisters' linked
 *   exile pile and the return gathers from it, so nothing else that has ever been exiled is
 *   reachable. Per the ruling, any creature card ever exiled by the second ability can come back,
 *   whenever it was exiled, but a non-creature card (an animated land) never can — that is the
 *   `filter = Creature` on the selection, not on the gather.
 * - "Put a creature card" is one card, mandatory when one exists: `chooseExactly(1)` clamps to
 *   zero when the pile holds no creature card, which is the printed behaviour of an empty pile.
 */
val SistersOfStoneDeath = card("Sisters of Stone Death") {
    manaCost = "{4}{B}{B}{G}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Gorgon"
    power = 7
    toughness = 5
    oracleText = "{G}: Target creature blocks Sisters of Stone Death this turn if able.\n" +
        "{B}{G}: Exile target creature blocking or blocked by Sisters of Stone Death.\n" +
        "{2}{B}: Put a creature card exiled with Sisters of Stone Death onto the battlefield " +
        "under your control."

    activatedAbility {
        cost = Costs.Mana("{G}")
        val blocker = target("target creature", TargetCreature())
        effect = Effects.ForceBlock(target = blocker)
        description = "Target creature blocks Sisters of Stone Death this turn if able."
    }

    activatedAbility {
        cost = Costs.Mana("{B}{G}")
        val paired = target(
            "target creature blocking or blocked by Sisters of Stone Death",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.blockingOrBlockedBySource()))
        )
        effect = Effects.ExileLinkedToSource(paired)
        description = "Exile target creature blocking or blocked by Sisters of Stone Death."
    }

    activatedAbility {
        cost = Costs.Mana("{2}{B}")
        effect = Effects.Pipeline {
            val exiled = gather(CardSource.FromLinkedExile())
            val chosen = chooseExactly(
                1,
                from = exiled,
                filter = GameObjectFilter.Creature,
                prompt = "Choose a creature card exiled with Sisters of Stone Death to put onto the battlefield"
            )
            move(chosen, CardDestination.ToZone(Zone.BATTLEFIELD, Player.You))
        }
        description = "Put a creature card exiled with Sisters of Stone Death onto the battlefield " +
            "under your control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "231"
        artist = "Donato Giancola"
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d214d25b-96c3-4479-88f0-3996805d6e6f.jpg?1783943612"
        ruling(
            "2005-10-01",
            "The third ability can get back any creature card exiled by the second ability, no " +
                "matter when it was exiled. If a card that's not a creature card (such as " +
                "Svogthos, the Restless Tomb) was exiled, the third ability can't return it."
        )
    }
}
