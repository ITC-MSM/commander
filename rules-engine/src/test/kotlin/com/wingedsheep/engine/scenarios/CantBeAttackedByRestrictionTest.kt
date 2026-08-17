package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.CantBeAttackedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * `CantBeAttackedBy(attackerFilter)` — the general defender-side attack restriction (CR 508.1c),
 * resolved by `CantBeAttackedByDefenderRule`.
 *
 * It replaces the older `CantBeAttackedWithout(requiredKeyword, attackerFilter)`, which could only
 * express the *negative* keyword form. `GameObjectFilter.withoutKeyword` already carried that
 * meaning, so one filter-shaped static now covers both polarities:
 *  - "Creatures **with** flying can't attack you" (Storm, Windrider) — `withKeyword(FLYING)`
 *  - "Creatures **without** flying can't attack you" (Form of the Dragon) — `withoutKeyword(FLYING)`
 *
 * The tests below assert both polarities, the CR 506.3 / 508.1b scope ("you" is the *player* — a
 * planeswalker they control is a defender in its own right, per Form of the Dragon's 2014-02-01
 * ruling) and the face-down guard (CR 708.2: a face-down permanent has no abilities).
 *
 * Every test keeps an **unrestricted ground attacker** on the attacking side. Without one the engine
 * has no legal attack at all and skips the declare-attackers step entirely, which would make an
 * "expect a rejection" assertion pass for the wrong reason (or never reach the step).
 */
class CantBeAttackedByRestrictionTest : FunSpec({

    /** Storm, Windrider's polarity: creatures *with* flying can't attack you. */
    val skyWard = card("Test Sky Ward") {
        manaCost = "{2}{W}"
        typeLine = "Enchantment"
        oracleText = "Creatures with flying can't attack you."
        staticAbility {
            ability = CantBeAttackedBy(GameObjectFilter.Creature.withKeyword(Keyword.FLYING))
        }
    }

    /** Form of the Dragon's polarity: creatures *without* flying can't attack you. */
    val groundWard = card("Test Ground Ward") {
        manaCost = "{2}{R}"
        typeLine = "Enchantment"
        oracleText = "Creatures without flying can't attack you."
        staticAbility {
            ability = CantBeAttackedBy(GameObjectFilter.Creature.withoutKeyword(Keyword.FLYING))
        }
    }

    /** The same restriction on a *creature*, so it can be turned face down (CR 708.2). */
    val skyWardCreature = card("Test Sky Warden") {
        manaCost = "{2}{W}"
        typeLine = "Creature — Wall"
        power = 0
        toughness = 4
        oracleText = "Creatures with flying can't attack you."
        staticAbility {
            ability = CantBeAttackedBy(GameObjectFilter.Creature.withKeyword(Keyword.FLYING))
        }
    }

    val testFlier = card("Test Sky Raider") {
        manaCost = "{1}{U}"
        typeLine = "Creature — Bird"
        power = 2
        toughness = 2
        keywords(Keyword.FLYING)
    }

    val testGrounder = card("Test Ground Pounder") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Beast"
        power = 2
        toughness = 2
    }

    val testWalker = card("Test Restriction Walker") {
        manaCost = "{2}"
        typeLine = "Legendary Planeswalker — Tester"
        startingLoyalty = 3
        loyaltyAbility(1) {
            effect = Effects.GainLife(1)
        }
    }

    val extras = listOf(skyWard, groundWard, skyWardCreature, testFlier, testGrounder, testWalker)

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + extras)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Reach the declare-attackers step of [player]'s own turn, and prove we are there. */
    fun GameTestDriver.attackStepOf(player: EntityId) {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        if (activePlayer != player) {
            passPriorityUntil(Step.POSTCOMBAT_MAIN)
            passPriorityUntil(Step.DECLARE_ATTACKERS)
        }
        currentStep shouldBe Step.DECLARE_ATTACKERS
        activePlayer shouldBe player
    }

    /** A ground creature on [player]'s side, ready to attack — keeps the combat step alive. */
    fun GameTestDriver.readyGrounder(player: EntityId): EntityId {
        val id = putCreatureOnBattlefield(player, "Test Ground Pounder")
        removeSummoningSickness(id)
        return id
    }

    test("positive filter: a flier can't attack the player who controls the restriction") {
        val d = driver()
        val active = d.activePlayer!!
        val opp = d.getOpponent(active)

        d.putPermanentOnBattlefield(opp, "Test Sky Ward")
        val grounder = d.readyGrounder(active)
        val flier = d.putCreatureOnBattlefield(active, "Test Sky Raider")
        d.removeSummoningSickness(flier)

        d.attackStepOf(active)
        withClue("Creatures with flying can't attack you") {
            d.declareAttackers(active, listOf(flier), opp).error shouldNotBe null
        }
        withClue("the whole declaration is illegal if any attacker in it is restricted") {
            d.declareAttackers(active, listOf(flier, grounder), opp).error shouldNotBe null
        }
        withClue("the unrestricted attacker is untouched") {
            d.declareAttackers(active, listOf(grounder), opp).error shouldBe null
        }
    }

    test("positive filter: a ground creature attacks freely past a flying-only restriction") {
        val d = driver()
        val active = d.activePlayer!!
        val opp = d.getOpponent(active)

        d.putPermanentOnBattlefield(opp, "Test Sky Ward")
        val grounder = d.readyGrounder(active)

        d.attackStepOf(active)
        d.declareAttackers(active, listOf(grounder), opp).error shouldBe null
    }

    test("negative filter: the Form of the Dragon shape stops ground creatures and lets fliers by") {
        val d = driver()
        val active = d.activePlayer!!
        val opp = d.getOpponent(active)

        d.putPermanentOnBattlefield(opp, "Test Ground Ward")
        val grounder = d.readyGrounder(active)
        val flier = d.putCreatureOnBattlefield(active, "Test Sky Raider")
        d.removeSummoningSickness(flier)

        d.attackStepOf(active)
        withClue("Creatures without flying can't attack you") {
            d.declareAttackers(active, listOf(grounder), opp).error shouldNotBe null
        }
        d.declareAttackers(active, listOf(flier), opp).error shouldBe null
    }

    test("\"you\" is the player: a restricted flier may still attack their planeswalker") {
        val d = driver()
        val active = d.activePlayer!!
        val opp = d.getOpponent(active)

        d.putPermanentOnBattlefield(opp, "Test Sky Ward")
        val walker = d.putPermanentOnBattlefield(opp, "Test Restriction Walker")
        d.replaceState(
            d.state.updateEntity(walker) { c ->
                c.with((c.get<CountersComponent>() ?: CountersComponent()).withAdded(CounterType.LOYALTY, 3))
            }
        )
        d.readyGrounder(active)
        val flier = d.putCreatureOnBattlefield(active, "Test Sky Raider")
        d.removeSummoningSickness(flier)

        d.attackStepOf(active)
        withClue("the flier still can't attack the player") {
            d.declareAttackers(active, listOf(flier), opp).error shouldNotBe null
        }
        withClue("a creature that can't attack you can still attack a planeswalker you control") {
            d.declareAttackers(active, mapOf(flier to walker)).error shouldBe null
        }
    }

    test("a face-down permanent contributes no restriction (CR 708.2)") {
        val d = driver()
        val active = d.activePlayer!!
        val opp = d.getOpponent(active)

        val warden = d.putCreatureOnBattlefield(opp, "Test Sky Warden")
        d.readyGrounder(active)
        val flier = d.putCreatureOnBattlefield(active, "Test Sky Raider")
        d.removeSummoningSickness(flier)

        d.attackStepOf(active)
        withClue("face up, the warden's static ability applies") {
            d.declareAttackers(active, listOf(flier), opp).error shouldNotBe null
        }

        d.replaceState(d.state.updateEntity(warden) { it.with(FaceDownComponent) })
        withClue("face down, it is a 2/2 with no abilities") {
            d.declareAttackers(active, listOf(flier), opp).error shouldBe null
        }
    }
})
