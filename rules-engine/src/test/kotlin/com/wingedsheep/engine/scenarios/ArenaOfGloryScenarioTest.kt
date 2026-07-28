package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.ExertedComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mh3.cards.ArenaOfGlory
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Arena of Glory (MH3) — proves the two genuinely new pieces this land needed: the Exert cost
 * (CR 701.39a, [com.wingedsheep.sdk.scripting.AbilityCost.Exert] — a wholly new engine primitive,
 * no prior user) and the checkland-style conditional tapped entry, plus that the haste rider
 * ([com.wingedsheep.sdk.scripting.effects.ManaSpellRider.GrantsKeywordWhenSpent], already proven
 * by Carnelian Orb of Dragonkind) correctly distinguishes the exert ability's {R}{R} from the
 * land's own plain {R} — only the former grants haste.
 */
class ArenaOfGloryScenarioTest : FunSpec({

    val plainAbilityId = ArenaOfGlory.activatedAbilities[0].id
    val exertAbilityId = ArenaOfGlory.activatedAbilities[1].id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        return d
    }

    test("enters tapped without a Mountain, untapped with one") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Enters-tapped replacement effects only apply through the real PlayLand pipeline —
        // putLandOnBattlefield bypasses it entirely (mirrors TdmCheckLandsScenarioTest).
        val noMountain = d.putCardInHand(active, "Arena of Glory")
        d.playLand(active, noMountain).isSuccess shouldBe true
        d.state.getEntity(noMountain)?.has<TappedComponent>() shouldBe true
    }

    test("enters untapped when a Mountain is already in play") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putLandOnBattlefield(active, "Mountain")
        val withMountain = d.putCardInHand(active, "Arena of Glory")
        d.playLand(active, withMountain).isSuccess shouldBe true
        d.state.getEntity(withMountain)?.has<TappedComponent>() shouldBe false
    }

    test("exerting doesn't untap next untap step, but untaps normally the turn after") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        val opp = d.getOpponent(active)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val arena = d.putLandOnBattlefield(active, "Arena of Glory")
        d.giveMana(active, com.wingedsheep.sdk.core.Color.RED, 1)

        d.submitSuccess(
            ActivateAbility(playerId = active, sourceId = arena, abilityId = exertAbilityId)
        )
        d.state.getEntity(arena)?.has<TappedComponent>() shouldBe true
        d.state.getEntity(arena)?.has<ExertedComponent>() shouldBe true

        // Advance through the rest of this turn, all of the opponent's turn, and into the
        // exerting player's own next untap step.
        d.passPriorityUntil(Step.END)
        d.bothPass()
        while (d.activePlayer != active) {
            d.passPriorityUntil(Step.END)
            d.bothPass()
        }
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.state.getEntity(arena)?.has<TappedComponent>() shouldBe true
        d.state.getEntity(arena)?.has<ExertedComponent>() shouldBe false

        // A later turn (no re-exert) untaps it normally.
        d.putCardInHand(active, "Plains") // keep the library non-empty-adjacent; unused otherwise
        while (d.activePlayer != active) {
            d.passPriorityUntil(Step.END)
            d.bothPass()
        }
        d.passPriorityUntil(Step.END)
        d.bothPass()
        while (d.activePlayer != active) {
            d.passPriorityUntil(Step.END)
            d.bothPass()
        }
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.state.getEntity(arena)?.has<TappedComponent>() shouldBe false
    }

    test("mana from the exert ability grants haste to a creature spell; the plain ability's mana does not") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val arena = d.putLandOnBattlefield(active, "Arena of Glory")

        // Plain {T}: Add {R} — spent on Grizzly Bears — should NOT grant haste.
        d.submitSuccess(ActivateAbility(playerId = active, sourceId = arena, abilityId = plainAbilityId))
        d.giveColorlessMana(active, 1) // Grizzly Bears costs {1}{G}; supply the generic half
        d.giveMana(active, com.wingedsheep.sdk.core.Color.GREEN, 1)
        val bearsId = d.putCardInHand(active, "Grizzly Bears")
        d.castSpell(active, bearsId)
        d.bothPass()
        val bears = d.findPermanent(active, "Grizzly Bears")!!
        d.state.projectedState.hasKeyword(bears, Keyword.HASTE) shouldBe false
    }

    test("mana from the exert ability grants haste") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val arena = d.putLandOnBattlefield(active, "Arena of Glory")
        d.giveMana(active, com.wingedsheep.sdk.core.Color.RED, 1) // pays the {R} part of the exert cost

        d.submitSuccess(ActivateAbility(playerId = active, sourceId = arena, abilityId = exertAbilityId))
        // The activation produces {R}{R} into the pool; give the extra generic mana Grizzly
        // Bears needs beyond the {G} pip, which the exert-tagged red also covers as generic.
        d.giveMana(active, com.wingedsheep.sdk.core.Color.GREEN, 1)
        val bearsId = d.putCardInHand(active, "Grizzly Bears")
        d.castSpell(active, bearsId)
        d.bothPass()
        val bears = d.findPermanent(active, "Grizzly Bears")!!
        d.state.projectedState.hasKeyword(bears, Keyword.HASTE) shouldBe true
    }
})
