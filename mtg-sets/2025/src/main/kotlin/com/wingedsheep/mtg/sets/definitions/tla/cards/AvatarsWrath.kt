package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Avatar's Wrath
 * {2}{W}{W}
 * Sorcery
 *
 * Choose up to one target creature, then airbend all other creatures. (Exile them. While each
 *   one is exiled, its owner may cast it for {2} rather than its mana cost.)
 * Until your next turn, your opponents can't cast spells from anywhere other than their hands.
 * Exile Avatar's Wrath.
 *
 * Modeling notes:
 * - "Choose up to one target creature, then airbend all other creatures" reuses [Effects.AirbendAll]
 *   (the fixed-{2}-recast exile-to-owner pattern) gathered from every creature on the battlefield.
 *   The spared "other" is the chosen target, so the gather sets `excludeChosenTargets = true`
 *   (the chosen creature is dropped from the airbended set) and `excludeSelf = false` (there is no
 *   battlefield source — Avatar's Wrath is a sorcery). "Up to one" is an optional single target,
 *   so choosing none airbends every creature.
 * - "Until your next turn, your opponents can't cast spells from anywhere other than their hands"
 *   is [Effects.CantCastSpellsFromNonHandZones] on each opponent — a per-player, until-your-next-turn
 *   restriction that leaves hand casts alone but forbids graveyard/exile/library-top/command-zone
 *   casts (flashback, escape, foretell, plot, a may-play permission). Enforced in the engine's
 *   CastSpellHandler and the non-hand CastFromZoneEnumerator.
 * - "Exile Avatar's Wrath" uses the `spell { selfExile() }` builder flag (CardScript.selfExileOnResolve),
 *   so StackResolver exiles the resolved sorcery instead of putting it into the graveyard — the same
 *   engine-honored self-exile Restock uses. (A mid-resolution `Move(Self, EXILE)` does NOT work: the
 *   graveyard placement is a post-resolution rules action the resolver applies afterward.)
 */
val AvatarsWrath = card("Avatar's Wrath") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Choose up to one target creature, then airbend all other creatures. (Exile them. " +
        "While each one is exiled, its owner may cast it for {2} rather than its mana cost.)\n" +
        "Until your next turn, your opponents can't cast spells from anywhere other than their hands.\n" +
        "Exile Avatar's Wrath."

    spell {
        target("up to one target creature", Targets.UpToCreatures(1))
        effect = Effects.Composite(
            listOf(
                // Airbend every creature except the chosen (up to one) target.
                Effects.AirbendAll(
                    filter = GameObjectFilter.Creature,
                    excludeSelf = false,
                    excludeChosenTargets = true
                ),
                // Your opponents can't cast spells from non-hand zones until your next turn.
                Effects.CantCastSpellsFromNonHandZones(
                    target = EffectTarget.PlayerRef(Player.EachOpponent),
                    duration = Duration.UntilYourNextTurn
                )
            )
        )
        // "Exile Avatar's Wrath." — the resolved sorcery is exiled instead of going to the
        // graveyard (engine-honored via CardScript.selfExileOnResolve in StackResolver).
        selfExile()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "12"
        artist = "Ainezu"
        imageUri = "https://cards.scryfall.io/normal/front/4/8/4811072d-fac0-40dd-a5cf-9694d51b12cf.jpg?1764119949"
    }
}
